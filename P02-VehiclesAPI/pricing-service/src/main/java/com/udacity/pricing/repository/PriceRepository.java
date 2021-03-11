package com.udacity.pricing.repository;

import com.udacity.pricing.domain.Price;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "price")
public interface PriceRepository extends CrudRepository<Price, Long> {
}
