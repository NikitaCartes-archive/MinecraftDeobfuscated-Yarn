package net.minecraft.advancement;

import java.util.Collection;

public interface CriteriaMerger {
	CriteriaMerger AND = collection -> {
		String[][] strings = new String[collection.size()][];
		int i = 0;

		for (String string : collection) {
			strings[i++] = new String[]{string};
		}

		return strings;
	};
	CriteriaMerger OR = collection -> new String[][]{(String[])collection.toArray(new String[0])};

	String[][] createRequirements(Collection<String> collection);
}
