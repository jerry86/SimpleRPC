package JAXB;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Origin")
public class Origin {
	String id;
	List<Program> programList;

	@XmlElement(name = "programList")
	public List<Program> getProgramList() {
		return programList;
	}

	// @XmlElement(name = "programList")
	public void setProgramList(List<Program> programList) {
		this.programList = programList;
	}

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	// @XmlAttribute(name = "id")
	public void setId(String id) {
		this.id = id;
	}

}
