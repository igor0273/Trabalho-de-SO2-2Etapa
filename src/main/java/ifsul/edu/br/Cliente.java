package ifsul.edu.br;


import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *  Classe usada para controlar 
 * os clientes e administradores
 * @author igor.rocha
 */
public class Cliente {
    private List<Conta> contas = new ArrayList<>();
    private PrintStream endPoint;
    private String name;
    private Boolean admin = false;
    private Socket socket;
    
    public Cliente(){
    }

    public List<Conta> getContas() {
        return contas;
    }

    public void setContas(List<Conta> contas) {
        this.contas = contas;
    }

    public PrintStream getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PrintStream endPoint) {
        this.endPoint = endPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    
}
