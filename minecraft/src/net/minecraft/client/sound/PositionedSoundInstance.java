package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class PositionedSoundInstance extends AbstractSoundInstance {
	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, BlockPos blockPos) {
		this(sound, category, volume, pitch, (float)blockPos.getX() + 0.5F, (float)blockPos.getY() + 0.5F, (float)blockPos.getZ() + 0.5F);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float volume) {
		return master(sound, volume, 0.25F);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float volume, float pitch) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.MASTER, pitch, volume, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true);
	}

	public static PositionedSoundInstance music(SoundEvent sound) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true);
	}

	public static PositionedSoundInstance record(SoundEvent sound, float x, float y, float z) {
		return new PositionedSoundInstance(sound, SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
	}

	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, float x, float y, float z) {
		this(sound, category, volume, pitch, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
	}

	private PositionedSoundInstance(
		SoundEvent sound,
		SoundCategory category,
		float volume,
		float pitch,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		float x,
		float y,
		float z
	) {
		this(sound.getId(), category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, false);
	}

	public PositionedSoundInstance(
		Identifier id,
		SoundCategory category,
		float volume,
		float pitch,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		float x,
		float y,
		float z,
		boolean looping
	) {
		super(id, category);
		this.volume = volume;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		this.z = z;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.attenuationType = attenuationType;
		this.looping = looping;
	}
}
