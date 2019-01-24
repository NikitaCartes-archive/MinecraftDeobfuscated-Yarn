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
import net.minecraft.world.loot.context.Parameter;
import net.minecraft.world.loot.context.Parameters;

public class CopyNameLootFunction extends ConditionalLootFunction {
	private final CopyNameLootFunction.Source source;

	private CopyNameLootFunction(LootCondition[] lootConditions, CopyNameLootFunction.Source source) {
		super(lootConditions);
		this.source = source;
	}

	@Override
	public Set<Parameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.source.field_1024);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Object object = lootContext.get(this.source.field_1024);
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
			jsonObject.addProperty("source", copyNameLootFunction.source.field_1025);
		}

		public CopyNameLootFunction method_477(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			CopyNameLootFunction.Source source = CopyNameLootFunction.Source.method_475(JsonHelper.getString(jsonObject, "source"));
			return new CopyNameLootFunction(lootConditions, source);
		}
	}

	public static enum Source {
		THIS("this", Parameters.field_1226),
		KILLER("killer", Parameters.field_1230),
		KILLER_PLAYER("killer_player", Parameters.field_1233),
		BLOCK_ENTITY("block_entity", Parameters.field_1228);

		public final String field_1025;
		public final Parameter<?> field_1024;

		private Source(String string2, Parameter<?> parameter) {
			this.field_1025 = string2;
			this.field_1024 = parameter;
		}

		public static CopyNameLootFunction.Source method_475(String string) {
			for (CopyNameLootFunction.Source source : values()) {
				if (source.field_1025.equals(string)) {
					return source;
				}
			}

			throw new IllegalArgumentException("Invalid name source " + string);
		}
	}
}
