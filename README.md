# HTTP Server

This is a simple multithreaded HTTP server implemented in Java. It supports basic HTTP methods like `GET` and `POST` and provides functionality for serving files, echoing strings, and retrieving user-agent information.

## Features

1. **Serve Static Files**:
   - The server can serve files from a specified directory.
   - Supports `GET` requests to retrieve files.
   - Supports `POST` requests to upload files.

2. **Echo Endpoint**:
   - The `/echo/{message}` endpoint echoes back the `{message}` provided in the URL.
   - Supports gzip compression if the client requests it via the `Accept-Encoding` header.

3. **User-Agent Endpoint**:
   - The `/user-agent` endpoint returns the `User-Agent` header sent by the client.

4. **Gzip Compression**:
   - If the client supports gzip (indicated by the `Accept-Encoding: gzip` header), the server compresses the response.

5. **Multithreaded**:
   - Each client connection is handled in a separate thread, allowing multiple clients to connect simultaneously.

6. **Port Configuration**:
   - The server listens on port `4221`.

7. **Error Handling**:
   - Returns `404 Not Found` for invalid endpoints or missing files.
   - Returns `201 Created` for successful file uploads.

## Usage

### Command-Line Arguments

The server accepts the following command-line argument:

- `--directory <path>`: Specifies the directory to serve files from. This is required for file-related endpoints.

### Endpoints

1. **Root Endpoint (`/`)**:
   - Responds with a simple `200 OK` status.

2. **Echo Endpoint (`/echo/{message}`)**:
   - Responds with the `{message}` provided in the URL.
   - Example: `GET /echo/hello` returns `hello`.

3. **User-Agent Endpoint (`/user-agent`)**:
   - Responds with the `User-Agent` header sent by the client.

4. **File Endpoints (`/files/{filename}`)**:
   - `GET /files/{filename}`: Retrieves the specified file from the directory.
   - `POST /files/{filename}`: Uploads a file with the specified name to the directory.

### Example Requests

#### Start the Server
```bash
java -jar http-server.jar --directory /path/to/directory
```

#### Echo Endpoint
```bash
curl http://localhost:4221/echo/hello
```

#### User-Agent Endpoint
```bash
curl http://localhost:4221/user-agent
```
- If curl doesn't work, use :
    ```bash
    Invoke-WebRequest -Uri "http://localhost:4221/user-agent" -Headers @{"User-Agent"="foobar/1.2.3"} -Verbose
    ```

#### File Upload (POST)
```bash
curl -X POST -d "File content" http://localhost:4221/files/example.txt
```

#### File Download (GET)
```bash
curl http://localhost:4221/files/example.txt
```

## Notes

- Ensure the directory specified with `--directory` exists and is writable.
- The server does not implement advanced HTTP features like authentication or HTTPS.

## Future Development

Here are some potential enhancements for the HTTP server:

1. **HTTPS Support**:
   - Add support for secure connections using SSL/TLS.
   - Allow users to specify certificates and private keys via command-line arguments.

2. **Authentication**:
   - Implement basic authentication for restricted endpoints.
   - Support token-based authentication for enhanced security.

3. **Advanced HTTP Features**:
   - Add support for HTTP methods like `PUT` and `DELETE`.
   - Implement support for HTTP/2 for better performance.

4. **Directory Listing**:
   - Provide an option to list files in the served directory when accessing `/files/`.

5. **Logging**:
   - Add detailed request and response logging to a file for debugging and auditing purposes.

6. **Configuration File**:
   - Allow server configuration (e.g., port, directory, etc.) via a configuration file instead of command-line arguments.

7. **Rate Limiting**:
   - Implement rate limiting to prevent abuse and ensure fair usage.

8. **Error Pages**:
   - Serve custom error pages (e.g., `404.html`) for better user experience.

9. **Testing and CI/CD**:
   - Add unit tests and integration tests for the server.
   - Set up a CI/CD pipeline for automated testing and deployment.

10. **Cross-Origin Resource Sharing (CORS)**:
    - Add support for CORS headers to allow cross-origin requests.

11. **Performance Optimization**:
    - Optimize the server for handling a large number of concurrent connections.
    - Use non-blocking I/O for better scalability.

## License

This project is licensed under the MIT License.
