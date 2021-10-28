import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

public class WagnerA1Server {

  // Socket Config
  private final static int PORT = 6789;
  private final static SocketAddress ADDR = new InetSocketAddress("localhost", PORT);

  private static String webServer;

  public static void main(String[] args) {
    try {
      // Creates socket
      ServerSocket sock = new ServerSocket();
      sock.bind(ADDR); // binds to 127.0.0.1:6789

      // Retrieves the single Web Server String from User
      Scanner sc = new Scanner(System.in);
      System.out.println("Enter the Web Server name as a single string for clients to connect to:");
      webServer = sc.nextLine();
      sc.close();

      // Only looks for connection if the sock is bound properly and open
      if(sock.isBound() && !sock.isClosed()) {
        System.out.println("Server listening at " + ADDR.toString() + "\n");

        // Infinite until SIGINT to allow multiple clients
        while (true) {
          try {
            // Listens and accepts client or throws error
            Socket newConn = sock.accept();

            // Handles new client (better would be using threads)
            handleClient(newConn);
          } catch (IOException e) {
            System.out.println("ERROR: connection to client failed: " + e);
          }
        }
      }

      // Closes Connection
      sock.close();
    } catch(Exception e) {
      System.out.println(e);
    } 
  }

  public static void handleClient(Socket conn) throws IOException{
    BufferedReader bfread = null;
    PrintWriter output = null;
    
    // Inits the buffer reader and output stream
    try {
      bfread = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      output = new PrintWriter(conn.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("ERROR: init to stream failed: " + e);
    }
    // Terminates connection and returns in input/output stream is null
    if (bfread == null || output == null) {
      conn.close();
      return;
    }
    
    // Sends client Webserver information
    output.println(webServer);

    // Reads from client until nothing more is sent
    while (true) {
      try {
        String line = bfread.readLine();
        // Terminates connection if nothing more is being sent by client
        if ((line == null) || line.equalsIgnoreCase("q")) {
          conn.close();
          return;
        } else {
          System.out.println(line);
        }
      } catch (IOException e) {
        System.out.println("ERROR: could not read line " + e);
      }
    }
  }

}
