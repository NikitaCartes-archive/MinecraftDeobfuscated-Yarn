package net.minecraft.enchantment.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;

public record ByCostEnchantmentProvider(RegistryEntryList<Enchantment> enchantments, IntProvider cost) implements EnchantmentProvider {
	public static final MapCodec<ByCostEnchantmentProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).fieldOf("enchantments").forGetter(ByCostEnchantmentProvider::enchantments),
					IntProvider.VALUE_CODEC.fieldOf("cost").forGetter(ByCostEnchantmentProvider::cost)
				)
				.apply(instance, ByCostEnchantmentProvider::new)
	);

	@Override
	public void provideEnchantments(ItemStack stack, ItemEnchantmentsComponent.Builder componentBuilder, Random random, LocalDifficulty localDifficulty) {
		for (EnchantmentLevelEntry enchantmentLevelEntry : EnchantmentHelper.generateEnchantments(random, stack, this.cost.get(random), this.enchantments.stream())) {
			componentBuilder.add(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
		}
	}

	@Override
	public MapCodec<ByCostEnchantmentProvider> getCodec() {
		return CODEC;
	}
}
