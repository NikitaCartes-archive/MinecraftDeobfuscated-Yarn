package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class PositionedSoundInstance extends AbstractSoundInstance {
	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, BlockPos pos) {
		this(sound, category, volume, pitch, random, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float pitch) {
		return master(sound, pitch, 0.25F);
	}

	public static PositionedSoundInstance master(RegistryEntry<SoundEvent> sound, float pitch) {
		return master(sound.value(), pitch);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float pitch, float volume) {
		return new PositionedSoundInstance(
			sound.getId(), SoundCategory.MASTER, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true
		);
	}

	public static PositionedSoundInstance music(SoundEvent sound) {
		return new PositionedSoundInstance(
			sound.getId(), SoundCategory.MUSIC, 1.0F, 1.0F, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true
		);
	}

	public static PositionedSoundInstance record(SoundEvent sound, Vec3d pos) {
		return new PositionedSoundInstance(
			sound, SoundCategory.RECORDS, 4.0F, 1.0F, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.LINEAR, pos.x, pos.y, pos.z
		);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound, float pitch, float volume) {
		return new PositionedSoundInstance(
			sound.getId(), SoundCategory.AMBIENT, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true
		);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound) {
		return ambient(sound, 1.0F, 1.0F);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound, Random random, double x, double y, double z) {
		return new PositionedSoundInstance(sound, SoundCategory.AMBIENT, 1.0F, 1.0F, random, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
	}

	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, double x, double y, double z) {
		this(sound, category, volume, pitch, random, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
	}

	private PositionedSoundInstance(
		SoundEvent sound,
		SoundCategory category,
		float volume,
		float pitch,
		Random random,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		double x,
		double y,
		double z
	) {
		this(sound.getId(), category, volume, pitch, random, repeat, repeatDelay, attenuationType, x, y, z, false);
	}

	public PositionedSoundInstance(
		Identifier id,
		SoundCategory category,
		float volume,
		float pitch,
		Random random,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		double x,
		double y,
		double z,
		boolean relative
	) {
		super(id, category, random);
		this.volume = volume;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		this.z = z;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.attenuationType = attenuationType;
		this.relative = relative;
	}
}
