package net.minecraft.loot.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

public class SetEnchantmentsLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetEnchantmentsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<Map<RegistryEntry<Enchantment>, LootNumberProvider>, boolean>and(
					instance.group(
						Codec.unboundedMap(Enchantment.ENTRY_CODEC, LootNumberProviderTypes.CODEC)
							.optionalFieldOf("enchantments", Map.of())
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
	public LootFunctionType<SetEnchantmentsLootFunction> getType() {
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
		if (stack.isOf(Items.BOOK)) {
			stack = stack.withItem(Items.ENCHANTED_BOOK);
			stack.set(DataComponentTypes.STORED_ENCHANTMENTS, stack.remove(DataComponentTypes.ENCHANTMENTS));
		}

		EnchantmentHelper.apply(
			stack,
			builder -> {
				if (this.add) {
					this.enchantments
						.forEach((enchantment, level) -> builder.set(enchantment, MathHelper.clamp(builder.getLevel(enchantment) + level.nextInt(context), 0, 255)));
				} else {
					this.enchantments.forEach((enchantment, level) -> builder.set(enchantment, MathHelper.clamp(level.nextInt(context), 0, 255)));
				}
			}
		);
		return stack;
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

		public SetEnchantmentsLootFunction.Builder enchantment(RegistryEntry<Enchantment> enchantment, LootNumberProvider level) {
			this.enchantments.put(enchantment, level);
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetEnchantmentsLootFunction(this.getConditions(), this.enchantments.build(), this.add);
		}
	}
}
