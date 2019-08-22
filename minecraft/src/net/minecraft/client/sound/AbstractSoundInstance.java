package net.minecraft.client.sound;

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
	private WeightedSoundSet soundSet;
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
	public WeightedSoundSet getSoundSet(SoundManager soundManager) {
		this.soundSet = soundManager.get(this.id);
		if (this.soundSet == null) {
			this.sound = SoundManager.MISSING_SOUND;
		} else {
			this.sound = this.soundSet.method_4887();
		}

		return this.soundSet;
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
	public boolean isLooping() {
		return this.looping;
	}
}
