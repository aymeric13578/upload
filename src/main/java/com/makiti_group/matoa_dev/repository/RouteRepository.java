package com.makiti_group.matoa_dev.repository;

import com.makiti_group.matoa_dev.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}
