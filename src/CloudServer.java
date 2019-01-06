import java.util.Date;
import java.util.HashMap;

public class CloudServer {

    private String id;
    private int state;
    private double rate;
    private double auctionRate;
    private String lastAuction;
    private Date start;
    private HashMap<String, Double> bids;

    /*
     * Construtor para CloudServer:
     * ID - nome usado para identificar o servidor Cloud
     * state - número usado para identificar o estado do servidor Cloud
     * rate - preço por hora definido para o servidor Cloud
     * auctionRate - preço por hora definido pelo leilão usado para
     * reservar o servidor Cloud
     * bids - HashMap usado para manter as ofertas dadas pelos utilizadores
     * durante o leilão
     *
     * Sobre a variável state - existem 4 opções: 0, 1, 2 e 3.
     * Estado igual a 0 significa que está livre,
     * 1 indica que um leilão está a decorrer sobre o servidor,
     * 2 indica que o leilão terminou e o servidor foi atribuido
     * a um utilizador, 3 indica que o servidor foi reservado a
     * pedido
     */
    CloudServer(String id, double rate){
        this.id = id;
        this.state = 0;
        this.rate = rate;
        this.auctionRate = 0;
        this.bids = new HashMap<>();
    }

    // Getter para variável id
    public synchronized String getId(){
        return this.id;
    }

    // Getter para variável state
    public synchronized int getState() {
        return state;
    }

    // Getter para variável rate
    public synchronized double getRate() {
        return rate;
    }

    // Getter para variável auctionRate
    public synchronized double getAuctionRate() {
        return auctionRate;
    }

    // Getter para variável lastAuction
    public synchronized String getLastAuction() {
        return lastAuction;
    }

    // Getter para variável start
    public synchronized Date getStart() {
        return start;
    }

    // Getter para variável bids
    public synchronized HashMap<String, Double> getBids() {
        return bids;
    }

    // Setter para variável id
    public synchronized void setId(String id) {
        this.id = id;
    }

    // Setter para variável id
    public synchronized void setState(int state) {
        this.state = state;
    }

    // Setter para variável id
    public synchronized void setRate(double rate) {
        this.rate = rate;
    }

    // Setter para variável id
    public synchronized void setAuctionRate(double auctionRate) {
        this.auctionRate = auctionRate;
    }

    // Setter para variável id
    public synchronized void setLastAuction(String lastAuction){
        this.lastAuction = lastAuction;
    }

    // Setter para variável id
    public synchronized void setStart(Date start) {
        this.start = start;
    }

    // Setter para variável id
    public synchronized void setBids(HashMap<String, Double> bids) {
        this.bids = bids;
    }

}
