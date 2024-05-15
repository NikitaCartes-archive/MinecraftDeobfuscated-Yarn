package net.minecraft.enchantment.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;

public interface EnchantmentProvider {
	Codec<EnchantmentProvider> CODEC = Registries.ENCHANTMENT_PROVIDER_TYPE.getCodec().dispatch(EnchantmentProvider::getCodec, Function.identity());

	void provideEnchantments(ItemStack stack, ItemEnchantmentsComponent.Builder componentBuilder, Random random, LocalDifficulty localDifficulty);

	MapCodec<? extends EnchantmentProvider> getCodec();
}
