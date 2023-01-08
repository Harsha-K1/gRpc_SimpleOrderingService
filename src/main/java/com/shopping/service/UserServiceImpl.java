package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends com.shopping.stubs.user.UserServiceGrpc.UserServiceImplBase {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    UserDao userDao = new UserDao();
    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {

        User user = userDao.getDetails(request.getUsername());
        UserResponse.Builder userRespBuilder =
                UserResponse.newBuilder()
                .setAge(user.getAge())
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setGender(Gender.valueOf(user.getGender()));

        //get orders by invoking the Order client
        List<Order> orders = getOrders(userRespBuilder);
        userRespBuilder.setNoOfOrders(orders.size());
        UserResponse userResponse = userRespBuilder.build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(UserResponse.Builder userRespBuilder) {
        logger.info("Creating a channel and calling the OrderClient.");
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                .usePlaintext()
                .build();
        OrderClient orderClient = new OrderClient(channel);
        List<Order> orders = orderClient.getOrders(userRespBuilder.getId());

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Channel did not shutdown!", ex);
        }
        return orders;
    }

}
