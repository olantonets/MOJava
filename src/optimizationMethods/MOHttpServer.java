package optimizationMethods; /**
 * Created by home on 02.03.2018.
 */
import java.net.*;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class MOHttpServer {

    public static void main(String[] args) throws Throwable {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        // Add new handlere here
        HttpContext context = server.createContext("/symplex-all", new SimplexMethodAllHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }


}
