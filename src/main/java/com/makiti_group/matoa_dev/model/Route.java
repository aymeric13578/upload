package com.makiti_group.matoa_dev.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Entity
@Setter
@Getter
@Table(name = "ROUTES")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @ManyToOne
    @JoinColumn(name = "departure_city_id", nullable = false)
    private City departureCity;

    @ManyToOne
    @JoinColumn(name = "arrival_city_id", nullable = false)
    private City arrivalCity;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column(name = "adult_price", nullable = false)
    private Double adultPrice;

    @Column(name = "child_price", nullable = false)
    private Double childPrice;

    @Column(name = "trip_adult_price", nullable = false)
    private Double tripAdultPrice;

    @Column(name = "trip_child_price", nullable = false)
    private Double tripChildPrice;

    @Column(name = "departure_time", nullable = false)
    private Time departureTime;

    @Column(name = "travel_days", nullable = false)
    private String day;

    @Column(name = "direction", nullable = false)
    private String direction;

    // Getters and Setters
}

