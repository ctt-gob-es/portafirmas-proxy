package es.gob.afirma.signfolder.server.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signfolder.client.EstadoNotifyPushResponse;
import es.gob.afirma.signfolder.client.UpdateNotifyPushResponse;
import es.gob.afirma.signfolder.server.proxy.SignLine.SignLineType;

/**
 * Factor&iacute;a para la creaci&oacute;n de respuestas XML hacia el
 * dispositivo cliente de firmas multi-fase.
 *
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s
 */
final class XmlResponsesFactory {

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

	private XmlResponsesFactory() {
		// No instanciable
	}

	static String createPresignResponse(final TriphaseRequestBean triRequest) {
		final StringBuilder sb = new StringBuilder(XML_HEADER);
		sb.append("<pres>"); //$NON-NLS-1$
		for (int i = 0; i < triRequest.size(); i++) {
			sb.append(createSingleReqPresignNode(triRequest.get(i)));
		}
		sb.append("</pres>"); //$NON-NLS-1$
		return sb.toString();
	}

	private static String createSingleReqPresignNode(final TriphaseRequest triphaseRequest) {
		final StringBuilder sb = new StringBuilder("<req id=\""); //$NON-NLS-1$
		sb.append(triphaseRequest.getRef());
		sb.append("\" status=\""); //$NON-NLS-1$
		// La prefirma termino correctamente
		if (triphaseRequest.isStatusOk()) {
			sb.append("OK\""); //$NON-NLS-1$
			if (triphaseRequest.isNeedConfirmation()) {
				final StringBuilder requirements = new StringBuilder();
				for (final String requirement : triphaseRequest.getRequirement().toArray(new String[0])) {
					if (requirements.length() == 0) {
						requirements.append(requirement);
					} else {
						requirements.append(";").append(requirement); //$NON-NLS-1$
					}
				}
				sb.append(" rqstText=\"").append(requirements).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
			}

			sb.append(">"); //$NON-NLS-1$
			for (final TriphaseSignDocumentRequest docReq : triphaseRequest) {

				// Si el documento necesita confirmacion, solo indicaremos el identificador
				// y el tipo de confirmacion
				if (docReq.isNeedConfirmation()) {
					sb.append("<doc docid=\"").append(docReq.getId()) //$NON-NLS-1$
						.append("\" needcnf=\"").append(docReq.isNeedConfirmation()).append("\" />"); //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}

				// Proporcionamos toda la informacion de la prefirma
				sb.append("<doc docid=\"").append(docReq.getId()) //$NON-NLS-1$
					.append("\" cop=\"").append(docReq.getCryptoOperation()) //$NON-NLS-1$
					.append("\" sigfrmt=\"").append(docReq.getSignatureFormat()) //$NON-NLS-1$
					.append("\" mdalgo=\"").append(docReq.getMessageDigestAlgorithm()) //$NON-NLS-1$
					.append("\">") //$NON-NLS-1$
					.append("<params>") //$NON-NLS-1$
					.append(docReq.getParams() != null ? escapeXmlCharacters(docReq.getParams()) : "") //$NON-NLS-1$
					.append("</params>"); //$NON-NLS-1$
				if (docReq.getPartialResult() != null) {
					sb.append("<result>"); //$NON-NLS-1$
					// Ahora mismo las firmas se envian de una en una, asi que
					// usamos directamente la primera de ellas
					final Map<String, String> triSign = docReq.getPartialResult().getTriSigns().get(0).getDict();
					for (final String key : triSign.keySet().toArray(new String[triSign.size()])) {
						sb.append("<p n=\"").append(key).append("\">").append(triSign.get(key)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					sb.append("</result>"); //$NON-NLS-1$
				}
				sb.append("</doc>"); //$NON-NLS-1$
			}
			sb.append("</req>"); //$NON-NLS-1$
		}
		// La prefirma fallo
		else {
			String exceptionb64 = null;
			final Throwable t = triphaseRequest.getThrowable();
			if (t != null) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					t.printStackTrace(new PrintWriter(baos));
					exceptionb64 = Base64.encode(baos.toByteArray());
				} catch (final IOException e) {
					// No hacemos nada
				}
			}

			if (exceptionb64 != null) {
				sb.append("KO\" exceptionb64=\"") //$NON-NLS-1$
						.append(exceptionb64).append("\" />"); //$NON-NLS-1$
			} else {
				sb.append("KO\" />"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	public static String createPostsignResponse(final TriphaseRequestBean triRequest) {
		final StringBuilder sb = new StringBuilder(XML_HEADER);
		sb.append("<posts>"); //$NON-NLS-1$
		for (int i = 0; i < triRequest.size(); i++) {
			sb.append(createSingleReqPostsignNode(triRequest.get(i)));
		}
		sb.append("</posts>"); //$NON-NLS-1$
		return sb.toString();
	}

	private static String createSingleReqPostsignNode(final TriphaseRequest triphaseRequest) {
		final StringBuilder sb = new StringBuilder("<req id=\""); //$NON-NLS-1$
		sb.append(triphaseRequest.getRef()).append("\" status=\""). //$NON-NLS-1$
				append(triphaseRequest.isStatusOk() ? "OK" : "KO"). //$NON-NLS-1$ //$NON-NLS-2$
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
			if (sr.getExpDate() != null) {
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
				sb.append("<params>").append(doc.getParams() != null ? escapeXmlCharacters(doc.getParams()) : "") //$NON-NLS-1$ //$NON-NLS-2$
						.append("</params>"); //$NON-NLS-1$
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
	 * Crea un XML con la informaci&oacute;n de detalle de una solicitud de
	 * firma.
	 *
	 * @param requestDetails
	 *            Detalle de la solicitud.
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

		if (requestDetails.getText() != null) {
			sb.append("<msg>").append(escapeXmlCharacters(requestDetails.getText())).append("</msg>"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		sb.append("<snders>"); //$NON-NLS-1$
		for (final String sender : requestDetails.getSenders()) {
			sb.append("<snder>").append(escapeXmlCharacters(sender)).append("</snder>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("</snders>"); //$NON-NLS-1$

		sb.append("<date>").append(requestDetails.getDate()).append("</date>"); //$NON-NLS-1$ //$NON-NLS-2$
		if (requestDetails.getExpDate() != null) {
			sb.append("<expdate>").append(requestDetails.getExpDate()).append("</expdate>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("<app>").append(escapeXmlCharacters(requestDetails.getApp())).append("</app>"); //$NON-NLS-1$ //$NON-NLS-2$
		if (requestDetails.getRejectReason() != null) {
			sb.append("<rejt>").append(escapeXmlCharacters(requestDetails.getRejectReason())).append("</rejt>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("<ref>").append(escapeXmlCharacters(requestDetails.getRef())).append("</ref>"); //$NON-NLS-1$ //$NON-NLS-2$

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
			sb.append("<params>").append(doc.getParams() != null ? escapeXmlCharacters(doc.getParams()) : "") //$NON-NLS-1$ //$NON-NLS-2$
					.append("</params>"); //$NON-NLS-1$
			sb.append("</doc>"); //$NON-NLS-1$
		}
		sb.append("</docs>"); //$NON-NLS-1$

		// Los adjuntos son opcionales
		boolean firstTime = true;
		for (final SignRequestDocument att : requestDetails.getAttached()) {
			if (firstTime) {
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
			sb.append("<params>").append(att.getParams() != null ? escapeXmlCharacters(att.getParams()) : "") //$NON-NLS-1$ //$NON-NLS-2$
					.append("</params>"); //$NON-NLS-1$
			sb.append("</attached>"); //$NON-NLS-1$
		}
		if (!firstTime) {
			sb.append("</attachedList>"); //$NON-NLS-1$
		}

		sb.append("</dtl>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 * Crea un XML con la informaci&oacute;n para configurar la
	 * aplicaci&oacute;n.
	 *
	 * @param appConfig
	 *            Configuraci&oacute;n..
	 * @return XML con la configuraci&oacute;n..
	 */
	static String createConfigurationResponse(final AppConfiguration appConfig) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);

		sb.append("<appConf>"); //$NON-NLS-1$
		for (int i = 0; i < appConfig.getAppIdsList().size(); i++) {
			sb.append("<app id=\"").append(appConfig.getAppIdsList().get(i)).append("\">"); //$NON-NLS-1$//$NON-NLS-2$
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
		for (final ApproveRequest req : approveRequests) {
			sb.append("<r id=\"").append(req.getRequestTagId()) //$NON-NLS-1$
					.append("\" ok=\"").append(req.isOk() ? "OK" : "KO").append("\"/>"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		sb.append("</apprq>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createLoginResponse(final LoginRequestData loginRequestData, final String ssid) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER).append("<lgnrq id='").append(loginRequestData.getId()); //$NON-NLS-1$
		if (ssid != null) {
			sb.append("' ssid='").append(ssid); //$NON-NLS-1$
		}
		sb.append("'>") //$NON-NLS-1$
				.append(Base64.encode(loginRequestData.getData())).append("</lgnrq>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createClaveLoginResponse(final String claveUrl, final String sessionId) {
		final StringBuilder sb = new StringBuilder().append(XML_HEADER).append("<lgnrq>") //$NON-NLS-1$
				.append("<url>") //$NON-NLS-1$
				.append(escapeXmlCharacters(claveUrl)).append("</url>"); //$NON-NLS-1$
		if (sessionId != null) {
			sb.append("<sessionId>") //$NON-NLS-1$
					.append(sessionId).append("</sessionId>"); //$NON-NLS-1$
		}
		sb.append("</lgnrq>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createValidateLoginResponse(final ValidateLoginResult validateLoginData) {
		final StringBuilder sb = new StringBuilder().append(XML_HEADER).append("<vllgnrq ok='") //$NON-NLS-1$
				.append(validateLoginData.isLogged()).append("'"); //$NON-NLS-1$
		if (validateLoginData.isLogged()) {
			sb.append(" dni='").append(validateLoginData.getDni()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			sb.append(" er='").append(validateLoginData.getError().toString()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("/>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createLogoutResponse() {
		final StringBuilder sb = new StringBuilder().append(XML_HEADER).append("<lgorq/>"); //$NON-NLS-1$

		return sb.toString();
	}

	public static String createNotificationRegistryResponse(final NotificationRegistryResult registryResult) {
		final StringBuilder sb = new StringBuilder().append(XML_HEADER).append("<reg ok='") //$NON-NLS-1$
				.append(registryResult.isRegistered()).append("'"); //$NON-NLS-1$
		if (!registryResult.isRegistered()) {
			sb.append(" err='").append(registryResult.getError()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("/>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 * Construye la respuesta del servicio de prefirma con ClaveFirma.
	 *
	 * @param trId
	 *            Identificador de transacci&oacute;n.
	 * @param url
	 *            URL de redirecci&oacute;n.
	 * @return XML con la respuesta de la operacion de prefirma con ClaveFirma.
	 */
	public static String createFireSignResponse(final boolean status, final int errorType) {

		final StringBuilder resp = new StringBuilder(XML_HEADER).append("<cfsig ok='").append(status).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!status) {
			resp.append(" er='").append(errorType).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		resp.append("/>"); //$NON-NLS-1$

		return resp.toString();
	}

	/**
	 * Construye el XML con el resultado de una operaci&oacute;n de datos de
	 * carga en FIRe.
	 *
	 * @param requestInfo
	 *            Resultado de carga de datos.
	 * @return XML con el resultado de la operaci&oacute;n de carga.
	 */
	public static String createFireLoadDataResponse(final FireLoadDataResult requestInfo) {
		final StringBuilder sb = new StringBuilder().append(XML_HEADER).append("<cfrqt ok='") //$NON-NLS-1$
				.append(requestInfo.isStatusOk()).append("'>") //$NON-NLS-1$
				.append(escapeXmlCharacters(requestInfo.getUrlRedirect())).append("</cfrqt>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 * Protege las cadenas que pueden crear malformaciones en un XML para que su
	 * contenido no sea tratado por el procesador XML.
	 *
	 * @param text
	 *            Texto a proteger.
	 * @return Cadena protegida.
	 */
	private static String escapeXmlCharacters(final String text) {
		return text == null ? null : "<![CDATA[" + text + "]]>"; //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Genera la respuesta XML del servicio de recuperaci&oacute;n de la configuraci&oacute;n
	 * de usuario.
	 *
	 * @param result
	 *            Resultado obtenido de Portafirmas Web.
	 * @return la respuesta XML para Portafirmas Android.
	 */
	public static String createGetUserConfigurationResponse(final GetUserConfigResult result) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<rsgtsrcg v=\"").append(getProxyVersion()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
		if (result.isError()) {
			sb.append("<err>"); //$NON-NLS-1$
			String msg;
			switch (result.getErrorType()) {
			case GetUserConfigResult.ERROR_TYPE_COMMUNICATION:
				msg = "Error de comunicaci\u00F3n con Portafirmas Web"; //$NON-NLS-1$
				break;
			case GetUserConfigResult.ERROR_TYPE_RESPONSE:
				msg = "Error en el procesado de la petici\u00F3n/respuesta de Portafirmas Web"; //$NON-NLS-1$
				break;
			default:
				msg = "Error desconocido"; //$NON-NLS-1$
			}
			sb.append(msg);
			sb.append("</err>"); //$NON-NLS-1$
		} else {
			addRolesToResult(sb, result.getUserRoles());
			addSIMConfig(sb, result.isSimConfigured());
			addPushNotificationsState(sb, result.isNotificationActivated());
			addUserWithVerifiers(sb, result.isValidatorAssigned());
			addFilters(sb, result);
		}
		sb.append("</rsgtsrcg>"); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * Obtiene el c&oacute;digo de versi&oacute;n declarada por el servicio proxy.
	 * @return C&oacute;digo de versi&oacute;n del servicio.
	 */
	private static String getProxyVersion() {
		return VersionService.getVersionCode();
	}

	/**
	 * A&ntilde;ade la lista de filtros.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param configuration
	 *            Configuraci&oacute;n de usuario donde est&aacute; la lista de filtros.
	 */
	private static void addFilters(final StringBuilder sb, final GetUserConfigResult configuration) {
		if (configuration.getApplicationsFilters() != null
				//|| configuration.getTagsFilters() != null
				|| configuration.getPeriodsFilters() != null
				|| configuration.getTypesFilters() != null) {
			sb.append("<fltrs>"); //$NON-NLS-1$
			addFiltersYear(sb, configuration.getYearsFilters());
			addFiltersMonths(sb, configuration.getPeriodsFilters());
			//addFiltersTags(sb, configuration.getTagsFilter());
			addFiltersTypes(sb, configuration.getTypesFilters());
			addFiltersApplications(sb, configuration.getApplicationsFilters());
			sb.append("</fltrs>"); //$NON-NLS-1$
		}
	}

	/**
	 * A&ntilde;ade la lista de filtros de aplicaciones.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param mobileFiltroApplicationList
	 *            Lista de filtros de aplicaciones.
	 */
	private static void addFiltersApplications(final StringBuilder sb,
			final List<GenericFilter> filtersList) {
		if (filtersList != null) {
			sb.append("<pps>"); //$NON-NLS-1$
			for (final GenericFilter appFilter : filtersList) {
				sb.append("<pp>"); //$NON-NLS-1$
				if (appFilter.getId() != null) {
					sb.append("<ppId>"); //$NON-NLS-1$
					sb.append(appFilter.getId());
					sb.append("</ppId>"); //$NON-NLS-1$
				}
				if (appFilter.getDescription() != null) {
					sb.append("<ppName>"); //$NON-NLS-1$
					sb.append(appFilter.getDescription());
					sb.append("</ppName>"); //$NON-NLS-1$
				}
				sb.append("</pp>"); //$NON-NLS-1$
			}
			sb.append("</pps>"); //$NON-NLS-1$
		}
	}

	/**
	 * A&ntilde;ade la lista de filtros de tipos.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param mobileFiltroTipoList
	 *            Lista de filtros de tipos.
	 */
	private static void addFiltersTypes(final StringBuilder sb, final List<GenericFilter> filtersList) {
		if (filtersList != null) {
			sb.append("<tps>"); //$NON-NLS-1$
			for (final GenericFilter typeFilter : filtersList) {
				sb.append("<tp>"); //$NON-NLS-1$
				if (typeFilter.getId() != null) {
					sb.append("<tpId>"); //$NON-NLS-1$
					sb.append(typeFilter.getId());
					sb.append("</tpId>"); //$NON-NLS-1$
				}
				if (typeFilter.getDescription() != null) {
					sb.append("<tpDescription>"); //$NON-NLS-1$
					sb.append(typeFilter.getDescription());
					sb.append("</tpDescription>"); //$NON-NLS-1$
				}
				sb.append("</tp>"); //$NON-NLS-1$
			}
			sb.append("</tps>"); //$NON-NLS-1$
		}

	}

//	/**
//	 * A&ntilde;ade la lista de filtros de etiquetas.
//	 *
//	 * @param sb
//	 *            Objeto que representa el XML en construcci&oacute;n.
//	 * @param mobileFiltroEtiquetaList
//	 *            Lista de filtros de etiquetas.
//	 */
//	private static void addFiltersTags(final StringBuilder sb, final List<TagFilter> filtersList) {
//		if (filtersList != null) {
//			sb.append("<tgs>"); //$NON-NLS-1$
//			for (final GenericFilter tagFilter : filtersList) {
//				sb.append("<tg>"); //$NON-NLS-1$
//				if (tagFilter.getId() != null) {
//					sb.append("<tgId>"); //$NON-NLS-1$
//					sb.append(tagFilter.getId());
//					sb.append("</tgId>"); //$NON-NLS-1$
//				}
//				if (tagFilter.getDescription() != null) {
//					sb.append("<tgDescription>"); //$NON-NLS-1$
//					sb.append(tagFilter.getDescription());
//					sb.append("</tgDescription>"); //$NON-NLS-1$
//				}
//				if (tagFilter.getColor() != null) {
//					sb.append("<tgColor>"); //$NON-NLS-1$
//					sb.append(tagFilter.getColor());
//					sb.append("</tgColor>"); //$NON-NLS-1$
//				}
//				sb.append("</tg>"); //$NON-NLS-1$
//			}
//			sb.append("</tgs>"); //$NON-NLS-1$
//		}
//
//	}

	/**
	 * A&ntilde;ade la lista de filtros de meses.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param mobileFiltroMesList
	 *            Lista de filtros de meses.
	 */
	private static void addFiltersMonths(final StringBuilder sb, final List<GenericFilter> filtersList) {
		if (filtersList != null) {
			sb.append("<mnths>"); //$NON-NLS-1$
			for (final GenericFilter monthFilter : filtersList) {
				sb.append("<mnth>"); //$NON-NLS-1$
				if (monthFilter.getId() != null) {
					sb.append("<mnthId>"); //$NON-NLS-1$
					sb.append(monthFilter.getId());
					sb.append("</mnthId>"); //$NON-NLS-1$
				}
				if (monthFilter.getDescription() != null) {
					sb.append("<mnthDescription>"); //$NON-NLS-1$
					sb.append(monthFilter.getDescription());
					sb.append("</mnthDescription>"); //$NON-NLS-1$
				}
				sb.append("</mnth>"); //$NON-NLS-1$
			}
			sb.append("</mnths>"); //$NON-NLS-1$
		}

	}

	/**
	 * A&ntilde;ade la lista de filtros de a&ntilde;os.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param mobileFiltroAnioList
	 *            Lista de filtros de a&ntilde;os.
	 */
	private static void addFiltersYear(final StringBuilder sb, final List<GenericFilter> filtersList) {
		if (filtersList != null) {
			sb.append("<yrs>"); //$NON-NLS-1$
			for (final GenericFilter yearFilter : filtersList) {
				sb.append("<yr>"); //$NON-NLS-1$
				if (yearFilter.getId() != null) {
					sb.append("<YrId>"); //$NON-NLS-1$
					sb.append(yearFilter.getId());
					sb.append("<YrId>"); //$NON-NLS-1$
				}
				if (yearFilter.getDescription() != null) {
					sb.append("<YrDescription>"); //$NON-NLS-1$
					sb.append(yearFilter.getDescription());
					sb.append("</YrDescription>"); //$NON-NLS-1$
				}
				sb.append("</yr>"); //$NON-NLS-1$
			}
			sb.append("</yrs>"); //$NON-NLS-1$
		}

	}

	/**
	 * A&ntilde;ade el valor que indica si el usuario tiene validadores.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param verifiersAssigned
	 *            Valor a a&ntilde;adir.
	 */
	private static void addUserWithVerifiers(final StringBuilder sb, final Boolean verifiersAssigned) {
		if (verifiersAssigned != null) {
			sb.append("<srvrf>"); //$NON-NLS-1$
			sb.append(verifiersAssigned.booleanValue() ? "S" : "N"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("</srvrf>"); //$NON-NLS-1$
		}
	}

	/**
	 * A&ntilde;ade el valor de la configuraci&oacute;n SIM de portafirmas-web.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param simConfigured
	 *            Valor de la configuraci&oacute;n SIM.
	 */
	private static void addSIMConfig(final StringBuilder sb, final Boolean simConfigured) {
		if (simConfigured != null) {
			sb.append("<smcg>"); //$NON-NLS-1$
			sb.append(simConfigured.booleanValue() ? "S" : "N"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("</smcg>"); //$NON-NLS-1$
		}
	}

	/**
	 * A&ntilde;ade el valor que indica si se tienen activas las notificaciones PUSH.
	 *
	 * @param sb
	 *            Objeto que representa el XML en construcci&oacute;n.
	 * @param notificationsActivated
	 *            Valor a a&ntilde;adir.
	 */
	private static void addPushNotificationsState(final StringBuilder sb, final Boolean notificationsActivated) {
		if (notificationsActivated != null) {
			sb.append("<ntpsh>"); //$NON-NLS-1$
			sb.append(notificationsActivated.booleanValue() ? "S" : "N"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("</ntpsh>"); //$NON-NLS-1$
		}
	}

	/**
	 * A&ntilde;ade la lista de usuarios como XML al stringBuilder recibido como
	 * par&aacute;metro.
	 *
	 * @param sb
	 *            Objeto donde se a&ntilde;adiran los usuarios.
	 * @param roles
	 *            Lista de usuarios a a&ntilde;adir.
	 */
	private static void addRolesToResult(final StringBuilder sb, final List<Role> roles) {
		if (roles != null) {
			sb.append("<rls>"); //$NON-NLS-1$
			for (final Role role : roles) {
				sb.append("<role>"); //$NON-NLS-1$
				sb.append("<id>"); //$NON-NLS-1$
				sb.append(role.getId());
				sb.append("</id>"); //$NON-NLS-1$
				sb.append("<roleName>"); //$NON-NLS-1$
				sb.append(role.getName());
				sb.append("</roleName>"); //$NON-NLS-1$
				sb.append("<userName>"); //$NON-NLS-1$
				sb.append(role.getUserName());
				sb.append("</userName>"); //$NON-NLS-1$
				sb.append("<dni>"); //$NON-NLS-1$
				sb.append(role.getUserId());

				sb.append("</dni>"); //$NON-NLS-1$
				// TODO: Codigo asociado a la gestion de las etiquetas de roles.
				// Descomentar cuando se requiera dicha funcionalidad.
				// if (role.getMobileFiltroEtiquetaList() != null &&
				// !role.getMobileFiltroEtiquetaList().isEmpty()
				// && role.getMobileFiltroEtiquetaList().get(0).getApplication()
				// != null
				// &&
				// !role.getMobileFiltroEtiquetaList().get(0).getApplication().isEmpty())
				// {
				// sb.append("<filters>"); //$NON-NLS-1$
				// for (MobileFiltroEtiqueta tag :
				// role.getMobileFiltroEtiquetaList().get(0).getApplication()) {
				// if (tag != null) {
				// sb.append("<filter>"); //$NON-NLS-1$
				// if (tag.getId() != null) {
				// sb.append("<fltId>"); //$NON-NLS-1$
				// sb.append(tag.getId());
				// sb.append("</fltId>"); //$NON-NLS-1$
				// }
				// if (tag.getDescripcion() != null) {
				// sb.append("<description>"); //$NON-NLS-1$
				// sb.append(tag.getDescripcion());
				// sb.append("</description>"); //$NON-NLS-1$
				// }
				// if (tag.getColor() != null) {
				// sb.append("<color>"); //$NON-NLS-1$
				// sb.append(tag.getColor());
				// sb.append("</color>"); //$NON-NLS-1$
				// }
				// sb.append("</filter>"); //$NON-NLS-1$
				// }
				// }
				// sb.append("</filters>"); //$NON-NLS-1$
				// }
				sb.append("</role>"); //$NON-NLS-1$
			}
			sb.append("</rls>"); //$NON-NLS-1$
		}
	}

	/**
	 * M&eacute;todo que genera la respuesta del servicio de recuperaci&oacute;n de usuario.
	 *
	 * @param result
	 *            Resultado recibida del portafirmas-web.
	 * @return resultado a enviar al portafirmas-m&oacute;vil.
	 */
	public static String createFindUserResponse(final FindUserResult result) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<rsfinduser>"); //$NON-NLS-1$

		if (result.isError()) {
			String msg;
			switch (result.getErrorType()) {
			case FindUserResult.ERROR_TYPE_COMMUNICATION:
				msg = "Error de comunicacion con Portafirmas Web"; //$NON-NLS-1$
				break;
			default:
				msg = "Error desconocido"; //$NON-NLS-1$
			}
			sb.append("<err>").append(msg).append("</err>"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			sb.append("<users>"); //$NON-NLS-1$
			addUsersToResult(sb, result.getUsers());
			sb.append("</users>"); //$NON-NLS-1$
		}
		sb.append("</rsfinduser>"); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * A&ntilde;ade la lista de usuarios como XML al stringBuilder recibido como
	 * par&aacute;metro.
	 *
	 * @param sb
	 *            Objeto donde se a&ntilde;adiran los usuarios.
	 * @param users
	 *            Lista de usuarios a a&ntilde;adir.
	 */
	private static void addUsersToResult(final StringBuilder sb, final List<GenericUser> users) {

		// Si el listado es nulo o vacio, no hacemos nada
		if (users == null || users.isEmpty()) {
			return;
		}

		for (final GenericUser user : users) {
			sb.append("<user ") //$NON-NLS-1$
				.append("id=\"").append(user.getId()).append("\" ") //$NON-NLS-1$ //$NON-NLS-2$
				.append("dni=\"").append(user.getDni()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<![CDATA[").append(user.getName()).append("]]>"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("</user>"); //$NON-NLS-1$
		}
	}

	/**
	 * M&eacute;todo que genera la respuesta para el servicio de validaci&oacute;n de
	 * peticiones.
	 *
	 * @param results
	 *            Resultado de la operaci&oacute;n recibida por portafirmas-web.
	 * @return resultado a enviar al portafirmas-m&oacute;vil.
	 */
	public static String createVerifyPetitionsResponse(final List<VerifyPetitionResult> results) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<verifrp>"); //$NON-NLS-1$
		if (results != null && !results.isEmpty()) {
			for (final VerifyPetitionResult result : results) {
				sb.append("<r"); //$NON-NLS-1$
				// Atributo id.
				sb.append(" id=\""); //$NON-NLS-1$
				sb.append(result.getId());
				// Atributo ok.
				sb.append("\" ok=\""); //$NON-NLS-1$
				sb.append(result.getResult() != null ? result.getResult() : "OK"); //$NON-NLS-1$
				sb.append("\"/>"); //$NON-NLS-1$
			}
		}
		sb.append("</verifrp>"); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * M&eacute;todo que genera la respuesta para el servicio de creaci&oacute;n de roles.
	 *
	 * @param result
	 *            Resultado de la operaci&oacute;n recibida por portafirmas-web.
	 * @return resultado a enviar al portafirmas-m&oacute;vil.
	 */
	public static String createCreationRoleResponse(final CreateRoleResult result) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<crtnwrl>"); //$NON-NLS-1$
		sb.append("<resp"); //$NON-NLS-1$
		// Atributo success.
		sb.append(" success=\""); //$NON-NLS-1$
		sb.append(result.isSuccess());
		sb.append("\">"); //$NON-NLS-1$
		// Mensaje de error
		if (!result.isSuccess()) {
			sb.append("<errorMsg>"); //$NON-NLS-1$
			switch (result.getErrorType()) {
			case CreateRoleResult.ERROR_TYPE_COMMUNICATION:
				sb.append("Error de comunicacion con Portafirmas Web"); //$NON-NLS-1$
				break;
			case CreateRoleResult.ERROR_TYPE_REQUEST:
				sb.append("Error en la peticion realizada a Portafirmas Web"); //$NON-NLS-1$
				break;
			case CreateRoleResult.ERROR_TYPE_DOCUMENT:
				sb.append("Error en el procesado de la peticion/respuesta de Portafirmas Web"); //$NON-NLS-1$
				break;
			default:
				sb.append("Error desconocido"); //$NON-NLS-1$
			}
			sb.append("</errorMsg>"); //$NON-NLS-1$
		}
		sb.append("</resp>"); //$NON-NLS-1$
		sb.append("</crtnwrl>"); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * M&eacute;todo que genera la respuesta para el servicio de obtener el estado de
	 * las notificaciones push.
	 *
	 * @param result
	 *            Resultado de la operaci&oacute;n recibida por portafirmas-web.
	 * @return resultado a enviar al portafirmas-m&oacute;vil.
	 */
	public static String createGetPushStatusResponse(final EstadoNotifyPushResponse result) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<pshsttsrs>"); //$NON-NLS-1$
		if (result.getValorNotifyPush() != null) {
			sb.append(result.getValorNotifyPush());
		}
		sb.append("</pshsttsrs>"); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * M&eacute;todo que genera la respuesta para el servicio de actualizaci&oacute;n del
	 * estado de las notificaciones push.
	 *
	 * @param result
	 *            Resultado de la operaci&oacute;n recibida por portafirmas-web.
	 * @return resultado a enviar al portafirmas-m&oacute;vil.
	 */
	public static String createUpdatePushStatusResponse(final UpdateNotifyPushResponse result) {
		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<pdtpshsttsrs>"); //$NON-NLS-1$
		if (result.getResultado() != null) {
			sb.append(result.getResultado());
		}
		sb.append("</pdtpshsttsrs>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 *  Construye el XML de respuesta a la peticion de listado de autorizaciones de un usuario.
	 * @param authorizationsList Resultado del listado de autorizaciones de un usuario.
	 * @return XML con el resultado del listar las autorizaciones.
	 */
	public static String createListAuthorizationsResponse(final ListAuthorizations authorizationsList) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<rsauthlist>"); //$NON-NLS-1$
		if (authorizationsList.isError()) {
			sb.append("<errorMsg>"); //$NON-NLS-1$
			switch (authorizationsList.getErrorType()) {
			case ListAuthorizations.ERROR_TYPE_COMMUNICATION:
				sb.append("Error de comunicacion con Portafirmas Web"); //$NON-NLS-1$
				break;
			default:
				sb.append("Error desconocido"); //$NON-NLS-1$
			}
			sb.append("</errorMsg>"); //$NON-NLS-1$
		}
		else {
			sb.append("<authlist>"); //$NON-NLS-1$
			for (final Authorization auth : authorizationsList.getAuthorizationsList()) {
				sb.append("<auth ") //$NON-NLS-1$
					.append("id=\"").append(auth.getId()).append("\" ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("type=\"").append(auth.getType()).append("\" ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("state=\"").append(auth.getState()).append("\" ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("startdate=\"").append(DateTimeFormatter.getAppFormatterInstance().format(auth.getStartDate())).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
				if (auth.getRevocationDate() != null) {
					sb.append("revdate=\"").append(DateTimeFormatter.getAppFormatterInstance().format(auth.getRevocationDate())).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (auth.isSended()) {
					sb.append("sended=\"").append(auth.isSended()).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				sb.append(">"); //$NON-NLS-1$

				final GenericUser user = auth.getUser();
				sb.append("<user ") //$NON-NLS-1$
					.append("id=\"").append(user.getId()).append("\" ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("dni=\"").append(user.getDni()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("<![CDATA[").append(user.getName()).append("]]>"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("</user>"); //$NON-NLS-1$

				final GenericUser authUser = auth.getAuthorizedUser();
				sb.append("<authuser ") //$NON-NLS-1$
					.append("id=\"").append(authUser.getId()).append("\" ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("dni=\"").append(authUser.getDni()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("<![CDATA[").append(authUser.getName()).append("]]>"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("</authuser>"); //$NON-NLS-1$

				if (auth.getObservations() != null) {
					sb.append("<observations><![CDATA[").append(auth.getObservations()).append("]]></observations>"); //$NON-NLS-1$ //$NON-NLS-2$
				}

				sb.append("</auth>"); //$NON-NLS-1$
			}
			sb.append("</authlist>"); //$NON-NLS-1$
		}
		sb.append("</rsauthlist>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 *  Construye el XML de respuesta de una peticion en la que solo se indica si
	 *  el resultado fue correcto o no.
	 * @param result Resultado de la operaci&oacute;n.
	 * @return XML con el resultado de la operaci&oacute;n.
	 */
	public static String createGenericResponse(final GenericResult result) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<rs>"); //$NON-NLS-1$
		sb.append("<result>").append(result.isSuccess()).append("</result>"); //$NON-NLS-1$ //$NON-NLS-2$
		// Mensaje de error
		if (!result.isSuccess()) {
			sb.append("<errorMsg>"); //$NON-NLS-1$
			if (result.getErrorMessage() != null) {
				sb.append(result.getErrorMessage());
			}
			else {
				switch (result.getErrorType()) {
				case GenericResult.ERROR_TYPE_COMMUNICATION:
					sb.append("Error de comunicacion con Portafirmas Web"); //$NON-NLS-1$
					break;
				case GenericResult.ERROR_TYPE_REQUEST:
					sb.append("Error en la peticion realizada a Portafirmas Web"); //$NON-NLS-1$
					break;
				case GenericResult.ERROR_TYPE_DOCUMENT:
					sb.append("Error en el procesado de la peticion/respuesta de Portafirmas Web"); //$NON-NLS-1$
					break;
				default:
					sb.append("Error desconocido"); //$NON-NLS-1$
				}
			}
			sb.append("</errorMsg>"); //$NON-NLS-1$
		}
		sb.append("</rs>"); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 *  Construye el XML de respuesta a la peticion de listado de validadores de un usuario.
	 * @param validatorsList Resultado del listado de validadores de un usuario.
	 * @return XML con el resultado del listar las validadores.
	 */
	public static String createListValidatorsResponse(final ListValidators validatorsList) {

		final StringBuilder sb = new StringBuilder();
		sb.append(XML_HEADER);
		sb.append("<rsvalidlist>"); //$NON-NLS-1$
		if (validatorsList.isError()) {
			sb.append("<err>"); //$NON-NLS-1$
			switch (validatorsList.getErrorType()) {
			case ListValidators.ERROR_TYPE_COMMUNICATION:
				sb.append("Error de comunicacion con Portafirmas Web"); //$NON-NLS-1$
				break;
			default:
				sb.append("Error desconocido"); //$NON-NLS-1$
			}
			sb.append("</err>"); //$NON-NLS-1$
		}
		else {
			sb.append("<validlist>"); //$NON-NLS-1$
			for (final Validator auth : validatorsList.getValidatorsList()) {
				sb.append("<valid ") //$NON-NLS-1$
					.append("forapps=\"").append(auth.isValidatorForApps()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$

				final GenericUser user = auth.getUser();
				sb.append("<user ") //$NON-NLS-1$
					.append("id=\"").append(user.getId()).append("\" ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("dni=\"").append(user.getDni()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("<![CDATA[").append(user.getName()).append("]]>"); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append("</user>"); //$NON-NLS-1$

				sb.append("</valid>"); //$NON-NLS-1$
			}
			sb.append("</validlist>"); //$NON-NLS-1$
		}
		sb.append("</rsvalidlist>"); //$NON-NLS-1$

		return sb.toString();
	}
}
