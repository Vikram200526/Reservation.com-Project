package TestExamples;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class RestaurantServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Define an endpoint for fetching restaurants
        server.createContext("/api/restaurants", new RestaurantHandler());
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on http://localhost:" + port);
    }

    static class RestaurantHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers so your frontend can talk to it locally
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            
            // In your actual app, you will call your SQL database logic here!
            // For now, we return a hardcoded JSON string to test the connection.
            String jsonResponse = "[{\"restaurant_name\": \"Burger Shack\", \"category\": \"Hamburger\"}, " +
                                  "{\"restaurant_name\": \"Pizza Palace\", \"category\": \"Pizza\"}]";

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();
        }
    }
}