package k.thees.myspringbootexample.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import k.thees.myspringbootexample.entities.TopicEntity;

@DataJpaTest
class TopicRepositoryTest {

	@Autowired
	TopicRepository topicRepository;

	@Test
	void testSaveTopic() {
		TopicEntity savedTopic = topicRepository.save(TopicEntity.builder().name("My Topic").build());

		assertThat(savedTopic).isNotNull();
		assertThat(savedTopic.getId()).isNotNull();
	}
}