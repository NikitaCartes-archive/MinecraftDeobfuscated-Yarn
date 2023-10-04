package net.minecraft.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class EnchantRandomlyLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Codec<RegistryEntryList<Enchantment>> ENCHANTMENT_LIST_CODEC = Registries.ENCHANTMENT
		.createEntryCodec()
		.listOf()
		.xmap(RegistryEntryList::of, enchantments -> enchantments.stream().toList());
	public static final Codec<EnchantRandomlyLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(Codecs.createStrictOptionalFieldCodec(ENCHANTMENT_LIST_CODEC, "enchantments").forGetter(function -> function.enchantments))
				.apply(instance, EnchantRandomlyLootFunction::new)
	);
	private final Optional<RegistryEntryList<Enchantment>> enchantments;

	EnchantRandomlyLootFunction(List<LootCondition> conditions, Optional<RegistryEntryList<Enchantment>> enchantments) {
		super(conditions);
		this.enchantments = enchantments;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.ENCHANT_RANDOMLY;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Random random = context.getRandom();
		Optional<RegistryEntry<Enchantment>> optional = this.enchantments
			.flatMap(enchantments -> enchantments.getRandom(random))
			.or(
				() -> {
					boolean bl = stack.isOf(Items.BOOK);
					List<RegistryEntry.Reference<Enchantment>> list = Registries.ENCHANTMENT
						.streamEntries()
						.filter(enchantment -> ((Enchantment)enchantment.value()).isAvailableForRandomSelection())
						.filter(enchantment -> bl || ((Enchantment)enchantment.value()).isAcceptableItem(stack))
						.toList();
					return Util.getRandomOrEmpty(list, random);
				}
			);
		if (optional.isEmpty()) {
			LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
			return stack;
		} else {
			return addEnchantmentToStack(stack, (Enchantment)((RegistryEntry)optional.get()).value(), random);
		}
	}

	private static ItemStack addEnchantmentToStack(ItemStack stack, Enchantment enchantment, Random random) {
		int i = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
		if (stack.isOf(Items.BOOK)) {
			stack = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
		} else {
			stack.addEnchantment(enchantment, i);
		}

		return stack;
	}

	public static EnchantRandomlyLootFunction.Builder create() {
		return new EnchantRandomlyLootFunction.Builder();
	}

	public static ConditionalLootFunction.Builder<?> builder() {
		return builder(conditions -> new EnchantRandomlyLootFunction(conditions, Optional.empty()));
	}

	public static class Builder extends ConditionalLootFunction.Builder<EnchantRandomlyLootFunction.Builder> {
		private final List<RegistryEntry<Enchantment>> enchantments = new ArrayList();

		protected EnchantRandomlyLootFunction.Builder getThisBuilder() {
			return this;
		}

		public EnchantRandomlyLootFunction.Builder add(Enchantment enchantment) {
			this.enchantments.add(enchantment.getRegistryEntry());
			return this;
		}

		@Override
		public LootFunction build() {
			return new EnchantRandomlyLootFunction(
				this.getConditions(), this.enchantments.isEmpty() ? Optional.empty() : Optional.of(RegistryEntryList.of(this.enchantments))
			);
		}
	}
}
