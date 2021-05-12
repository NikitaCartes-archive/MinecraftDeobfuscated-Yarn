package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamageLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	final LootNumberProvider durabilityRange;
	final boolean add;

	SetDamageLootFunction(LootCondition[] lootConditions, LootNumberProvider lootNumberProvider, boolean bl) {
		super(lootConditions);
		this.durabilityRange = lootNumberProvider;
		this.add = bl;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_DAMAGE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.durabilityRange.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isDamageable()) {
			int i = stack.getMaxDamage();
			float f = this.add ? 1.0F - (float)stack.getDamage() / (float)i : 0.0F;
			float g = 1.0F - MathHelper.clamp(this.durabilityRange.nextFloat(context) + f, 0.0F, 1.0F);
			stack.setDamage(MathHelper.floor(g * (float)i));
		} else {
			LOGGER.warn("Couldn't set damage of loot item {}", stack);
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider durabilityRange) {
		return builder(conditions -> new SetDamageLootFunction(conditions, durabilityRange, false));
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider durabilityRange, boolean add) {
		return builder(conditions -> new SetDamageLootFunction(conditions, durabilityRange, add));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetDamageLootFunction> {
		public void toJson(JsonObject jsonObject, SetDamageLootFunction setDamageLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setDamageLootFunction, jsonSerializationContext);
			jsonObject.add("damage", jsonSerializationContext.serialize(setDamageLootFunction.durabilityRange));
			jsonObject.addProperty("add", setDamageLootFunction.add);
		}

		public SetDamageLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "damage", jsonDeserializationContext, LootNumberProvider.class);
			boolean bl = JsonHelper.getBoolean(jsonObject, "add", false);
			return new SetDamageLootFunction(lootConditions, lootNumberProvider, bl);
		}
	}
}
