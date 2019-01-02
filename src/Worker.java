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
    private String cliente;
    private HashMap<String, Conta> contas;
    private HashMap<String, HashMap<String, CloudServer>> servidores;
    private ReentrantLock lock;

    Worker(Socket socket, HashMap<String, Conta> contas,
           HashMap<String, HashMap<String, CloudServer>> servidores){
        this.socket = socket;
        this.contas = contas;
        this.servidores = servidores;
        this.lock = new ReentrantLock();
    }

    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());

            System.out.println("Conexão aceitada");
            out.println("Ligação estabelecida");
            out.flush();

            String line = "";

            boolean login = login();
            if(login) {
                menu:
                while (true) {
                    writeMenu();
                    line = in.readLine();
                    switch (line) {
                        case "1":
                            showServers();
                            break;
                        case "2":
                            out.println("Não implementado");
                            out.flush();
                            break;
                        case "3":
                            out.println("Não implementado");
                            out.flush();
                            break;
                        case "4":
                            out.println("Dívida: " + contas.get(this.cliente).getDivida());
                            out.flush();
                            break;
                        case "5":
                            checkServers();
                            break;
                        case "6":
                            freeServers();
                            break;
                        case "7":
                            break menu;
                        default:
                            out.println("Opção inválida");
                            out.flush();
                            break;
                    }
                }
            }

            out.println("Sessão encerrada");
            out.flush();
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
            System.out.println("Conexão terminada");
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
                            this.cliente = email;
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
                        this.cliente = email;
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
        out.println("Escolha uma opção:\n" +
                "1 - Ver servidores disponíveis\n" +
                "2 - Reservar servidor a pedido\n" +
                "3 - Reservar uma instância a leilão\n" +
                "4 - Consultar dívida\n" +
                "5 - Verificar servidores reservados\n" +
                "6 - Libertar servidores reservados\n" +
                "7 - Sair");
        out.flush();
    }

    private void showServers(){
        for(HashMap<String, CloudServer> hm : servidores.values()){
            for(CloudServer cs : hm.values()){
                switch (cs.getState()) {
                    case (0):
                        out.println(cs.getId() + ", Estado: Disponível");
                        out.flush();
                        break;
                    case (1):
                        out.println(cs.getId() + ", Estado: Em processo de leilão");
                        out.flush();
                        break;
                    case (2):
                        out.println(cs.getId() + ", Estado: Reservado a leilão");
                        out.flush();
                        break;
                    case (3):
                        out.println(cs.getId() + ", Estado: Reservado a pedido");
                        out.flush();
                        break;
                    default:
                        out.println(cs.getId() + ", Estado: Desconhecido");
                        out.flush();
                        break;
                }
            }
        }
    }

    private int checkServers(){
        if(contas.get(cliente).getReservados().size() == 0){
            out.println("Não tem servidores reservados");
            out.flush();
            return 0;
        }
        else {
            out.println("Servidores reservados por si:");
            out.flush();
            for (String s : contas.get(cliente).getReservados().keySet()) {
                out.println(s);
                out.flush();
            }
            return 1;
        }
    }

    private void freeServers() throws IOException {
        int check = checkServers();
        if(check == 0) return;
        out.println("Indique o ID do servidor que pretende libertar");
        out.flush();
        String id = in.readLine();
        lock.lock();
        CloudServer cs = contas.get(cliente).getReservados().remove(id);
        if(cs == null){
            out.println("ID inválido");
            out.flush();
        }
        else{
            out.println("Servidor libertado com sucesso");
            out.flush();
        }
        lock.unlock();
    }

}
