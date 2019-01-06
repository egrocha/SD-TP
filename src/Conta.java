import java.util.HashMap;

public class Conta {

    private String email;
    private String password;
    private double divida;
    private HashMap<String, String> reservados;

    /*
     * Construtor para Conta:
     * email - email usado para login do utilizador
     * password - password usada para autenticação do utilizador
     * divida - dívida acumulada pelo utilizador
     * reservados - regista quais as reservas mantidas pelo utilizador
     */
    Conta(String email, String password){
        this.email = email;
        this.password = password;
        this.divida = 0;
        this.reservados = new HashMap<>();
    }

    // Getter para a variável email
    public synchronized String getEmail() {
        return email;
    }

    // Getter para a variável password
    public synchronized String getPassword() {
        return password;
    }

    // Getter para a variável divida
    public synchronized double getDivida(){
        return divida;
    }

    // Getter para a variável reservados
    public synchronized HashMap<String, String> getReservados(){
        return this.reservados;
    }

    // Setter para a variável email
    public synchronized void setEmail(String email) {
        this.email = email;
    }

    // Setter para a variável password
    public synchronized void setPassword(String password) {
        this.password = password;
    }

    // Setter para a variável divida
    public synchronized void setDivida(double divida) {
        this.divida = divida;
    }

    // Setter para a variável reservados
    public synchronized void setReservados(HashMap<String, String> reservados) {
        this.reservados = reservados;
    }

    /*
     * Função usada para aumentar a dívida do utilizador
     */
    public synchronized void addDivida(double valor){
        this.divida += valor;
    }

}
