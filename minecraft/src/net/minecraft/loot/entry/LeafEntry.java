package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LeafEntry extends LootPoolEntry {
	protected final int weight;
	protected final int quality;
	protected final LootFunction[] functions;
	private final BiFunction<ItemStack, LootContext, ItemStack> compiledFunctions;
	private final LootChoice choice = new LeafEntry.Choice() {
		@Override
		public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
			LeafEntry.this.generateLoot(LootFunction.apply(LeafEntry.this.compiledFunctions, lootConsumer, context), context);
		}
	};

	protected LeafEntry(int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		super(conditions);
		this.weight = weight;
		this.quality = quality;
		this.functions = functions;
		this.compiledFunctions = LootFunctionTypes.join(functions);
	}

	@Override
	public void validate(LootTableReporter reporter) {
		super.validate(reporter);

		for (int i = 0; i < this.functions.length; i++) {
			this.functions[i].validate(reporter.makeChild(".functions[" + i + "]"));
		}
	}

	protected abstract void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context);

	@Override
	public boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
		if (this.test(lootContext)) {
			consumer.accept(this.choice);
			return true;
		} else {
			return false;
		}
	}

	public static LeafEntry.Builder<?> builder(LeafEntry.Factory factory) {
		return new LeafEntry.BasicBuilder(factory);
	}

	static class BasicBuilder extends LeafEntry.Builder<LeafEntry.BasicBuilder> {
		private final LeafEntry.Factory factory;

		public BasicBuilder(LeafEntry.Factory factory) {
			this.factory = factory;
		}

		protected LeafEntry.BasicBuilder getThisBuilder() {
			return this;
		}

		@Override
		public LootPoolEntry build() {
			return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
		}
	}

	public abstract static class Builder<T extends LeafEntry.Builder<T>> extends LootPoolEntry.Builder<T> implements LootFunctionConsumingBuilder<T> {
		protected int weight = 1;
		protected int quality = 0;
		private final List<LootFunction> functions = Lists.<LootFunction>newArrayList();

		public T apply(LootFunction.Builder builder) {
			this.functions.add(builder.build());
			return this.getThisBuilder();
		}

		protected LootFunction[] getFunctions() {
			return (LootFunction[])this.functions.toArray(new LootFunction[0]);
		}

		public T weight(int weight) {
			this.weight = weight;
			return this.getThisBuilder();
		}

		public T quality(int quality) {
			this.quality = quality;
			return this.getThisBuilder();
		}
	}

	public abstract class Choice implements LootChoice {
		protected Choice() {
		}

		@Override
		public int getWeight(float luck) {
			return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * luck), 0);
		}
	}

	@FunctionalInterface
	public interface Factory {
		LeafEntry build(int weight, int quality, LootCondition[] conditions, LootFunction[] functions);
	}

	public abstract static class Serializer<T extends LeafEntry> extends LootPoolEntry.Serializer<T> {
		public void addEntryFields(JsonObject jsonObject, T leafEntry, JsonSerializationContext jsonSerializationContext) {
			if (leafEntry.weight != 1) {
				jsonObject.addProperty("weight", leafEntry.weight);
			}

			if (leafEntry.quality != 0) {
				jsonObject.addProperty("quality", leafEntry.quality);
			}

			if (!ArrayUtils.isEmpty((Object[])leafEntry.functions)) {
				jsonObject.add("functions", jsonSerializationContext.serialize(leafEntry.functions));
			}
		}

		public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			int i = JsonHelper.getInt(jsonObject, "weight", 1);
			int j = JsonHelper.getInt(jsonObject, "quality", 0);
			LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, i, j, lootConditions, lootFunctions);
		}

		protected abstract T fromJson(
			JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions
		);
	}
}
