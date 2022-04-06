package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

/**
 * A sound instance played when a player is riding a minecart.
 */
@Environment(EnvType.CLIENT)
public class MinecartInsideSoundInstance extends MovingSoundInstance {
	private static final float field_33006 = 0.0F;
	private static final float field_33007 = 0.75F;
	private final PlayerEntity player;
	private final AbstractMinecartEntity minecart;
	private final boolean underwater;

	public MinecartInsideSoundInstance(PlayerEntity player, AbstractMinecartEntity minecart, boolean underwater) {
		super(underwater ? SoundEvents.ENTITY_MINECART_INSIDE_UNDERWATER : SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL, SoundInstance.createRandom());
		this.player = player;
		this.minecart = minecart;
		this.underwater = underwater;
		this.attenuationType = SoundInstance.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0F;
	}

	@Override
	public boolean canPlay() {
		return !this.minecart.isSilent();
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	@Override
	public void tick() {
		if (this.minecart.isRemoved() || !this.player.hasVehicle() || this.player.getVehicle() != this.minecart) {
			this.setDone();
		} else if (this.underwater != this.player.isSubmergedInWater()) {
			this.volume = 0.0F;
		} else {
			float f = (float)this.minecart.getVelocity().horizontalLength();
			if (f >= 0.01F) {
				this.volume = MathHelper.clampedLerp(0.0F, 0.75F, f);
			} else {
				this.volume = 0.0F;
			}
		}
	}
}
