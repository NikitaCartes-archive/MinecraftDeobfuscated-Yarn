package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.LootChoiceProvider;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;

public abstract class CombinedEntry extends LootEntry {
	final LootEntry[] children;
	final LootChoiceProvider predicate;

	protected CombinedEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
		super(lootConditions);
		this.children = lootEntrys;
		this.predicate = this.combine(lootEntrys);
	}

	@Override
	public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		super.check(lootTableReporter, function, set, lootContextType);
		if (this.children.length == 0) {
			lootTableReporter.report("Empty children list");
		}

		for (int i = 0; i < this.children.length; i++) {
			this.children[i].check(lootTableReporter.makeChild(".entry[" + i + "]"), function, set, lootContextType);
		}
	}

	protected abstract LootChoiceProvider combine(LootChoiceProvider[] lootChoiceProviders);

	@Override
	public final boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
		return !this.test(lootContext) ? false : this.predicate.expand(lootContext, consumer);
	}

	public static <T extends CombinedEntry> CombinedEntry.Serializer<T> createSerializer(Identifier identifier, Class<T> class_, CombinedEntry.Factory<T> factory) {
		return new CombinedEntry.Serializer<T>(identifier, class_) {
			@Override
			protected T method_398(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] lootEntrys, LootCondition[] lootConditions) {
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

		public void toJson(JsonObject jsonObject, T combinedEntry, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("children", jsonSerializationContext.serialize(combinedEntry.children));
		}

		public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
			return this.method_398(jsonObject, jsonDeserializationContext, lootEntrys, lootConditions);
		}

		protected abstract T method_398(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] lootEntrys, LootCondition[] lootConditions
		);
	}
}
