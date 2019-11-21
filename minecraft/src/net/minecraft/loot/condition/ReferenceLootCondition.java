package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReferenceLootCondition implements LootCondition {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Identifier id;

	public ReferenceLootCondition(Identifier id) {
		this.id = id;
	}

	@Override
	public void check(LootTableReporter reporter) {
		if (reporter.hasCondition(this.id)) {
			reporter.report("Condition " + this.id + " is recursively called");
		} else {
			LootCondition.super.check(reporter);
			LootCondition lootCondition = reporter.getCondition(this.id);
			if (lootCondition == null) {
				reporter.report("Unknown condition table called " + this.id);
			} else {
				lootCondition.check(reporter.withSupplier(".{" + this.id + "}", this.id));
			}
		}
	}

	public boolean test(LootContext lootContext) {
		LootCondition lootCondition = lootContext.getCondition(this.id);
		if (lootContext.addCondition(lootCondition)) {
			boolean var3;
			try {
				var3 = lootCondition.test(lootContext);
			} finally {
				lootContext.removeCondition(lootCondition);
			}

			return var3;
		} else {
			LOGGER.warn("Detected infinite loop in loot tables");
			return false;
		}
	}

	public static class Factory extends LootCondition.Factory<ReferenceLootCondition> {
		protected Factory() {
			super(new Identifier("reference"), ReferenceLootCondition.class);
		}

		public void toJson(JsonObject jsonObject, ReferenceLootCondition referenceLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("name", referenceLootCondition.id.toString());
		}

		public ReferenceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new ReferenceLootCondition(identifier);
		}
	}
}
