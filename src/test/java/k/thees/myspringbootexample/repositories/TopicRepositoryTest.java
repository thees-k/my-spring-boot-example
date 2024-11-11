package k.thees.myspringbootexample.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.validation.ConstraintViolationException;
import k.thees.myspringbootexample.entities.TopicEntity;
import k.thees.myspringbootexample.model.TopicStyle;

@DataJpaTest
class TopicRepositoryTest {

	@Autowired
	TopicRepository topicRepository;

	@Test
	void testSave() {
		TopicEntity savedTopic = topicRepository
				.save(TopicEntity.builder()
						.name("name")
						.style(TopicStyle.BLUE)
						.code("test code")
						.price(new BigDecimal("100.00"))
						.build());

		// Validation only takes place when writing to the database
		topicRepository.flush();

		assertThat(savedTopic).isNotNull();
		assertThat(savedTopic.getId()).isNotNull();
	}

	@Test
	void testSaveWithNoValidTopic() {
		topicRepository.save(TopicEntity.builder().build());

		// Validation only takes place when writing to the database
		assertThrows(ConstraintViolationException.class, () -> topicRepository.flush());
	}
}