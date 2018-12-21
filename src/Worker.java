import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Worker implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private HashMap<String, Conta> contas;
    private ReentrantLock lock;

    Worker(Socket socket, HashMap<String, Conta> contas){
        this.socket = socket;
        this.contas = contas;
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

            System.out.println("fora");

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

    private boolean login() throws IOException {

        while(true){
            out.println("Login ou signup? (\"exit\" para sair)");
            out.flush();
            String line = in.readLine();
            if(line.toLowerCase().equals("login")){
                while(true) {
                    out.println("Indique o seu email e password (Formato: email password)");
                    out.flush();
                    String[] dados = in.readLine().split(" ");
                    if(contas.containsKey(dados[0])){
                        if(dados[1].equals(contas.get(dados[0]).getPassword())){
                            out.println("Login efetuado");
                            out.flush();
                            return true;
                        }
                    }
                    else{
                        out.println("Dados inválidos");
                        out.flush();
                    }
                }
            }
            else if(line.toLowerCase().equals("signup")){
                while(true) {
                    out.println("Indique o seu email e password (Formato: email password)");
                    out.flush();
                    String[] dados = in.readLine().split(" ");
                    if (contas.containsKey(dados[0])) {
                        out.println("ERRO: Email já está registado");
                        out.flush();
                    }
                    else{
                        Conta conta = new Conta(dados[0], dados[1]);
                        this.lock.lock();
                        contas.put(dados[0], conta);
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

}
