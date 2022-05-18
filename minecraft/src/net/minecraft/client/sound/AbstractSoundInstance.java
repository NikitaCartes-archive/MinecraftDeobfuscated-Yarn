package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public abstract class AbstractSoundInstance implements SoundInstance {
	protected Sound sound;
	protected final SoundCategory category;
	protected final Identifier id;
	protected float volume = 1.0F;
	protected float pitch = 1.0F;
	protected double x;
	protected double y;
	protected double z;
	protected boolean repeat;
	protected int repeatDelay;
	protected SoundInstance.AttenuationType attenuationType = SoundInstance.AttenuationType.LINEAR;
	protected boolean relative;
	protected Random field_38800;

	protected AbstractSoundInstance(SoundEvent sound, SoundCategory category, Random random) {
		this(sound.getId(), category, random);
	}

	protected AbstractSoundInstance(Identifier soundId, SoundCategory category, Random random) {
		this.id = soundId;
		this.category = category;
		this.field_38800 = random;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public WeightedSoundSet getSoundSet(SoundManager soundManager) {
		WeightedSoundSet weightedSoundSet = soundManager.get(this.id);
		if (weightedSoundSet == null) {
			this.sound = SoundManager.MISSING_SOUND;
		} else {
			this.sound = weightedSoundSet.getSound(this.field_38800);
		}

		return weightedSoundSet;
	}

	@Override
	public Sound getSound() {
		return this.sound;
	}

	@Override
	public SoundCategory getCategory() {
		return this.category;
	}

	@Override
	public boolean isRepeatable() {
		return this.repeat;
	}

	@Override
	public int getRepeatDelay() {
		return this.repeatDelay;
	}

	@Override
	public float getVolume() {
		return this.volume * this.sound.getVolume().get(this.field_38800);
	}

	@Override
	public float getPitch() {
		return this.pitch * this.sound.getPitch().get(this.field_38800);
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getZ() {
		return this.z;
	}

	@Override
	public SoundInstance.AttenuationType getAttenuationType() {
		return this.attenuationType;
	}

	@Override
	public boolean isRelative() {
		return this.relative;
	}

	public String toString() {
		return "SoundInstance[" + this.id + "]";
	}
}
