package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public interface CriterionConditions {
	Identifier getId();

	JsonObject toJson();
}
