package net.minecraft.advancement;

import java.util.Collection;

public interface CriterionMerger {
	CriterionMerger AND = criteriaNames -> {
		String[][] strings = new String[criteriaNames.size()][];
		int i = 0;

		for (String string : criteriaNames) {
			strings[i++] = new String[]{string};
		}

		return strings;
	};
	CriterionMerger OR = criteriaNames -> new String[][]{(String[])criteriaNames.toArray(new String[0])};

	String[][] createRequirements(Collection<String> criteriaNames);
}
