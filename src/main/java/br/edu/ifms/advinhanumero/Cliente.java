/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifms.advinhanumero;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author santos
 */
public class Cliente {

    private String nome;
    private Socket socket;

    public Cliente() throws IOException {
        this.inicializar();
    }

    public Cliente(Socket socket, String nome) throws IOException {
        this.socket = socket;
        this.nome = nome;
    }

    private void inicializar() throws IOException {
        System.out.println("Para jogar online, informe o IP e a PORTA"
                + "do Servidor");
        String ip;
        int porta;
        Scanner sc = new Scanner(System.in);
        System.out.println("IP/HostName: ");
        ip = sc.next();
//        ip = "127.0.1.1";
        System.out.println("PORTA: ");
        porta = sc.nextInt();
//        porta = 12345;

        System.out.println("Informe seu nome de jogador: ");
        this.nome = sc.next();

        this.socket = new Socket(ip, porta);
        System.out.printf("Conexão estabelecida com o host %s:%d\n",
                ip, porta);

        this.waitForResponse();
    }

    private void registrarSocket() throws IOException {
        PrintStream ps = new PrintStream(this.socket.getOutputStream());
        ps.println("nome=" + this.nome);
    }

    private void waitForResponse() throws IOException {
        ClienteRunnable r = new ClienteRunnable(this);
        Thread t = new Thread(r);
        t.start();
    }
    
    private void informarNumero() throws IOException {
        System.out.println("Informe o número a ser descoberto: ");
        Scanner sc = new Scanner(System.in);
        Integer numero = sc.nextInt();
        
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.printf("numero=%d\n", numero);
    }
    
    private void chutar() throws IOException {
        System.out.println("Chute um número: ");
        Scanner sc = new Scanner(System.in);
        Integer chute = sc.nextInt();
        
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.printf("chute=%d\n", chute);
    }

    private void executaComando(Cliente cliente) {
        try {
            Scanner s = new Scanner(cliente.getSocket().getInputStream());
            while (s.hasNext()) {
                String data = s.nextLine();
                if (data.contains("cmd_")) {
                    switch (data) {
                        case "cmd_nomeJogador":
                            registrarSocket();
                            break;
                        case "cmd_informarNumero":
                            informarNumero();
                            break;
                        case "cmd_chutar":
                            chutar();
                            break;
                    }
                } else {
                    System.out.println(data);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getNome() {
        return nome;
    }

    private class ClienteRunnable implements Runnable {

        private Cliente cliente;

        public ClienteRunnable(Cliente cliente) {
            this.cliente = cliente;
        }

        @Override
        public void run() {
            cliente.executaComando(cliente);
        }

    }
}
