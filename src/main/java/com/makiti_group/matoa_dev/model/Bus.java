package com.makiti_group.matoa_dev.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "BUS")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "seat_total")
    private Integer seatTotal;

    @Column(name = "number", nullable = false)
    private String number;

    public Bus() {
    }

    public Bus(String type, String model, int seatTotal, String number) {
        this.type = type;
        this.model = model;
        this.seatTotal = seatTotal;
        this.number = number;
    }

    // Getters and Setters
}
