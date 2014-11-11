package message;

import java.io.Serializable;

public class MapperToMapperMsg implements Serializable {

	public boolean isAlive;

	public MapperToMapperMsg() {
		this.isAlive = true;
	}

}
