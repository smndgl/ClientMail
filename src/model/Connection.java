package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidParameterException;

public class Connection {
    private static final int PORT = 8189;
    private Socket socket = null;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    public void connect() throws IOException {
        try {
            InetAddress address = InetAddress.getByName(null); //localhost
            socket = new Socket(address, PORT);
            System.out.println("client socket: " + socket);

            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
        } catch (ConnectException e) {
            System.err.println("Cannot connect: "+ e.getMessage()); // looop
        }
    }

    public boolean isConnected() {
        boolean connected = (socket != null) && (socket.isConnected()) && !(socket.isClosed());
        while(!connected) {
            try {
                this.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Connection status : "+ connected);

        return connected;
    }

    public void close() {
        System.out.println("Closing client");
        try {
            if (objectOut != null) objectOut.close();
            if (objectIn != null) objectIn.close();
            if (socket != null) socket.close();
            objectOut = null;
            objectIn = null;
        } catch (IOException e) {
            System.err.println("Disconnection exception: " + e.getMessage());
        }
    }

    public Message getMessage() {
        if (this.isConnected()) {
            try {
                Object res = objectIn.readObject();
                if (res instanceof Message) {
                    return (Message) res;
                }
            } catch (IOException | ClassNotFoundException e) {
                if(e instanceof IOException)
                    System.out.println("Connection closed");
                else
                    ((ClassNotFoundException) e).printStackTrace();
                this.close();
            }
        }
        return null;
    }

    public void login(String username) throws IOException{
        if(this.isConnected()) {
            objectOut.writeObject(new Message<>(MessageType.login, username));
        }
    }

    public void logout(String username) throws IOException {
        if(this.isConnected()) {
            objectOut.writeObject(new Message<>(MessageType.logout, username));
        }
    }

    public void fetchMailbox(String FILTER) throws IOException {
        if(this.isConnected()) {
            objectOut.writeObject(new Message<>(MessageType.fetch, FILTER));
        }
    }

    public void send(Email email) throws IOException {
        if(this.isConnected()) {
            objectOut.writeObject(new Message<>(MessageType.send, email));
        }
    }

    public void delete(Email email, String mailbox) throws IOException {
        if(this.isConnected()) {
            if(mailbox.equals("inbox"))
                objectOut.writeObject(new Message<>(MessageType.delete_i, email));
            else if(mailbox.equals("sent"))
                objectOut.writeObject(new Message<>(MessageType.delete_s, email));
            else
                System.err.println("HOW ?!");
        }
    }
}
