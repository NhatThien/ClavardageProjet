package com.insa.network.handler;

import com.insa.Message.Message;
import com.insa.Message.MessageType;
import com.insa.model.Peer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPSenderHandler implements Runnable {

    private Socket chatSocket;
    private PrintWriter writer;
    private byte [] message;

    public TCPSenderHandler(Peer peer, byte [] message, MessageType type) throws IOException {

        String cString = new String(message);

        byte[] c = Message.buildMessage(type.getTypeAsString(), cString);

        this.chatSocket = new Socket (peer.getHost(), peer.getPort());
        this.writer = new PrintWriter(chatSocket.getOutputStream());
        this.message = c;
    }

    @Override
    public void run() {
        try {
            writer.println(message);
            writer.close();
            chatSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
