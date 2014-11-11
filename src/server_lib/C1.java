package server_lib;

import java.util.ArrayList;

import util.ProgramUniqueIdentifier;

public interface C1 {

	public String procedure_0(C1 c1);

	public double[] sort(double[] array, C1 c1);

	public static ArrayList<ProgramUniqueIdentifier> getPuidList() {
		ArrayList<ProgramUniqueIdentifier> puidList = new ArrayList<>();

		ArrayList<String> list_sort = new ArrayList<>();
		list_sort.add("double[]");
		ProgramUniqueIdentifier puid_sort = new ProgramUniqueIdentifier("C", "1", "sort", list_sort);
		puidList.add(puid_sort);

		ArrayList<String> list_proc0 = new ArrayList<>();
		list_proc0.add("");
		ProgramUniqueIdentifier puid_proc0 = new ProgramUniqueIdentifier("C", "1", "procedure_0",
				list_proc0);
		puidList.add(puid_proc0);

		return puidList;
	}
}
