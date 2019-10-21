<%@page import="es.gob.afirma.signfolder.server.proxy.sessions.SessionCollector"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="es.gob.afirma.signfolder.server.proxy.SessionParams"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>Portafirmas</title>
</head>
<body id="home" onload="document.redirectForm.submit()">
	<div style="width: 100%;">
		<h3 style="text-align: center;">
			<span>Redirigiendo...</span>
		</h3>
	</div>
<%

	String ssid = request.getParameter("ssid");

	// Cargamos la sesion que corresponde en lugar de aceptar la que nos llegue
	session = SessionCollector.getSession(request, ssid);
	
	Logger.getLogger("es.gob.afirma").info("Id de sesion: " + (session != null ? session.getId() : null));
	Logger.getLogger("es.gob.afirma").info("Valor Boolean.TRUE: " + Boolean.TRUE);
	Logger.getLogger("es.gob.afirma").info("Valor INIT_WITH_CLAVE: " + session.getAttribute(SessionParams.INIT_WITH_CLAVE));
	if (session == null || !Boolean.TRUE.equals(session.getAttribute(SessionParams.INIT_WITH_CLAVE))) {
		Logger.getLogger("es.gob.afirma").warning("No se encontro la sesion o no se inicio con Clave");
		response.sendRedirect("error.jsp?type=session");
		return;
	}

	String url = (String) session.getAttribute(SessionParams.CLAVE_URL);
	String token = (String) session.getAttribute(SessionParams.CLAVE_REQUEST_TOKEN);
	String excludedIdps = (String) session.getAttribute(SessionParams.CLAVE_EXCLUDED_IDPS);
	String forcedIdp = (String) session.getAttribute(SessionParams.CLAVE_FORCED_IDP);
	
	if (url == null || token == null) {
		Logger.getLogger("es.gob.afirma").warning("No se recibio la URL o el token de inicio de sesion para su envio a Clave");
		response.sendRedirect("error.jsp?type=request");
		return;
	}
 %>
  	<form name="redirectForm" method="post" action="<%= url %>">
		<input type="hidden" name="SAMLRequest" value=<%= token %> />
 		<input type="hidden" name="excludedIdPList" value="<%= excludedIdps %>" />
		<input type="hidden" name="forcedIdP" value="<%= forcedIdp %>" />
	</form>
  
</body>
</html>
