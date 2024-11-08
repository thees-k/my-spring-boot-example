package k.thees.myspringbootexample.other;

public class MyMain {

	public static void main(String[] args) {
		MyDto dto = new MyDto();
		dto.setFullName("Thees K");

		MyEntity entity = new MyMapStructMapperImpl().dtoToEntity(dto);
		System.out.println(entity.getName());
	}

}
