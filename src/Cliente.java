import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

        String line = "";
        while(!line.equals("exit")){
            line = in.readLine();
            out.println(line);
            out.flush();
        }

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

}
