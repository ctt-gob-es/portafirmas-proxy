package es.gob.afirma.signfolder.server.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signfolder.server.proxy.SignLine.SignLineType;

/** Factor&iacute;a para la creaci&oacute;n de respuestas XML hacia el dispositivo cliente de firmas multi-fase.
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s */
final class XmlResponsesFactory {

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

	private static final String XML_PRESIGN_OPEN = "<pres>"; //$NON-NLS-1$
	private static final String XML_PRESIGN_CLOSE = "</pres>"; //$NON-NLS-1$
	private static final String XML_POSTSIGN_OPEN = "<posts>"; //$NON-NLS-1$
	private static final String XML_POSTSIGN_CLOSE = "</posts>"; //$NON-NLS-1$

	private XmlResponsesFactory() {
		// No instanciable
	}

	static String createPresignResponse(final TriphaseRequestBean triRequest) {
		final StringBuilder sb = new StringBuilder(XML_HEADER);
		sb.append(XML_PRESIGN_OPEN);
		for (int i=0; i<triRequest.size(); i++) {
			sb.append(createSingleReqPresignNode(triRequest.get(i)));
		}
		sb.append(XML_PRESIGN_CLOSE);
		return sb.toString();
	}

	private static String createSingleReqPresignNode(final TriphaseRequest triphaseRequest) {
		final StringBuilder sb = new StringBuilder("<req id=\""); //$NON-NLS-1$
		sb.append(triphaseRequest.getRef());
		sb.append("\" status=\""); //$NON-NLS-1$
		if (triphaseRequest.isStatusOk()) {
			sb.append("OK\">"); //$NON-NLS-1$
			for (final TriphaseSignDocumentRequest docReq : triphaseRequest) {
				sb.append("<doc docid=\"").append(docReq.getId()) //$NON-NLS-1$
				.append("\" cop=\"").append(docReq.getCryptoOperation()) //$NON-NLS-1$
				.append("\" sigfrmt=\"").append(docReq.getSignatureFormat()) //$NON-NLS-1$
				.append("\" mdalgo=\"").append(docReq.getMessageDigestAlgorithm()).append("\">") //$NON-NLS-1$ //$NON-NLS-2$
				.append("<params>").append(docReq.getParams() != null ? docReq.getParams() : "").append("</params>") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				.append("<result>"); //$NON-NLS-1$
				// Ahora mismo las firmas se envian de una en una, asi que usamos directamente la primera de ellas
				final Map<String, String> triSign = docReq.getPartialResult().getTriSigns().get(0).getDict();
				for (final String key : triSign.keySet().toArray(new String[triSign.size()])) {
					sb.append("<p n=\"").append(key).append("\">").append(triSign.get(key)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				sb.append("</result></doc>"); //$NON-NLS-1$
			}
			sb.append("</req>"); //$NON-NLS-1$
		}
		else {
			String exceptionb64 = null;
			final Throwable t = triphaseRequest.getThrowable();
			if (t != null) {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				t.printStackTrace(new PrintWriter(baos));
				exceptionb64 = Base64.encode(baos.toByteArray());
				try { baos.close(); } catch (final IOException e) { /* No hacemos nada */ }
			}

			if (exceptionb64 != null) {
				sb.append("KO\" exceptionb64=\"") //$NON-NLS-1$
				.append(exceptionb64)
				.append("\" />"); //$NON-NLS-1$
			}
			else {
				sb.append("KO\" />"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	public static String createPostsignResponse(final TriphaseRequestBean triRequest) {
		final StringBuilder sb = new StringBuilder(XML_HEADER);
		sb.append(XML_POSTSIGN_OPEN);
		for (int i=0;i<triRequest.size();i++) {
			sb.append(createSingleReqPostsignNode(triRequest.get(i)));
		}
		sb.append(XML_POSTSIGN_CLOSE);
		return sb.toString();
	}

	private static String createSingleReqPostsignNode(final TriphaseRequest triphaseRequest) {
		final StringBuilder sb = new StringBuilder("<req id=\""); //$NON-NLS-1$
		sb.append(triphaseRequest.getRef()).
		append("\" status=\""). //$NON-NLS-1$
		append(triphaseRequest.isStatusOk() ? "OK": "KO"). //$NON-NLS-1$ //$NON-NLS-2$
		append("\"/>"); //$NON-NLS-1$

		return sb.toString();
	}

	static String createRequestsListResponse(final PartialSignRequestsList partialSignRequests) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<list n='"); //$NON-NLS-1$
		sb.append(partialSignRequests.getTotalSignRequests());
		sb.append("'>"); //$NON-NLS-1$

		for (final SignRequest sr : partialSignRequests.getCurrentSignRequests()) {
			sb.append("<rqt id=\"").append(sr.getId()) //$NON-NLS-1$
			.append("\" priority=\"").append(sr.getPriority()) //$NON-NLS-1$
			.append("\" workflow=\"").append(sr.isWorkflow()) //$NON-NLS-1$
			.append("\" forward=\"").append(sr.isForward()) //$NON-NLS-1$
			.append("\" type=\"").append(sr.getType()) //$NON-NLS-1$
			.append("\">"); //$NON-NLS-1$

			sb.append("<subj>").append(escapeXmlCharacters(sr.getSubject())).append("</subj>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<snder>").append(escapeXmlCharacters(sr.getSender())).append("</snder>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<view>").append(sr.getView()).append("</view>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<date>").append(sr.getDate()).append("</date>"); //$NON-NLS-1$ //$NON-NLS-2$
			if(sr.getExpDate() != null) {
				sb.append("<expdate>").append(sr.getExpDate()).append("</expdate>"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			sb.append("<docs>"); //$NON-NLS-1$
			for (final SignRequestDocument doc : sr.getDocumentsRequests()) {
				sb.append("<doc docid=\"").append(doc.getId()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("<nm>").append(escapeXmlCharacters(doc.getName())).append("</nm>"); //$NON-NLS-1$ //$NON-NLS-2$
				if (doc.getSize() != null) {
					sb.append("<sz>").append(doc.getSize()).append("</sz>"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				sb.append("<mmtp>").append(escapeXmlCharacters(doc.getMimeType())).append("</mmtp>"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("<sigfrmt>").append(escapeXmlCharacters(doc.getSignFormat())).append("</sigfrmt>"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("<mdalgo>").append(escapeXmlCharacters(doc.getMessageDigestAlgorithm())).append("</mdalgo>"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("<params>").append(doc.getParams() != null ? escapeXmlCharacters(doc.getParams()) : "").append("</params>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				sb.append("</doc>"); //$NON-NLS-1$
			}
			sb.append("</docs>"); //$NON-NLS-1$
			sb.append("</rqt>"); //$NON-NLS-1$
		}

		sb.append("</list>"); //$NON-NLS-1$

		return sb.toString();
	}

	static String createRejectsResponse(final RequestResult[] requestResults) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<rjcts>"); //$NON-NLS-1$
		for (final RequestResult rr : requestResults) {
			sb.append("<rjct id=\"").append(rr.getId()) //$NON-NLS-1$
			.append("\" status=\"").append(rr.isStatusOk() ? "OK" : "KO") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.append("\"/>"); //$NON-NLS-1$
		}
		sb.append("</rjcts>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 * Crea un XML con la informaci&oacute;n de detalle de una solicitud de firma.
	 * @param requestDetails Detalle de la solicitud.
	 * @return XML con los datos detallados de la solicitud.
	 */
	static String createRequestDetailResponse(final Detail requestDetails) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);

		sb.append("<dtl id=\"").append(requestDetails.getId()) //$NON-NLS-1$
		.append("\" priority=\"").append(requestDetails.getPriority()) //$NON-NLS-1$
		.append("\" workflow=\"").append(requestDetails.isWorkflow()) //$NON-NLS-1$
		.append("\" forward=\"").append(requestDetails.isForward()) //$NON-NLS-1$
		.append("\" type=\"").append(requestDetails.getType()) //$NON-NLS-1$
		.append("\">"); //$NON-NLS-1$

		sb.append("<subj>").append(escapeXmlCharacters(requestDetails.getSubject())).append("</subj>"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("<snders>"); //$NON-NLS-1$
		for (final String sender : requestDetails.getSenders()) {
			sb.append("<snder>").append(escapeXmlCharacters(sender)).append("</snder>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("</snders>"); //$NON-NLS-1$

		sb.append("<date>").append(requestDetails.getDate()).append("</date>"); //$NON-NLS-1$ //$NON-NLS-2$
		if(requestDetails.getExpDate() != null) {
			sb.append("<expdate>").append(requestDetails.getExpDate()).append("</expdate>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("<app>").append(escapeXmlCharacters(requestDetails.getApp())).append("</app>"); //$NON-NLS-1$ //$NON-NLS-2$
		if (requestDetails.getRejectReason() != null) {
			sb.append("<rejt>").append(escapeXmlCharacters(requestDetails.getRejectReason())).append("</rejt>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("<ref>").append(requestDetails.getRef()).append("</ref>"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("<signlinestype>").append(requestDetails.getSignLinesFlow()).append("</signlinestype>"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("<sgnlines>"); //$NON-NLS-1$
		for (final SignLine signLine : requestDetails.getSignLines()) {
			sb.append("<sgnline"); //$NON-NLS-1$
			if (signLine.getType() == SignLineType.VISTOBUENO) {
				sb.append(" type='VISTOBUENO'"); //$NON-NLS-1$
			}
			sb.append(">"); //$NON-NLS-1$
			for (final String receiver : signLine.getSigners()) {
				sb.append("<rcvr>").append(escapeXmlCharacters(receiver)).append("</rcvr>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			sb.append("</sgnline>"); //$NON-NLS-1$
		}
		sb.append("</sgnlines>"); //$NON-NLS-1$

		sb.append("<docs>"); //$NON-NLS-1$
		for (final SignRequestDocument doc : requestDetails.getDocs()) {
			sb.append("<doc docid=\"").append(doc.getId()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<nm>").append(escapeXmlCharacters(doc.getName())).append("</nm>"); //$NON-NLS-1$ //$NON-NLS-2$
			if (doc.getSize() != null) {
				sb.append("<sz>").append(doc.getSize()).append("</sz>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			sb.append("<mmtp>").append(doc.getMimeType()).append("</mmtp>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<sigfrmt>").append(doc.getSignFormat()).append("</sigfrmt>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<mdalgo>").append(doc.getMessageDigestAlgorithm()).append("</mdalgo>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<params>").append(doc.getParams() != null ? escapeXmlCharacters(doc.getParams()) : "").append("</params>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			sb.append("</doc>"); //$NON-NLS-1$
		}
		sb.append("</docs>"); //$NON-NLS-1$

		// Los adjuntos son opcionales
		boolean firstTime = true;
		for (final SignRequestDocument att : requestDetails.getAttached()) {
			if(firstTime) {
				sb.append("<attachedList>"); //$NON-NLS-1$
				firstTime = false;
			}
			sb.append("<attached docid=\"").append(att.getId()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<nm>").append(escapeXmlCharacters(att.getName())).append("</nm>"); //$NON-NLS-1$ //$NON-NLS-2$
			if (att.getSize() != null) {
				sb.append("<sz>").append(att.getSize()).append("</sz>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			sb.append("<mmtp>").append(att.getMimeType()).append("</mmtp>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<sigfrmt>").append(att.getSignFormat()).append("</sigfrmt>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<mdalgo>").append(att.getMessageDigestAlgorithm()).append("</mdalgo>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<params>").append(att.getParams() != null ? escapeXmlCharacters(att.getParams()) : "").append("</params>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			sb.append("</attached>"); //$NON-NLS-1$
		}
		if(!firstTime) {
			sb.append("</attachedList>"); //$NON-NLS-1$
		}

		sb.append("</dtl>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 * Crea un XML con la informaci&oacute;n para configurar la aplicaci&oacute;n.
	 * @param appConfig Configuraci&oacute;n..
	 * @return XML con la configuraci&oacute;n..
	 */
	static String createConfigurationResponse(final AppConfiguration appConfig) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);

		sb.append("<appConf>"); //$NON-NLS-1$
		for (int i = 0; i < appConfig.getAppIdsList().size(); i++) {
			sb.append("<app id=\"").append(appConfig.getAppIdsList().get(i)).append("\">");  //$NON-NLS-1$//$NON-NLS-2$
			sb.append(escapeXmlCharacters(appConfig.getAppNamesList().get(i)));
			sb.append("</app>"); //$NON-NLS-1$
		}
		sb.append("</appConf>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createApproveRequestsResponse(final ApproveRequestList approveRequests) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);

		sb.append("<apprq>"); //$NON-NLS-1$
		for (final ApproveRequest req :  approveRequests) {
			sb.append("<r id=\"").append(req.getRequestTagId()) //$NON-NLS-1$
			.append("\" ok=\"").append(req.isOk() ? "OK" : "KO").append("\"/>");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		sb.append("</apprq>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createRequestLoginResponse(LoginRequestData loginRequestData) {
		final StringBuilder sb = new StringBuilder()
				.append(XML_HEADER)
				.append("<lgnrq id='").append(loginRequestData.getId()).append("'>") //$NON-NLS-1$ //$NON-NLS-2$
				.append(Base64.encode(loginRequestData.getData()))
				.append("</lgnrq>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createRequestLoginErrorResponse() {
		final StringBuilder sb = new StringBuilder()
				.append(XML_HEADER)
				.append("<lgnrq err='Error' />"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createValidateLoginResponse(ValidateLoginResult validateLoginData) {
		final StringBuilder sb = new StringBuilder()
				.append(XML_HEADER)
				.append("<vllgnrq ok='").append(validateLoginData.isLogged()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		if (validateLoginData.isLogged()) {
			sb.append(" dni='").append(validateLoginData.getDni()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			sb.append(" er='").append(validateLoginData.getError()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("/>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createRequestLogoutResponse() {
		final StringBuilder sb = new StringBuilder()
				.append(XML_HEADER)
				.append("<lgorq/>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createNotificationRegistryResponse(NotificationRegistryResult registryResult) {
		final StringBuilder sb = new StringBuilder()
				.append(XML_HEADER)
				.append("<reg ok='").append(registryResult.isRegistered()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!registryResult.isRegistered()) {
			sb.append(" err='").append(registryResult.getError()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("/>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 * Construye la respuesta del servicio de prefirma con ClaveFirma.
	 * @param trId Identificador de transacci&oacute;n.
	 * @param url URL de redirecci&oacute;n.
	 * @return XML con la respuesta de la operacion de prefirma con ClaveFirma.
	 */
	public static String createClaveFirmaPreSignResponse(String trId, String url) {
		return createClaveFirmaPreSignResponse(trId, url, null);
	}

	/**
	 * Construye la respuesta de error del servicio de prefirma con ClaveFirma.
	 * @param err Mensaje de error si ocurrio alguno.
	 * @return XML con la respuesta de la operacion de prefirma con ClaveFirma.
	 */
	public static String createClaveFirmaPreSignErrorResponse(String err) {
		return new StringBuilder(XML_HEADER)
				.append("<cfpre ok='false' err='") //$NON-NLS-1$
				.append(err).append("'/>").toString(); //$NON-NLS-1$
	}

	/**
	 * Construye la respuesta del servicio de prefirma con ClaveFirma.
	 * @param trId Identificador de transacci&oacute;n o {@code null} si ocurri&oacute; un error.
	 * @param url URL de redirecci&oacute;n.
	 * @param triRequests Peticiones realizadas.
	 * @return XML con la respuesta de la operacion de prefirma con ClaveFirma.
	 */
	public static String createClaveFirmaPreSignResponse(String trId, String url, TriphaseRequest[] triRequests) {
		final StringBuilder resp =  new StringBuilder(XML_HEADER)
		.append("<cfpre ok='true' ") //$NON-NLS-1$
			.append("tr='").append(trId) //$NON-NLS-1$
			.append("' url='").append(Base64.encode(url.getBytes())) //$NON-NLS-1$
			.append("'>"); //$NON-NLS-1$

		resp.append(XML_PRESIGN_OPEN);

		if (triRequests != null) {
			for (final TriphaseRequest triRequest : triRequests) {
				resp.append(createSingleReqPresignNode(triRequest));
			}
			resp.append(XML_PRESIGN_CLOSE);
		}

		resp.append("</cfpre>"); //$NON-NLS-1$

		return resp.toString();
	}

	/**
	 * Sustituye los caracteres que pueden conllevar a la malformaci&oacute;n de un XML por sus
	 * correspondientes versiones escapadas de HTML.
	 * @param text Texto a escapar.
	 * @return Cadena con los caracteres escapados.
	 */
	private static String escapeXmlCharacters(final String text) {
		return text == null ? null : text.replace("<", "&_lt;").replace(">", "&_gt;");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
