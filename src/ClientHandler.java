import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientHandler implements Runnable {
    // ATTRIBUTES:
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;
    private static Set<ClientHandler> clientHandlers;

    // CONSTRUCTOR:
    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        ClientHandler.clientHandlers = clientHandlers;
    }

    // THREAD:
    @Override
    public void run() { // questo thread viene creato per ogni client e permette l'invio dei messaggi da parte dei client
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Enter your nickname:");
            this.nickname = in.readLine();
            broadcastMessage(nickname + " has joined the chat!", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("/quit")){
                    closeConnection();
                } else {
                    //System.out.println(clientHandlers);
                    broadcastMessage(nickname + ": " + message, this);
                }
            }
            closeConnection();
        } catch (IOException e) {
            System.out.println("Socket chiuso con: " + nickname); // messaggio che va al server quando la connessione viene chiusa con un client
        }
    }

    // PUBLIC METHODS:
    public void sendMessage(String message) { // mando il messaggio sul socket (client), viene usato dal metodo broadcastMessage di Server
        out.println(message);
    }

    // PRIVATE METHODS:
    private static synchronized void broadcastMessage(String message, ClientHandler excludeClient) { // mando il messaggio in broadcast ai vari client usando il metodo syncronized broadcastMessage di Server
        //Server.broadcastMessage(message, this);
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) { // non invia il messaggio al client mittente
                client.sendMessage(message);
            }
        }
    }

    private void closeConnection(){ // metodo per chiudere il socket e i canali di lettura e scrittura
        try{
            sendMessage("Non sei più collegato al server");
            if (socket.isConnected()){
                socket.close();
                clientHandlers.remove(this);
                broadcastMessage(nickname + " has left the chat.", this);
            }
            if (in != null){
                in.close();
            }
            if (out != null){
                out.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}



