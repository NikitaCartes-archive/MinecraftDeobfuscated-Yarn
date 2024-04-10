package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.EnchantmentTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyOneEnchantmentTagProvider extends EnchantmentTagProvider {
	public OneTwentyOneEnchantmentTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, registryLookupFuture, FeatureSet.of(FeatureFlags.VANILLA, FeatureFlags.UPDATE_1_21));
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.createTooltipOrderTag(
			lookup,
			new Enchantment[]{
				Enchantments.BINDING_CURSE,
				Enchantments.VANISHING_CURSE,
				Enchantments.RIPTIDE,
				Enchantments.CHANNELING,
				Enchantments.WIND_BURST,
				Enchantments.FROST_WALKER,
				Enchantments.SHARPNESS,
				Enchantments.SMITE,
				Enchantments.BANE_OF_ARTHROPODS,
				Enchantments.IMPALING,
				Enchantments.POWER,
				Enchantments.DENSITY,
				Enchantments.BREACH,
				Enchantments.PIERCING,
				Enchantments.SWEEPING_EDGE,
				Enchantments.MULTISHOT,
				Enchantments.FIRE_ASPECT,
				Enchantments.FLAME,
				Enchantments.KNOCKBACK,
				Enchantments.PUNCH,
				Enchantments.PROTECTION,
				Enchantments.BLAST_PROTECTION,
				Enchantments.FIRE_PROTECTION,
				Enchantments.PROJECTILE_PROTECTION,
				Enchantments.FEATHER_FALLING,
				Enchantments.FORTUNE,
				Enchantments.LOOTING,
				Enchantments.SILK_TOUCH,
				Enchantments.LUCK_OF_THE_SEA,
				Enchantments.EFFICIENCY,
				Enchantments.QUICK_CHARGE,
				Enchantments.LURE,
				Enchantments.RESPIRATION,
				Enchantments.AQUA_AFFINITY,
				Enchantments.SOUL_SPEED,
				Enchantments.SWIFT_SNEAK,
				Enchantments.DEPTH_STRIDER,
				Enchantments.THORNS,
				Enchantments.LOYALTY,
				Enchantments.UNBREAKING,
				Enchantments.INFINITY,
				Enchantments.MENDING
			}
		);
	}
}
