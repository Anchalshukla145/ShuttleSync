package com.movein.shuttlesync.repository;

import com.movein.shuttlesync.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
}
