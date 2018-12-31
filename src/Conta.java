public class Conta {

    private String email;
    private String password;
    private double divida;

    Conta(String email, String password){
        this.email = email;
        this.password = password;
        this.divida = 0;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getDivida(){
        return divida;
    }

    public void addDivida(double valor){
        this.divida += valor;
    }

}
