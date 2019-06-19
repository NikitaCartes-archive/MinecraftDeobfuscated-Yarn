package net.minecraft.world.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.FunctionConsumerBuilder;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootFunctions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LeafEntry extends LootEntry {
	protected final int weight;
	protected final int quality;
	protected final LootFunction[] functions;
	private final BiFunction<ItemStack, LootContext, ItemStack> compiledFunctions;
	private final LootChoice choice = new LeafEntry.Choice() {
		@Override
		public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
			LeafEntry.this.drop(LootFunction.apply(LeafEntry.this.compiledFunctions, consumer, lootContext), lootContext);
		}
	};

	protected LeafEntry(int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions) {
		super(lootConditions);
		this.weight = i;
		this.quality = j;
		this.functions = lootFunctions;
		this.compiledFunctions = LootFunctions.join(lootFunctions);
	}

	@Override
	public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		super.check(lootTableReporter, function, set, lootContextType);

		for (int i = 0; i < this.functions.length; i++) {
			this.functions[i].check(lootTableReporter.makeChild(".functions[" + i + "]"), function, set, lootContextType);
		}
	}

	protected abstract void drop(Consumer<ItemStack> consumer, LootContext lootContext);

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

		protected LeafEntry.BasicBuilder method_440() {
			return this;
		}

		@Override
		public LootEntry build() {
			return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
		}
	}

	public abstract static class Builder<T extends LeafEntry.Builder<T>> extends LootEntry.Builder<T> implements FunctionConsumerBuilder<T> {
		protected int weight = 1;
		protected int quality = 0;
		private final List<LootFunction> functions = Lists.<LootFunction>newArrayList();

		public T method_438(LootFunction.Builder builder) {
			this.functions.add(builder.build());
			return this.getThisBuilder();
		}

		protected LootFunction[] getFunctions() {
			return (LootFunction[])this.functions.toArray(new LootFunction[0]);
		}

		public T setWeight(int i) {
			this.weight = i;
			return this.getThisBuilder();
		}

		public T setQuality(int i) {
			this.quality = i;
			return this.getThisBuilder();
		}
	}

	public abstract class Choice implements LootChoice {
		protected Choice() {
		}

		@Override
		public int getWeight(float f) {
			return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * f), 0);
		}
	}

	@FunctionalInterface
	public interface Factory {
		LeafEntry build(int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions);
	}

	public abstract static class Serializer<T extends LeafEntry> extends LootEntry.Serializer<T> {
		public Serializer(Identifier identifier, Class<T> class_) {
			super(identifier, class_);
		}

		public void method_442(JsonObject jsonObject, T leafEntry, JsonSerializationContext jsonSerializationContext) {
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

		public final T method_441(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			int i = JsonHelper.getInt(jsonObject, "weight", 1);
			int j = JsonHelper.getInt(jsonObject, "quality", 0);
			LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
			return this.fromJson(jsonObject, jsonDeserializationContext, i, j, lootConditions, lootFunctions);
		}

		protected abstract T fromJson(
			JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] lootConditions, LootFunction[] lootFunctions
		);
	}
}
