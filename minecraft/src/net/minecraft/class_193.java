package net.minecraft;

import java.util.Collection;

public interface class_193 {
	class_193 field_16882 = collection -> {
		String[][] strings = new String[collection.size()][];
		int i = 0;

		for (String string : collection) {
			strings[i++] = new String[]{string};
		}

		return strings;
	};
	class_193 field_1257 = collection -> new String[][]{(String[])collection.toArray(new String[0])};

	String[][] createRequirements(Collection<String> collection);
}
