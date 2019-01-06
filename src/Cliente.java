import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    private String address;
    private int port;

    /*
     * Construtor para Cliente:
     * address - endereço IP definido para o servidor
     * port - porta usada para aceder ao servidor
     */
    Cliente(String address, int port){
        this.address = address;
        this.port = port;
    }

    /*
     * Função que inicia o processo de cliente e liga-o
     * ao servidor. Lê linhas do input e envia-as ao
     * Worker que lhe foi atribuido. Inicia também uma thread
     * com uma instância da classe ReaderCliente, que lê as
     * linhas enviadas pelo servidor.
     */
    public void start() throws IOException {
        Socket socket = new Socket(address, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        System.out.println(inServer.readLine());

        ReaderCliente readerCliente = new ReaderCliente(inServer);
        Thread thread = new Thread(readerCliente);
        thread.start();

        String line;
        do {
            try {
                line = in.readLine();
                out.println(line);
                out.flush();
            } catch (IOException e){break;}
        } while (thread.isAlive());

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

}
