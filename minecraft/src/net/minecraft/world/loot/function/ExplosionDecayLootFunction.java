package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

public class ExplosionDecayLootFunction extends ConditionalLootFunction {
	private ExplosionDecayLootFunction(class_4570[] args) {
		super(args);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Float float_ = lootContext.get(LootContextParameters.EXPLOSION_RADIUS);
		if (float_ != null) {
			Random random = lootContext.getRandom();
			float f = 1.0F / float_;
			int i = itemStack.getCount();
			int j = 0;

			for (int k = 0; k < i; k++) {
				if (random.nextFloat() <= f) {
					j++;
				}
			}

			itemStack.setCount(j);
		}

		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> builder() {
		return builder(ExplosionDecayLootFunction::new);
	}

	public static class Factory extends ConditionalLootFunction.Factory<ExplosionDecayLootFunction> {
		protected Factory() {
			super(new Identifier("explosion_decay"), ExplosionDecayLootFunction.class);
		}

		public ExplosionDecayLootFunction method_479(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
			return new ExplosionDecayLootFunction(args);
		}
	}
}
