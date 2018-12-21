import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Servidor {

    private String address;
    private int port;
    private HashMap<String, Conta> contas;

    Servidor(String address, int port){
        this.address = address;
        this.port = port;
        this.contas = new HashMap<>();
    }

    public void start() throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        while(true){
            Socket socket = serverSocket.accept();
            Worker worker = new Worker(socket, contas);
            Thread thread = new Thread(worker);
            thread.start();
        }

    }

}
