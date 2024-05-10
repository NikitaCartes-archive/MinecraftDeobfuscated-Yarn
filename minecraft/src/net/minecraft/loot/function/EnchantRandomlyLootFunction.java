package net.minecraft.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class EnchantRandomlyLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<EnchantRandomlyLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<Optional<RegistryEntryList<Enchantment>>, boolean>and(
					instance.group(
						RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("options").forGetter(function -> function.options),
						Codec.BOOL.optionalFieldOf("only_compatible", Boolean.valueOf(true)).forGetter(function -> function.onlyCompatible)
					)
				)
				.apply(instance, EnchantRandomlyLootFunction::new)
	);
	private final Optional<RegistryEntryList<Enchantment>> options;
	private final boolean onlyCompatible;

	EnchantRandomlyLootFunction(List<LootCondition> conditions, Optional<RegistryEntryList<Enchantment>> options, boolean onlyCompatible) {
		super(conditions);
		this.options = options;
		this.onlyCompatible = onlyCompatible;
	}

	@Override
	public LootFunctionType<EnchantRandomlyLootFunction> getType() {
		return LootFunctionTypes.ENCHANT_RANDOMLY;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Random random = context.getRandom();
		boolean bl = stack.isOf(Items.BOOK);
		boolean bl2 = !bl && this.onlyCompatible;
		Stream<RegistryEntry<Enchantment>> stream = ((Stream)this.options
				.map(RegistryEntryList::stream)
				.orElseGet(() -> context.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).streamEntries().map(Function.identity())))
			.filter(entry -> !bl2 || ((Enchantment)entry.value()).isAcceptableItem(stack));
		List<RegistryEntry<Enchantment>> list = stream.toList();
		Optional<RegistryEntry<Enchantment>> optional = Util.getRandomOrEmpty(list, random);
		if (optional.isEmpty()) {
			LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
			return stack;
		} else {
			return addEnchantmentToStack(stack, (RegistryEntry<Enchantment>)optional.get(), random);
		}
	}

	private static ItemStack addEnchantmentToStack(ItemStack stack, RegistryEntry<Enchantment> enchantment, Random random) {
		int i = MathHelper.nextInt(random, enchantment.value().getMinLevel(), enchantment.value().getMaxLevel());
		if (stack.isOf(Items.BOOK)) {
			stack = new ItemStack(Items.ENCHANTED_BOOK);
		}

		stack.addEnchantment(enchantment, i);
		return stack;
	}

	public static EnchantRandomlyLootFunction.Builder create() {
		return new EnchantRandomlyLootFunction.Builder();
	}

	public static EnchantRandomlyLootFunction.Builder builder(RegistryWrapper.WrapperLookup registryLookup) {
		return create().options(registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
	}

	public static class Builder extends ConditionalLootFunction.Builder<EnchantRandomlyLootFunction.Builder> {
		private Optional<RegistryEntryList<Enchantment>> options = Optional.empty();
		private boolean onlyCompatible = true;

		protected EnchantRandomlyLootFunction.Builder getThisBuilder() {
			return this;
		}

		public EnchantRandomlyLootFunction.Builder option(RegistryEntry<Enchantment> enchantment) {
			this.options = Optional.of(RegistryEntryList.of(enchantment));
			return this;
		}

		public EnchantRandomlyLootFunction.Builder options(RegistryEntryList<Enchantment> options) {
			this.options = Optional.of(options);
			return this;
		}

		public EnchantRandomlyLootFunction.Builder allowIncompatible() {
			this.onlyCompatible = false;
			return this;
		}

		@Override
		public LootFunction build() {
			return new EnchantRandomlyLootFunction(this.getConditions(), this.options, this.onlyCompatible);
		}
	}
}
