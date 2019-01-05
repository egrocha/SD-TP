import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Worker implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String cliente;
    private HashMap<String, Conta> contas;
    private HashMap<String, HashMap<String, CloudServer>> servers;
    private final ArrayList<String> lostAuctions;
    private ReentrantLock lock;

    Worker(Socket socket, HashMap<String, Conta> contas,
           HashMap<String, HashMap<String, CloudServer>> servers,
           ArrayList<String> lostAuctions){
        this.socket = socket;
        this.contas = contas;
        this.servers = servers;
        this.lostAuctions = lostAuctions;
        this.lock = new ReentrantLock();
    }

    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());

            System.out.println("Conexão aceitada");
            out.println("Ligação estabelecida");
            out.flush();

            boolean login = login();
            checkLostAuctions();
            createListeners();
            if(login) {
                menu:
                while (true) {
                    writeMenu();
                    String line = in.readLine();
                    switch (line) {
                        case "1":
                            showServers();
                            break;
                        case "2":
                            menuReserveServer(0);
                            break;
                        case "3":
                            menuReserveServer(1);
                            break;
                        case "4":
                            out.println("Dívida: " + contas.get(this.cliente).getDivida());
                            out.flush();
                            break;
                        case "5":
                            checkServers();
                            break;
                        case "6":
                            freeServerStart();
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
        for(HashMap<String, CloudServer> hm : servers.values()){
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

    private void menuReserveServer(int flag) throws IOException {
        out.println("Qual categoria de servidor quer reservar?\nCategorias: " +
                "large, medium, micro");
        out.flush();
        while(true) {
            String line = in.readLine();
            if (line.toLowerCase().equals("large")) {
                showAvailableServers("large", flag);
                break;
            }
            if(line.toLowerCase().equals("medium")){
                showAvailableServers("medium", flag);
                break;
            }
            if(line.toLowerCase().equals("micro")){
                showAvailableServers("micro", flag);
                break;
            }
            else {
                out.println("Opção inválida");
                out.flush();
            }
        }
    }

    private void showAvailableServers(String category, int flag) throws IOException {
        int count = 0;
        ArrayList<String> aux = new ArrayList<>();
        for(CloudServer cs : this.servers.get(category).values()){
            if(cs.getState() == 0){
                count++;
                String id = cs.getId();
                aux.add(id);
                out.println(id);
                out.flush();
            }
        }
        if(count != 0) {
            out.println("Qual servidor pretende reservar?");
            out.flush();
            while (true) {
                String line = in.readLine();
                if (aux.contains(line)) {
                    if(flag == 0) {
                        reserveServer(category, line, flag);
                    }
                    else{
                        startAuction(category, line);
                    }
                    out.println("Servidor reservado com sucesso");
                    out.flush();
                    break;
                }
                else{
                    out.println("Servidor inválido");
                    out.flush();
                }
            }
        }
        else if(flag == 0){
            out.println("Não existem servidores disponíveis nessa categoria.\n" +
                    "Quer tentar libertar servidores em reserva por leilão? (sim/nao)");
            out.flush();
            loop:
            while (true) {
                String line = in.readLine();
                switch (line.toLowerCase()) {
                    case "sim":
                        showAuctionedServers(category);
                        break loop;
                    case "nao":
                        break loop;
                    default:
                        out.println("Opção inválida");
                        out.flush();
                        break;
                }
            }
        }
        else{
            out.println("Não existem servidores disponíveis nesta categoria");
            out.flush();
        }
    }

    private void showAuctionedServers(String category) throws IOException {
        int count = 0;
        ArrayList<String> aux = new ArrayList<>();
        for(CloudServer cs : this.servers.get(category).values()){
            if(cs.getState() == 2){
                count++;
                String id = cs.getId();
                aux.add(id);
                out.println(id);
                out.flush();
            }
        }
        if(count == 0){
            out.println("Não existem servidores reservados em leilão nesta categoria");
            out.flush();
        }
        else{
            out.println("Qual servidor pretende reservar?");
            out.flush();
            while(true){
                String line = in.readLine();
                if(aux.contains(line)){
                    reserveServer(category, line, 2);
                    out.println("Servidor reservado com sucesso");
                    out.flush();
                    break;
                }
                else{
                    out.println("Servidor inválido");
                }
            }
        }
    }

    private void startAuction(String category, String id){
        CloudServer cs = servers.get(category).get(id);

    }

    private void reserveServer(String category, String serverID, int flag){
        if(flag == 2){
            lostAuctions.add(category+"-"+serverID);
            synchronized (lostAuctions){
                lostAuctions.notifyAll();
            }
            CloudServer cs = servers.get(category).get(serverID);
            String lastAuction = cs.getLastAuction();
            Date start = cs.getStart();
            Date end = new Date();
            double rate = cs.getRate();
            contas.get(lastAuction).addDivida(calcDebt(start, end, rate));
        }
        this.servers.get(category).get(serverID).setState(3);
        this.servers.get(category).get(serverID).setStart(new Date());
        this.contas.get(cliente).getReservados().put(category+"-"+serverID, serverID);
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

    private void freeServerStart() throws IOException {
        int check = checkServers();
        if(check == 0) return;
        out.println("Indique o ID do servidor que pretende libertar");
        out.flush();
        String line = in.readLine();
        if(contas.get(cliente).getReservados().containsKey(line)){
            freeServer(line);
            contas.get(cliente).getReservados().remove(line);
            out.println("Servidor libertado com successo");
            out.flush();
        }
        else{
            out.println("Servidor inválido");
            out.flush();
        }
    }

    private void freeServer(String id){
        String[] parts = id.split("-");
        String name = this.contas.get(cliente).getReservados().get(id);
        CloudServer aux = this.servers.get(parts[0]).get(name);
        aux.setState(0);
        Date start = aux.getStart();
        double rate = aux.getRate();
        Date end = new Date();
        double debt = calcDebt(start, end, rate);
        this.contas.get(cliente).addDivida(debt);
    }

    private double calcDebt(Date start, Date end, double rate){
        long time = end.getTime() - start.getTime();
        return time/1000/60/60 * rate;
    }

    private void createListeners(){
        new Thread(() -> {
            while (true) {
                synchronized (lostAuctions) {
                    try {
                        lostAuctions.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkLostAuctions();
                }
            }
        }).start();
    }

    private void checkLostAuctions(){
        HashMap<String, String> reservados = contas.get(cliente).getReservados();
        for (String s : reservados.keySet()) {
            if (lostAuctions.contains(s)) {
                lostAuctions.remove(s);
                reservados.remove(s);
                out.println("A sua reserva com ID " + s + " foi cancelada");
                out.flush();
                break;
            }
        }
    }

}
