package k.thees.myspringbootexample.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "integer", updatable = false, nullable = false)
	private Integer id;

	@Size(max = 100)
	@Column(length = 100)
	private String author;

	@Size(max = 100)
	@Column(length = 100)
	@NotBlank
	private String title;

	@NotNull
	private BigDecimal price;
}
