package net.minecraft.data.server.tag.rebalance;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

public class TradeRebalanceEnchantmentTagProvider extends TagProvider<Enchantment> {
	public TradeRebalanceEnchantmentTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.ENCHANTMENT, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(EnchantmentTags.DESERT_COMMON_TRADE).add(Enchantments.FIRE_PROTECTION, Enchantments.THORNS, Enchantments.INFINITY);
		this.getOrCreateTagBuilder(EnchantmentTags.JUNGLE_COMMON_TRADE).add(Enchantments.FEATHER_FALLING, Enchantments.PROJECTILE_PROTECTION, Enchantments.POWER);
		this.getOrCreateTagBuilder(EnchantmentTags.PLAINS_COMMON_TRADE).add(Enchantments.PUNCH, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS);
		this.getOrCreateTagBuilder(EnchantmentTags.SAVANNA_COMMON_TRADE).add(Enchantments.KNOCKBACK, Enchantments.BINDING_CURSE, Enchantments.SWEEPING_EDGE);
		this.getOrCreateTagBuilder(EnchantmentTags.SNOW_COMMON_TRADE).add(Enchantments.AQUA_AFFINITY, Enchantments.LOOTING, Enchantments.FROST_WALKER);
		this.getOrCreateTagBuilder(EnchantmentTags.SWAMP_COMMON_TRADE).add(Enchantments.DEPTH_STRIDER, Enchantments.RESPIRATION, Enchantments.VANISHING_CURSE);
		this.getOrCreateTagBuilder(EnchantmentTags.TAIGA_COMMON_TRADE).add(Enchantments.BLAST_PROTECTION, Enchantments.FIRE_ASPECT, Enchantments.FLAME);
		this.getOrCreateTagBuilder(EnchantmentTags.DESERT_SPECIAL_TRADE).add(Enchantments.EFFICIENCY);
		this.getOrCreateTagBuilder(EnchantmentTags.JUNGLE_SPECIAL_TRADE).add(Enchantments.UNBREAKING);
		this.getOrCreateTagBuilder(EnchantmentTags.PLAINS_SPECIAL_TRADE).add(Enchantments.PROTECTION);
		this.getOrCreateTagBuilder(EnchantmentTags.SAVANNA_SPECIAL_TRADE).add(Enchantments.SHARPNESS);
		this.getOrCreateTagBuilder(EnchantmentTags.SNOW_SPECIAL_TRADE).add(Enchantments.SILK_TOUCH);
		this.getOrCreateTagBuilder(EnchantmentTags.SWAMP_SPECIAL_TRADE).add(Enchantments.MENDING);
		this.getOrCreateTagBuilder(EnchantmentTags.TAIGA_SPECIAL_TRADE).add(Enchantments.FORTUNE);
	}
}
