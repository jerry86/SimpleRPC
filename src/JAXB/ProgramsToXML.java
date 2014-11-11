package JAXB;

import java.io.File;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class ProgramsToXML {

	public static void main(String[] args) {

		Parameter para1 = new Parameter();
		para1.setParameterName("matrix1");
		para1.setParameterType("double[][]");
		Parameter para2 = new Parameter();
		para2.setParameterName("matrix2");
		para2.setParameterType("double[][]");
		List<Parameter> paraList1 = new ArrayList<Parameter>();
		paraList1.add(para1);
		paraList1.add(para2);

		Parameter para3 = new Parameter();
		para3.setParameterName("array1");
		para3.setParameterType("double[]");
		List<Parameter> paraList2 = new ArrayList<Parameter>();
		paraList2.add(para3);

		Procedure procMulti = new Procedure();
		procMulti.setProcedureName("multiply");
		procMulti.setProcedureType("double[][]");
		procMulti.setParameterList(paraList1);

		Procedure procSort = new Procedure();
		procSort.setProcedureName("sort");
		procSort.setProcedureType("void");
		procSort.setParameterList(paraList2);

		Procedure procMax = new Procedure();
		procMax.setProcedureName("max");
		procMax.setProcedureType("double");
		procMax.setParameterList(paraList2);

		Procedure procMin = new Procedure();
		procMin.setProcedureName("min");
		procMin.setProcedureType("double");
		procMin.setParameterList(paraList2);

		/* ========== Program A (version 1 and 2) ============ */
		Version version1 = new Version();
		version1.setProgramVersion("1");
		List<Procedure> listProc1 = new ArrayList<>();
		listProc1.add(procMulti);
		listProc1.add(procSort);
		listProc1.add(procMax);
		listProc1.add(procMin);
		version1.setProcedureList(listProc1);

		Version version2 = new Version();
		version2.setProgramVersion("2");
		List<Procedure> listProc2 = new ArrayList<>();
		listProc2.add(procMax);
		listProc2.add(procMin);
		version2.setProcedureList(listProc2);

		Program progA = new Program();
		progA.setProgramName("A");
		List<Version> versionListA = new ArrayList<>();
		versionListA.add(version1);
		versionListA.add(version2);
		progA.setVersionList(versionListA);

		/* ============ Program B (version 1 and 2) ============= */
		version1 = new Version();
		version1.setProgramVersion("1");
		listProc1 = new ArrayList<>();
		listProc1.add(procMulti);
		listProc1.add(procMax);
		version1.setProcedureList(listProc1);

		version2 = new Version();
		version2.setProgramVersion("2");
		listProc2 = new ArrayList<>();
		listProc2.add(procMulti);
		listProc2.add(procMin);
		version2.setProcedureList(listProc2);

		Program progB = new Program();
		progB.setProgramName("B");
		List<Version> versionListB = new ArrayList<>();
		versionListB.add(version1);
		versionListB.add(version2);
		progB.setVersionList(versionListB);

		/* ============ Program C version 1 =============== */
		version1 = new Version();
		version1.setProgramVersion("1");
		listProc1 = new ArrayList<>();
		listProc1.add(procSort);
		version1.setProcedureList(listProc1);

		Program progC = new Program();
		progC.setProgramName("C");
		List<Version> versionListC = new ArrayList<>();
		versionListC.add(version1);
		progC.setVersionList(versionListC);

		/* ================ Set up origin =============== */
		Origin origin = new Origin();
		origin.setId("1001");
		List<Program> progList = new ArrayList<>();
		progList.add(progA);
		progList.add(progB);
		progList.add(progC);
		origin.setProgramList(progList);

		try {
			File file = new File("SRPC_specification.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(Origin.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(origin, file);
			jaxbMarshaller.marshal(origin, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}
}