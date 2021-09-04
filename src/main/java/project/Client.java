package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String serverHost;
    private int serverPort;

    public static void main(String[] args){
        Client client = new Client(Config.HOST, Config.PORT_NUMBER);
        client.startClient();
    }

    private Client(String host, int portNumber){
        this.serverHost = host;
        this.serverPort = portNumber;
    }

    /**
     * Starts the client, connects to server, and plays telphone game
     *
     */
    public void startClient(){
        try{
            System.out.println("Connecting to server...");

            //Connect to the server's socket
            Socket socket = new Socket(serverHost, serverPort);
            try{
                Thread.sleep(1000); // waiting for network communicating.
            }catch(Exception e){
                e.printStackTrace();
            }
            System.out.println("Connected to server");

            //Read the starting information
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Utils.readParagraph(bufferedReader);

            //Get word from server
            Utils.readParagraph(bufferedReader);

            //Send my word to server
            Scanner scan = new Scanner(System.in);
            String readWord = null;
            while(readWord == null || readWord.trim().equals("")){
                // null, empty, whitespace(s) not allowed.
                readWord = scan.nextLine();
                if(readWord.trim().equals("")){
                    System.out.println("Invalid. Please enter again:");
                }
            }
            System.out.println("The word you have chosen to pass on is: " + readWord);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            pw.write(readWord + "\n");
            pw.flush();
            System.out.println("Word has been sent to server, please wait for end results...");

            //Get Final Information
            Utils.readParagraph(bufferedReader);

            //Close socket and buffer reader
            bufferedReader.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }



}
