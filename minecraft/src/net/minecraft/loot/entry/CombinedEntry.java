package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public abstract class CombinedEntry extends LootEntry {
	protected final LootEntry[] children;
	private final EntryCombiner predicate;

	protected CombinedEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
		super(lootConditions);
		this.children = lootEntrys;
		this.predicate = this.combine(lootEntrys);
	}

	@Override
	public void check(LootTableReporter lootTableReporter) {
		super.check(lootTableReporter);
		if (this.children.length == 0) {
			lootTableReporter.report("Empty children list");
		}

		for (int i = 0; i < this.children.length; i++) {
			this.children[i].check(lootTableReporter.makeChild(".entry[" + i + "]"));
		}
	}

	protected abstract EntryCombiner combine(EntryCombiner[] entryCombiners);

	@Override
	public final boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
		return !this.test(lootContext) ? false : this.predicate.expand(lootContext, consumer);
	}

	public static <T extends CombinedEntry> CombinedEntry.Serializer<T> createSerializer(Identifier identifier, Class<T> class_, CombinedEntry.Factory<T> factory) {
		return new CombinedEntry.Serializer<T>(identifier, class_) {
			@Override
			protected T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] lootEntrys, LootCondition[] lootConditions) {
				return factory.create(lootEntrys, lootConditions);
			}
		};
	}

	@FunctionalInterface
	public interface Factory<T extends CombinedEntry> {
		T create(LootEntry[] lootEntrys, LootCondition[] lootConditions);
	}

	public abstract static class Serializer<T extends CombinedEntry> extends LootEntry.Serializer<T> {
		public Serializer(Identifier identifier, Class<T> class_) {
			super(identifier, class_);
		}

		public void method_397(JsonObject jsonObject, T combinedEntry, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("children", jsonSerializationContext.serialize(combinedEntry.children));
		}

		public final T method_396(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, lootEntrys, lootConditions);
		}

		protected abstract T fromJson(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] lootEntrys, LootCondition[] lootConditions
		);
	}
}
