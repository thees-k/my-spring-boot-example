package k.thees.myspringbootexample.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import k.thees.myspringbootexample.entities.CustomerEntity;
import k.thees.myspringbootexample.model.CustomerDto;
import k.thees.myspringbootexample.repositories.CustomerRepository;

@SpringBootTest
public class CustomerControllerIntegrationTest {

	@Autowired
	private CustomerController customerController;

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	public void testGetCustomerById() {

		CustomerEntity firstFoundCustomerEntity = customerRepository.findAll().get(0);

		CustomerDto customerDTO = customerController.getCustomerById(firstFoundCustomerEntity.getId());

		assertNotNull(customerDTO);
		assertEquals(customerDTO.getId(), firstFoundCustomerEntity.getId());
		assertEquals(customerDTO.getVersion(), firstFoundCustomerEntity.getVersion());
		assertEquals(customerDTO.getName(), firstFoundCustomerEntity.getName());
	}

	@Test
	public void testGetCustomerByIdThrowsNotFoundException() {

		UUID randomUuid = UUID.randomUUID();
		assertThrows(MyNotFoundException.class, () -> customerController.getCustomerById(randomUuid));
	}

	@Test
	public void testListAllCustomers() {

		List<CustomerDto> customerDtos = customerController.listAllCustomers();
		assertEquals(customerDtos.size(), 3);
	}

	@Rollback
	@Transactional
	@Test
	public void testListAllCustomersReturnsEmptyList() {

		customerRepository.deleteAll();
		List<CustomerDto> customerDtos = customerController.listAllCustomers();
		assertTrue(customerDtos.isEmpty());
	}

}
