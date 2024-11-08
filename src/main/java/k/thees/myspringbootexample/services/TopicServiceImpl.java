package k.thees.myspringbootexample.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.model.TopicStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TopicServiceImpl implements TopicService {

	private Map<UUID, TopicDto> topicMap;

	public TopicServiceImpl() {
		this.topicMap = new HashMap<>();

		TopicDto topic1 = TopicDto.builder().id(UUID.randomUUID()).version(1).name("Galaxy Cat").style(TopicStyle.BLUE)
				.upc("12356").price(new BigDecimal("12.99")).quantityOnHand(122).createdDate(LocalDateTime.now())
				.updateDate(LocalDateTime.now()).build();

		TopicDto topic2 = TopicDto.builder().id(UUID.randomUUID()).version(1).name("Crank").style(TopicStyle.BLUE)
				.upc("12356222").price(new BigDecimal("11.99")).quantityOnHand(392).createdDate(LocalDateTime.now())
				.updateDate(LocalDateTime.now()).build();

		TopicDto topic3 = TopicDto.builder().id(UUID.randomUUID()).version(1).name("Sunshine City")
				.style(TopicStyle.YELLOW).upc("12356").price(new BigDecimal("13.99")).quantityOnHand(144)
				.createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

		topicMap.put(topic1.getId(), topic1);
		topicMap.put(topic2.getId(), topic2);
		topicMap.put(topic3.getId(), topic3);
	}

	@Override
	public void patch(UUID id, TopicDto topicDto) {
		TopicDto existing = topicMap.get(id);

		if (StringUtils.hasText(topicDto.getName())) {
			existing.setName(topicDto.getName());
		}

		if (topicDto.getStyle() != null) {
			existing.setStyle(topicDto.getStyle());
		}

		if (topicDto.getPrice() != null) {
			existing.setPrice(topicDto.getPrice());
		}

		if (topicDto.getQuantityOnHand() != null) {
			existing.setQuantityOnHand(topicDto.getQuantityOnHand());
		}

		if (StringUtils.hasText(topicDto.getUpc())) {
			existing.setUpc(topicDto.getUpc());
		}
	}

	@Override
	public void delete(UUID id) {
		topicMap.remove(id);
	}

	@Override
	public void update(UUID id, TopicDto topicDto) {
		TopicDto existing = topicMap.get(id);
		existing.setName(topicDto.getName());
		existing.setPrice(topicDto.getPrice());
		existing.setUpc(topicDto.getUpc());
		existing.setQuantityOnHand(topicDto.getQuantityOnHand());
	}

	@Override
	public List<TopicDto> getAll() {
		return new ArrayList<>(topicMap.values());
	}

	@Override
	public Optional<TopicDto> get(UUID id) {

		log.debug("Get Topic by Id - in service. Id: " + id.toString());

		return Optional.of(topicMap.get(id));
	}

	@Override
	public TopicDto create(TopicDto topicDto) {

		TopicDto savedTopicDto = TopicDto.builder().id(UUID.randomUUID()).version(1).createdDate(LocalDateTime.now())
				.updateDate(LocalDateTime.now()).name(topicDto.getName()).style(topicDto.getStyle())
				.quantityOnHand(topicDto.getQuantityOnHand()).upc(topicDto.getUpc()).price(topicDto.getPrice()).build();

		topicMap.put(savedTopicDto.getId(), savedTopicDto);

		return savedTopicDto;
	}
}
