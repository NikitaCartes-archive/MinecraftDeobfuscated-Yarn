package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamageLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final UniformLootTableRange durabilityRange;

	private SetDamageLootFunction(LootCondition[] lootConditions, UniformLootTableRange uniformLootTableRange) {
		super(lootConditions);
		this.durabilityRange = uniformLootTableRange;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (itemStack.isDamageable()) {
			float f = 1.0F - this.durabilityRange.nextFloat(lootContext.getRandom());
			itemStack.setDamage(MathHelper.floor(f * (float)itemStack.getMaxDamage()));
		} else {
			LOGGER.warn("Couldn't set damage of loot item {}", itemStack);
		}

		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> builder(UniformLootTableRange uniformLootTableRange) {
		return builder(lootConditions -> new SetDamageLootFunction(lootConditions, uniformLootTableRange));
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetDamageLootFunction> {
		protected Factory() {
			super(new Identifier("set_damage"), SetDamageLootFunction.class);
		}

		public void method_636(JsonObject jsonObject, SetDamageLootFunction setDamageLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setDamageLootFunction, jsonSerializationContext);
			jsonObject.add("damage", jsonSerializationContext.serialize(setDamageLootFunction.durabilityRange));
		}

		public SetDamageLootFunction method_635(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			return new SetDamageLootFunction(lootConditions, JsonHelper.deserialize(jsonObject, "damage", jsonDeserializationContext, UniformLootTableRange.class));
		}
	}
}
