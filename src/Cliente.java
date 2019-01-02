import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Cliente {

    private String address;
    private int port;

    Cliente(String address, int port){
        this.address = address;
        this.port = port;
    }

    public void start() throws IOException {
        Socket socket = new Socket(address, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        System.out.println(inServer.readLine());

        ReaderCliente readerCliente = new ReaderCliente(inServer);
        Thread thread = new Thread(readerCliente);
        thread.start();

        String line = "";
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
