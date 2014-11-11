package server_lib;

import java.util.ArrayList;

import util.ProgramUniqueIdentifier;

public interface B1 {

	public String procedure_0(B1 b1);

	public double[][] multiply(double[][] matrix1, double[][] matrix2, B1 b1);

	public double max(double[] array, B1 b1);

	public static ArrayList<ProgramUniqueIdentifier> getPuidList() {
		ArrayList<ProgramUniqueIdentifier> puidList = new ArrayList<>();

		ArrayList<String> list_multiply = new ArrayList<>();
		list_multiply.add("double[][]");
		list_multiply.add("double[][]");
		ProgramUniqueIdentifier puid_multiply = new ProgramUniqueIdentifier("B", "1", "multiply",
				list_multiply);
		puidList.add(puid_multiply);

		ArrayList<String> list_max = new ArrayList<>();
		list_max.add("double[]");
		ProgramUniqueIdentifier puid_max = new ProgramUniqueIdentifier("B", "1", "max", list_max);
		puidList.add(puid_max);

		ArrayList<String> list_proc0 = new ArrayList<>();
		list_proc0.add("");
		ProgramUniqueIdentifier puid_proc0 = new ProgramUniqueIdentifier("B", "1", "procedure_0",
				list_proc0);
		puidList.add(puid_proc0);

		return puidList;
	}
}
