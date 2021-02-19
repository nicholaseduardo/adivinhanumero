/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifms.advinhanumero;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author santos
 */
public class Server {

    private ServerSocket serverSocket;
    private Cliente jogador1;
    private Cliente jogador2;

    private Integer numero;
    private Integer chute;
    private Integer tentativas;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(12345);
        this.tentativas = 0;

        System.out.printf("Servidor ativo na porta %d\n", 12345);
        System.out.printf("IP do servidor: %s\n", InetAddress.getLocalHost().getHostAddress());
        
        waitForResponse();
    }

    private String lerComando(String comando, String data) {
        String[] dados = data.split("=");
        if (comando.equals(dados[0])) {
            return dados[1];
        }
        return null;
    }

    private void waitForResponse() throws IOException {
        ServerRunnable r = new ServerRunnable();
        Thread t = new Thread(r);
        t.start();
    }

    private void executar() {
        do {
            try {
                Socket socket = this.serverSocket.accept();
                registrarJogador(socket);

                String hostAddress = socket.getInetAddress().getHostAddress();
                System.out.printf("[Conexão estabelecida com]: %s\n", hostAddress);
            } catch (IOException | InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        } while(jogador1 == null || jogador2 == null);
        
        jogar();
    }

    private void jogar() {
        try {
            PrintStream ps1 = new PrintStream(jogador1.getSocket().getOutputStream());
            ps1.println("cmd_informarNumero");

            Scanner sc = new Scanner(jogador1.getSocket().getInputStream());
            String sNumero = lerComando("numero", sc.nextLine());
            this.numero = Integer.parseInt(sNumero);

            PrintStream ps2 = new PrintStream(jogador2.getSocket().getOutputStream());
            ps2.println("cmd_chutar");

            Scanner scChute = new Scanner(jogador2.getSocket().getInputStream());
            while (scChute.hasNext()) {
                String sChute = lerComando("chute", scChute.nextLine());
                this.chute = Integer.parseInt(sChute);
                this.tentativas++;

                ps1.printf("O jogador %s chutou o número %d.\n",
                        jogador2.getNome(), this.chute);

                if (this.chute > this.numero) {
                    ps2.println("Você chutou Alto!\nTente novamente.\n");
                    ps2.println("cmd_chutar");
                } else if (this.chute < this.numero) {
                    ps2.println("Você chutou Baixo!\nTente novamente.\n");
                    ps2.println("cmd_chutar");
                } else {
                    break;
                }
            }

            ps1.printf("O jogador %s acertou o número %d após %d tentativas\n",
                    jogador2.getNome(), this.numero, this.tentativas);
            ps2.printf("Parabéns, você acertou o número informado por %s com %d tentativas.\n",
                    jogador1.getNome(), this.tentativas);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    private void registrarJogador(Socket socket) throws IOException, InterruptedException {
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.println("cmd_nomeJogador");

        Scanner sc = new Scanner(socket.getInputStream());
        String nome = lerComando("nome", sc.nextLine());

        Cliente cliente = new Cliente(socket, nome);
        ps.println("Jogador adicionado no Servidor!");
        int size = 1;
        if (jogador1 == null) {
            jogador1 = cliente;

            System.out.printf("Jogador 1: %s adicionado.\n", nome);
            ps.println("Aguardando o outro jogador!");
        } else {
            jogador2 = cliente;
            System.out.printf("Jogador 2: %s adicionado.\n", nome);
            
            ps.printf("Aguardando o Jogador %s informar o número a ser descoberto!\n",
                    jogador1.getNome());

            PrintStream ps1 = new PrintStream(jogador1.getSocket().getOutputStream());
            ps1.printf("Jogador %s conectado ao jogo.\n", cliente.getNome());
        }
    }
    
    private void encerrarJogo() {
        try {
            jogador1.getSocket().close();
            jogador2.getSocket().close();
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ServerRunnable implements Runnable {

        @Override
        public void run() {
            executar();
            encerrarJogo();
        }

    }

}
