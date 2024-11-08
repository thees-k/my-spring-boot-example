package k.thees.myspringbootexample.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import k.thees.myspringbootexample.repositories.TopicRepository;
import k.thees.myspringbootexample.repositories.CustomerRepository;

@DataJpaTest
class BootstrapDataTest {

	@Autowired
	TopicRepository topicRepository;

	@Autowired
	CustomerRepository customerRepository;

	BootstrapData bootstrapData;

	@BeforeEach
	void setUp() {
		bootstrapData = new BootstrapData(topicRepository, customerRepository);
	}

	@Test
	void Testrun() throws Exception {
		bootstrapData.run();

		assertThat(topicRepository.count()).isEqualTo(3);
		assertThat(customerRepository.count()).isEqualTo(3);
	}
}
