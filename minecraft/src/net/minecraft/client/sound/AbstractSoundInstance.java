package net.minecraft.client.sound;

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
	protected final SoundCategory category;
	protected final Identifier id;
	protected float volume = 1.0F;
	protected float pitch = 1.0F;
	protected float x;
	protected float y;
	protected float z;
	protected boolean repeat;
	protected int repeatDelay;
	protected SoundInstance.AttenuationType field_5440 = SoundInstance.AttenuationType.field_5476;
	protected boolean field_18935;
	protected boolean looping;

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
	public WeightedSoundSet method_4783(SoundManager soundManager) {
		this.field_5443 = soundManager.method_4869(this.id);
		if (this.field_5443 == null) {
			this.field_5444 = SoundManager.MISSING_SOUND;
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
	public boolean isLooping() {
		return this.looping;
	}
}
