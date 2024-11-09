package k.thees.myspringbootexample.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TopicDto {

	public static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	private UUID id;
	private Integer version;
	private String name;
	private TopicStyle style;
	private String code;
	private Integer quantity;
	private BigDecimal price;

	@JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
	private LocalDateTime createdDate;

	@JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
	private LocalDateTime updateDate;
}
