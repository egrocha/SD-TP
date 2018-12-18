import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Worker implements Runnable{

    private Socket socket;

    Worker(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println("Connection established");
            out.flush();

            String line = "";
            while(!line.equals("exit")){
                line = in.readLine();
                System.out.println(line);
                out.println("ok");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
