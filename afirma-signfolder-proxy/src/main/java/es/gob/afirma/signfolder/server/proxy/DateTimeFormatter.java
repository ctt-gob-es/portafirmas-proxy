package es.gob.afirma.signfolder.server.proxy;

import java.text.SimpleDateFormat;

/**
 * Instanciador para obtener el objeto con el que procesar el formato de las fechas en texto
 * enviadas desde y hacia el Portafirmas.
 */
public class DateTimeFormatter {

    private static final String SIGNFOLDER_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss"; //$NON-NLS-1$
    private static final String APP_DATETIME_FORMAT = "dd/MM/yyyy HH:mm"; //$NON-NLS-1$

    private static SimpleDateFormat signfolderFormatter = null;
    private static SimpleDateFormat appFormatter = null;

    public static SimpleDateFormat getSignFolderFormatterInstance() {
        if (signfolderFormatter == null) {
        	signfolderFormatter = new SimpleDateFormat(SIGNFOLDER_DATETIME_FORMAT);
        }
        return signfolderFormatter;
    }

    public static SimpleDateFormat getAppFormatterInstance() {
        if (appFormatter == null) {
            appFormatter = new SimpleDateFormat(APP_DATETIME_FORMAT);
        }
        return appFormatter;
    }
}
