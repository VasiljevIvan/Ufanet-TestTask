package ru.vasiljev.UfanetTestTask.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vasiljev.UfanetTestTask.models.Order;
import ru.vasiljev.UfanetTestTask.models.events.OrderEvent;
import ru.vasiljev.UfanetTestTask.models.events.RegisterOrderEvent;
import ru.vasiljev.UfanetTestTask.repositories.CustomersRepository;
import ru.vasiljev.UfanetTestTask.repositories.EmployeesRepository;
import ru.vasiljev.UfanetTestTask.repositories.OrderEventsRepository;
import ru.vasiljev.UfanetTestTask.repositories.OrdersRepository;

import java.util.Optional;

import static ru.vasiljev.UfanetTestTask.Constants.*;

@Service
@Transactional(readOnly = true)
public class OrderEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);
    private final EmployeesRepository employeesRepository;
    private final CustomersRepository customersRepository;
    private final OrderEventsRepository orderEventsRepository;
    private final OrdersRepository ordersRepository;

    public OrderEventHandler(EmployeesRepository employeesRepository, CustomersRepository customersRepository,
                             OrderEventsRepository orderEventsRepository, OrdersRepository ordersRepository) {
        this.employeesRepository = employeesRepository;
        this.customersRepository = customersRepository;
        this.orderEventsRepository = orderEventsRepository;
        this.ordersRepository = ordersRepository;
    }

    @Transactional
    public void handleOrderEvent(OrderEvent event) {
        checkEmployeeExists(event.getEmployee().getId());
        if (event.getEventType().equals(REGISTER)) {
            checkCustomerExists(((RegisterOrderEvent) event).getCustomer().getId());
            saveOrderEventAndOrder(event);
        } else {
            Order order = checkOrderExistsAndGet(event.getOrder().getId());
            switch (event.getEventType()) {
                case CANCEL -> {
                    if (!order.getStatus().equals(CANCEL) && !order.getStatus().equals(ISSUED))
                        saveOrderEventAndOrder(event);
                    else if (order.getStatus().equals(CANCEL)) throw new RuntimeException("Заказ уже отменен");
                    else if (order.getStatus().equals(ISSUED)) throw new RuntimeException("Заказ уже выдан");
                }
                case TAKEN_TO_WORK -> {
                    switch (order.getStatus()) {
                        case REGISTER -> saveOrderEventAndOrder(event);
                        case CANCEL -> throw new RuntimeException("Заказ уже отменен");
                        case ISSUED -> throw new RuntimeException("Заказ уже выдан");
                        default -> throw new RuntimeException("Заказ должен иметь статус \'Зарегистрирован\'");
                    }
                }
                case READY -> {
                    switch (order.getStatus()) {
                        case TAKEN_TO_WORK -> saveOrderEventAndOrder(event);
                        case CANCEL -> throw new RuntimeException("Заказ уже отменен");
                        case ISSUED -> throw new RuntimeException("Заказ уже выдан");
                        default -> throw new RuntimeException("Заказ должен иметь статус \'Взят в работу\'");
                    }
                }
                case ISSUED -> {
                    switch (order.getStatus()) {
                        case READY -> saveOrderEventAndOrder(event);
                        case CANCEL -> throw new RuntimeException("Заказ уже отменен");
                        case ISSUED -> throw new RuntimeException("Заказ уже выдан");
                        default -> throw new RuntimeException("Заказ должен иметь статус \'Готов к выдаче\'");
                    }
                }
            }
        }
    }

    public void checkEmployeeExists(int id) {
        if (employeesRepository.findById(id).isEmpty())
            throw new RuntimeException("Сотрудник с таким id не найден");
    }

    public void checkCustomerExists(int id) {
        if (customersRepository.findById(id).isEmpty())
            throw new RuntimeException("Покупатель с таким id не найден");
    }

    public Order checkOrderExistsAndGet(int id) {
        Optional<Order> optionalOrder = ordersRepository.findById(id);
        if (optionalOrder.isEmpty())
            throw new RuntimeException("Заказ с таким id не найден");
        else return optionalOrder.get();
    }

    @Transactional
    public void saveOrderEventAndOrder(OrderEvent event) {
        ordersRepository.save(event.getOrder());
        orderEventsRepository.save(event);
        switch (event.getEventType()) {
            case REGISTER -> logger.info(String.format("Заказ №%d создан", event.getOrder().getId()));
            case CANCEL -> logger.info(String.format("Заказ №%d отменен", event.getOrder().getId()));
            case TAKEN_TO_WORK -> logger.info(String.format("Заказ №%d взят в работу", event.getOrder().getId()));
            case READY -> logger.info(String.format("Заказ №%d готов", event.getOrder().getId()));
            case ISSUED -> logger.info(String.format("Заказ №%d выдан", event.getOrder().getId()));
        }
    }
}
