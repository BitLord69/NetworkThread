package com.janinc;
import com.janinc.server.NetworkServer;

public class Main {

    public static void main(String[] args) {
        NetworkServer ns = NetworkServer.get();
        Thread serverThread = new Thread(ns, "serverThread");
        serverThread.start();

 //       Client c = new Client();
//       Thread inputThread = new Thread(c, "Network Input");
//       inputThread.start();
    } // main
} // class Main
