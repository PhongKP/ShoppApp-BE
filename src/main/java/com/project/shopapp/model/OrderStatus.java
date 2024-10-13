package com.project.shopapp.model;

public enum     OrderStatus {

    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED;

    public static OrderStatus fromString(String status) throws IllegalArgumentException {
        switch (status.toLowerCase()) {
            case "pending":
                return PENDING;
            case "processing":
                return PROCESSING;
            case "shipped":
                return SHIPPED;
            case "delivered":
                return DELIVERED;
            case "cancelled":
                return CANCELLED;
            default:
                return null;
        }
    }

}
