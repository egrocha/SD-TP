import java.io.BufferedReader;
import java.io.IOException;

public class ReaderCliente implements Runnable{

    private BufferedReader in;

    public ReaderCliente(BufferedReader in) {
        this.in = in;
    }

    public void run() {
        try{
            String line;
            while(true){
                line = in.readLine();
                if(line != null) System.out.println(line);
                else break;
            }
            System.out.println("Carregue no ENTER para sair");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
