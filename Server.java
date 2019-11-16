package server;

import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public static void main(String[] args) throws IOException {
      int filesize=104857600; // filesize temporary hardcoded

      long start = System.currentTimeMillis();
      int bytesRead;
      int current = 0;
      int n=1;
      PrintWriter out = null;
      

      // create socket
    
	ServerSocket servsock = new ServerSocket(9999);     
      
      
      while (true) {
      System.out.println("Waiting...");
     
      Socket sock = servsock.accept();
     
      
      System.out.println("Accepted connection : " + sock);
      
      // receive file
          byte [] mybytearray  = new byte [filesize];
          InputStream is = sock.getInputStream();
          FileOutputStream fos = new FileOutputStream("C:\\Users\\--\\Desktop\\picture\\picture.jpg"); // destination path and name of file
          BufferedOutputStream bos = new BufferedOutputStream(fos);
          bytesRead = is.read(mybytearray,0,mybytearray.length);
          
          current = bytesRead;
          
          out = new PrintWriter(sock.getOutputStream(), true);
          // thanks to A. Cádiz for the bug fix
          //current=0;
          do {
            bytesRead =
                is.read(mybytearray, current, (mybytearray.length-current));
          
             if(bytesRead >= 0) current += bytesRead;
            System.out.println("num. of read "+bytesRead);
          } while(bytesRead > -1);

          bos.write(mybytearray, 0 , current);
          bos.flush();
         // System.out.println("num. of file write "+ current);
          
          long end = System.currentTimeMillis();
          System.out.println(end-start);
          

          bos.close();
          sock.close();
      
          

      }
  }

     
          
         
             
  
}


