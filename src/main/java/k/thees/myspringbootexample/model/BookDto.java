package k.thees.myspringbootexample.model;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookDto {

	private Integer id;

	@Size(max = 100)
	private Optional<String> author;

	@Size(max = 100)
	private Optional<String> title;

	private Optional<BigDecimal> price;
}
