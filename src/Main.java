import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha uma opção");
        System.out.println("1 - Cliente");
        System.out.println("2 - Servidor");
        int op = scanner.nextInt();
        if(op == 1){
            Cliente cliente = new Cliente("127.0.0.1", 12345);
            cliente.start();
        }
        else if(op == 2){
            Servidor servidor = new Servidor(12345);
            servidor.start();
        }
    }

}
