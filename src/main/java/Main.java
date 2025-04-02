import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

public class Main {
  private static String directoryPath = null;

  public static void main(String[] args) {
    // Parse command line arguments
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--directory") && i + 1 < args.length) {
        directoryPath = args[i + 1];
        break;
      }
    }
    
    System.out.println("Logs will appear here!");
    
    try (ServerSocket serverSocket = new ServerSocket(4221)) {
      serverSocket.setReuseAddress(true);
      
      while (true) {  // Accepting new connections continuously
        Socket clientSocket = serverSocket.accept();
        // Creating a new thread for each client
        Thread clientThread = new Thread(() -> handleClient(clientSocket));
        clientThread.start();
      }
      
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  private static void handleClient(Socket clientSocket) {
    try {
      InputStream input = clientSocket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      String line = reader.readLine();
      System.out.println(line);
      String[] HttpRequest = line.split(" ",0);
      String method = HttpRequest[0];  // Get HTTP method
      String path = HttpRequest[1];
      //Reading all headers
      String userAgent = "";
      int contentLength = 0;  // Adding a content length variable
      boolean clientSupportsGzip = false;  // Adding a flag for gzip support
      
      while ((line = reader.readLine()) != null && !line.isEmpty()) {
          if (line.startsWith("User-Agent: ")) {
              userAgent = line.substring(12);
          } else if (line.startsWith("Content-Length: ")) {
              contentLength = Integer.parseInt(line.substring(16));
          } else if (line.startsWith("Accept-Encoding: ")) {
              // Splitting the Accept-Encoding header by commas and check each encoding scheme
              String[] encodings = line.substring(16).split(",");
              for (String encoding : encodings) {
                  if (encoding.trim().equals("gzip")) {
                      clientSupportsGzip = true;
                      break;
                  }
              }
          }
      }
      OutputStream output = clientSocket.getOutputStream();
      if(path.equals("/")){
          output.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
      }else if (path.startsWith("/echo/")) {
        String echoStr = path.substring(6);
        String responseBody = echoStr;
        String response;
        if (clientSupportsGzip) {
            // Compress the response body using gzip
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
            gzipStream.write(responseBody.getBytes());
            gzipStream.flush();  // Add this line to ensure all data is written
            gzipStream.close();
            byte[] compressedBody = byteStream.toByteArray();
            
            response = String.format(
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Encoding: gzip\r\n" +
                "Content-Length: %d\r\n" +
                "\r\n",
                compressedBody.length
            );
            output.write(response.getBytes());
            output.write(compressedBody);
        } else {
            response = String.format(
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: %d\r\n" +
                "\r\n" +
                "%s",
                responseBody.length(),
                responseBody
            );
        }
        output.write(response.getBytes());
      } else if (path.equals("/user-agent")) {
        String response = String.format(
            "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/plain\r\n" +
            "Content-Length: %d\r\n" +
            "\r\n" +
            "%s",
            userAgent.length(),
            userAgent
        );
        output.write(response.getBytes());
      } else if (path.startsWith("/files/")) {
        String filename = path.substring(7); // Removing the "/files/" prefix
        File file = new File(directoryPath, filename);
        
        if (method.equals("POST")) {  // Handling POST request
            // Reading the request body
            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            
            // Writing the content to the file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(body);
            fileWriter.close();
            
            output.write("HTTP/1.1 201 Created\r\n\r\n".getBytes());
        } else if (method.equals("GET")) {
            if (file.exists() && file.isFile()) {
                byte[] fileContent = java.nio.file.Files.readAllBytes(file.toPath());
                String response = String.format(
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/octet-stream\r\n" +
                    "Content-Length: %d\r\n" +
                    "\r\n",
                    fileContent.length
                );
                output.write(response.getBytes());
                output.write(fileContent);
            } else {
                output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }
        }
      } else {
        output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
      }
      System.out.println("accepted new connection");
      clientSocket.close();  // Closing the socket when done
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
