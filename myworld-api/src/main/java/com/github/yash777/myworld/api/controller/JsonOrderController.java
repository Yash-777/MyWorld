package com.github.yash777.myworld.api.controller;

import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/orders")
public class JsonOrderController {

    private static final List<Order> orders = new ArrayList<>();
    private static final List<com.github.yash777.myworld.api.controller.JsonOrderController.Order.DeliveryAgent> deliveryAgents = List.of(
            new com.github.yash777.myworld.api.controller.JsonOrderController.Order.DeliveryAgent("John Doe", "9990001111", "AGENT-1"),
            new com.github.yash777.myworld.api.controller.JsonOrderController.Order.DeliveryAgent("Emily Smith", "9990002222", "AGENT-2")
    );
    private static final AtomicInteger orderCounter = new AtomicInteger(1);

    // Place an order
    @PostMapping("/place")
    public Order placeOrder(@RequestBody PlaceOrderRequest request) {
        List<Order.SubOrderItem> subOrders = new ArrayList<>();
        int totalPointsEarned = 0;

        for (PlaceOrderRequest.Item item : request.getItems()) {
            double offerPrice = item.getQuantity() * item.getPrice();
            int itemPoints = (int) (offerPrice / 30);

            Order.SubOrderItem subOrder = new Order.SubOrderItem(
                    UUID.randomUUID().toString(),
                    new Order.ProductDetails(item.getProductId(), item.getModel(), item.getCategory(), item.getQuantity()),
                    item.getPrice(),
                    item.getPrice(),
                    Order.OrderStatus.CONFIRMED,
                    itemPoints,
                    0,
                    assignAgent(),
                    new Timestamp(System.currentTimeMillis()),
                    null, null, null
            );

            totalPointsEarned += itemPoints;
            subOrders.add(subOrder);
        }

        String orderId = "ORD-" + orderCounter.getAndIncrement();

        Order order = new Order(
                orderId,
                request.getUserId(),
                request.getUserShortName(),
                new Timestamp(System.currentTimeMillis()),
                Order.OrderStatus.CONFIRMED,
                Order.PaymentStatus.PAID,
                request.getItems().stream().mapToDouble(i -> i.getQuantity() * i.getPrice()).sum(),
                request.getPointsUsed(),
                totalPointsEarned,
                totalPointsEarned,
                subOrders,
                new Order.DeliveryTimeline(new Timestamp(System.currentTimeMillis()), null, null, null),
                "PAID"
        );

        orders.add(order);
        return order;
    }

    // Cancel a sub-order item by ID
    @PostMapping("/{orderId}/cancel/{subOrderId}")
    public Order cancelItem(@PathVariable String orderId, @PathVariable String subOrderId) {
        Order order = findOrderById(orderId);

        for (Order.SubOrderItem item : order.getSubOrders()) {
            if (item.getUniqueId().equals(subOrderId) && item.getStatus() != Order.OrderStatus.CANCELED) {
                item.setStatus(Order.OrderStatus.CANCELED);
                item.setPointsDeductedOnReturn(item.getPointsEarned());
                order.setTotalPointsEarned(order.getTotalPointsEarned() - item.getPointsEarned());
                break;
            }
        }

        return order;
    }

    // List all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orders;
    }

    // Helper methods
    private Order findOrderById(String orderId) {
        return orders.stream().filter(o -> o.getOrderId().equals(orderId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    private Order.DeliveryAgent assignAgent() {
        return deliveryAgents.get(new Random().nextInt(deliveryAgents.size()));
    }

    // --- Models ---

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class PlaceOrderRequest {
        private String userId;
        private String userShortName;
        private int pointsUsed;
        private List<Item> items;

        @Data @AllArgsConstructor @NoArgsConstructor
        public static class Item {
            private String productId;
            private String model;
            private String category;
            private double price;
            private int quantity;
        }
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Order {
        private String orderId;
        private String userId;
        private String userShortName;
        private Timestamp orderDate;
        private OrderStatus orderStatus;
        private PaymentStatus paymentStatus;
        private double paymentAmount;
        private int pointsUsed;
        private int pointsEarned;
        private int totalPointsEarned;
        private List<SubOrderItem> subOrders;
        private DeliveryTimeline deliveryTimeline;
        private String amountPaidStatus;

        @Data @AllArgsConstructor @NoArgsConstructor
        public static class SubOrderItem {
            private String uniqueId;
            private ProductDetails productDetails;
            private double offerPrice;
            private double actualPrice;
            private OrderStatus status;
            private int pointsEarned;
            private int pointsDeductedOnReturn;
            private DeliveryAgent deliveryAgent;
            private Timestamp pickedOn;
            private Timestamp deliveredOn;
            private Timestamp returnedOn;
            private Timestamp shippedOn;
        }

        @Data @AllArgsConstructor @NoArgsConstructor
        public static class ProductDetails {
            private String productId;
            private String model;
            private String category;
            private int quantity;
        }

        @Data @AllArgsConstructor @NoArgsConstructor
        public static class DeliveryTimeline {
            private Timestamp placedAt;
            private Timestamp pickedAt;
            private Timestamp shippedAt;
            private Timestamp deliveredAt;
        }

        @Data @AllArgsConstructor @NoArgsConstructor
        public static class DeliveryAgent {
            private String name;
            private String phoneNumber;
            private String agentId;
        }

        public enum OrderStatus {
            CONFIRMED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, RETURNED, CANCELED
        }

        public enum PaymentStatus {
            COD, PAID, REFUNDED
        }
    }
}

