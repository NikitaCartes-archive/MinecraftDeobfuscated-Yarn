package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;

public abstract class CombinedEntry extends LootEntry {
	protected final LootEntry[] children;
	private final EntryCombiner predicate;

	protected CombinedEntry(LootEntry[] lootEntrys, class_4570[] args) {
		super(args);
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
			protected T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] lootEntrys, class_4570[] args) {
				return factory.create(lootEntrys, args);
			}
		};
	}

	@FunctionalInterface
	public interface Factory<T extends CombinedEntry> {
		T create(LootEntry[] lootEntrys, class_4570[] args);
	}

	public abstract static class Serializer<T extends CombinedEntry> extends LootEntry.Serializer<T> {
		public Serializer(Identifier identifier, Class<T> class_) {
			super(identifier, class_);
		}

		public void method_397(JsonObject jsonObject, T combinedEntry, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("children", jsonSerializationContext.serialize(combinedEntry.children));
		}

		public final T method_396(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
			LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, lootEntrys, args);
		}

		protected abstract T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] lootEntrys, class_4570[] args);
	}
}
