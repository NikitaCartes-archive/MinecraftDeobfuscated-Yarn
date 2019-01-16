package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AbstractSoundInstance implements SoundInstance {
	protected Sound sound;
	@Nullable
	private WeightedSoundSet accessor;
	protected final SoundCategory category;
	protected final Identifier id;
	protected float volume = 1.0F;
	protected float pitch = 1.0F;
	protected float x;
	protected float y;
	protected float z;
	protected boolean repeat;
	protected int repeatDelay;
	protected SoundInstance.AttenuationType attenuationType = SoundInstance.AttenuationType.LINEAR;
	protected boolean field_5445;

	protected AbstractSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory) {
		this(soundEvent.getId(), soundCategory);
	}

	protected AbstractSoundInstance(Identifier identifier, SoundCategory soundCategory) {
		this.id = identifier;
		this.category = soundCategory;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public WeightedSoundSet getAccess(SoundLoader soundLoader) {
		this.accessor = soundLoader.get(this.id);
		if (this.accessor == null) {
			this.sound = SoundLoader.SOUND_MISSING;
		} else {
			this.sound = this.accessor.getSound();
		}

		return this.accessor;
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
		return this.volume * this.sound.getVolume();
	}

	@Override
	public float getPitch() {
		return this.pitch * this.sound.getPitch();
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	@Override
	public float getZ() {
		return this.z;
	}

	@Override
	public SoundInstance.AttenuationType getAttenuationType() {
		return this.attenuationType;
	}

	@Override
	public boolean method_4787() {
		return this.field_5445;
	}
}
