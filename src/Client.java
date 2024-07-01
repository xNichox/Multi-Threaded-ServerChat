import java.io.*;
import java.net.Socket;

public class Client {
    // ATTRIBUTES:
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;
    private Socket socket;

    // CONSTRUCTOR:
    public Client(){
    }

    // PUBLIC METHODS:
    public void startClient(){
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true); // manda gli output al socket (server)
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // prende gli input dal socket (server)
            stdIn = new BufferedReader(new InputStreamReader(System.in)); // prende gli input da tastiera

            System.out.println("Sei connesso al server");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PRIVATE METHODS
    private void listenForMessages(){
        // Thread to listen for messages from the server
         new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage); // prendo il messaggio del server e lo stampo sulla console
                    }
                } catch (IOException e) {
                    closeConnection();
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Server non raggiungibile, sei stato disconnesso"); // se il server chiude i socket, chiudo la connessione e lancio un'eccezione
                }
            }
        }).start();

        // Thread to listen user input and send to the server
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userInput;
                try {
                    while ((userInput = stdIn.readLine()) != null) {
                        out.println(userInput); // invio il messaggio al server, che lo manda in broadcast agli altri client e stampa il messaggio sullo standard output (console)
                    }
                } catch (IOException e) {
                    Thread.currentThread().interrupt(); // se stdIn è stato chiuso => il Server è andato giù e sono stato disconnesso, stoppo il thread
                }
            }
        }).start();
    }

    private void closeConnection(){ // metodo che chiude i socket ed i buffer
        try{
            if (socket.isConnected()){
                socket.close();
            }
            if (in != null){
                in.close();
            }
            if (stdIn != null){
                stdIn.close();
            }
            if (out != null){
                out.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        Client client = new Client();
        client.startClient();
        client.listenForMessages();
    }
}

