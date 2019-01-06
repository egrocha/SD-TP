import java.util.HashMap;

public class AuctionController implements Runnable{

    private CloudServer cs;

    /*
     * Construtor para AuctionController:
     * cs - CloudServer em que para qual o leilão está a
     * decorrer
     */
    public AuctionController(CloudServer cs) {
        this.cs = cs;
    }

    /*
     * Função que inicia o AuctionController, espera algum tempo
     * e depois verifica qual o valor máximo colocado no mapa de ofertas
     * e qual o utilizador que o colocou. No fim acorda todos os Workers
     * que participaram no leilão e se encontram à espera do fim dele
     */
    public void run(){
        HashMap<String, Double> bids = cs.getBids();
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double max = 0;
        double x = 0;
        String winner = "";
        synchronized (bids) {
            for (String s : bids.keySet()) {
                x = bids.get(s);
                if (x > max) {
                    max = x;
                    winner = s;
                }
            }
            cs.setLastAuction(winner);
            cs.setAuctionRate(x);
            bids.clear();
            bids.notifyAll();
        }
    }

}
