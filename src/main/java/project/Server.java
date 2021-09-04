package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server {

    private int serverPort;
    private ArrayList<Socket> clientsNetworkingInfo;
    private ServerSocket serverSocket;
    private ArrayList<String> telephoneHistory = new ArrayList<>();
    private String [] randomWordList = {"frog","wide","expression","willing","use","minerals",
            "volume","current","ten","began","chain","load",
            "further","sound","gold","escape","worried","lower",
            "current","enjoy","gasoline","stomach","run","police",
            "talk","single","breeze","made","neighborhood","thick",
            "faster","basis","dry","every","allow","offer",
            "solve","quarter","continent","plenty","though","castle"};

    public Server() {
        this.serverPort = Config.PORT_NUMBER;
    }

    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Starting Server...");
        server.startServer();
    }

    /**
     * Starts the server, and runs the functions required for the game
     *
     */
    private void startServer(){
        clientsNetworkingInfo = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e){
            System.err.println("Could not listen on port: " + serverPort);
            System.exit(1);
        }
        acceptClients();
        sendStartInfoToAllClients();
        startTelephoneGame();
    }

    /**
     * Accepting clients to join game, only let certain amount of clients in
     *
     */
    public void acceptClients() {
        while(clientsNetworkingInfo.size() < Config.NUM_OF_CLIENTS) {
            try{
                //Accept client
                Socket socket = serverSocket.accept();
                System.out.println("accepts : " + socket.getRemoteSocketAddress());

                //Keep clients socket information for latter use
                clientsNetworkingInfo.add(socket);

            } catch (IOException ex){
                System.out.println("Accept failed on : "+serverPort);
            }
        }
        System.out.println("Client Limit Reached");
    }

    /**
     * Send information about the telephone game to all clients, containing instructions and what player they are
     *
     */
    public void sendStartInfoToAllClients(){
        System.out.println("Sending start info to clients");

        //Create Player List
        String playerList = "";
        for (int i = 0; i < clientsNetworkingInfo.size(); i++){
            playerList += "\nPlayer " + i;
        }

        //Create start message
        String startMessage = "Server is starting the game..." + "\n"
                + "Welcome to the game of Telephone" + "\n"
                + "Instructions: First player starts with a word, you can either change it or keep it and the final word will be revealed at the end" + "\n"
                + "Player Order List: "
                + playerList + "\n"
                + "Please wait your turn...";

        for(int i = 0; i < clientsNetworkingInfo.size(); i++){
            if(!clientsNetworkingInfo.get(i).isClosed()){
                try{

                    //Write start information to clients socket
                    Utils.writeParagraph(new PrintWriter(clientsNetworkingInfo.get(i).getOutputStream(), true), "You are Player: " + i + "\n" + startMessage );

                }
                catch ( IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Starts the telephone game
     *
     */
    public void startTelephoneGame(){
        System.out.println("Starting Telephone Game");
        //Get random word from randomWordList
        Random r =new Random();
        int randomInt = r.nextInt(randomWordList.length);
        final String startingWord = randomWordList[randomInt];
        String wordGettingPassedAround = startingWord;

        //Game loop - giving the starting word to the first player and whatever word he gives back, pass that to the next player
        for(int i = 0; i < clientsNetworkingInfo.size(); i++){
            if(!clientsNetworkingInfo.get(i).isClosed()){
                try{

                    //Write to each clients socket the current word getting passed around
                    Utils.writeParagraph(new PrintWriter(clientsNetworkingInfo.get(i).getOutputStream(), true), "Word: " + wordGettingPassedAround + "\n" + "Please write the same word or mix it up! :");

                    //Read clients word response
                    BufferedReader br = new BufferedReader(new InputStreamReader(clientsNetworkingInfo.get(i).getInputStream()));
                    String newWord = br.readLine();
                    wordGettingPassedAround = newWord;

                    //Add to telphone history
                    telephoneHistory.add("Player " + i + " said " + newWord);
                }
                catch ( IOException e){
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Game finished, displaying end results to clients");
        //Game Finished - After all the players passed on words, the final word will be revieled along with the history off all words said by all players
        for(int i = 0; i < clientsNetworkingInfo.size(); i++){
            if(!clientsNetworkingInfo.get(i).isClosed()){

                //Make neat history string
                String telephoneHistoryString = "History: " + "\n";
                for(int y = 0; y < telephoneHistory.size(); y++){
                    telephoneHistoryString += telephoneHistory.get(y) + "\n";
                }

                //Give all ending information to client
                try{
                    Utils.writeParagraph(new PrintWriter(clientsNetworkingInfo.get(i).getOutputStream(), true), "\nGame is done!" + "\n" + "Initial word was: " + startingWord + "\n" + telephoneHistoryString);
                }
                catch ( IOException e){
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Server shutting down...");
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
