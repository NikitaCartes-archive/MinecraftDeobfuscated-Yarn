package net.minecraft.data.server.tag.onetwentyone;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

public class OneTwentyOneDamageTypeTagProvider extends TagProvider<DamageType> {
	public OneTwentyOneDamageTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.DAMAGE_TYPE, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(DamageTypeTags.BREEZE_IMMUNE_TO).add(DamageTypes.ARROW, DamageTypes.TRIDENT);
		this.getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE).add(DamageTypes.WIND_CHARGE);
		this.getOrCreateTagBuilder(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS).add(DamageTypes.WIND_CHARGE);
	}
}
