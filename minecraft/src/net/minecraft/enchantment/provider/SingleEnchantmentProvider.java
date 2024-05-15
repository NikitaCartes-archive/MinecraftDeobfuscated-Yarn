package net.minecraft.enchantment.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;

public record SingleEnchantmentProvider(RegistryEntry<Enchantment> enchantment, IntProvider level) implements EnchantmentProvider {
	public static final MapCodec<SingleEnchantmentProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(SingleEnchantmentProvider::enchantment),
					IntProvider.VALUE_CODEC.fieldOf("level").forGetter(SingleEnchantmentProvider::level)
				)
				.apply(instance, SingleEnchantmentProvider::new)
	);

	@Override
	public void provideEnchantments(ItemStack stack, ItemEnchantmentsComponent.Builder componentBuilder, Random random, LocalDifficulty localDifficulty) {
		componentBuilder.add(
			this.enchantment, MathHelper.clamp(this.level.get(random), this.enchantment.value().getMinLevel(), this.enchantment.value().getMaxLevel())
		);
	}

	@Override
	public MapCodec<SingleEnchantmentProvider> getCodec() {
		return CODEC;
	}
}
