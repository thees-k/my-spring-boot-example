package k.thees.myspringbootexample.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import k.thees.myspringbootexample.model.CustomerDto;
import k.thees.myspringbootexample.services.CustomerService;
import k.thees.myspringbootexample.services.CustomerServiceImpl;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

	@MockBean
	CustomerService customerService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	CustomerServiceImpl customerServiceImpl;

	@BeforeEach
	void setUp() {
		customerServiceImpl = new CustomerServiceImpl();
	}

	@Captor
	ArgumentCaptor<UUID> uuidArgumentCaptor;

	@Captor
	ArgumentCaptor<CustomerDto> customerArgumentCaptor;

	@Test
	void testPatchCustomer() throws Exception {
		CustomerDto customer = customerServiceImpl.getAllCustomers().get(0);

		Map<String, Object> customerMap = new HashMap<>();
		customerMap.put("name", "New Name");

		mockMvc.perform(patch(CustomerController.CUSTOMERS_PATH_ID, customer.getId())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerMap)))
				.andExpect(status().isNoContent());

		verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

		assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customer.getId());
		assertThat(customerArgumentCaptor.getValue().getName()).isEqualTo(customerMap.get("name"));
	}

	@Test
	void testDeleteCustomer() throws Exception {
		CustomerDto customer = customerServiceImpl.getAllCustomers().get(0);

		mockMvc.perform(
				delete(CustomerController.CUSTOMERS_PATH_ID, customer.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

		assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
	}

	@Test
	void testUpdateCustomer() throws Exception {
		CustomerDto customer = customerServiceImpl.getAllCustomers().get(0);

		mockMvc.perform(put(CustomerController.CUSTOMERS_PATH_ID, customer.getId())
				.content(objectMapper.writeValueAsString(customer)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

		verify(customerService).updateCustomerById(uuidArgumentCaptor.capture(), any(CustomerDto.class));

		assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
	}

	@Test
	void testCreateCustomer() throws Exception {
		CustomerDto customer = customerServiceImpl.getAllCustomers().get(0);
		customer.setId(null);
		customer.setVersion(null);

		given(customerService.saveNewCustomer(any(CustomerDto.class)))
				.willReturn(customerServiceImpl.getAllCustomers().get(1));

		mockMvc.perform(post(CustomerController.CUSTOMERS_PATH).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isCreated()).andExpect(header().exists("Location"));
	}

	@Test
	void listAllCustomers() throws Exception {
		given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());

		mockMvc.perform(get(CustomerController.CUSTOMERS_PATH).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()", is(3)));
	}

	@Test
	void getCustomerByIdNotFound() throws Exception {

		given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

		mockMvc.perform(get(CustomerController.CUSTOMERS_PATH_ID, UUID.randomUUID())).andExpect(status().isNotFound());
	}

	@Test
	void getCustomerById() throws Exception {
		CustomerDto customer = customerServiceImpl.getAllCustomers().get(0);

		given(customerService.getCustomerById(customer.getId())).willReturn(Optional.of(customer));

		mockMvc.perform(get(CustomerController.CUSTOMERS_PATH_ID, customer.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name", is(customer.getName())));
	}
}
