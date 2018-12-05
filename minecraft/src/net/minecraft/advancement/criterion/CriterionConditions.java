package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.Identifier;

public interface CriterionConditions {
	Identifier getId();

	default JsonElement method_807() {
		return JsonNull.INSTANCE;
	}
}
