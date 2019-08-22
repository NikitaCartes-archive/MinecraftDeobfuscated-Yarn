package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RidingMinecartSoundInstance extends MovingSoundInstance {
	private final AbstractMinecartEntity minecart;
	private float distance = 0.0F;

	public RidingMinecartSoundInstance(AbstractMinecartEntity abstractMinecartEntity) {
		super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
		this.minecart = abstractMinecartEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0F;
		this.x = (float)abstractMinecartEntity.x;
		this.y = (float)abstractMinecartEntity.y;
		this.z = (float)abstractMinecartEntity.z;
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	@Override
	public void tick() {
		if (this.minecart.removed) {
			this.done = true;
		} else {
			this.x = (float)this.minecart.x;
			this.y = (float)this.minecart.y;
			this.z = (float)this.minecart.z;
			float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.minecart.getVelocity()));
			if ((double)f >= 0.01) {
				this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
				this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 0.7F);
			} else {
				this.distance = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}
