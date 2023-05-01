package ru.vasiljev.UfanetTestTask.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.vasiljev.UfanetTestTask.dto.OrderEventDTO;
import ru.vasiljev.UfanetTestTask.models.Customer;
import ru.vasiljev.UfanetTestTask.models.Employee;
import ru.vasiljev.UfanetTestTask.models.Order;
import ru.vasiljev.UfanetTestTask.models.Product;
import ru.vasiljev.UfanetTestTask.models.events.*;
import ru.vasiljev.UfanetTestTask.repositories.OrdersRepository;
import ru.vasiljev.UfanetTestTask.repositories.ProductsRepository;

import java.time.LocalDateTime;

import static ru.vasiljev.UfanetTestTask.Constants.*;

@Service
@Transactional(readOnly = true)
public class OrderEventCreator {
    private final ProductsRepository productsRepository;
    private final OrdersRepository ordersRepository;

    public OrderEventCreator(ProductsRepository productsRepository, OrdersRepository ordersRepository) {
        this.productsRepository = productsRepository;
        this.ordersRepository = ordersRepository;
    }

    @Transactional
    public RegisterOrderEvent createRegisterOrderEvent(OrderEventDTO orderEventDTO) {
        Integer orderId = orderEventDTO.getOrderId();
        Integer employeeId = orderEventDTO.getEmployeeId();
        Integer customerId = orderEventDTO.getCustomerId();
        Integer productId = orderEventDTO.getProductId();
        String cancelReason = orderEventDTO.getCancelReason();
        if (orderId != null || employeeId == null || customerId == null || productId == null || cancelReason != null)
            throw new RuntimeException("Неверный запрос регистрации заказа");
        LocalDateTime createdAt = LocalDateTime.now();
        Employee employee = new Employee(orderEventDTO.getEmployeeId());
        Customer customer = new Customer(orderEventDTO.getCustomerId());
        Product product = productsRepository.findById(orderEventDTO.getProductId()).orElseThrow(RuntimeException::new);
        LocalDateTime expectedCompletionTime = calculateExpectedCompletionTime(createdAt, product);
        Order order = new Order(ordersRepository.nextOrderId());
        order.setStatus(REGISTER);
        return RegisterOrderEvent.builder()
                .eventType(REGISTER)
                .createdAt(createdAt)
                .order(order)
                .employee(employee)
                .customer(customer)
                .product(product)
                .productPrice(product.getPrice())
                .expectedCompletionTime(expectedCompletionTime).build();
    }

    public CancelOrderEvent createCancelOrderEvent(OrderEventDTO orderEventDTO) {
        Integer orderId = orderEventDTO.getOrderId();
        Integer employeeId = orderEventDTO.getEmployeeId();
        Integer customerId = orderEventDTO.getCustomerId();
        Integer productId = orderEventDTO.getProductId();
        String cancelReason = orderEventDTO.getCancelReason();
        if (orderId == null || employeeId == null || customerId != null || productId != null || cancelReason == null)
            throw new RuntimeException("Неверный запрос отмены заказа");
        LocalDateTime createdAt = LocalDateTime.now();
        Employee employee = new Employee(orderEventDTO.getEmployeeId());
        Order order = new Order(orderEventDTO.getOrderId());
        order.setStatus(CANCEL);
        return CancelOrderEvent.builder()
                .eventType(CANCEL)
                .order(order)
                .employee(employee)
                .cancelReason(orderEventDTO.getCancelReason())
                .createdAt(createdAt).build();
    }

    public TakenToWorkOrderEvent createTakenToWorkOrderEvent(OrderEventDTO orderEventDTO) {
        Integer orderId = orderEventDTO.getOrderId();
        Integer employeeId = orderEventDTO.getEmployeeId();
        Integer customerId = orderEventDTO.getCustomerId();
        Integer productId = orderEventDTO.getProductId();
        String cancelReason = orderEventDTO.getCancelReason();
        if (orderId == null || employeeId == null || customerId != null || productId != null || cancelReason != null)
            throw new RuntimeException("Неверный запрос передачи в работу заказа");
        LocalDateTime createdAt = LocalDateTime.now();
        Employee employee = new Employee(orderEventDTO.getEmployeeId());
        Order order = new Order(orderEventDTO.getOrderId());
        order.setStatus(TAKEN_TO_WORK);
        return TakenToWorkOrderEvent.builder()
                .eventType(TAKEN_TO_WORK)
                .order(order)
                .employee(employee)
                .createdAt(createdAt).build();
    }

    public ReadyOrderEvent createReadyOrderEvent(OrderEventDTO orderEventDTO) {
        Integer orderId = orderEventDTO.getOrderId();
        Integer employeeId = orderEventDTO.getEmployeeId();
        Integer customerId = orderEventDTO.getCustomerId();
        Integer productId = orderEventDTO.getProductId();
        String cancelReason = orderEventDTO.getCancelReason();
        if (orderId == null || employeeId == null || customerId != null || productId != null || cancelReason != null)
            throw new RuntimeException("Неверный запрос готовности заказа");
        LocalDateTime createdAt = LocalDateTime.now();
        Employee employee = new Employee(orderEventDTO.getEmployeeId());
        Order order = new Order(orderEventDTO.getOrderId());
        order.setStatus(READY);
        return ReadyOrderEvent.builder()
                .eventType(READY)
                .order(order)
                .employee(employee)
                .createdAt(createdAt).build();
    }

    public IssuedOrderEvent createIssuedOrderEvent(OrderEventDTO orderEventDTO) {
        Integer orderId = orderEventDTO.getOrderId();
        Integer employeeId = orderEventDTO.getEmployeeId();
        Integer customerId = orderEventDTO.getCustomerId();
        Integer productId = orderEventDTO.getProductId();
        String cancelReason = orderEventDTO.getCancelReason();
        if (orderId == null || employeeId == null || customerId != null || productId != null || cancelReason != null)
            throw new RuntimeException("Неверный запрос выдачи заказа");
        LocalDateTime createdAt = LocalDateTime.now();
        Employee employee = new Employee(orderEventDTO.getEmployeeId());
        Order order = new Order(orderEventDTO.getOrderId());
        order.setStatus(ISSUED);
        return IssuedOrderEvent.builder()
                .eventType(ISSUED)
                .order(order)
                .employee(employee)
                .createdAt(createdAt).build();
    }

    private LocalDateTime calculateExpectedCompletionTime(LocalDateTime createdAt, Product product) {
        return createdAt.plusMinutes(product.getMinutesToPrepare());
    }
}
