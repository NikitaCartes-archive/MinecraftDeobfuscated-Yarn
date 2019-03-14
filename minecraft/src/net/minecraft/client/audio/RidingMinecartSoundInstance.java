package net.minecraft.client.audio;

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
		super(SoundEvents.field_14784, SoundCategory.field_15254);
		this.minecart = abstractMinecartEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0F;
		this.x = (float)abstractMinecartEntity.x;
		this.y = (float)abstractMinecartEntity.y;
		this.z = (float)abstractMinecartEntity.z;
	}

	@Override
	public boolean method_4785() {
		return true;
	}

	@Override
	public void tick() {
		if (this.minecart.invalid) {
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
