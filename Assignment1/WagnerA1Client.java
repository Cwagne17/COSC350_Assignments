import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class WagnerA1Client {

  // Localhost Server Socket Conn Config
  private static final int PORT = 6789;
  private static final SocketAddress ADDR = new InetSocketAddress("localhost", PORT);

  // Web Server info from Localhost Server
  private static String url = null;
  private static Map<String, String> res = new HashMap<String, String>();

  // I/O objects
  private static BufferedReader input = null;
  private static PrintWriter output = null;

  public static void main(String[] args) {
    try {
      // Creates new Socket
      Socket sock = new Socket();
      sock.connect(ADDR); // Attempts to connect to 127.0.0.1:6789
      System.out.println(sock.isConnected() ? "Connection Success at " + ADDR.toString() + "\n" : "Connection Failed\n");
      
      // Inits the I/O Objects
      input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      output = new PrintWriter(sock.getOutputStream(), true);
      
      // Web Sever URL
      url = input.readLine();
      System.out.println("Web Server URL from Localhost Server: " + url);
      
      
      try {
        // Attempts Connection to URL
        Socket webConn = new Socket(url, 80);
        System.out.println(webConn.isConnected() ? "Connected to " + url : "Failed to Connect to " + url);
        
        // Attempts GET Req
        makeReq(webConn);

        output.print("W: " + res.get("Location"));
        output.print(" N: " + res.get("Content-Length") + "B");
        output.print(" T: " + res.get("Time") + "ms\n");

        webConn.close();
      } catch (IOException e) {
        System.out.println("ERROR with Web Server Connection");
        e.printStackTrace();
      }

      // Closes all connections
      output.close();
      input.close();
      sock.close();
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
  }

  public static void makeReq(Socket conn) throws IOException {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
      long beforeTime = System.currentTimeMillis();

      // Makes Request over connection
      out.println(
          "GET / HTTP/1.1 \n" 
        + "Host: " + url 
        + "\n"
      );

      // Reads HTTP response
      String line;
      while (!(line = in.readLine()).equals("")) {
        String[] splitResponse = line.split(":", 2);
        if (splitResponse.length == 2) {
          res.put(splitResponse[0], splitResponse[1].trim());
        } else {
          res.put("Status", splitResponse[0]);
        }
      }
      long afterTime = System.currentTimeMillis();
      res.put("Time", String.valueOf(afterTime - beforeTime));
    } catch (IOException e) {
      System.out.println("ERROR: failed making request or reading response");
      e.printStackTrace();
    }
  }
}