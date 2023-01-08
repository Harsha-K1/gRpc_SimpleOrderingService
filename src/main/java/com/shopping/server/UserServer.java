package com.shopping.server;

import com.shopping.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServer {

    private static final Logger logger = Logger.getLogger(UserServer.class.getName());
    private Server server;

    public void startServer() {
        int port = 50051;

        try{
            server = ServerBuilder.forPort(port)
                    .addService(new UserServiceImpl())
                    .build()
                    .start();
            logger.info("Server started on port: "+port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Clean server shutdown in case JVM shuts down.");
                try{
                    UserServer.this.stopServer();
                } catch (InterruptedException ex){
                    logger.log(Level.SEVERE, "Server shutdown", ex);
                }
            }));
        } catch (IOException ex){
            logger.log(Level.SEVERE, "Server did not start!", ex);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UserServer userServer = new UserServer();
        userServer.startServer();
        userServer.blockUntilShutDown();
    }

    public void stopServer() throws InterruptedException {
        if(server != null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutDown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }

}
