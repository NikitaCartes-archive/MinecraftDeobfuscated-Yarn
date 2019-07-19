package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public abstract class CombinedEntry extends LootEntry {
	protected final LootEntry[] children;
	private final EntryCombiner predicate;

	protected CombinedEntry(LootEntry[] children, LootCondition[] conditions) {
		super(conditions);
		this.children = children;
		this.predicate = this.combine(children);
	}

	@Override
	public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
		super.check(reporter, supplierGetter, parentLootTables, contextType);
		if (this.children.length == 0) {
			reporter.report("Empty children list");
		}

		for (int i = 0; i < this.children.length; i++) {
			this.children[i].check(reporter.makeChild(".entry[" + i + "]"), supplierGetter, parentLootTables, contextType);
		}
	}

	protected abstract EntryCombiner combine(EntryCombiner[] children);

	@Override
	public final boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
		return !this.test(lootContext) ? false : this.predicate.expand(lootContext, consumer);
	}

	public static <T extends CombinedEntry> CombinedEntry.Serializer<T> createSerializer(Identifier id, Class<T> type, CombinedEntry.Factory<T> entry) {
		return new CombinedEntry.Serializer<T>(id, type) {
			@Override
			protected T fromJson(JsonObject json, JsonDeserializationContext context, LootEntry[] children, LootCondition[] conditions) {
				return entry.create(children, conditions);
			}
		};
	}

	@FunctionalInterface
	public interface Factory<T extends CombinedEntry> {
		T create(LootEntry[] children, LootCondition[] conditions);
	}

	public abstract static class Serializer<T extends CombinedEntry> extends LootEntry.Serializer<T> {
		public Serializer(Identifier identifier, Class<T> class_) {
			super(identifier, class_);
		}

		public void toJson(JsonObject jsonObject, T combinedEntry, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("children", jsonSerializationContext.serialize(combinedEntry.children));
		}

		public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, lootEntrys, lootConditions);
		}

		protected abstract T fromJson(JsonObject json, JsonDeserializationContext context, LootEntry[] children, LootCondition[] conditions);
	}
}
