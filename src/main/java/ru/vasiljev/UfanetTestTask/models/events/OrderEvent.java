package ru.vasiljev.UfanetTestTask.models.events;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;
import ru.vasiljev.UfanetTestTask.models.Employee;
import ru.vasiljev.UfanetTestTask.models.Order;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "order_event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public abstract class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Transient
    private String eventType;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
