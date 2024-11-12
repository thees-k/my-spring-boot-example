package k.thees.myspringbootexample.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import k.thees.myspringbootexample.repositories.BookRepository;
import k.thees.myspringbootexample.repositories.TopicRepository;

@DataJpaTest
class BootstrapDataTest {

	@Autowired
	TopicRepository topicRepository;

	@Autowired
	BookRepository bookRepository;

	BootstrapData bootstrapData;

	@BeforeEach
	void setUp() {
		bootstrapData = new BootstrapData(topicRepository, bookRepository);
	}

	@Test
	void testRun() throws Exception {
		bootstrapData.run();

		assertThat(topicRepository.count()).isEqualTo(3);
		assertThat(bookRepository.count()).isEqualTo(3);

		assertNotNull(bookRepository.findAll().get(0).getId());
	}
}
