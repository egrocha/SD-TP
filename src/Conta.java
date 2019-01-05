import java.util.HashMap;

public class Conta {

    private String email;
    private String password;
    private double divida;
    private HashMap<String, String> reservados;

    Conta(String email, String password){
        this.email = email;
        this.password = password;
        this.divida = 0;
        this.reservados = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public HashMap<String, String> getReservados(){
        return this.reservados;
    }

    public double getDivida(){
        return divida;
    }

    public void addDivida(double valor){
        this.divida += valor;
    }

}
