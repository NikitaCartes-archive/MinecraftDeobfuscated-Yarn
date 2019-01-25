package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Nameable;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class CopyNameLootFunction extends ConditionalLootFunction {
	private final CopyNameLootFunction.Source source;

	private CopyNameLootFunction(LootCondition[] lootConditions, CopyNameLootFunction.Source source) {
		super(lootConditions);
		this.source = source;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.source.field_1024);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Object object = lootContext.method_296(this.source.field_1024);
		if (object instanceof Nameable) {
			Nameable nameable = (Nameable)object;
			if (nameable.hasCustomName()) {
				itemStack.setDisplayName(nameable.getDisplayName());
			}
		}

		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> create(CopyNameLootFunction.Source source) {
		return create(lootConditions -> new CopyNameLootFunction(lootConditions, source));
	}

	public static class Factory extends ConditionalLootFunction.Factory<CopyNameLootFunction> {
		public Factory() {
			super(new Identifier("copy_name"), CopyNameLootFunction.class);
		}

		public void method_476(JsonObject jsonObject, CopyNameLootFunction copyNameLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, copyNameLootFunction, jsonSerializationContext);
			jsonObject.addProperty("source", copyNameLootFunction.source.name);
		}

		public CopyNameLootFunction method_477(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			CopyNameLootFunction.Source source = CopyNameLootFunction.Source.get(JsonHelper.getString(jsonObject, "source"));
			return new CopyNameLootFunction(lootConditions, source);
		}
	}

	public static enum Source {
		THIS("this", LootContextParameters.field_1226),
		KILLER("killer", LootContextParameters.field_1230),
		KILLER_PLAYER("killer_player", LootContextParameters.field_1233),
		BLOCK_ENTITY("block_entity", LootContextParameters.field_1228);

		public final String name;
		public final LootContextParameter<?> field_1024;

		private Source(String string2, LootContextParameter<?> lootContextParameter) {
			this.name = string2;
			this.field_1024 = lootContextParameter;
		}

		public static CopyNameLootFunction.Source get(String string) {
			for (CopyNameLootFunction.Source source : values()) {
				if (source.name.equals(string)) {
					return source;
				}
			}

			throw new IllegalArgumentException("Invalid name source " + string);
		}
	}
}
