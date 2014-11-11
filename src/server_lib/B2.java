package server_lib;

import java.util.ArrayList;

import util.ProgramUniqueIdentifier;

public interface B2 {

	public String procedure_0(B2 b2);

	public double[][] multiply(double[][] matrix1, double[][] matrix2, B2 b2);

	public double min(double[] array, B2 b2);

	public static ArrayList<ProgramUniqueIdentifier> getPuidList() {
		ArrayList<ProgramUniqueIdentifier> puidList = new ArrayList<>();

		ArrayList<String> list_proc0 = new ArrayList<>();
		list_proc0.add("");
		ProgramUniqueIdentifier puid_proc0 = new ProgramUniqueIdentifier("B", "2", "procedure_0",
				list_proc0);
		puidList.add(puid_proc0);

		ArrayList<String> list_multiply = new ArrayList<>();
		list_multiply.add("double[][]");
		list_multiply.add("double[][]");
		ProgramUniqueIdentifier puid_multiply = new ProgramUniqueIdentifier("B", "2", "multiply",
				list_multiply);
		puidList.add(puid_multiply);

		ArrayList<String> list_min = new ArrayList<>();
		list_min.add("double[]");
		ProgramUniqueIdentifier puid_min = new ProgramUniqueIdentifier("B", "2", "min", list_min);
		puidList.add(puid_min);

		return puidList;
	}
}
