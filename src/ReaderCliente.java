import java.io.BufferedReader;
import java.io.IOException;

public class ReaderCliente implements Runnable{

    BufferedReader in;

    public ReaderCliente(BufferedReader in) {
        this.in = in;
    }

    public void run() {
        try{
            while(true){
                System.out.println(in.readLine());
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
