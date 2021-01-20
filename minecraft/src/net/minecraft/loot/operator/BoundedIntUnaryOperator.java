package net.minecraft.loot.operator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class BoundedIntUnaryOperator {
	@Nullable
	private final LootNumberProvider min;
	@Nullable
	private final LootNumberProvider max;
	private final BoundedIntUnaryOperator.Applier applier;
	private final BoundedIntUnaryOperator.Tester tester;

	public Set<LootContextParameter<?>> getRequiredParameters() {
		Builder<LootContextParameter<?>> builder = ImmutableSet.builder();
		if (this.min != null) {
			builder.addAll(this.min.getRequiredParameters());
		}

		if (this.max != null) {
			builder.addAll(this.max.getRequiredParameters());
		}

		return builder.build();
	}

	private BoundedIntUnaryOperator(@Nullable LootNumberProvider min, @Nullable LootNumberProvider max) {
		this.min = min;
		this.max = max;
		if (min == null) {
			if (max == null) {
				this.applier = (context, value) -> value;
				this.tester = (context, value) -> true;
			} else {
				this.applier = (context, value) -> Math.min(max.nextInt(context), value);
				this.tester = (context, value) -> value <= max.nextInt(context);
			}
		} else if (max == null) {
			this.applier = (context, value) -> Math.max(min.nextInt(context), value);
			this.tester = (context, value) -> value >= min.nextInt(context);
		} else {
			this.applier = (context, value) -> MathHelper.clamp(value, min.nextInt(context), max.nextInt(context));
			this.tester = (context, value) -> value >= min.nextInt(context) && value <= max.nextInt(context);
		}
	}

	public static BoundedIntUnaryOperator create(int value) {
		ConstantLootNumberProvider constantLootNumberProvider = ConstantLootNumberProvider.create((float)value);
		return new BoundedIntUnaryOperator(constantLootNumberProvider, constantLootNumberProvider);
	}

	public static BoundedIntUnaryOperator create(int min, int max) {
		return new BoundedIntUnaryOperator(ConstantLootNumberProvider.create((float)min), ConstantLootNumberProvider.create((float)max));
	}

	public static BoundedIntUnaryOperator createMin(int min) {
		return new BoundedIntUnaryOperator(ConstantLootNumberProvider.create((float)min), null);
	}

	public static BoundedIntUnaryOperator createMax(int max) {
		return new BoundedIntUnaryOperator(null, ConstantLootNumberProvider.create((float)max));
	}

	public int apply(LootContext context, int value) {
		return this.applier.apply(context, value);
	}

	public boolean test(LootContext context, int value) {
		return this.tester.test(context, value);
	}

	@FunctionalInterface
	interface Applier {
		int apply(LootContext context, int value);
	}

	public static class Serializer implements JsonDeserializer<BoundedIntUnaryOperator>, JsonSerializer<BoundedIntUnaryOperator> {
		public BoundedIntUnaryOperator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
			if (jsonElement.isJsonPrimitive()) {
				return BoundedIntUnaryOperator.create(jsonElement.getAsInt());
			} else {
				JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
				LootNumberProvider lootNumberProvider = jsonObject.has("min")
					? JsonHelper.deserialize(jsonObject, "min", jsonDeserializationContext, LootNumberProvider.class)
					: null;
				LootNumberProvider lootNumberProvider2 = jsonObject.has("max")
					? JsonHelper.deserialize(jsonObject, "max", jsonDeserializationContext, LootNumberProvider.class)
					: null;
				return new BoundedIntUnaryOperator(lootNumberProvider, lootNumberProvider2);
			}
		}

		public JsonElement serialize(BoundedIntUnaryOperator boundedIntUnaryOperator, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (Objects.equals(boundedIntUnaryOperator.max, boundedIntUnaryOperator.min)) {
				return jsonSerializationContext.serialize(boundedIntUnaryOperator.min);
			} else {
				if (boundedIntUnaryOperator.max != null) {
					jsonObject.add("max", jsonSerializationContext.serialize(boundedIntUnaryOperator.max));
				}

				if (boundedIntUnaryOperator.min != null) {
					jsonObject.add("min", jsonSerializationContext.serialize(boundedIntUnaryOperator.min));
				}

				return jsonObject;
			}
		}
	}

	@FunctionalInterface
	interface Tester {
		boolean test(LootContext context, int value);
	}
}
