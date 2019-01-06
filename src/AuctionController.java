import java.util.HashMap;

public class AuctionController implements Runnable{

    private CloudServer cs;

    public AuctionController(CloudServer cs) {
        this.cs = cs;
    }

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
            bids.notifyAll();
        }
    }

}
