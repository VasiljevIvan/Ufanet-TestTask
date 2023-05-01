package ru.vasiljev.UfanetTestTask.services;

import ru.vasiljev.UfanetTestTask.models.Order;
import ru.vasiljev.UfanetTestTask.models.events.OrderEvent;

public interface OrderService {
    void publishEvent(OrderEvent event);
    Order findOrder(int id);
}
