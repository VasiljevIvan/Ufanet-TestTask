package ru.vasiljev.UfanetTestTask.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vasiljev.UfanetTestTask.models.events.OrderEvent;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "order_")
public class Order {
    @Id
    private int id;

    private String status;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<OrderEvent> events = new ArrayList<>();

    public Order(int id) {
        this.id = id;
    }
}
