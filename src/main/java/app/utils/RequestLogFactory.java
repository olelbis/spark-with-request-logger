package app.utils;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.CustomRequestLog;


public class RequestLogFactory {

    @SuppressWarnings("unused")
	private Logger logger;

    public RequestLogFactory(Logger logger) {
        this.logger = logger;
    }

    CustomRequestLog create() {
        return new CustomRequestLog(null) {
        };
    }
}
