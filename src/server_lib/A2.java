package server_lib;

import java.util.ArrayList;

import util.ProgramUniqueIdentifier;

public interface A2 {
	public String procedure_0(A2 a2);

	public double max(double[] array, A2 a2);

	public double min(double[] array, A2 a2);

	public static ArrayList<ProgramUniqueIdentifier> getPuidList() {
		ArrayList<ProgramUniqueIdentifier> puidList = new ArrayList<>();

		ArrayList<String> list_proc0 = new ArrayList<>();
		list_proc0.add("");
		ProgramUniqueIdentifier puid_proc0 = new ProgramUniqueIdentifier("A", "2", "procedure_0",
				list_proc0);
		puidList.add(puid_proc0);

		ArrayList<String> list_max = new ArrayList<>();
		list_max.add("double[]");
		ProgramUniqueIdentifier puid_max = new ProgramUniqueIdentifier("A", "2", "max", list_max);
		puidList.add(puid_max);

		ArrayList<String> list_min = new ArrayList<>();
		list_min.add("double[]");
		ProgramUniqueIdentifier puid_min = new ProgramUniqueIdentifier("A", "2", "min", list_min);
		puidList.add(puid_min);

		return puidList;
	}
}
