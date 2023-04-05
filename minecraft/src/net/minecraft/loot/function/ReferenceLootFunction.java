package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public class ReferenceLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	final Identifier name;

	ReferenceLootFunction(LootCondition[] conditions, Identifier name) {
		super(conditions);
		this.name = name;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.REFERENCE;
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootDataKey<LootFunction> lootDataKey = new LootDataKey<>(LootDataType.ITEM_MODIFIERS, this.name);
		if (reporter.isInStack(lootDataKey)) {
			reporter.report("Function " + this.name + " is recursively called");
		} else {
			super.validate(reporter);
			reporter.getDataLookup()
				.getElementOptional(lootDataKey)
				.ifPresentOrElse(
					itemModifier -> itemModifier.validate(reporter.makeChild(".{" + this.name + "}", lootDataKey)),
					() -> reporter.report("Unknown function table called " + this.name)
				);
		}
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		LootFunction lootFunction = context.getDataLookup().getElement(LootDataType.ITEM_MODIFIERS, this.name);
		if (lootFunction == null) {
			LOGGER.warn("Unknown function: {}", this.name);
			return stack;
		} else {
			LootContext.Entry<?> entry = LootContext.itemModifier(lootFunction);
			if (context.markActive(entry)) {
				ItemStack var5;
				try {
					var5 = (ItemStack)lootFunction.apply(stack, context);
				} finally {
					context.markInactive(entry);
				}

				return var5;
			} else {
				LOGGER.warn("Detected infinite loop in loot tables");
				return stack;
			}
		}
	}

	public static ConditionalLootFunction.Builder<?> builder(Identifier name) {
		return builder(conditions -> new ReferenceLootFunction(conditions, name));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<ReferenceLootFunction> {
		public void toJson(JsonObject jsonObject, ReferenceLootFunction referenceLootFunction, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("name", referenceLootFunction.name.toString());
		}

		public ReferenceLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new ReferenceLootFunction(lootConditions, identifier);
		}
	}
}
