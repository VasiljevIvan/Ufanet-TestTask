package ru.vasiljev.UfanetTestTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vasiljev.UfanetTestTask.models.events.OrderEvent;

public interface OrderEventsRepository extends JpaRepository<OrderEvent, Integer> {

}
