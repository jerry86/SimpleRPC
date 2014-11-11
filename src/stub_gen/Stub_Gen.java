package stub_gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import JAXB.Parameter;
import JAXB.Procedure;
import JAXB.Program;
import JAXB.Version;
import JAXB.XMLToProgram;

public class Stub_Gen {

	public static List<Program> programList = new ArrayList<>();

	public static void main(String[] args) {
		File inputFile = new File("SRPC_specification.xml");
		XMLToProgram xmlToProgram = new XMLToProgram();
		programList = xmlToProgram.getProgramsList(inputFile);
		generateServerLib(programList);
		generateClientLib(programList);
	}

	public static void generateServerLib(List<Program> programList) {
		if (programList == null || programList.isEmpty()) {
			return;
		}
		/* === generating .java file for each program/version combination === */
		for (Program program : programList) {
			for (Version version : program.getVersionList()) {
				try {
					String className = program.getProgramName() + version.getProgramVersion();
					File outFile = new File("src\\server_lib\\" + className + ".java");
					if (!outFile.exists()) { // create outFile if not exists
						outFile.createNewFile();
					}
					/* === first line of the class === */
					String beginning = "package server_lib;\n\npublic class " + className
							+ " {\n\n";
					FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(beginning);
					/* === building methods in the class === */
					for (Procedure procedure : version.getProcedureList()) {
						String methodName = procedure.getProcedureName();
						String methodReturnType = procedure.getProcedureType();
						String methodBeginLineString = "public " + methodReturnType + " "
								+ methodName + "(";
						/* === parameter list=== */
						for (Parameter parameter : procedure.getParameterList()) {
							methodBeginLineString += parameter.getParameterType() + " "
									+ parameter.getParameterName() + ", ";
						}
						methodBeginLineString = methodBeginLineString.substring(0,
								methodBeginLineString.length() - 2);
						methodBeginLineString += ") {";
						bw.write(methodBeginLineString);
						/* === last line of each method === */
						if (procedure.getProcedureType().equals("void")) {
							bw.write("return;\n}\n");
						} else if (procedure.getProcedureType().equals("double")) {
							bw.write("return 0;\n}\n");
						} else {
							bw.write("return null;\n}\n");
						}
					}
					/* === last line of the class === */
					bw.write("\n}\n");
					bw.close();
					System.out.println("ServerLib generated: " + className + ".java");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * This method generates client libraries that contain the methods that a client 
	 * may call.
	 * Each one of the libraries also extends a connection handle that handles message 
	 * delivery among the client, the port-mapper and the server.
	 * 
	 * @param programList
	 */
	public static void generateClientLib(List<Program> programList) {
		if (programList == null || programList.isEmpty()) {
			return;
		}
		/* === generating .java file for each program/version combination === */
		for (Program program : programList) {
			for (Version version : program.getVersionList()) {
				try {
					String className = program.getProgramName() + version.getProgramVersion();
					File outFile = new File("src\\client_lib\\" + className + ".java");
					if (!outFile.exists()) { // create outFile if not exists
						outFile.createNewFile();
					}
					/* === first line of the class === */
					String beginning = "package client_lib;\n\nimport stub_gen.ConnectionHandler;\n\nimport util.ClientToMapperRequest;\n\npublic class "
							+ className + " extends ConnectionHandler {\n\n";
					FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(beginning);
					/* === building methods in the class === */
					for (Procedure procedure : version.getProcedureList()) {
						String bodyString = "";
						String methodReturnType = procedure.getProcedureType();
						String methodName = procedure.getProcedureName();
						String methodBeginLineString = "public " + methodReturnType + " "
								+ methodName + "(";
						/* === parameter list=== */
						for (Parameter parameter : procedure.getParameterList()) {
							methodBeginLineString += parameter.getParameterType() + " "
									+ parameter.getParameterName() + ", ";
							/* this part of code invokes a connection handler */
							bodyString = "ClientToMapperRequest  ctmRequest = new ClientToMapperRequest(\""
									+ program.getProgramName()
									+ "\", \""
									+ version.getProgramVersion()
									+ "\", \""
									+ procedure.getProcedureType()
									+ "\", \""
									+ procedure.getProcedureName()
									+ "\", \""
									+ parameter.getParameterType()
									+ "\", \""
									+ parameter.getParameterName() + "\");\n";
						}
						methodBeginLineString = methodBeginLineString.substring(0,
								methodBeginLineString.length() - 2);
						methodBeginLineString += ") {\n";
						bw.write(methodBeginLineString);
						bw.write(bodyString);

						/* === last line of each method === */
						if (procedure.getProcedureType().equals("void")) {
							bw.write("return;\n}\n");
						} else if (procedure.getProcedureType().equals("double")) {
							bw.write("return 0;\n}\n");
						} else {
							bw.write("return null;\n}\n");
						}
					}
					/* === last line of the class === */
					bw.write("\n}\n");
					bw.close();
					System.out.println("ClientLib generated: " + className + ".java");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
