package k.thees.myspringbootexample.mappers;

import org.mapstruct.Mapper;

import k.thees.myspringbootexample.entities.CustomerEntity;
import k.thees.myspringbootexample.model.CustomerDto;

@Mapper
public interface CustomerMapper {

	CustomerEntity customerDtoToCustomer(CustomerDto dto);

	CustomerDto customerToCustomerDto(CustomerEntity customer);

}
