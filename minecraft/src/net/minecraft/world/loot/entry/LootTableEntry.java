package net.minecraft.world.loot.entry;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.function.LootFunction;

public class LootTableEntry extends LeafEntry {
	private final Identifier id;

	private LootTableEntry(Identifier identifier, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
		super(i, j, lootConditions, lootFunctions);
		this.id = identifier;
	}

	@Override
	public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
		LootSupplier lootSupplier = lootContext.method_301().getSupplier(this.id);
		lootSupplier.drop(lootContext, consumer);
	}

	@Override
	public void method_415(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		if (set.contains(this.id)) {
			lootTableReporter.report("Table " + this.id + " is recursively called");
		} else {
			super.method_415(lootTableReporter, function, set, lootContextType);
			LootSupplier lootSupplier = (LootSupplier)function.apply(this.id);
			if (lootSupplier == null) {
				lootTableReporter.report("Unknown loot table called " + this.id);
			} else {
				Set<Identifier> set2 = ImmutableSet.<Identifier>builder().addAll(set).add(this.id).build();
				lootSupplier.method_330(lootTableReporter.makeChild("->{" + this.id + "}"), function, set2, lootContextType);
			}
		}
	}

	public static LeafEntry.Builder<?> builder(Identifier identifier) {
		return builder((i, j, lootConditions, lootFunctions) -> new LootTableEntry(identifier, i, j, lootConditions, lootFunctions));
	}

	public static class Serializer extends LeafEntry.Serializer<LootTableEntry> {
		public Serializer() {
			super(new Identifier("loot_table"), LootTableEntry.class);
		}

		public void method_431(JsonObject jsonObject, LootTableEntry lootTableEntry, JsonSerializationContext jsonSerializationContext) {
			super.method_442(jsonObject, lootTableEntry, jsonSerializationContext);
			jsonObject.addProperty("name", lootTableEntry.id.toString());
		}

		protected LootTableEntry method_432(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new LootTableEntry(identifier, i, j, lootConditions, lootFunctions);
		}
	}
}
