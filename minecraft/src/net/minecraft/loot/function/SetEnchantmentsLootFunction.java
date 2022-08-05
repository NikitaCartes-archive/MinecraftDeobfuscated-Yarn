package net.minecraft.loot.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SetEnchantmentsLootFunction extends ConditionalLootFunction {
	final Map<Enchantment, LootNumberProvider> enchantments;
	final boolean add;

	SetEnchantmentsLootFunction(LootCondition[] conditions, Map<Enchantment, LootNumberProvider> enchantments, boolean add) {
		super(conditions);
		this.enchantments = ImmutableMap.copyOf(enchantments);
		this.add = add;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_ENCHANTMENTS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return (Set<LootContextParameter<?>>)this.enchantments
			.values()
			.stream()
			.flatMap(numberProvider -> numberProvider.getRequiredParameters().stream())
			.collect(ImmutableSet.toImmutableSet());
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Object2IntMap<Enchantment> object2IntMap = new Object2IntOpenHashMap<>();
		this.enchantments.forEach((enchantment, numberProvider) -> object2IntMap.put(enchantment, numberProvider.nextInt(context)));
		if (stack.getItem() == Items.BOOK) {
			ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
			object2IntMap.forEach((enchantment, level) -> EnchantedBookItem.addEnchantment(itemStack, new EnchantmentLevelEntry(enchantment, level)));
			return itemStack;
		} else {
			Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
			if (this.add) {
				object2IntMap.forEach((enchantment, level) -> addEnchantmentToMap(map, enchantment, Math.max((Integer)map.getOrDefault(enchantment, 0) + level, 0)));
			} else {
				object2IntMap.forEach((enchantment, level) -> addEnchantmentToMap(map, enchantment, Math.max(level, 0)));
			}

			EnchantmentHelper.set(map, stack);
			return stack;
		}
	}

	private static void addEnchantmentToMap(Map<Enchantment, Integer> map, Enchantment enchantment, int level) {
		if (level == 0) {
			map.remove(enchantment);
		} else {
			map.put(enchantment, level);
		}
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetEnchantmentsLootFunction.Builder> {
		private final Map<Enchantment, LootNumberProvider> enchantments = Maps.<Enchantment, LootNumberProvider>newHashMap();
		private final boolean add;

		public Builder() {
			this(false);
		}

		public Builder(boolean add) {
			this.add = add;
		}

		protected SetEnchantmentsLootFunction.Builder getThisBuilder() {
			return this;
		}

		public SetEnchantmentsLootFunction.Builder enchantment(Enchantment enchantment, LootNumberProvider level) {
			this.enchantments.put(enchantment, level);
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetEnchantmentsLootFunction(this.getConditions(), this.enchantments, this.add);
		}
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetEnchantmentsLootFunction> {
		public void toJson(JsonObject jsonObject, SetEnchantmentsLootFunction setEnchantmentsLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setEnchantmentsLootFunction, jsonSerializationContext);
			JsonObject jsonObject2 = new JsonObject();
			setEnchantmentsLootFunction.enchantments.forEach((enchantment, numberProvider) -> {
				Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
				if (identifier == null) {
					throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
				} else {
					jsonObject2.add(identifier.toString(), jsonSerializationContext.serialize(numberProvider));
				}
			});
			jsonObject.add("enchantments", jsonObject2);
			jsonObject.addProperty("add", setEnchantmentsLootFunction.add);
		}

		public SetEnchantmentsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Map<Enchantment, LootNumberProvider> map = Maps.<Enchantment, LootNumberProvider>newHashMap();
			if (jsonObject.has("enchantments")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "enchantments");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					String string = (String)entry.getKey();
					JsonElement jsonElement = (JsonElement)entry.getValue();
					Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT
						.getOrEmpty(new Identifier(string))
						.orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
					LootNumberProvider lootNumberProvider = jsonDeserializationContext.deserialize(jsonElement, LootNumberProvider.class);
					map.put(enchantment, lootNumberProvider);
				}
			}

			boolean bl = JsonHelper.getBoolean(jsonObject, "add", false);
			return new SetEnchantmentsLootFunction(lootConditions, map, bl);
		}
	}
}
