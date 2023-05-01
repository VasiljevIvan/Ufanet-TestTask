package ru.vasiljev.UfanetTestTask.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vasiljev.UfanetTestTask.dto.OrderEventDTO;
import ru.vasiljev.UfanetTestTask.models.Order;
import ru.vasiljev.UfanetTestTask.models.events.*;
import ru.vasiljev.UfanetTestTask.services.OrderServiceImpl;
import ru.vasiljev.UfanetTestTask.util.ErrorResponse;
import ru.vasiljev.UfanetTestTask.util.OrderEventCreator;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/events")
public class OrderEventController {

    private final OrderEventCreator orderEventCreator;
    private final OrderServiceImpl orderService;

    @Autowired
    public OrderEventController(OrderEventCreator orderEventCreator, OrderServiceImpl orderService) {
        this.orderEventCreator = orderEventCreator;
        this.orderService = orderService;
    }

    @PostMapping("/getorder/{id}")
    public ResponseEntity<Order> reg(@PathVariable("id") int id) {
        return new ResponseEntity<>(orderService.findOrder(id), HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<Order> reg(@RequestBody OrderEventDTO orderEventDTO) {
        RegisterOrderEvent registerOrderEvent = orderEventCreator.createRegisterOrderEvent(orderEventDTO);
        orderService.publishEvent(registerOrderEvent);
        return new ResponseEntity<>(orderService.findOrder(registerOrderEvent.getOrder().getId()), HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Order> ccl(@RequestBody OrderEventDTO orderEventDTO) {
        CancelOrderEvent cancelOrderEvent = orderEventCreator.createCancelOrderEvent(orderEventDTO);
        orderService.publishEvent(cancelOrderEvent);
        return new ResponseEntity<>(orderService.findOrder(cancelOrderEvent.getOrder().getId()), HttpStatus.OK);
    }

    @PostMapping("/taken_to_work")
    public ResponseEntity<Order> wrk(@RequestBody OrderEventDTO orderEventDTO) {
        TakenToWorkOrderEvent takenToWorkOrderEvent = orderEventCreator.createTakenToWorkOrderEvent(orderEventDTO);
        orderService.publishEvent(takenToWorkOrderEvent);
        return new ResponseEntity<>(orderService.findOrder(takenToWorkOrderEvent.getOrder().getId()), HttpStatus.OK);
    }

    @PostMapping("/ready")
    public ResponseEntity<Order> rdy(@RequestBody OrderEventDTO orderEventDTO) {
        ReadyOrderEvent readyOrderEvent = orderEventCreator.createReadyOrderEvent(orderEventDTO);
        orderService.publishEvent(readyOrderEvent);
        return new ResponseEntity<>(orderService.findOrder(readyOrderEvent.getOrder().getId()), HttpStatus.OK);
    }

    @PostMapping("/issued")
    public ResponseEntity<Order> iss(@RequestBody OrderEventDTO orderEventDTO) {
        IssuedOrderEvent issuedOrderEvent = orderEventCreator.createIssuedOrderEvent(orderEventDTO);
        orderService.publishEvent(issuedOrderEvent);
        return new ResponseEntity<>(orderService.findOrder(issuedOrderEvent.getOrder().getId()), HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
