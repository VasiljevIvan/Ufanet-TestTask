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

    @PostMapping("/registration")
    public ResponseEntity<Order> reg(@RequestBody OrderEventDTO orderEventDTO) {
        RegisterOrderEvent registerOrderEvent = orderEventCreator.createRegisterOrderEvent(orderEventDTO);
        orderService.publishEvent(registerOrderEvent);
        return new ResponseEntity<>(registerOrderEvent.getOrder(), HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Order> ccl(@RequestBody OrderEventDTO orderEventDTO) {
        CancelOrderEvent cancelOrderEvent = orderEventCreator.createCancelOrderEvent(orderEventDTO);
        orderService.publishEvent(cancelOrderEvent);
        return new ResponseEntity<>(cancelOrderEvent.getOrder(), HttpStatus.OK);
    }

    @PostMapping("/taken_to_work")
    public ResponseEntity<TakenToWorkOrderEvent> wrk(@RequestBody OrderEventDTO orderEventDTO) {
        TakenToWorkOrderEvent takenToWorkOrderEvent = orderEventCreator.createTakenToWorkOrderEvent(orderEventDTO);
        orderService.publishEvent(takenToWorkOrderEvent);
        return new ResponseEntity<>(takenToWorkOrderEvent, HttpStatus.OK);
    }

    @PostMapping("/ready")
    public ResponseEntity<ReadyOrderEvent> rdy(@RequestBody OrderEventDTO orderEventDTO) {
        ReadyOrderEvent readyOrderEvent = orderEventCreator.createReadyOrderEvent(orderEventDTO);
        orderService.publishEvent(readyOrderEvent);
        return new ResponseEntity<>(readyOrderEvent, HttpStatus.OK);
    }

    @PostMapping("/issued")
    public ResponseEntity<IssuedOrderEvent> iss(@RequestBody OrderEventDTO orderEventDTO) {
        IssuedOrderEvent issuedOrderEvent = orderEventCreator.createIssuedOrderEvent(orderEventDTO);
        orderService.publishEvent(issuedOrderEvent);
        return new ResponseEntity<>(issuedOrderEvent, HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
