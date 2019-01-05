import java.util.Calendar;
import java.util.Date;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.ArrayList;

public class Servidor {

    private int port;
    private HashMap<String, Conta> contas;
    private HashMap<String, HashMap<String, CloudServer>> servidores;
    private ArrayList<String> lostAuctions;
    private volatile Object auctionLock;

    Servidor(int port){
        this.port = port;
        this.contas = new HashMap<>();
        this.servidores = new HashMap<>();
        this.lostAuctions = new ArrayList<>();
        this.auctionLock = new Object();

        contasTeste();
        servidoresTeste();
    }

    public void start() throws IOException {
        System.out.println("Servidor iniciado");
        ServerSocket serverSocket = new ServerSocket(port);

        while(true){
            Socket socket = serverSocket.accept();
            Worker worker = new Worker(socket, contas, servidores, lostAuctions);
            Thread thread = new Thread(worker);
            thread.start();
        }
    }

    public void contasTeste(){
        Conta c = new Conta("teste","teste");
        Conta c2 = new Conta("teste2","teste2");
        Conta c3 = new Conta("teste3","teste3");
        Conta c4 = new Conta("teste4","teste4");
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
        cs.setState(3);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,7);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        Date d = cal.getTime();
        cs.setStart(d);
        this.contas.get("teste").getReservados().put("large-s1.large", "s1.large");
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY,9);
        cal2.set(Calendar.MINUTE,30);
        cal2.set(Calendar.SECOND,0);
        cal2.set(Calendar.MILLISECOND,0);
        Date d2 = cal.getTime();
        cs2.setStart(d2);
        cs2.setState(2);
        cs2.setAuctionRate(1);
        cs2.setLastAuction("teste2");
        this.contas.get("teste2").getReservados().put("large-s2.large", "s2.large");
    }

}
