package net.minecraft.loot.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public class SetEnchantmentsLootFunction extends ConditionalLootFunction {
	public static final Codec<SetEnchantmentsLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<Map<RegistryEntry<Enchantment>, LootNumberProvider>, boolean>and(
					instance.group(
						Codecs.createStrictOptionalFieldCodec(Codec.unboundedMap(Registries.ENCHANTMENT.getEntryCodec(), LootNumberProviderTypes.CODEC), "enchantments", Map.of())
							.forGetter(function -> function.enchantments),
						Codec.BOOL.fieldOf("add").orElse(false).forGetter(function -> function.add)
					)
				)
				.apply(instance, SetEnchantmentsLootFunction::new)
	);
	private final Map<RegistryEntry<Enchantment>, LootNumberProvider> enchantments;
	private final boolean add;

	SetEnchantmentsLootFunction(List<LootCondition> conditions, Map<RegistryEntry<Enchantment>, LootNumberProvider> enchantments, boolean add) {
		super(conditions);
		this.enchantments = Map.copyOf(enchantments);
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
		this.enchantments.forEach((enchantment, numberProvider) -> object2IntMap.put((Enchantment)enchantment.value(), numberProvider.nextInt(context)));
		if (stack.isOf(Items.BOOK)) {
			ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
			object2IntMap.forEach(itemStack::addEnchantment);
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
		private final ImmutableMap.Builder<RegistryEntry<Enchantment>, LootNumberProvider> enchantments = ImmutableMap.builder();
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
			this.enchantments.put(enchantment.getRegistryEntry(), level);
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetEnchantmentsLootFunction(this.getConditions(), this.enchantments.build(), this.add);
		}
	}
}
