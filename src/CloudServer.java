import java.util.Date;

public class CloudServer {

    private String id;
    private int state;
    private double rate;
    private double auctionRate;
    private String lastAuction;
    private Date start;

    CloudServer(String id, double rate){
        this.id = id;
        this.state = 0;
        this.rate = rate;
        this.auctionRate = 0;
    }

    public String getId(){
        return this.id;
    }

    public int getState() {
        return state;
    }

    public double getRate() {
        return rate;
    }

    public double getAuctionRate() {
        return auctionRate;
    }

    public String getLastAuction() {
        return lastAuction;
    }

    public Date getStart() {
        return start;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setAuctionRate(double auctionRate) {
        this.auctionRate = auctionRate;
    }

    public void setLastAuction(String lastAuction){
        this.lastAuction = lastAuction;
    }

    public void setStart(Date start) {
        this.start = start;
    }

}
