package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;

public class SetLootTableLootFunction extends ConditionalLootFunction {
	private final Identifier id;
	private final long seed;

	private SetLootTableLootFunction(class_4570[] args, Identifier identifier, long l) {
		super(args);
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
	public void check(LootTableReporter lootTableReporter) {
		if (lootTableReporter.method_22570(this.id)) {
			lootTableReporter.report("Table " + this.id + " is recursively called");
		} else {
			super.check(lootTableReporter);
			LootSupplier lootSupplier = lootTableReporter.method_22574(this.id);
			if (lootSupplier == null) {
				lootTableReporter.report("Unknown loot table called " + this.id);
			} else {
				lootSupplier.check(lootTableReporter.method_22569("->{" + this.id + "}", this.id));
			}
		}
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetLootTableLootFunction> {
		protected Factory() {
			super(new Identifier("set_loot_table"), SetLootTableLootFunction.class);
		}

		public void method_626(JsonObject jsonObject, SetLootTableLootFunction setLootTableLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setLootTableLootFunction, jsonSerializationContext);
			jsonObject.addProperty("name", setLootTableLootFunction.id.toString());
			if (setLootTableLootFunction.seed != 0L) {
				jsonObject.addProperty("seed", setLootTableLootFunction.seed);
			}
		}

		public SetLootTableLootFunction method_627(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			long l = JsonHelper.getLong(jsonObject, "seed", 0L);
			return new SetLootTableLootFunction(args, identifier, l);
		}
	}
}
