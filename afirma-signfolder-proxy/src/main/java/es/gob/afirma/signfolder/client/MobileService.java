
package es.gob.afirma.signfolder.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "MobileService", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:v2.0")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface MobileService {


    /**
     * 
     * @param initPage
     * @param signFormats
     * @param certificate
     * @param pageSize
     * @param state
     * @param filters
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileRequestList
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "requestList", targetNamespace = "")
    @RequestWrapper(localName = "queryRequestList", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.QueryRequestList")
    @ResponseWrapper(localName = "queryRequestListResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.QueryRequestListResponse")
    public MobileRequestList queryRequestList(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "state", targetNamespace = "")
        String state,
        @WebParam(name = "initPage", targetNamespace = "")
        String initPage,
        @WebParam(name = "pageSize", targetNamespace = "")
        String pageSize,
        @WebParam(name = "signFormats", targetNamespace = "")
        MobileStringList signFormats,
        @WebParam(name = "filters", targetNamespace = "")
        MobileRequestFilterList filters)
        throws MobileException
    ;

    /**
     * 
     * @param requestId
     * @param certificate
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileRequest
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "request", targetNamespace = "")
    @RequestWrapper(localName = "queryRequest", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.QueryRequest")
    @ResponseWrapper(localName = "queryRequestResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.QueryRequestResponse")
    public MobileRequest queryRequest(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "requestId", targetNamespace = "")
        String requestId)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @param documentId
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileDocument
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "document", targetNamespace = "")
    @RequestWrapper(localName = "documentPreview", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.DocumentPreview")
    @ResponseWrapper(localName = "documentPreviewResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.DocumentPreviewResponse")
    public MobileDocument documentPreview(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "documentId", targetNamespace = "")
        String documentId)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @param requestTagId
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileDocumentList
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "documentList", targetNamespace = "")
    @RequestWrapper(localName = "getDocumentsToSign", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.GetDocumentsToSign")
    @ResponseWrapper(localName = "getDocumentsToSignResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.GetDocumentsToSignResponse")
    public MobileDocumentList getDocumentsToSign(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "requestTagId", targetNamespace = "")
        String requestTagId)
        throws MobileException
    ;

    /**
     * 
     * @param docSignInfoList
     * @param certificate
     * @param requestTagId
     * @return
     *     returns java.lang.String
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "result", targetNamespace = "")
    @RequestWrapper(localName = "saveSign", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.SaveSign")
    @ResponseWrapper(localName = "saveSignResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.SaveSignResponse")
    public String saveSign(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "requestTagId", targetNamespace = "")
        String requestTagId,
        @WebParam(name = "docSignInfoList", targetNamespace = "")
        MobileDocSignInfoList docSignInfoList)
        throws MobileException
    ;

    /**
     * 
     * @param textRejection
     * @param requestId
     * @param certificate
     * @return
     *     returns java.lang.String
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "responseId", targetNamespace = "")
    @RequestWrapper(localName = "rejectRequest", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.RejectRequest")
    @ResponseWrapper(localName = "rejectRequestResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.RejectRequestResponse")
    public String rejectRequest(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "requestId", targetNamespace = "")
        String requestId,
        @WebParam(name = "textRejection", targetNamespace = "")
        String textRejection)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileApplicationList
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "applicationList", targetNamespace = "")
    @RequestWrapper(localName = "queryApplicationsMobile", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.QueryApplicationsMobile")
    @ResponseWrapper(localName = "queryApplicationsMobileResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.QueryApplicationsMobileResponse")
    public MobileApplicationList queryApplicationsMobile(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @param requestTagId
     * @return
     *     returns java.lang.String
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "responseId", targetNamespace = "")
    @RequestWrapper(localName = "approveRequest", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.ApproveRequest")
    @ResponseWrapper(localName = "approveRequestResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.ApproveRequestResponse")
    public String approveRequest(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "requestTagId", targetNamespace = "")
        String requestTagId)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @param documentId
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileDocument
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "document", targetNamespace = "")
    @RequestWrapper(localName = "signPreview", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.SignPreview")
    @ResponseWrapper(localName = "signPreviewResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.SignPreviewResponse")
    public MobileDocument signPreview(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "documentId", targetNamespace = "")
        String documentId)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @param documentId
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileDocument
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "document", targetNamespace = "")
    @RequestWrapper(localName = "reportPreview", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.ReportPreview")
    @ResponseWrapper(localName = "reportPreviewResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.ReportPreviewResponse")
    public MobileDocument reportPreview(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "documentId", targetNamespace = "")
        String documentId)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @param register
     * @return
     *     returns es.gob.afirma.signfolder.client.MobileSIMUserStatus
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "registerStatus", targetNamespace = "")
    @RequestWrapper(localName = "registerSIMUser", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.RegisterSIMUser")
    @ResponseWrapper(localName = "registerSIMUserResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.RegisterSIMUserResponse")
    public MobileSIMUserStatus registerSIMUser(
        @WebParam(name = "certificate", targetNamespace = "")
        String certificate,
        @WebParam(name = "register", targetNamespace = "")
        MobileSIMUser register)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @return
     *     returns java.lang.String
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "nifCif", targetNamespace = "")
    @RequestWrapper(localName = "validateUser", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.ValidateUser")
    @ResponseWrapper(localName = "validateUserResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.ValidateUserResponse")
    public String validateUser(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @param estadoNotifyPush
     * @return
     *     returns java.lang.String
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "resultado", targetNamespace = "")
    @RequestWrapper(localName = "updateNotifyPush", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.UpdateNotifyPush")
    @ResponseWrapper(localName = "updateNotifyPushResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.UpdateNotifyPushResponse")
    public String updateNotifyPush(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate,
        @WebParam(name = "estadoNotifyPush", targetNamespace = "")
        String estadoNotifyPush)
        throws MobileException
    ;

    /**
     * 
     * @param certificate
     * @return
     *     returns java.lang.String
     * @throws MobileException
     */
    @WebMethod
    @WebResult(name = "valorNotifyPush", targetNamespace = "")
    @RequestWrapper(localName = "estadoNotifyPush", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.EstadoNotifyPush")
    @ResponseWrapper(localName = "estadoNotifyPushResponse", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:request:v2.0", className = "es.gob.afirma.signfolder.client.EstadoNotifyPushResponse")
    public String estadoNotifyPush(
        @WebParam(name = "certificate", targetNamespace = "")
        byte[] certificate)
        throws MobileException
    ;

}
