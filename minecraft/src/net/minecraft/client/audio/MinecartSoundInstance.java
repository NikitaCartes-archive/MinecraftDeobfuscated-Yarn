package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MinecartSoundInstance extends MovingSoundInstance {
	private final PlayerEntity player;
	private final AbstractMinecartEntity minecart;

	public MinecartSoundInstance(PlayerEntity playerEntity, AbstractMinecartEntity abstractMinecartEntity) {
		super(SoundEvents.field_14832, SoundCategory.field_15254);
		this.player = playerEntity;
		this.minecart = abstractMinecartEntity;
		this.attenuationType = SoundInstance.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public void tick() {
		if (!this.minecart.invalid && this.player.hasVehicle() && this.player.getRiddenEntity() == this.minecart) {
			float f = MathHelper.sqrt(this.minecart.velocityX * this.minecart.velocityX + this.minecart.velocityZ * this.minecart.velocityZ);
			if ((double)f >= 0.01) {
				this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 1.0F) * 0.75F;
			} else {
				this.volume = 0.0F;
			}
		} else {
			this.done = true;
		}
	}
}
