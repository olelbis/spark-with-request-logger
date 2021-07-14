# Embedded Jetty Spark instance with request logger

How to create a Spark instance with an embedded Jetty server containing a log4j request logger.
This is a tutorial rewamp with the latest Spark libs & dependency, the original are still available at:

[Sparkjava](http://sparkjava.com/tutorials/jetty-request-log)

or:

[Github](https://github.com/ygaller/spark-with-request-logger)

## Creating an embedded Jetty server with a request logger

Starting from 2.6.0 Spark introduced the option of providing a configurable embedded Jetty server. 
This tutorial shows how to use this capability in order to configure such a server
that supports logging of incoming requests using log4j.

### Initial setup

We'll start with a basic hello world instance of Spark

~~~java

public class ApplicationMain {

    public static void main(String[] args) {
        get("/hello", (request, response) -> "world");
    }

}

~~~

### Creating the logger

First up, let's create the access logger. Given a log4j logger, we will want to log messages in a standard format. For this purpose, we can implement an instance of `CustomRequestLog` that takes our logger as an argument and write output to a file in the same directory of jar file.

~~~java

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

~~~

### The embedded server factory

We can't just provide Spark with a server instance. Rather, we need to provide a factory that Spark will invoke when it decides to create the server. This factory can take the request log as an argument

~~~java

class EmbeddedJettyServerFactory implements JettyServerFactory {
    private EmbeddedJettyFactoryConstructor embeddedJettyFactoryConstructor;

    EmbeddedJettyServerFactory(EmbeddedJettyFactoryConstructor embeddedJettyFactoryConstructor) {
        this.embeddedJettyFactoryConstructor = embeddedJettyFactoryConstructor;

    }

    @Override
    public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
        Server server;
        if (maxThreads > 0) {
            int max = maxThreads > 0 ? maxThreads : 200;
            int min = minThreads > 0 ? minThreads : 8;
            int idleTimeout = threadTimeoutMillis > 0 ? threadTimeoutMillis : '\uea60';
            server = new Server(new QueuedThreadPool(max, min, idleTimeout));
        } else {
            server = new Server();
        }

        server.setRequestLog(embeddedJettyFactoryConstructor.requestLog);
        return server;
    }

    @Override
    public Server create(ThreadPool threadPool) {
        return new Server(threadPool);
    }
}

~~~

The implementation here is identical to Spark's embedded Jetty with the addition of `server.setRequestLog(requestLog);`

### Creating a utitily function for the embedded server

Let's tie it together in a utility function that accepts our original log4j logger and overrides Spark's default Jetty implementation with ours

~~~java

public class SparkUtils {
    public static void createServerWithRequestLog(Logger logger) {
        EmbeddedJettyFactory factory = createEmbeddedJettyFactoryWithRequestLog(logger);
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, factory);
    }

    private static EmbeddedJettyFactory createEmbeddedJettyFactoryWithRequestLog(Logger logger) {
    	CustomRequestLog requestLog = new RequestLogFactory(logger).create();
        return new EmbeddedJettyFactoryConstructor(requestLog).create();
    }
}

~~~

Now all that remains is to define the log4j logger and call the utility function in our main. 

~~~java

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(ApplicationMain.class);
        SparkUtils.createServerWithRequestLog(logger);
        String[] answer = {"World", "Solar System", "Galaxy", "Universe"};
        get("/hello", (request, response) ->{
        	response.type("application/json");
        	response.status(200);
        	return answer;},JsonUtil.json() );
   
        }

~~~

## The result

After we spin up our Spark instance and go to [http://localhost:4567/hello], we will see the following output in logfile http-2021_07_14.log:

~~~console

0:0:0:0:0:0:0:1 - - [14/Jul/2021:21:36:18 +0000] "GET /hello HTTP/1.1" 200 57 "-" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36"

~~~
