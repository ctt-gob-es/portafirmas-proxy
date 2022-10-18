package es.gob.afirma.signfolder.server.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.triphase.signer.processors.AutoTriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.CAdESASiCSTriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.CAdESTriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.FacturaETriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.PAdESTriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.Pkcs1TriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.TriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.XAdESASiCSTriPhasePreProcessor;
import es.gob.afirma.triphase.signer.processors.XAdESTriPhasePreProcessor;

/**
 * Manejador para el uso est&aacute;tico de las operaciones de prefirma y postfirma.
 */
public class TriSigner {

	private static final String CRYPTO_OPERATION_TYPE_SIGN = "sign"; //$NON-NLS-1$
	private static final String CRYPTO_OPERATION_TYPE_COSIGN = "cosign"; //$NON-NLS-1$
	private static final String CRYPTO_OPERATION_TYPE_COUNTERSIGN = "countersign"; //$NON-NLS-1$

	private static final String EXTRAPARAM_COUNTERSIGN_TARGET = "target"; //$NON-NLS-1$
	private static final String EXTRAPARAM_CHECK_SIGNATURES = "checkSignatures"; //$NON-NLS-1$


	/**
	 * N&uacute;mero de p&aacute;ginas por defecto de un PDF sobre las que
	 * comprobar si se ha producido un PDF Shadow Attack.
	 */
	private static final int DEFAULT_PAGES_TO_CHECK_PSA = 10;

	/** Codificaci&oacute;n de texto por defecto. */
	private static final String DEFAULT_ENCODING = "utf-8"; //$NON-NLS-1$

	/** Manejador del log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(TriSigner.class);

	/**
	 * Prefirma el documento de una petici&oacute;n y muta la propia peticion para almacenar en ella
	 * el resultado.
	 * @param docReq Petici&oacute;n de firma de un documento.
	 * @param signerCert Certificado de firma.
	 * @param forcedExtraParams Par&aacute;metros de firma que se deben aplicar forzosamente.
	 * @throws IOException Cuando no se puede obtener el documento para prefirmar.
	 * @throws AOException Cuando ocurre un error al generar la prefirma.
	 */
	public static void doPreSign(final TriphaseSignDocumentRequest docReq,
			final X509Certificate signerCert,
			final String forcedExtraParams) throws IOException, AOException {

		// Configuramos el formato y la operacion criptografica adecuada
		String cop;
		final String format = normalizeSignatureFormat(docReq.getSignatureFormat());
		if (AOSignConstants.SIGN_FORMAT_PADES.equals(format)) {
			cop = CRYPTO_OPERATION_TYPE_SIGN;
		}
		else {
			 cop = normalizeOperationType(docReq.getCryptoOperation());
		}

		final byte[] content = docReq.getContent();

		final String algorithm = digestToSignatureAlgorithmName(docReq.getMessageDigestAlgorithm());

		// Forzamos que se incluyan una serie de parametros en la configuracion de firma. Si ya
		// se incluia alguno de estos con otro valor acabara siendo pisado ya que en un properties
		// tiene preferencia el ultimo valor leido
		final Properties extraParams = buildExtraParams(docReq.getParams());
		addFormatExtraParam(extraParams, docReq.getSignatureFormat());
		addForcedExtraParams(extraParams, forcedExtraParams.split(";")); //$NON-NLS-1$

		final TriPhasePreProcessor preprocessor = getPreprocessor(format, extraParams);

		final TriphaseData triphaseData = presignService(content, signerCert, preprocessor, cop, algorithm, extraParams);

		docReq.setPartialResult(triphaseData);
	}

