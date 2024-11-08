package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import k.thees.myspringbootexample.mappers.CustomerMapper;
import k.thees.myspringbootexample.model.CustomerDto;
import k.thees.myspringbootexample.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;

	@Override
	public Optional<CustomerDto> getCustomerById(UUID uuid) {

		return customerRepository//
				.findById(uuid).map(customerMapper::customerToCustomerDto).map(Optional::of).orElse(Optional.empty());
	}

	@Override
	public List<CustomerDto> getAllCustomers() {
		return customerRepository.findAll().stream().map(customerMapper::customerToCustomerDto).toList();
	}

	@Override
	public CustomerDto saveNewCustomer(CustomerDto customer) {
		return null;
	}

	@Override
	public void updateCustomerById(UUID customerId, CustomerDto customer) {

	}

	@Override
	public void deleteCustomerById(UUID customerId) {

	}

	@Override
	public void patchCustomerById(UUID customerId, CustomerDto customer) {

	}
}
