import java.sql.Date;

public class CloudServer {

    private String id;
    private int state;
    private double rate;
    private Date start;

    CloudServer(String id, double rate){
        this.id = id;
        this.state = 0;
        this.rate = rate;
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

    public void setStart(Date start) {
        this.start = start;
    }
}
