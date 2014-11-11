package JAXB;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Procedure {
	String procedureName;
	String procedureType;
	List<Parameter> parametersList;

	@XmlAttribute(name = "procName")
	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procName) {
		this.procedureName = procName;
	}

	@XmlAttribute(name = "procType")
	public String getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(String procType) {
		this.procedureType = procType;
	}

	@XmlElement(name = "parametersList")
	public List<Parameter> getParameterList() {
		return parametersList;
	}

	public void setParameterList(List<Parameter> paraList) {
		this.parametersList = paraList;
	}

}
