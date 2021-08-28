package me.khoro.main.repository;

import me.khoro.main.model.entity.Domain;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DomainRepository extends CrudRepository<Domain, UUID> {
}
