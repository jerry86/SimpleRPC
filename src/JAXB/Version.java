package JAXB;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Version {

	String programVersion;
	List<Procedure> procedureList;

	@XmlAttribute(name = "programVersion")
	public String getProgramVersion() {
		return programVersion;
	}

	public void setProgramVersion(String progVersion) {
		this.programVersion = progVersion;
	}

	@XmlElement(name = "procedureList")
	public List<Procedure> getProcedureList() {
		return procedureList;
	}

	public void setProcedureList(List<Procedure> procList) {
		this.procedureList = procList;
	}

}
