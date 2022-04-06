package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractBeeSoundInstance extends MovingSoundInstance {
	private static final float field_32991 = 0.0F;
	private static final float field_32992 = 1.2F;
	private static final float field_32993 = 0.0F;
	protected final BeeEntity bee;
	private boolean replaced;

	public AbstractBeeSoundInstance(BeeEntity entity, SoundEvent sound, SoundCategory soundCategory) {
		super(sound, soundCategory, SoundInstance.createRandom());
		this.bee = entity;
		this.x = (double)((float)entity.getX());
		this.y = (double)((float)entity.getY());
		this.z = (double)((float)entity.getZ());
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
			float f = (float)this.bee.getVelocity().horizontalLength();
			if (f >= 0.01F) {
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
