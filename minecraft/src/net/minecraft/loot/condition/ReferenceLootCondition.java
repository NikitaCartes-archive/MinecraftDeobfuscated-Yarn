package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import org.slf4j.Logger;

public class ReferenceLootCondition implements LootCondition {
	private static final Logger LOGGER = LogUtils.getLogger();
	final Identifier id;

	ReferenceLootCondition(Identifier id) {
		this.id = id;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.REFERENCE;
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootDataKey<LootCondition> lootDataKey = new LootDataKey<>(LootDataType.PREDICATES, this.id);
		if (reporter.isInStack(lootDataKey)) {
			reporter.report("Condition " + this.id + " is recursively called");
		} else {
			LootCondition.super.validate(reporter);
			reporter.getDataLookup()
				.getElementOptional(lootDataKey)
				.ifPresentOrElse(
					predicate -> predicate.validate(reporter.makeChild(".{" + this.id + "}", lootDataKey)), () -> reporter.report("Unknown condition table called " + this.id)
				);
		}
	}

	public boolean test(LootContext lootContext) {
		LootCondition lootCondition = lootContext.getDataLookup().getElement(LootDataType.PREDICATES, this.id);
		if (lootCondition == null) {
			LOGGER.warn("Tried using unknown condition table called {}", this.id);
			return false;
		} else {
			LootContext.Entry<?> entry = LootContext.predicate(lootCondition);
			if (lootContext.markActive(entry)) {
				boolean var4;
				try {
					var4 = lootCondition.test(lootContext);
				} finally {
					lootContext.markInactive(entry);
				}

				return var4;
			} else {
				LOGGER.warn("Detected infinite loop in loot tables");
				return false;
			}
		}
	}

	public static LootCondition.Builder builder(Identifier id) {
		return () -> new ReferenceLootCondition(id);
	}

	public static class Serializer implements JsonSerializer<ReferenceLootCondition> {
		public void toJson(JsonObject jsonObject, ReferenceLootCondition referenceLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("name", referenceLootCondition.id.toString());
		}

		public ReferenceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new ReferenceLootCondition(identifier);
		}
	}
}
