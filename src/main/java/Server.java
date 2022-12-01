
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe executa o servidor que ira executar o jogo e mostrar as mensagens para
 * os jogadores
 *
 * @author Igor Rocha
 *
 */
public class Server extends Thread {

    private static List<Cliente> clientesList;
    private static List<Agencia> agenciasList;
    private static List<Conta> contasList;
    private static List<Cliente> clienteAdminList;
    private Cliente cliente;
    private Socket connection;
    private Agencia agencia;
    private static int i;
    private static volatile boolean end;
    private static String winner;
    private static boolean start;
    private static boolean adminLogado = false;
    private Conta conta;

    // Construtor
    public Server(Cliente cliente) {
        this.cliente = cliente;
    }

    // Instancia Thread para cada cliente
    public void run() {
        try {
            BufferedReader startingPoint = new BufferedReader(
                    new InputStreamReader(cliente.getSocket().getInputStream()));
            PrintStream endPoint = new PrintStream(cliente.getSocket().getOutputStream());
            cliente.setEndPoint(endPoint);

            String name = startingPoint.readLine();

            this.cliente.getEndPoint().flush();
            this.cliente.setName(name);

            String line = "n";

            while ((line != null && !(line.trim().equals("")))) {
                if (!this.cliente.getAdmin()) {
                    endPoint.println("1. Depositar\n2. Sacar\n3. Extrado\n4 Listar Contas\n5. Sair ");
                    line = startingPoint.readLine();

                    switch (line) {

                        case "1": // Depositar
                            depositar(endPoint, startingPoint);
                            break;
                        case "2": // Mudar o status do jogador

                            break;
                        case "3": // Inicia o jogo
                            extrado(endPoint, startingPoint);
                            break;

                        case "4":
                            this.cliente.getContas().forEach(x -> {
                                endPoint.println(x.getNumeroConta());
                            });

                            break;

                        case "5":
                            this.interrupt();
                            break;
                        default: // Opção inválida
                            endPoint.println("Erro: Opção inválida!");
                            break;

                    }

                } else {
                    endPoint.println("1. Add Agencia\n2. Add Conta\n3. Delete Agencia\n4. Delete Conta\n5 Listas Agencia\n6 listar Contas\n7. Sair ");
                    line = startingPoint.readLine();

                    switch (line) {

                        case "1": // Add Agencia
                            addAgencia(endPoint, startingPoint);
                            break;
                        case "2": // Add Conta
                            addConta(endPoint, startingPoint);
                            break;
                        case "3": // Inicia o jogo
                            deleteAgencia(endPoint, startingPoint);
                            break;

                        case "4":
                            deleteConta(endPoint, startingPoint);
                            break;

                        case "5":
                            listaAgencia(endPoint);

                            break;

                        case "6":
                            listaContas(endPoint);
                            break;
                        case "7":
                            this.interrupted();
                            this.stop();

                            break;
                        default: // Opção inválida
                            endPoint.println("Erro: Opção inválida!");
                            break;

                    }
                }

            }

            clientesList.remove(endPoint);
            clientesList.remove(this.cliente);
            connection.close();

        } catch (NullPointerException e) {
            System.out.println("IOException: " + e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAgencia(PrintStream endPoint, BufferedReader startingPoint) throws IOException, InterruptedException {
        try {
            Agencia x = new Agencia();
            endPoint.println("Informe o numero da agencia");
            x.setNumero(startingPoint.readLine().trim());
            endPoint.println("Informe a Descrção do agencia");
            x.setDescricao(startingPoint.readLine().trim());
            this.sleep(500);
            if (x != null) {
                this.agenciasList.add(x);
            }
            endPoint.println("Agencia Adicionada com sucesso");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deleteAgencia(PrintStream endPoint, BufferedReader startingPoint) throws IOException {
        try {
            endPoint.println("Informe o numero da agencia");
            String nAgencia = startingPoint.readLine().trim();
            this.sleep(500);
            if (agenciasList.removeIf(x -> x.getNumero().equals(nAgencia))) {
                endPoint.println("Agencia de Numero: " + nAgencia + "removida com sucesso");
            } else {
                endPoint.println("Erro ao remover a agencia de numero: " + nAgencia);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteConta(PrintStream endPoint, BufferedReader startingPoint) throws IOException, InterruptedException {
        try {

            endPoint.println("Informe o numero da conta");
            String nConta = startingPoint.readLine().trim();
            this.sleep(500);
            if (contasList.removeIf(x -> x.getNumeroConta().equals(nConta))) {
                endPoint.println("Conta de Numero: " + nConta + " Removida com sucesso\n\n\n");
                clientesList.forEach(cli -> {
                    if (!cli.getAdmin()) {
                        if (cli.getContas().removeIf(x -> x.getNumeroConta().equals(nConta))) {
                            cli.getEndPoint().println("Sua Conta de numero: " + nConta + " foi removida \n\n\n\n\n");
                        }
                    }
                });
            } else {
                endPoint.println("Erro ao remover a conta de numero: " + nConta + "\n\n\n\n\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addConta(PrintStream endPoint, BufferedReader startingPoint) throws IOException, InterruptedException {
        try {
            Conta c = new Conta();
            endPoint.println("Informe o numero da conta");
            c.setNumeroConta(startingPoint.readLine().trim());
            endPoint.println();
            endPoint.println("Informe o Nome do cliente");
            c.setNomeCliente(startingPoint.readLine().trim());
            endPoint.println();
            endPoint.println("Informe o Cpf do cliente");
            c.setCpf(startingPoint.readLine().trim());
            c.setSaldo(0);
            endPoint.println();
            endPoint.println("Informe o numero da agencia");
            String aux = startingPoint.readLine();
            agenciasList.forEach(ag -> {
                if (ag.getNumero().equals(aux)) {
                    c.setAgencia(ag);
                }
            });

            Server.sleep(500);
            boolean contaCriada = false;
            clientesList.forEach((x) -> {
                if (x.getName().equals(c.getNomeCliente())) {
                    if (x.getContas().size() == 3) {
                        endPoint.println("O Cliente " + x.getName() + " ja possui 3 contas vinculadas a ele por isso não é possivel criar essa conta\n\n\n\n");
                    } else {
                        x.getEndPoint().println("Conta de numero " + c.getNumeroConta() + "Vinculada ao cliente " + x.getName());
                        x.getContas().add(c);
                        contasList.add(c);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void extrado(PrintStream endPoint, BufferedReader startingPoint) throws IOException{
        endPoint.println("Informe o numero da conta");
        String nConta = startingPoint.readLine().trim();
       
        this.cliente.getContas().forEach(x->{
            if(x.getNumeroConta().equals(nConta)){
                 endPoint.println("Numero da Conta: " + x.getNumeroConta() + "\nNumero da Agencia: " + x.getAgencia().getNumero()
                        + "\n Nome do Cliente: " + x.getNomeCliente() + "\n Saldo: "+x.getSaldo()+"\n-------------------");
        
            }
               
        });
        
    }

    public void listaAgencia(PrintStream endPoint) {

        if (agenciasList.size() == 0) {
            endPoint.println("Nenhuma Agencia Cadastrada");
        } else {
            agenciasList.forEach(x -> {
                endPoint.println("Numero Agencia: " + x.getNumero() + "\nDescrição: " + x.getDescricao() + "\n---------------");
            });
        }
    }

    public void listaContas(PrintStream endPoint) {
        if (contasList.size() == 0) {
            endPoint.println("Nenhuma Conta Bancaria Cadastrada");
        } else {
            contasList.forEach(x -> {
                endPoint.println("Numero da Conta: " + x.getNumeroConta() + "\nNumero da Agencia: " + x.getAgencia().getNumero()
                        + "\n Nome do Cliente: " + x.getNomeCliente() + "\n-------------------");
            });
        }

    }

    public void depositar(PrintStream endPoint, BufferedReader startingPoint) throws IOException, InterruptedException {
        if (this.cliente.getContas().size() == 0) {
            endPoint.println("Voce nao possui contas para realizar o deposito aguarde o admin vincular uma conta\n\n\n");
        } else {
            endPoint.println("Informe o numero da conta");
            String nConta = startingPoint.readLine().trim();
            endPoint.println("Informe quando deseja depositar");
            int valDepositar = Integer.parseInt(startingPoint.readLine().trim());
            this.sleep(500);
             this.cliente.getContas().forEach(x->{
            if(x.getNumeroConta().equals(nConta)){
                int aux = x.getSaldo() + valDepositar;
                x.setSaldo(aux);
                 endPoint.println("Depositado valor de: "+valDepositar+" na conte de numero: "+x.getNumeroConta()+"\n\n\n\n");
        
            }
               
        });
        }

    }

    // Função Main
    public static void main(String[] args) {

        // Inicializa variaveis statics
        // players = new ArrayList<>();
        clientesList = Collections.synchronizedList(new ArrayList<>());
        agenciasList = Collections.synchronizedList(new ArrayList<>());
        contasList = Collections.synchronizedList(new ArrayList<>());
        clienteAdminList = Collections.synchronizedList(new ArrayList<>());
        end = false;
        start = false;
        i = 0;

        try {
            // Inicialzia o socket na porta desejada
            ServerSocket s = new ServerSocket(2222);
            while (true) {

                // Mostra se o token esta definido com um operador ternario
                System.out.println("Esperando conexão...\nIP: 127.0.0.1\nPorta: 2222");
                Socket connection = s.accept();

                // Novo player do client
                Cliente player = new Cliente();
                if (!adminLogado) {

                    player.setAdmin(true);
                    adminLogado = true;
                }
                player.setSocket(connection);

                clientesList.add(player);
                i++;
                System.out.println("Conectou!: " + connection.getRemoteSocketAddress());

                // Cria a thread do client
                Thread t = new Server(player);
                t.start();

            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public static List<Cliente> getClientes() {
        return clientesList;
    }

    public static void setClientes(List<Cliente> aClientes) {
        clientesList = aClientes;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia aAgencia) {
        agencia = aAgencia;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta aConta) {
        conta = aConta;
    }
}
