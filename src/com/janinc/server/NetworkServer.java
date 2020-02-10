package com.janinc.server;

import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkServer implements Runnable {
    private static NetworkServer mInstance;

    public final static String SPLIT_CHARACTER = "::";
    public static final int PORT = 80;
    public static final int MSG_SIZE = 512;

    public static final int USERNAME = 0;
    public static final int MESSAGE = 1;

    private static final String RECEIVED_MESSAGE = "Server" + SPLIT_CHARACTER + "Message received!";

    private final int SLEEP_MS = 100;

    private DatagramSocket socket;
    private ConcurrentHashMap<InetSocketAddress, String> userList = new ConcurrentHashMap<InetSocketAddress, String>();

    private boolean isRunning = true;

    private NetworkServer() {
        System.out.println("I början på NetworkServers konstruktor...");

        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(SLEEP_MS);
        } catch (SocketException e) {
            System.out.println("NetworkServers konstruktor: " + e.getMessage());
        } // catch

        System.out.println("I slutet på NetworkServers konstruktor...");
    } // NetworkServer

    public static NetworkServer get() {
        if (mInstance == null) {
            System.out.println("I NetworkServer.get -> skapar instans!");
            mInstance = new NetworkServer();
        }
        return mInstance;
    } // get

    public void sendMsgToClient(String msg, SocketAddress clientSocketAddress) {
        byte[] buffer = msg.getBytes();

        DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientSocketAddress);

        try {
            socket.send(response);
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    } // sendMsgToClient

    @Override
    public void run() {
        while (isRunning) {
            DatagramPacket clientRequest = new DatagramPacket(new byte[MSG_SIZE], MSG_SIZE);

            if (!receiveMsgFromAnyClient(clientRequest)) {
                continue;
            } // if !receiveMsgFromAnyClient...

            String clientMsg = new String(clientRequest.getData(), 0, clientRequest.getLength());
            System.out.println("NetworkServer.run - Message received! '" + clientMsg + "'");

            String[] msgParts = clientMsg.split(SPLIT_CHARACTER);

            userList.put(new InetSocketAddress(clientRequest.getAddress(), clientRequest.getPort()), msgParts[USERNAME]);
            userList.
                    entrySet().
                    stream().
                    filter(e -> !e.getValue().equals(msgParts[USERNAME])).
                    forEach(e -> sendMsgToClient(clientMsg, e.getKey()));

            sendMsgToClient(RECEIVED_MESSAGE, new InetSocketAddress(clientRequest.getAddress(), clientRequest.getPort()));
            // TODO: Save the msg to a queue instead
        } // while...
    } // run

    private boolean receiveMsgFromAnyClient(DatagramPacket clientRequest) {
        try {
            socket.receive(clientRequest);
        } catch (Exception ex) {
            return false;
        } // catch
        return true;
    } // receiveMsgFromAnyClient
} // class NetworkServer