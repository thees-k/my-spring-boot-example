package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import k.thees.myspringbootexample.model.CustomerDto;

public interface CustomerService {

	Optional<CustomerDto> getCustomerById(UUID uuid);

	List<CustomerDto> getAllCustomers();

	CustomerDto saveNewCustomer(CustomerDto customer);

	void updateCustomerById(UUID customerId, CustomerDto customer);

	void deleteCustomerById(UUID customerId);

	void patchCustomerById(UUID customerId, CustomerDto customer);
}
