package k.thees.myspringbootexample.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import k.thees.myspringbootexample.entities.CustomerEntity;

@DataJpaTest
class CustomerRepositoryTest {

	@Autowired
	CustomerRepository customerRepository;

	@Test
	void testSaveCustomer() {
		CustomerEntity customer = customerRepository.save(CustomerEntity.builder().name("New Name").build());

		assertThat(customer.getId()).isNotNull();

	}
}