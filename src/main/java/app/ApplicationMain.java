package app;

import app.utils.SparkUtils;
import app.utils.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static spark.Spark.get;

public class ApplicationMain {

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(ApplicationMain.class);
        SparkUtils.createServerWithRequestLog(logger);
        String[] lang = {"World", "Solar System", "Galaxy", "Universe"};
        get("/hello", (request, response) ->{
        	response.type("application/json");
        	response.status(200);
        	return lang;},JsonUtil.json() );
   
        
    }

}
