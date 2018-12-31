import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Servidor {

    private String address;
    private int port;
    private HashMap<String, Conta> contas;
    private HashMap<String, CloudServer> servidores;

    Servidor(String address, int port){
        this.address = address;
        this.port = port;
        this.contas = new HashMap<>();

        dadosTeste();
    }

    public void start() throws IOException {

        System.out.println("Servidor iniciado");
        ServerSocket serverSocket = new ServerSocket(port);

        while(true){
            Socket socket = serverSocket.accept();
            Worker worker = new Worker(socket, contas, servidores);
            Thread thread = new Thread(worker);
            thread.start();
        }

    }

    public void dadosTeste(){
        Conta c = new Conta("teste","teste");
        this.contas.put("teste",c);
    }

}
