package org.servDroid.server;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

////////////////////////////////////////
//  This class is not implemented!!   //
////////////////////////////////////////



public class Handler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        // check the request method and process if it is a GET
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            // Set response headers
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            //* response is OK (200)
            exchange.sendResponseHeaders(200, 0);

            // Get response body
            OutputStream responseBody = exchange.getResponseBody();

            // Print all request headers to HTTP response
            Headers requestHeaders = exchange.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();
            Iterator<String> iter = keySet.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                List<String> values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\n";
                responseBody.write(s.getBytes());
            }
            // Close the responseBody
            responseBody.close();
        }
    }
}
