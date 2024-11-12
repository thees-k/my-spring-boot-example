package k.thees.myspringbootexample.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookDto {

	private Integer id;

	@Size(max = 100)
	private String author;

	@Size(max = 100)
	@NotBlank
	private String title;

	@NotNull
	private BigDecimal price;
}
