import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Worker implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private HashMap<String, Conta> contas;
    private HashMap<String, CloudServer> servidores;
    private ReentrantLock lock;

    Worker(Socket socket, HashMap<String, Conta> contas, HashMap<String, CloudServer> servidores){
        this.socket = socket;
        this.contas = contas;
        this.servidores = servidores;
        this.lock = new ReentrantLock();
    }

    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());

            out.println("Ligação establecida");
            out.flush();

            String line = "";

            boolean login = login();
            if(!login) return;

            while(true){
                writeMenu();
                line = in.readLine();
                if(line.equals("1")){
                    out.println("Não implementado");
                    out.flush();
                }
                if(line.equals("2")){
                    out.println("Não implementado");
                    out.flush();
                }
                if(line.equals("3")){
                    out.println("Não implementado");
                    out.flush();
                }
                if(line.equals("4")){
                    break;
                }
                System.out.println(line);
            }

            out.println("exit");
            out.flush();
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean login() throws IOException {
        while(true){
            out.println("Login ou signup? (\"exit\" para sair)");
            out.flush();
            String line = in.readLine();
            if(line.toLowerCase().equals("login")){
                while(true) {
                    out.println("Indique as suas credênciais\nEmail:\nPassword:");
                    out.flush();
                    String email = in.readLine();
                    String password = in.readLine();
                    this.lock.lock();
                    if(contas.containsKey(email)){
                        if(password.equals(contas.get(email).getPassword())){
                            this.lock.unlock();
                            out.println("Login efetuado");
                            out.flush();
                            return true;
                        }
                        else this.lock.unlock();
                    }
                    else{
                        this.lock.unlock();
                        out.println("Dados inválidos");
                        out.flush();
                    }
                }
            }
            else if(line.toLowerCase().equals("signup")){
                while(true) {
                    out.println("Indique as suas credênciais desejadas\nEmail:\nPassword:");
                    out.flush();
                    String email = in.readLine();
                    String password = in.readLine();
                    this.lock.lock();
                    if (contas.containsKey(email)) {
                        this.lock.unlock();
                        out.println("ERRO: Email já está registado");
                        out.flush();
                    }
                    else{
                        Conta conta = new Conta(email, password);
                        contas.put(email, conta);
                        this.lock.unlock();
                        out.println("Registo efetuado");
                        out.flush();
                        return true;
                    }
                }
            }
            else if(line.toLowerCase().equals("exit")) return false;
        }
    }

    private void writeMenu(){
        out.println("Escolha uma opção:\n1 - Reservar servidor a pedido\n" +
                "2 - Reservar uma instância a leilão\n3 - Consultar dívida\n"+
                "4 - Sair");
        out.flush();
    }

}
