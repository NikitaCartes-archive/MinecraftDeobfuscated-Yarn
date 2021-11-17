package net.minecraft.loot.provider.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.MathHelper;

public class UniformLootNumberProvider implements LootNumberProvider {
	final LootNumberProvider min;
	final LootNumberProvider max;

	UniformLootNumberProvider(LootNumberProvider min, LootNumberProvider max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.UNIFORM;
	}

	public static UniformLootNumberProvider create(float min, float max) {
		return new UniformLootNumberProvider(ConstantLootNumberProvider.create(min), ConstantLootNumberProvider.create(max));
	}

	@Override
	public int nextInt(LootContext context) {
		return MathHelper.nextInt(context.getRandom(), this.min.nextInt(context), this.max.nextInt(context));
	}

	@Override
	public float nextFloat(LootContext context) {
		return MathHelper.nextFloat(context.getRandom(), this.min.nextFloat(context), this.max.nextFloat(context));
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Sets.<LootContextParameter<?>>union(this.min.getRequiredParameters(), this.max.getRequiredParameters());
	}

	public static class Serializer implements JsonSerializer<UniformLootNumberProvider> {
		public UniformLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "min", jsonDeserializationContext, LootNumberProvider.class);
			LootNumberProvider lootNumberProvider2 = JsonHelper.deserialize(jsonObject, "max", jsonDeserializationContext, LootNumberProvider.class);
			return new UniformLootNumberProvider(lootNumberProvider, lootNumberProvider2);
		}

		public void toJson(JsonObject jsonObject, UniformLootNumberProvider uniformLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("min", jsonSerializationContext.serialize(uniformLootNumberProvider.min));
			jsonObject.add("max", jsonSerializationContext.serialize(uniformLootNumberProvider.max));
		}
	}
}
