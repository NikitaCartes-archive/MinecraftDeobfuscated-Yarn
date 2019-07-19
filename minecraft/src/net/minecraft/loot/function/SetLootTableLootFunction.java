package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class SetLootTableLootFunction extends ConditionalLootFunction {
	private final Identifier id;
	private final long seed;

	private SetLootTableLootFunction(LootCondition[] conditions, Identifier id, long seed) {
		super(conditions);
		this.id = id;
		this.seed = seed;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("LootTable", this.id.toString());
			if (this.seed != 0L) {
				compoundTag.putLong("LootTableSeed", this.seed);
			}

			stack.getOrCreateTag().put("BlockEntityTag", compoundTag);
			return stack;
		}
	}

	@Override
	public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
		if (parentLootTables.contains(this.id)) {
			reporter.report("Table " + this.id + " is recursively called");
		} else {
			super.check(reporter, supplierGetter, parentLootTables, contextType);
			LootTable lootTable = (LootTable)supplierGetter.apply(this.id);
			if (lootTable == null) {
				reporter.report("Unknown loot table called " + this.id);
			} else {
				Set<Identifier> set = ImmutableSet.<Identifier>builder().addAll(parentLootTables).add(this.id).build();
				lootTable.check(reporter.makeChild("->{" + this.id + "}"), supplierGetter, set, contextType);
			}
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetLootTableLootFunction> {
		protected Factory() {
			super(new Identifier("set_loot_table"), SetLootTableLootFunction.class);
		}

		public void toJson(JsonObject jsonObject, SetLootTableLootFunction setLootTableLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setLootTableLootFunction, jsonSerializationContext);
			jsonObject.addProperty("name", setLootTableLootFunction.id.toString());
			if (setLootTableLootFunction.seed != 0L) {
				jsonObject.addProperty("seed", setLootTableLootFunction.seed);
			}
		}

		public SetLootTableLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			long l = JsonHelper.getLong(jsonObject, "seed", 0L);
			return new SetLootTableLootFunction(lootConditions, identifier, l);
		}
	}
}
