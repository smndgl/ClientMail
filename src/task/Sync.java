package task;

import model.Connection;
import model.DataModel;
import model.Message;

import java.net.Socket;

public class Sync implements Runnable{
    private final Connection connection;
    private final DataModel model;

    public Sync(Connection socket, DataModel model) {
        this.model = model;
        this.connection = model.getConnectionInstance();
    }

    @Override
    public void run() {
        while(connection.isConnected()) {
            try {

                Thread.sleep(5000);

            }
            catch (InterruptedException e) {
                System.err.println(Thread.currentThread().getName()+" Error: "+ e.getMessage());
            }
        }
    }
}
