package br.edu.ifms.advinhanumero;

import java.io.IOException;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author santos
 */
public class Main {

    public static Integer menu() {
        System.out.println("\n\n1. Iniciar um servidor");
        System.out.println("2. Conectar ao servidor do jogo");
        System.out.println("3. Sair do jogo");
        System.out.println("\nDigite uma opção: ");

        Scanner sc = new Scanner(System.in);
        Integer op = sc.nextInt();
        return op;
    }

    public static void main(String args[]) {
        System.out.println("Bem-vindo ao Jogo do Adivinha o Número");
        System.out.println("\nPara jogar, escolha uma das opções abaixo\n");

        Integer op = menu();
        try {
            switch (op) {
                case 1:
                    Server server = new Server();

                    break;
                case 2:
                    Cliente cliente = new Cliente();

                    break;
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
