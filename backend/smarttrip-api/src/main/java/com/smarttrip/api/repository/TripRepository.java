package com.smarttrip.api.repository;

import com.smarttrip.api.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
