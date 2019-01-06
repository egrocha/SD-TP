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
    private ArrayList<String> lostAuctions;
    private ReentrantLock lock;

    /*
     * Construtor para a classe Worker:
     * socket - Socket por onde o Worker interage com o Cliente
     * contas - mapa que contém todas as contas de clientes
     * servers - mapa que contém todos os servidores Cloud geridos pelo sistema
     * lostAuctions - lista que contém todos as reservas de leilão perdidas no sistema
     * lock - ReentrantLock usado pelo sistema para controlar concorrência
     */
    Worker(Socket socket, HashMap<String, Conta> contas,
           HashMap<String, HashMap<String, CloudServer>> servers,
           ArrayList<String> lostAuctions){
        this.socket = socket;
        this.contas = contas;
        this.servers = servers;
        this.lostAuctions = lostAuctions;
        this.lock = new ReentrantLock();
    }

    /*
     * Função que inicia o Worker, passando o utilizador pelo processo de login,
     * mostrando o menu principal e criando as Threads que ficam atentas a se o
     * utilizador perde alguma das suas reservas em leilão. Após mostrar o
     * menu, espera pela escolha do utilizador sobre qual processo irá
     * iniciar
     */
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
                            lock.lock();
                            out.println("Dívida: " + contas.get(this.cliente).getDivida());
                            lock.unlock();
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

    /*
     * Função que controla a autenticação do utilizador. Dá ao utilizador
     * a escolha entre tentar entrar numa conta existente ou criar uma nova.
     * Caso os dados inseridos pelo utilizador sejam validados, é-lhe permitido
     * entrar no sistema.
     */
    private boolean login() throws IOException {
        out.println("Login ou signup? (\"exit\" para sair)");
        out.flush();
        while(true){
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
                            this.cliente = email;
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
                        this.cliente = email;
                        this.lock.unlock();
                        out.println("Registo efetuado");
                        out.flush();
                        return true;
                    }
                }
            }
            else if(line.toLowerCase().equals("exit")) return false;
            else{
                out.println("Opção inválida");
                out.flush();
            }
        }
    }

    /*
     * Escreve o menu usado para selecionar as funcionalidades
     * disponíveis no sistema
     */
    private void writeMenu(){
        out.println("Escolha uma opção:\n" +
                "1 - Ver servidores disponíveis\n" +
                "2 - Reservar servidor a pedido\n" +
                "3 - Reservar servidor a leilão\n" +
                "4 - Consultar dívida\n" +
                "5 - Verificar servidores reservados\n" +
                "6 - Libertar servidores reservados\n" +
                "7 - Sair");
        out.flush();
    }

    /*
     * Usado para mostrar os servidores Cloud disponíveis no sistema. Mostra
     * o nome, estado e preço por hora dos servidores.
     */
    private void showServers(){
        for(HashMap<String, CloudServer> hm : servers.values()){
            for(CloudServer cs : hm.values()){
                lock.lock();
                switch (cs.getState()) {
                    case (0):
                        out.println("Nome: "+cs.getId()+", Estado: Disponível, Preço: "+cs.getRate());
                        lock.unlock();
                        out.flush();
                        break;
                    case (1):
                        out.println("Nome: "+cs.getId() + ", Estado: Em processo de leilão, Preço: "+cs.getRate());
                        lock.unlock();
                        out.flush();
                        break;
                    case (2):
                        out.println("Nome: "+cs.getId() + ", Estado: Reservado a leilão, Preço: "+cs.getRate());
                        lock.unlock();
                        out.flush();
                        break;
                    case (3):
                        out.println("Nome: "+cs.getId() + ", Estado: Reservado a pedido, Preço: "+cs.getRate());
                        lock.unlock();
                        out.flush();
                        break;
                    default:
                        out.println("Nome: "+cs.getId() + ", Estado: Desconhecido, Preço: "+cs.getRate());
                        lock.unlock();
                        out.flush();
                        break;
                }
            }
        }
    }

    /*
     * Menu principal usado para reserva de servidores Cloud. Mostra
     * a função showAvailableServers com a categoria selecionada e o modo
     * em que o menu foi chamado.
     * No nosso sistema temos 3 categorias de servidores: grandes, médios e
     * pequenos. A variável flag define se a função foi chamada no contexto
     * de reserva por pedido ou pedido a leilão.
     */
    private void menuReserveServer(int flag) throws IOException {
        out.println("Qual categoria de servidor quer reservar?\nCategorias: " +
                "large, medium, micro (\"exit\" para sair)");
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
            if(line.toLowerCase().equals("exit")) break;
            else {
                out.println("Opção inválida");
                out.flush();
            }
        }
    }

    /*
     * É chamada pela função menuReserveServer para mostrar os servidores
     * disponíveis numa dada categoria. Se a flag tiver o valor 1 (o que
     * significa que o utilizador está a tentar reservar por leilão),
     * o servidor procura servidores disponíveis ou em processo de leilão.
     * Caso contrário, o servidor mostra apenas servidores disponíveis, ou,
     * caso não haja nenhuns disponíveis, os servidores que foram leiloados.
     * Dependendo das escolhas do utilizador, irá chamar a função reserveServer,
     * enterAuction ou showAuctionedServers
     */
    private void showAvailableServers(String category, int flag) throws IOException {
        int count = 0;
        ArrayList<String> aux = new ArrayList<>();
        for(CloudServer cs : this.servers.get(category).values()){
            if(cs.getState() == 0 || cs.getState() == flag){
                count++;
                String id = cs.getId();
                aux.add(id);
                out.println("ID: "+id+", Custo: "+cs.getRate());
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
                        reserveServer(category, line, 0);
                        out.println("Servidor reservado com sucesso");
                        out.flush();
                    }
                    else{
                        out.println("Quanto deseja pagar por hora?");
                        out.flush();
                        double rate = Double.parseDouble(in.readLine());
                        enterAuction(category, line, rate);
                    }
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

    /*
     * Esta função mostra os servidores que foram reservados a leilão.
     * É chamada quando não existem servidores disponíveis para uma
     * reserva a pedido.
     */
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
                    reserveServerReplace(category, line);
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

    /*
     * Função usada para entrar ou começar leilões novos. Caso não haja um
     * leilão a decorrer nesse momento inicia um novo, senão entra no existente.
     * Nos dois casos fica à espera do resultado e caso ganhe irá reservar
     * o servidor. Para iniciar um leilão cria uma instância da classe
     * AuctionController. O Worker fica à espera do resultado através
     * de uma chamada de wait() e será acordado pelo AuctionController
     * quando o leilão tiver terminado.
     */
    private void enterAuction(String category, String id, double rate)  {
        CloudServer cs = servers.get(category).get(id);
        HashMap<String, Double> bids = cs.getBids();
        lock.lock();
        if(bids.isEmpty()){
            lock.unlock();
            cs.setState(1);
            out.println("A iniciar leilão...");
            out.flush();
            lock.lock();
            bids.put(cliente, rate);
            lock.unlock();
            AuctionController auctionController = new AuctionController(cs);
            Thread thread = new Thread(auctionController);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            lock.unlock();
            out.println("A entrar no leilão...");
            out.flush();
            lock.lock();
            bids.put(cliente, rate);
            lock.unlock();
            synchronized (bids){
                try {
                    bids.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(cs.getLastAuction().equals(cliente)){
            out.println("Ganhou o leilão");
            out.flush();
            reserveServer(category, cs.getId(), 1);
        }
        else{
            out.println("Perdeu o leilão");
            out.flush();
        }
    }

    /*
     * A função reserveServerReplace é usada quando o utilizador
     * reserva a leilão um servidor que anteriormente estava reservada
     * por leilão.
     */
    private void reserveServerReplace(String category, String serverID) {
        lock.lock();
        lostAuctions.add(category + "-" + serverID);
        lock.unlock();
        synchronized (lostAuctions) {
            lostAuctions.notifyAll();
        }
        CloudServer cs = servers.get(category).get(serverID);
        String lastAuction = cs.getLastAuction();
        Date start = cs.getStart();
        Date end = new Date();
        double rate = cs.getRate();
        contas.get(lastAuction).addDivida(calcDebt(start, end, rate));
        reserveServer(category, serverID, 0);
    }

    /*
     * Função usada para reservar servidores Cloud. Caso a flag
     * tenha o valor de 0 o servidor é reservado a pedido. Se a
     * flag tiver o valor de 1 o servidor fica reservado a leilão.
     */
    private void reserveServer(String category, String serverID, int flag) {
        CloudServer cs = this.servers.get(category).get(serverID);
        if(flag == 0) cs.setState(3);
        if(flag == 1) cs.setState(2);
        cs.setStart(new Date());
        String reserveID = category + "-" + serverID;
        lock.lock();
        this.contas.get(cliente).getReservados().put(reserveID, serverID);
        lock.unlock();
        out.println("ID de reserva: " + reserveID);
    }

    /*
     * Função que verifica se os cliente tem servidores Cloud reservados e,
     * caso tenha, imprime-os para o cliente
     */
    private int checkServers(){
        lock.lock();
        if(contas.get(cliente).getReservados().size() == 0){
            lock.unlock();
            out.println("Não tem servidores reservados");
            out.flush();
            return 0;
        }
        else {
            lock.unlock();
            out.println("Servidores reservados por si:");
            out.flush();
            for (String s : contas.get(cliente).getReservados().keySet()) {
                out.println(s);
                out.flush();
            }
            return 1;
        }
    }

    /*
     * Inicia o processo de libertação de servidores. Primeiro usa
     * a função checkServers para ver se o utilizador tem servidores
     * reservados e caso tenha pergunta qual servidor o cliente quer
     * libertar. Chama a função freeServer para terminar o processo
     */
    private void freeServerStart() throws IOException {
        int check = checkServers();
        if(check == 0) return;
        out.println("Indique o ID do servidor que pretende libertar");
        out.flush();
        String line = in.readLine();
        if(contas.get(cliente).getReservados().containsKey(line)){
            freeServer(line);
            lock.lock();
            contas.get(cliente).getReservados().remove(line);
            lock.unlock();
            out.println("Servidor libertado com successo");
            out.flush();
        }
        else{
            out.println("Servidor inválido");
            out.flush();
        }
    }

    /*
     * Termina o processo de libertação de servidores. Procura o servidor
     * através do ID de reserva e cálcula a dívida acumulada pelo cliente.
     * O estado do servidor Cloud reservado volta para 0, indicando que
     * está livre.
     */
    private void freeServer(String id){
        String[] parts = id.split("-");
        lock.lock();
        String name = this.contas.get(cliente).getReservados().get(id);
        lock.unlock();
        CloudServer aux = this.servers.get(parts[0]).get(name);
        aux.setState(0);
        Date start = aux.getStart();
        double rate = aux.getRate();
        Date end = new Date();
        double debt = calcDebt(start, end, rate);
        this.contas.get(cliente).addDivida(debt);
    }

    /*
     * Função que, dada duas datas e um preço por hora, calcula a
     * dívida que vai ser acumulada pelo utilizador
     */
    private double calcDebt(Date start, Date end, double rate){
        long time = end.getTime() - start.getTime();
        return time/1000/60/60 * rate;
    }

    /*
     * Cria novas Threads que ficam à espera que as reservas por leilão
     * mantidas pelo utilizador sejam interrompidas, para que o possa
     * avisar do que aconteceu e qual foi o servidor Cloud que lhe foi
     * retirado.
     */
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

    /*
     * Função usada pelas Threads criadas pela função createListeners
     * para verificar se o utilizador que está a ser vigiado perdeu alguma
     * das suas reservas. Por cada reserva perdida envia um aviso ao
     * utilizador com o ID de reserva
     */
    private void checkLostAuctions(){
        HashMap<String, String> reservados = contas.get(cliente).getReservados();
        for (String s : reservados.keySet()) {
            lock.lock();
            if (lostAuctions.contains(s)) {
                lostAuctions.remove(s);
                reservados.remove(s);
                lock.unlock();
                out.println("A sua reserva com ID " + s + " foi cancelada");
                out.flush();
                break;
            }
            else lock.unlock();
        }
    }

}
