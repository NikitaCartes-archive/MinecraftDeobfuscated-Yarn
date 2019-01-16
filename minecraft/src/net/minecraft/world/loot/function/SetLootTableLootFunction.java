package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;

public class SetLootTableLootFunction extends ConditionalLootFunction {
	private final Identifier id;
	private final long seed;

	private SetLootTableLootFunction(LootCondition[] lootConditions, Identifier identifier, long l) {
		super(lootConditions);
		this.id = identifier;
		this.seed = l;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (itemStack.isEmpty()) {
			return itemStack;
		} else {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putString("LootTable", this.id.toString());
			if (this.seed != 0L) {
				compoundTag.putLong("LootTableSeed", this.seed);
			}

			itemStack.getOrCreateTag().put("BlockEntityTag", compoundTag);
			return itemStack;
		}
	}

	@Override
	public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		if (set.contains(this.id)) {
			lootTableReporter.report("Table " + this.id + " is recursively called");
		} else {
			super.check(lootTableReporter, function, set, lootContextType);
			LootSupplier lootSupplier = (LootSupplier)function.apply(this.id);
			if (lootSupplier == null) {
				lootTableReporter.report("Unknown loot table called " + this.id);
			} else {
				Set<Identifier> set2 = ImmutableSet.<Identifier>builder().addAll(set).add(this.id).build();
				lootSupplier.check(lootTableReporter.makeChild("->{" + this.id + "}"), function, set2, lootContextType);
			}
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetLootTableLootFunction> {
		protected Factory() {
			super(new Identifier("set_loot_table"), SetLootTableLootFunction.class);
		}

		public void method_626(JsonObject jsonObject, SetLootTableLootFunction setLootTableLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setLootTableLootFunction, jsonSerializationContext);
			jsonObject.addProperty("name", setLootTableLootFunction.id.toString());
			if (setLootTableLootFunction.seed != 0L) {
				jsonObject.addProperty("seed", setLootTableLootFunction.seed);
			}
		}

		public SetLootTableLootFunction method_627(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			long l = JsonHelper.getLong(jsonObject, "seed", 0L);
			return new SetLootTableLootFunction(lootConditions, identifier, l);
		}
	}
}
