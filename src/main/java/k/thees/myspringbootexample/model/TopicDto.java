package k.thees.myspringbootexample.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TopicDto {

	public static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	private UUID id;
	private Integer version;

	@NotBlank // a "Bean Validation Constraint"
	@NotNull // Actually not necessary because @NotBlank includes it already. But there are more messages to the user in the response.
	// If name is == null, it will say {"name":"must not be blank"},{"name":"must not be null"}
	@Size(max = 255) // see field "name" of TopicEntity for explanation
	private String name;

	@NotNull
	private TopicStyle style;

	@NotBlank
	@Size(max = 10) // See field "code" of TopicEntity, "@Size" and "@Column" - must be the same value (here: 10)!
	private String code;
	private Integer quantity;

	@NotNull
	private BigDecimal price;

	@JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
	private LocalDateTime createdDate;

	@JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
	private LocalDateTime updateDate;
}
