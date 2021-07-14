package app.utils;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLogWriter;



public class RequestLogFactory {

    @SuppressWarnings("unused")
	private Logger logger;

    public RequestLogFactory(Logger logger) {
        this.logger = logger;
    }
    RequestLogWriter rlw = new RequestLogWriter();
    
    CustomRequestLog create() {
    	rlw.setTimeZone("GMT");
    	rlw.setFilename("http-yyyy_MM_dd.log");
    	rlw.setFilenameDateFormat("yyyy_MM_dd");
    	rlw.setAppend(false);
    	//System.out.println(rlw.getFilenameDateFormat());
        return new CustomRequestLog(rlw,CustomRequestLog.EXTENDED_NCSA_FORMAT) {
        };
    }
}
