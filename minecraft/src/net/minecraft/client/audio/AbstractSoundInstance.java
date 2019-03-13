package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AbstractSoundInstance implements SoundInstance {
	protected Sound field_5444;
	@Nullable
	private WeightedSoundSet field_5443;
	protected final SoundCategory field_5447;
	protected final Identifier field_5448;
	protected float volume = 1.0F;
	protected float pitch = 1.0F;
	protected float x;
	protected float y;
	protected float z;
	protected boolean repeat;
	protected int repeatDelay;
	protected SoundInstance.AttenuationType field_5440 = SoundInstance.AttenuationType.LINEAR;
	protected boolean field_5445;

	protected AbstractSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory) {
		this(soundEvent.getId(), soundCategory);
	}

	protected AbstractSoundInstance(Identifier identifier, SoundCategory soundCategory) {
		this.field_5448 = identifier;
		this.field_5447 = soundCategory;
	}

	@Override
	public Identifier method_4775() {
		return this.field_5448;
	}

	@Override
	public WeightedSoundSet method_4783(SoundLoader soundLoader) {
		this.field_5443 = soundLoader.method_4869(this.field_5448);
		if (this.field_5443 == null) {
			this.field_5444 = SoundLoader.SOUND_MISSING;
		} else {
			this.field_5444 = this.field_5443.method_4887();
		}

		return this.field_5443;
	}

	@Override
	public Sound getSound() {
		return this.field_5444;
	}

	@Override
	public SoundCategory method_4774() {
		return this.field_5447;
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
		return this.volume * this.field_5444.getVolume();
	}

	@Override
	public float getPitch() {
		return this.pitch * this.field_5444.getPitch();
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
		return this.field_5440;
	}

	@Override
	public boolean method_4787() {
		return this.field_5445;
	}
}
