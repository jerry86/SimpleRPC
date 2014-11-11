package JAXB;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Program {

	String programName;
	List<Version> versionList;

	@XmlAttribute(name = "programName")
	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String progName) {
		this.programName = progName;
	}

	@XmlElement(name = "versionList")
	public List<Version> getVersionList() {
		return versionList;
	}

	public void setVersionList(List<Version> versionList) {
		this.versionList = versionList;
	}

}
