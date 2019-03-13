package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
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
		this.field_5440 = SoundInstance.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public void method_16896() {
		if (!this.minecart.invalid && this.player.hasVehicle() && this.player.getRiddenEntity() == this.minecart) {
			float f = MathHelper.sqrt(Entity.method_17996(this.minecart.method_18798()));
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
