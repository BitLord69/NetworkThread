package com.janinc;

import com.janinc.server.NetworkServer;

public class Main {

    public static void main(String[] args) {
        NetworkServer ns = NetworkServer.get();
        Thread serverThread = new Thread(ns, "serverThread");
        serverThread.start();


    } // main
} // class Main
