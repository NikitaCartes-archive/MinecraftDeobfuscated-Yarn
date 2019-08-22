package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractBeeSoundInstance extends MovingSoundInstance {
	protected final BeeEntity bee;
	private boolean field_20531;

	public AbstractBeeSoundInstance(BeeEntity beeEntity, SoundEvent soundEvent, SoundCategory soundCategory) {
		super(soundEvent, soundCategory);
		this.bee = beeEntity;
		this.x = (float)beeEntity.x;
		this.y = (float)beeEntity.y;
		this.z = (float)beeEntity.z;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0F;
	}

	@Override
	public void tick() {
		boolean bl = this.method_22136();
		if (bl && !this.done) {
			MinecraftClient.getInstance().getSoundManager().method_22140(this.method_22135());
			this.field_20531 = true;
		}

		if (!this.bee.removed && !this.field_20531) {
			this.x = (float)this.bee.x;
			this.y = (float)this.bee.y;
			this.z = (float)this.bee.z;
			float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.bee.getVelocity()));
			if ((double)f >= 0.01) {
				this.pitch = MathHelper.lerp(MathHelper.clamp(f, this.method_22137(), this.method_22138()), this.method_22137(), this.method_22138());
				this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 1.2F);
			} else {
				this.pitch = 0.0F;
				this.volume = 0.0F;
			}
		} else {
			this.done = true;
		}
	}

	private float method_22137() {
		return this.bee.isBaby() ? 1.1F : 0.7F;
	}

	private float method_22138() {
		return this.bee.isBaby() ? 1.5F : 1.1F;
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	protected abstract MovingSoundInstance method_22135();

	protected abstract boolean method_22136();
}
