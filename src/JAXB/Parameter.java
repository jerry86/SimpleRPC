package JAXB;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Parameter {
	String parameterName;
	String parameterType;

	@XmlElement(name = "parameterName")
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String paraName) {
		this.parameterName = paraName;
	}

	@XmlAttribute(name = "parameterType")
	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String paraType) {
		this.parameterType = paraType;
	}

}