	/**
	 * Compone un objeto de propiedades a partir de un listado de extraParams
	 * proporcionados en base 64.
	 *
	 * @param params Cadena base64 con un listado de propiedades.
	 * @return Conjunto de par&aacute;metros.
	 * @throws AOException Cuando no se pueden decodificar los par&aacute;metros.
	 */
	private static Properties buildExtraParams(final String params) throws AOException {

		 final Properties extraParams = new Properties();

		 if (params != null && params.length() > 0) {
			 byte[] paramsBytes;
			 try {

				 // Identificamos si podemos usar el juego de caracteres que deseamos o el por defecto
				 Charset charset;
				 try {
					 charset = Charset.forName(DEFAULT_ENCODING);
				 }
				 catch (final Exception e) {
					charset = null;
				}

				 // Cargamos el Properties de una forma u otra segun si hemos podidod definir el juego de caracteres o no
				 if (charset != null) {
					 paramsBytes = new String(Base64.decode(params), charset).replace("\\n", "\n").getBytes(charset); //$NON-NLS-1$ //$NON-NLS-2$
					 final InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(paramsBytes), charset);
					 extraParams.load(reader);
				 }
				 else {
					 paramsBytes = new String(Base64.decode(params)).replace("\\n", "\n").getBytes(); //$NON-NLS-1$ //$NON-NLS-2$
					 extraParams.load(new ByteArrayInputStream(paramsBytes));
				 }
			 } catch (final IOException e) {
				 throw new AOException("Error al decodificar los parametros de firma", e); //$NON-NLS-1$
			 }
		 }

