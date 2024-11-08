package k.thees.myspringbootexample.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import k.thees.myspringbootexample.entities.TopicEntity;
import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.repositories.TopicRepository;

@SpringBootTest
class TopicControllerIntegrationTest {
	@Autowired
	TopicController topicController;

	@Autowired
	TopicRepository topicRepository;

	@Test
	void testGetTopicWithMyNotFoundException() {
		assertThrows(MyNotFoundException.class, () -> topicController.get(UUID.randomUUID()));
	}

	@Test
	void testGet() {
		TopicEntity topicEntity = topicRepository.findAll().get(0);

		TopicDto dto = topicController.get(topicEntity.getId()).getBody();

		assertThat(dto).isNotNull();
	}

	@Test
	void testGetAll() {
		List<TopicDto> dtos = topicController.getAll().getBody();

		assertThat(dtos.size()).isEqualTo(3);
	}

	@Rollback
	@Transactional
	@Test
	void testGetAllReturnsEmptyList() {
		topicRepository.deleteAll();
		List<TopicDto> dtos = topicController.getAll().getBody();

		assertThat(dtos.size()).isEqualTo(0);
	}
}
