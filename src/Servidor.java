import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Servidor {

    private String address;
    private int port;
    private HashMap<String, Conta> contas;
    private HashMap<String, HashMap<String, CloudServer>> servidores;

    Servidor(String address, int port){
        this.address = address;
        this.port = port;
        this.contas = new HashMap<>();
        this.servidores = new HashMap<>();

        contasTeste();
        servidoresTeste();
    }

    public void start() throws IOException {
        System.out.println("Servidor iniciado");
        ServerSocket serverSocket = new ServerSocket(port);

        while(true){
            Socket socket = serverSocket.accept();
            Worker worker = new Worker(socket, contas, servidores);
            Thread thread = new Thread(worker);
            thread.start();
        }
    }

    public void contasTeste(){
        Conta c = new Conta("teste","teste");
        Conta c2 = new Conta("teste2","teste2");
        Conta c3 = new Conta("teste3","teste3");
        Conta c4 = new Conta("admin","admin");
        this.contas.put(c.getEmail(), c);
        this.contas.put(c2.getEmail(), c2);
        this.contas.put(c3.getEmail(), c3);
        this.contas.put(c4.getEmail(), c4);
    }

    public void servidoresTeste(){
        HashMap<String, CloudServer> large = new HashMap<>();
        HashMap<String, CloudServer> medium = new HashMap<>();
        HashMap<String, CloudServer> micro = new HashMap<>();
        CloudServer cs = new CloudServer("s1.large", 5);
        CloudServer cs2 = new CloudServer("s2.large", 7);
        CloudServer cs3 = new CloudServer("a1.medium", 2);
        CloudServer cs4 = new CloudServer("a2.medium", 1.5);
        CloudServer cs5 = new CloudServer("b1.micro", 0.4);
        CloudServer cs6 = new CloudServer("b2.micro", 0.3);
        large.put(cs.getId(), cs);
        large.put(cs2.getId(), cs2);
        medium.put(cs3.getId(), cs3);
        medium.put(cs4.getId(), cs4);
        micro.put(cs5.getId(), cs5);
        micro.put(cs6.getId(), cs6);
        this.servidores.put("large", large);
        this.servidores.put("medium", medium);
        this.servidores.put("micro", micro);
        this.contas.get("teste").getReservados().put("s1.large", cs);
    }


    public void teste() throws InterruptedException {
        Date now = new Date();
        long start = now.getTime();
        TimeUnit.SECONDS.sleep(5);
        Date after = new Date();
        long finish = after.getTime();
        System.out.println(finish - start);
    }
}
