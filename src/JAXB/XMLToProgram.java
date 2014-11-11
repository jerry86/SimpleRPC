package JAXB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLToProgram {

	public List<Program> getProgramsList(File file) {
		List<Program> programList = new ArrayList<>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Origin.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Origin origin = (Origin) jaxbUnmarshaller.unmarshal(file);
			return origin.getProgramList();
		} catch (JAXBException e) {
			System.out.println("Error in generating programs list from SRPC_specification.xml!");
			e.printStackTrace();
		}
		return programList;
	}

	public static void main(String[] args) {
		try {
			File file = new File("SRPC_specification.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(Origin.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Origin origin = (Origin) jaxbUnmarshaller.unmarshal(file);
			/* === debugging === */
			System.out.println(origin.getProgramList().get(0).getVersionList().get(0)
					.getProcedureList().get(0).getProcedureName());
			System.out.println(origin.getProgramList().get(0).getVersionList().get(0)
					.getProcedureList().get(0).getParameterList().get(0).getParameterName());
			System.out.println(origin.getProgramList().get(0).getVersionList().get(0)
					.getProcedureList().get(0).getParameterList().get(0).getParameterType());
			System.out.println(origin);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
