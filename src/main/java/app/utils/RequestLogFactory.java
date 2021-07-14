package app.utils;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.CustomRequestLog;

import java.io.IOException;

public class RequestLogFactory {

    private Logger logger;

    public RequestLogFactory(Logger logger) {
        this.logger = logger;
    }

    CustomRequestLog create() {
        return new CustomRequestLog(null) {
            @SuppressWarnings("unused")
			protected boolean isEnabled() {
                return true;
            }

            @SuppressWarnings("unused")
			public void write(String s) throws IOException {
                logger.info(s);
            }
        };
    }
}
