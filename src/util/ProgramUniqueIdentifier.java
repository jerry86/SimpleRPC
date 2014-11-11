package util;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramUniqueIdentifier implements Serializable {
	public String programName;
	public String version;
	public String procedureName;
	public ArrayList<String> parameterTypes;

	public ProgramUniqueIdentifier() {
		super();
		this.programName = null;
		this.version = null;
		this.procedureName = null;
		this.parameterTypes = new ArrayList<>();
	}

	public ProgramUniqueIdentifier(String programName, String version, String procedureName,
			ArrayList<String> parameterTypes) {
		super();
		this.programName = programName;
		this.version = version;
		this.procedureName = procedureName;
		this.parameterTypes = parameterTypes;
	}

	public String toString() {
		String str = programName + ", " + version + ", " + procedureName;
		for (String s : parameterTypes) {
			str += ", " + s;
		}
		return str;
	}

	@Override
	public int hashCode() {
		return programName.hashCode() + version.hashCode() + procedureName.hashCode()
				+ parameterTypes.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProgramUniqueIdentifier) {
			if (((ProgramUniqueIdentifier) o).programName.equals(this.programName)
					&& ((ProgramUniqueIdentifier) o).version.equals(this.version)
					&& ((ProgramUniqueIdentifier) o).procedureName.equals(this.procedureName)) {
				for (String s : ((ProgramUniqueIdentifier) o).parameterTypes) {
					if (!this.parameterTypes.contains(s)) {
						return false;
					}
					return true;
				}
			}
		}
		return false;

	}
}
