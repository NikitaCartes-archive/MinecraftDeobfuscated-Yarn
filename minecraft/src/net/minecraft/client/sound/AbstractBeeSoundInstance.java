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
	private boolean replaced;

	public AbstractBeeSoundInstance(BeeEntity beeEntity, SoundEvent soundEvent, SoundCategory soundCategory) {
		super(soundEvent, soundCategory);
		this.bee = beeEntity;
		this.x = (double)((float)beeEntity.getX());
		this.y = (double)((float)beeEntity.getY());
		this.z = (double)((float)beeEntity.getZ());
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0F;
	}

	@Override
	public void tick() {
		boolean bl = this.shouldReplace();
		if (bl && !this.isDone()) {
			MinecraftClient.getInstance().getSoundManager().playNextTick(this.getReplacement());
			this.replaced = true;
		}

		if (!this.bee.isRemoved() && !this.replaced) {
			this.x = (double)((float)this.bee.getX());
			this.y = (double)((float)this.bee.getY());
			this.z = (double)((float)this.bee.getZ());
			float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.bee.getVelocity()));
			if ((double)f >= 0.01) {
				this.pitch = MathHelper.lerp(MathHelper.clamp(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
				this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 1.2F);
			} else {
				this.pitch = 0.0F;
				this.volume = 0.0F;
			}
		} else {
			this.setDone();
		}
	}

	private float getMinPitch() {
		return this.bee.isBaby() ? 1.1F : 0.7F;
	}

	private float getMaxPitch() {
		return this.bee.isBaby() ? 1.5F : 1.1F;
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	@Override
	public boolean canPlay() {
		return !this.bee.isSilent();
	}

	protected abstract MovingSoundInstance getReplacement();

	protected abstract boolean shouldReplace();
}
