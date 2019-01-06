import java.io.BufferedReader;
import java.io.IOException;

public class ReaderCliente implements Runnable{

    private BufferedReader in;

    /*
     * Construtor para ReaderCliente:
     * in - leitor usado para ler as linhas enviadas pelo servidor
     */
    public ReaderCliente(BufferedReader in) {
        this.in = in;
    }

    /*
     * Função run que inicia a thread criada para manter
     * esta instância. Lê linhas continuamente enquanto for
     * possível. Quando começar a ler linhas null termina a sua
     * execução.
     */
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
