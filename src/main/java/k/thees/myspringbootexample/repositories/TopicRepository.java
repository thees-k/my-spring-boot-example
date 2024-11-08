package k.thees.myspringbootexample.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import k.thees.myspringbootexample.entities.TopicEntity;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID> {
}
