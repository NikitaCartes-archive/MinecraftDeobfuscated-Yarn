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
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;

public record ByCostWithDifficultyEnchantmentProvider(RegistryEntryList<Enchantment> enchantments, int minCost, int maxCostSpan) implements EnchantmentProvider {
	public static final int MAX_COST = 10000;
	public static final MapCodec<ByCostWithDifficultyEnchantmentProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).fieldOf("enchantments").forGetter(ByCostWithDifficultyEnchantmentProvider::enchantments),
					Codecs.rangedInt(1, 10000).fieldOf("min_cost").forGetter(ByCostWithDifficultyEnchantmentProvider::minCost),
					Codecs.rangedInt(0, 10000).fieldOf("max_cost_span").forGetter(ByCostWithDifficultyEnchantmentProvider::maxCostSpan)
				)
				.apply(instance, ByCostWithDifficultyEnchantmentProvider::new)
	);

	@Override
	public void provideEnchantments(ItemStack stack, ItemEnchantmentsComponent.Builder componentBuilder, Random random, LocalDifficulty localDifficulty) {
		float f = localDifficulty.getClampedLocalDifficulty();
		int i = MathHelper.nextBetween(random, this.minCost, this.minCost + (int)(f * (float)this.maxCostSpan));

		for (EnchantmentLevelEntry enchantmentLevelEntry : EnchantmentHelper.generateEnchantments(random, stack, i, this.enchantments.stream())) {
			componentBuilder.add(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
		}
	}

	@Override
	public MapCodec<ByCostWithDifficultyEnchantmentProvider> getCodec() {
		return CODEC;
	}
}
