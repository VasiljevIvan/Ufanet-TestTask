package ru.vasiljev.UfanetTestTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vasiljev.UfanetTestTask.models.Order;

public interface OrdersRepository extends JpaRepository<Order, Integer> {
    @Query(value = "select nextval('order_id_seq')", nativeQuery = true)
    int nextOrderId();
}
