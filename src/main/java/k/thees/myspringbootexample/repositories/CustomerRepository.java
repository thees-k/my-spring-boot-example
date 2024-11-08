package k.thees.myspringbootexample.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import k.thees.myspringbootexample.entities.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
}
