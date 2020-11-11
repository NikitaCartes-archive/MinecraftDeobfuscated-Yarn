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
	private final BoundedIntUnaryOperator.class_5639 field_27897;
	private final BoundedIntUnaryOperator.class_5638 field_27898;

	public Set<LootContextParameter<?>> method_32386() {
		Builder<LootContextParameter<?>> builder = ImmutableSet.builder();
		if (this.min != null) {
			builder.addAll(this.min.getRequiredParameters());
		}

		if (this.max != null) {
			builder.addAll(this.max.getRequiredParameters());
		}

		return builder.build();
	}

	private BoundedIntUnaryOperator(@Nullable LootNumberProvider lootNumberProvider, @Nullable LootNumberProvider lootNumberProvider2) {
		this.min = lootNumberProvider;
		this.max = lootNumberProvider2;
		if (lootNumberProvider == null) {
			if (lootNumberProvider2 == null) {
				this.field_27897 = (lootContext, i) -> i;
				this.field_27898 = (lootContext, i) -> true;
			} else {
				this.field_27897 = (lootContext, i) -> Math.min(lootNumberProvider2.nextInt(lootContext), i);
				this.field_27898 = (lootContext, i) -> i <= lootNumberProvider2.nextInt(lootContext);
			}
		} else if (lootNumberProvider2 == null) {
			this.field_27897 = (lootContext, i) -> Math.max(lootNumberProvider.nextInt(lootContext), i);
			this.field_27898 = (lootContext, i) -> i >= lootNumberProvider.nextInt(lootContext);
		} else {
			this.field_27897 = (lootContext, i) -> MathHelper.clamp(i, lootNumberProvider.nextInt(lootContext), lootNumberProvider2.nextInt(lootContext));
			this.field_27898 = (lootContext, i) -> i >= lootNumberProvider.nextInt(lootContext) && i <= lootNumberProvider2.nextInt(lootContext);
		}
	}

	public static BoundedIntUnaryOperator method_32387(int i) {
		ConstantLootNumberProvider constantLootNumberProvider = ConstantLootNumberProvider.create((float)i);
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

	public int method_32389(LootContext lootContext, int i) {
		return this.field_27897.apply(lootContext, i);
	}

	public boolean method_32393(LootContext lootContext, int i) {
		return this.field_27898.test(lootContext, i);
	}

	public static class Serializer implements JsonDeserializer<BoundedIntUnaryOperator>, JsonSerializer<BoundedIntUnaryOperator> {
		public BoundedIntUnaryOperator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
			if (jsonElement.isJsonPrimitive()) {
				return BoundedIntUnaryOperator.method_32387(jsonElement.getAsInt());
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
	interface class_5638 {
		boolean test(LootContext lootContext, int i);
	}

	@FunctionalInterface
	interface class_5639 {
		int apply(LootContext lootContext, int i);
	}
}
