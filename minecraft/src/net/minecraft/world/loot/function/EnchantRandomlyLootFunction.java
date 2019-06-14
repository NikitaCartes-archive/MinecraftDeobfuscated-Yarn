package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Enchantment> enchantments;

	private EnchantRandomlyLootFunction(LootCondition[] lootConditions, Collection<Enchantment> collection) {
		super(lootConditions);
		this.enchantments = ImmutableList.copyOf(collection);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		Random random = lootContext.getRandom();
		Enchantment enchantment2;
		if (this.enchantments.isEmpty()) {
			List<Enchantment> list = Lists.<Enchantment>newArrayList();

			for (Enchantment enchantment : Registry.ENCHANTMENT) {
				if (itemStack.getItem() == Items.field_8529 || enchantment.isAcceptableItem(itemStack)) {
					list.add(enchantment);
				}
			}

			if (list.isEmpty()) {
				LOGGER.warn("Couldn't find a compatible enchantment for {}", itemStack);
				return itemStack;
			}

			enchantment2 = (Enchantment)list.get(random.nextInt(list.size()));
		} else {
			enchantment2 = (Enchantment)this.enchantments.get(random.nextInt(this.enchantments.size()));
		}

		int i = MathHelper.nextInt(random, enchantment2.getMinimumLevel(), enchantment2.getMaximumLevel());
		if (itemStack.getItem() == Items.field_8529) {
			itemStack = new ItemStack(Items.field_8598);
			EnchantedBookItem.method_7807(itemStack, new InfoEnchantment(enchantment2, i));
		} else {
			itemStack.method_7978(enchantment2, i);
		}

		return itemStack;
	}

	public static ConditionalLootFunction.Builder<?> method_489() {
		return builder(lootConditions -> new EnchantRandomlyLootFunction(lootConditions, ImmutableList.<Enchantment>of()));
	}

	public static class Factory extends ConditionalLootFunction.Factory<EnchantRandomlyLootFunction> {
		public Factory() {
			super(new Identifier("enchant_randomly"), EnchantRandomlyLootFunction.class);
		}

		public void method_491(JsonObject jsonObject, EnchantRandomlyLootFunction enchantRandomlyLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, enchantRandomlyLootFunction, jsonSerializationContext);
			if (!enchantRandomlyLootFunction.enchantments.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (Enchantment enchantment : enchantRandomlyLootFunction.enchantments) {
					Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
					if (identifier == null) {
						throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
					}

					jsonArray.add(new JsonPrimitive(identifier.toString()));
				}

				jsonObject.add("enchantments", jsonArray);
			}
		}

		public EnchantRandomlyLootFunction method_490(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			List<Enchantment> list = Lists.<Enchantment>newArrayList();
			if (jsonObject.has("enchantments")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "enchantments")) {
					String string = JsonHelper.asString(jsonElement, "enchantment");
					Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT
						.getOrEmpty(new Identifier(string))
						.orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
					list.add(enchantment);
				}
			}

			return new EnchantRandomlyLootFunction(lootConditions, list);
		}
	}
}
