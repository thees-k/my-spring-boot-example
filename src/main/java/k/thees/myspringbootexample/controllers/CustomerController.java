package k.thees.myspringbootexample.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import k.thees.myspringbootexample.model.CustomerDto;
import k.thees.myspringbootexample.services.CustomerService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class CustomerController {
	public static final String CUSTOMERS_PATH = "/api/v1/customers";
	public static final String CUSTOMERS_PATH_ID = CUSTOMERS_PATH + "/{customerId}";

	private final CustomerService customerService;

	@PatchMapping(CUSTOMERS_PATH_ID)
	public ResponseEntity patchCustomerById(@PathVariable("customerId") UUID customerId,
			@RequestBody CustomerDto customer) {

		customerService.patchCustomerById(customerId, customer);

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(CUSTOMERS_PATH_ID)
	public ResponseEntity deleteCustomerById(@PathVariable("customerId") UUID customerId) {

		customerService.deleteCustomerById(customerId);

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PutMapping(CUSTOMERS_PATH_ID)
	public ResponseEntity updateCustomerByID(@PathVariable("customerId") UUID customerId,
			@RequestBody CustomerDto customer) {

		customerService.updateCustomerById(customerId, customer);

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(CUSTOMERS_PATH)
	public ResponseEntity handlePost(@RequestBody CustomerDto customer) {
		CustomerDto savedCustomer = customerService.saveNewCustomer(customer);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", CUSTOMERS_PATH + "/" + savedCustomer.getId().toString());

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@GetMapping(CUSTOMERS_PATH)
	public List<CustomerDto> listAllCustomers() {
		return customerService.getAllCustomers();
	}

	@GetMapping(value = CUSTOMERS_PATH_ID)
	public CustomerDto getCustomerById(@PathVariable("customerId") UUID id) {
		return customerService.getCustomerById(id).orElseThrow(MyNotFoundException::new);
	}

}
