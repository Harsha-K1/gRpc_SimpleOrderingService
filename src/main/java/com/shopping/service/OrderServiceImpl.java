package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private OrderDao orderDao = new OrderDao();

    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        logger.info("Got orders from OrderDao and converting to OrderReponse proto objects.");
        List<Order> orders = orderDao.getOrders(request.getUserId());

        // Below is to map the DB Order object to the proto Order object
        List<com.shopping.stubs.order.Order> userOrders = orders.stream().map(order -> com.shopping.stubs.order.Order.newBuilder()
                .setUserId(order.getUserId())
                .setOrderId(order.getOrderId())
                .setNoOfItems(order.getNumOfItems())
                .setTotalAmount(order.getTotalAmt())
                .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
                .build()).collect(Collectors.toList());

        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(userOrders).build();
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();

    }
}