		 return extraParams;
	 }

	/**
	 * Agrega a los extraParams cualquier par&aacute;metro necesario en base al formato
	 * de firma establecido.
	 * @param extraParams Conjunto de par&aacute;metros.
	 * @param format Formato de firma definido para el documento.
	 */
	private static void addFormatExtraParam(final Properties extraParams, final String format) {
		if (format != null) {
			// Las firmas XAdES obtendran su estructura segun lo indicado en el
			// nombre del formato. Esto es necesario porque el Portafirmas utiliza
			// el nombrede formato para configurar las firmas cuando el cliente
			// @firma lo hace en base a extraParams
			if (format.toUpperCase().contains("XADES")) { //$NON-NLS-1$
				if (format.toUpperCase().contains("ENVELOPING")) { //$NON-NLS-1$
					extraParams.setProperty("format", AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING); //$NON-NLS-1$
				}
				else if (format.toUpperCase().contains("ENVELOPED")) { //$NON-NLS-1$
					extraParams.setProperty("format", AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED); //$NON-NLS-1$
				}
			}
			// La firma PAdES siempre configuraran la politica de firma de la AGE v1.9
			if (format.equals("PDF")) { //$NON-NLS-1$
				extraParams.setProperty("signatureSubFilter", "ETSI.CAdES.detached"); //$NON-NLS-1$ //$NON-NLS-2$
				extraParams.setProperty("policyIdentifier", "2.16.724.1.3.1.1.2.1.9"); //$NON-NLS-1$ //$NON-NLS-2$
				extraParams.setProperty("policyIdentifierHash", "G7roucf600+f03r/o0bAOQ6WAs0="); //$NON-NLS-1$ //$NON-NLS-2$
				extraParams.setProperty("policyIdentifierHashAlgorithm", "1.3.14.3.2.26"); //$NON-NLS-1$ //$NON-NLS-2$
				extraParams.setProperty("policyQualifier", "https://sede.060.gob.es/politica_de_firma_anexo_1.pdf"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	/**
	 * Agrega a los par&aacute;metros de configuraci&oacute;n de la firma unos
	 * par&aacute;metros adicionales dando preferencias a estos &uacute;ltimos
	 * en caso de pisarte.
	 * @param extraParams Par&aacute;metros de firma.
	 * @param forcedParams Listado de nuevos par&aacute;metros.
	 * @throws AOException Cuando no se pueden decodificar los par&aacute;metros de firma.
	 */
	private static void addForcedExtraParams(final Properties extraParams, final String[] forcedParams) throws AOException {

		// Si no hay prametros que agregar devolvemos los parametros originales
		if (forcedParams == null ||
				forcedParams.length == 0 ||
				forcedParams.length == 1 && forcedParams[0].trim().length() == 0) {
			return;
		}

		for (final String forcedParam : forcedParams) {
			final int sepPos = forcedParam.indexOf('=');
			if (sepPos != -1 && sepPos != forcedParam.length() - 1) {
				extraParams.setProperty(forcedParam.substring(0, sepPos), forcedParam.substring(sepPos + 1));
			}
		}
	}

	/**
	 * Postfirma el documento de una petici&oacute;n.
	 * @param docReq Petici&oacute;n de firma de un documento.
	 * @param signerCert Certificado de firma.
	 * @param forcedExtraParams Par&aacute;metros de firma que se deben aplicar forzosamente.
	 * @throws IOException Cuando no se puede obtener el documento para postfirmar.
	 * @throws AOException Cuando ocurre un error al generar la postfirma.
	 */
	public static void doPostSign(final TriphaseSignDocumentRequest docReq,
			final X509Certificate signerCert,
			final String forcedExtraParams) throws IOException, AOException {

		// Configuramos el formato y la operacion criptografica adecuada
		String cop;
		final String format = normalizeSignatureFormat(docReq.getSignatureFormat());
		if (AOSignConstants.SIGN_FORMAT_PADES.equals(format)) {
			cop = CRYPTO_OPERATION_TYPE_SIGN;
		}
		else {
			 cop = normalizeOperationType(docReq.getCryptoOperation());
		}

		final byte[] content = docReq.getContent();
		final String algorithm = digestToSignatureAlgorithmName(docReq.getMessageDigestAlgorithm());

		// Forzamos que se incluyan una serie de parametros en la configuracion de firma. Si ya
		// se incluia alguno de estos con otro valor acabara siendo pisado ya que en un properties
		// tiene preferencia el ultimo valor leido
		final Properties extraParams = buildExtraParams(docReq.getParams());
		addFormatExtraParam(extraParams, docReq.getSignatureFormat());
		addForcedExtraParams(extraParams, forcedExtraParams.split(";")); //$NON-NLS-1$

		final TriPhasePreProcessor preprocessor = getPreprocessor(format, extraParams);

		byte[] result;
		try {
			result = postsignService(content, signerCert, preprocessor, cop, algorithm, extraParams, docReq.getPartialResult());
		} catch (final NoSuchAlgorithmException e) {
			throw new AOException("Un algoritmo configurado no es valido", e); //$NON-NLS-1$
		}

		docReq.setResult(result);
	}

	/**
	 * Transforma el nombre de un algoritmo de huella digital en uno de firma que
	 * utilice ese mismo algoritmo de huella digital y un cifrado RSA.
	 * @param digestAlgorithm Algoritmo de huella digital.
	 * @return Nombre del algoritmo de firma.
	 */
	private static String digestToSignatureAlgorithmName(final String digestAlgorithm) {
		return digestAlgorithm.replace("-", "").toUpperCase() + "withRSA";  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
	}

	/**
	 * Normalizamos el nombre del formato de firma.
	 * @param format Formato de firma.
	 * @return Nombre de formato normalizado o el mismo formato de entrada si no se ha encontrado correspondencia.
	 */
	private static String normalizeSignatureFormat(final String format) {
		String normalizeFormat = format;
		if (format.toLowerCase().contains("pdf") || format.toLowerCase().contains("pades")) { //$NON-NLS-1$ //$NON-NLS-2$
			normalizeFormat = AOSignConstants.SIGN_FORMAT_PADES;
		} else if (format.equalsIgnoreCase("cades")) { //$NON-NLS-1$
			normalizeFormat = AOSignConstants.SIGN_FORMAT_CADES;
		} else if (format.toLowerCase().contains("xades")) { //$NON-NLS-1$
			normalizeFormat = AOSignConstants.SIGN_FORMAT_XADES;
		}
		return normalizeFormat;
	}

	/**
	 * Normalizamos el nombre del tipo de operaci&oacute;n criptogr&aacute;fica..
	 * @param operationType Tipo de operaci&oacute;n.
	 * @return Nombre del tipo de operaci&oacute;n normalizado o el mismo de entrada
	 * si no se ha encontrado correspondencia.
	 */
	private static String normalizeOperationType(final String operationType) {
		String normalizedOp = operationType;
		if ("firmar".equalsIgnoreCase(normalizedOp)) { //$NON-NLS-1$
			normalizedOp = CRYPTO_OPERATION_TYPE_SIGN;
		} else if ("cofirmar".equalsIgnoreCase(normalizedOp)) { //$NON-NLS-1$
			normalizedOp = CRYPTO_OPERATION_TYPE_COSIGN;
		} else if ("contrafirmar".equalsIgnoreCase(normalizedOp)) { //$NON-NLS-1$
			normalizedOp = CRYPTO_OPERATION_TYPE_COUNTERSIGN;
		}

		return normalizedOp;
	}

	private static TriphaseData presignService(final byte[] docBytes, final X509Certificate signerCert, final TriPhasePreProcessor prep,
			final String subOperation, final String algorithm, final Properties extraParams)
					throws IOException, AOException {

		// Comprobamos si se ha pedido validar las firmas antes de agregarles una nueva
        final boolean checkSignatures = Boolean.parseBoolean(extraParams.getProperty(EXTRAPARAM_CHECK_SIGNATURES));

        final X509Certificate[] certChain = new X509Certificate[] { signerCert };

        TriphaseData preRes;
        if (CRYPTO_OPERATION_TYPE_SIGN.equalsIgnoreCase(subOperation)) {
        	preRes = prep.preProcessPreSign(
        			docBytes,
        			algorithm,
        			certChain,
        			extraParams,
        			checkSignatures);
        }
        else if (CRYPTO_OPERATION_TYPE_COSIGN.equalsIgnoreCase(subOperation)) {
        	preRes = prep.preProcessPreCoSign(
        			docBytes,
        			algorithm,
        			certChain,
        			extraParams,
        			checkSignatures);
        }
        else if (CRYPTO_OPERATION_TYPE_COUNTERSIGN.equalsIgnoreCase(subOperation)) {

        	CounterSignTarget target = CounterSignTarget.LEAFS;
        	if (extraParams.containsKey(EXTRAPARAM_COUNTERSIGN_TARGET)) {
        		final String targetValue = extraParams.getProperty(EXTRAPARAM_COUNTERSIGN_TARGET).trim();
        		if (CounterSignTarget.TREE.toString().equalsIgnoreCase(targetValue)) {
        			target = CounterSignTarget.TREE;
        		}
        	}
        	preRes = prep.preProcessPreCounterSign(
        			docBytes,
        			algorithm,
        			certChain,
        			extraParams,
        			target,
        			checkSignatures);
        }
        else {
        	throw new AOException("No se reconoce el codigo de sub-operacion: " + subOperation); //$NON-NLS-1$
        }

		return preRes;
	}

	private static byte[] postsignService(final byte[] docBytes, final X509Certificate signerCert, final TriPhasePreProcessor prep,
			final String subOperation, final String algorithm, final Properties extraParams, final TriphaseData triphaseData)
					throws NoSuchAlgorithmException, AOException, IOException {

        final X509Certificate[] certChain = new X509Certificate[] { signerCert };

        final byte[] signedDoc;
        if (CRYPTO_OPERATION_TYPE_SIGN.equals(subOperation)) {
        	signedDoc = prep.preProcessPostSign(
        			docBytes,
        			algorithm,
        			certChain,
        			extraParams,
        			triphaseData
        			);
        }
        else if (CRYPTO_OPERATION_TYPE_COSIGN.equals(subOperation)) {
        	signedDoc = prep.preProcessPostCoSign(
        			docBytes,
        			algorithm,
        			certChain,
        			extraParams,
        			triphaseData
        			);
        }
        else if (CRYPTO_OPERATION_TYPE_COUNTERSIGN.equals(subOperation)) {

        	CounterSignTarget target = CounterSignTarget.LEAFS;
        	if (extraParams.containsKey(EXTRAPARAM_COUNTERSIGN_TARGET)) {
        		final String targetValue = extraParams.getProperty(EXTRAPARAM_COUNTERSIGN_TARGET).trim();
        		if (CounterSignTarget.TREE.toString().equalsIgnoreCase(targetValue)) {
        			target = CounterSignTarget.TREE;
        		}
        	}

        	signedDoc = prep.preProcessPostCounterSign(
        			docBytes,
        			algorithm,
        			certChain,
        			extraParams,
        			triphaseData,
        			target
        			);
        }
        else {
        	throw new AOException("No se reconoce el codigo de sub-operacion: " + subOperation); //$NON-NLS-1$
        }

		return signedDoc;
	}

	private static TriPhasePreProcessor getPreprocessor(final String format, final Properties extraParams)
			throws IllegalArgumentException {

		// Instanciamos el preprocesador adecuado
		final TriPhasePreProcessor prep;
		if (AOSignConstants.SIGN_FORMAT_PADES.equalsIgnoreCase(format) ||
			AOSignConstants.SIGN_FORMAT_PADES_TRI.equalsIgnoreCase(format)) {
					prep = new PAdESTriPhasePreProcessor();
					configurePdfShadowAttackParameters(extraParams);
		}
		else if (AOSignConstants.SIGN_FORMAT_CADES.equalsIgnoreCase(format) ||
				 AOSignConstants.SIGN_FORMAT_CADES_TRI.equalsIgnoreCase(format)) {
					prep = new CAdESTriPhasePreProcessor();
		}
		else if (AOSignConstants.SIGN_FORMAT_XADES.equalsIgnoreCase(format) ||
				 AOSignConstants.SIGN_FORMAT_XADES_TRI.equalsIgnoreCase(format)) {
					prep = new XAdESTriPhasePreProcessor();
		}
		else if (AOSignConstants.SIGN_FORMAT_CADES_ASIC_S.equalsIgnoreCase(format) ||
				 AOSignConstants.SIGN_FORMAT_CADES_ASIC_S_TRI.equalsIgnoreCase(format)) {
					prep = new CAdESASiCSTriPhasePreProcessor();
		}
		else if (AOSignConstants.SIGN_FORMAT_XADES_ASIC_S.equalsIgnoreCase(format) ||
				 AOSignConstants.SIGN_FORMAT_XADES_ASIC_S_TRI.equalsIgnoreCase(format)) {
					prep = new XAdESASiCSTriPhasePreProcessor();
		}
		else if (AOSignConstants.SIGN_FORMAT_FACTURAE.equalsIgnoreCase(format) ||
				 AOSignConstants.SIGN_FORMAT_FACTURAE_TRI.equalsIgnoreCase(format) ||
				 AOSignConstants.SIGN_FORMAT_FACTURAE_ALT1.equalsIgnoreCase(format)) {
					prep = new FacturaETriPhasePreProcessor();
		}
		else if (AOSignConstants.SIGN_FORMAT_PKCS1.equalsIgnoreCase(format) ||
				 AOSignConstants.SIGN_FORMAT_PKCS1_TRI.equalsIgnoreCase(format)) {
					prep = new Pkcs1TriPhasePreProcessor();
		}
		else if (AOSignConstants.SIGN_FORMAT_AUTO.equalsIgnoreCase(format)) {
			prep = new AutoTriPhasePreProcessor();
		}
		else {
			LOGGER.error("Formato de firma no soportado: " + format); //$NON-NLS-1$
			throw new IllegalArgumentException("Formato de firma no soportado: " + format); //$NON-NLS-1$
		}

		return prep;
	}

	private static void configurePdfShadowAttackParameters(final Properties extraParams) {
		if (!Boolean.parseBoolean(extraParams.getProperty(PdfExtraParams.ALLOW_SHADOW_ATTACK))) {
			final int maxPagestoCheck = ConfigManager.getMaxPagesToCheckPSA();
			int pagesToCheck = DEFAULT_PAGES_TO_CHECK_PSA;
			if (extraParams.containsKey(PdfExtraParams.PAGES_TO_CHECK_PSA)) {
				final String pagesToCheckProp = extraParams.getProperty(PdfExtraParams.PAGES_TO_CHECK_PSA);
				if (PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL.equalsIgnoreCase(pagesToCheckProp)) {
					pagesToCheck = Integer.MAX_VALUE;
				}
				else {
					try {
						pagesToCheck = Integer.parseInt(pagesToCheckProp);
					}
					catch (final Exception e) {
						pagesToCheck = DEFAULT_PAGES_TO_CHECK_PSA;
					}
				}
			}
			// Comprobaremos el menor numero de paginas posible que sera el indicado por la aplicacion
			// (el por defecto si no se paso un valor) o el maximo establecido por el servicio
			pagesToCheck = Math.min(pagesToCheck, maxPagestoCheck);
			if (pagesToCheck <= 0) {
				extraParams.setProperty(PdfExtraParams.ALLOW_SHADOW_ATTACK, Boolean.TRUE.toString());
			}
			else {
				extraParams.setProperty(PdfExtraParams.PAGES_TO_CHECK_PSA, Integer.toString(pagesToCheck));
			}
		}
	}
}
