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

	public ReferenceLootCondition(Identifier identifier) {
		this.id = identifier;
	}

	@Override
	public void check(LootTableReporter lootTableReporter) {
		if (lootTableReporter.hasCondition(this.id)) {
			lootTableReporter.report("Condition " + this.id + " is recursively called");
		} else {
			LootCondition.super.check(lootTableReporter);
			LootCondition lootCondition = lootTableReporter.getCondition(this.id);
			if (lootCondition == null) {
				lootTableReporter.report("Unknown condition table called " + this.id);
			} else {
				lootCondition.check(lootTableReporter.withSupplier(".{" + this.id + "}", this.id));
			}
		}
	}

	public boolean method_22579(LootContext lootContext) {
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

		public void method_22582(JsonObject jsonObject, ReferenceLootCondition referenceLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("name", referenceLootCondition.id.toString());
		}

		public ReferenceLootCondition method_22581(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new ReferenceLootCondition(identifier);
		}
	}
}
