package k.thees.myspringbootexample.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import k.thees.myspringbootexample.model.TopicStyle;
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
public class TopicEntity {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
	private UUID id;

	@Version
	private Integer version;

	@NotBlank
	@NotNull // Actually not necessary because @NotBlank includes it already. But there are more messages to the user in the response.
	// If name is == null, it will say {"name":"must not be blank"},{"name":"must not be null"}
	private String name;

	@NotNull
	private TopicStyle style;

	@NotBlank
	private String code;
	private Integer quantity;

	@NotNull
	private BigDecimal price;

	private LocalDateTime createdDate;
	private LocalDateTime updateDate;
}
