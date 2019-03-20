package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class PositionedSoundInstance extends AbstractSoundInstance {
	public PositionedSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, BlockPos blockPos) {
		this(soundEvent, soundCategory, f, g, (float)blockPos.getX() + 0.5F, (float)blockPos.getY() + 0.5F, (float)blockPos.getZ() + 0.5F);
	}

	public static PositionedSoundInstance master(SoundEvent soundEvent, float f) {
		return master(soundEvent, f, 0.25F);
	}

	public static PositionedSoundInstance master(SoundEvent soundEvent, float f, float g) {
		return new PositionedSoundInstance(soundEvent.getId(), SoundCategory.field_15250, g, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true);
	}

	public static PositionedSoundInstance music(SoundEvent soundEvent) {
		return new PositionedSoundInstance(
			soundEvent.getId(), SoundCategory.field_15253, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true
		);
	}

	public static PositionedSoundInstance record(SoundEvent soundEvent, float f, float g, float h) {
		return new PositionedSoundInstance(soundEvent, SoundCategory.field_15247, 4.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, f, g, h);
	}

	public PositionedSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, float h, float i, float j) {
		this(soundEvent, soundCategory, f, g, false, 0, SoundInstance.AttenuationType.LINEAR, h, i, j);
	}

	private PositionedSoundInstance(
		SoundEvent soundEvent,
		SoundCategory soundCategory,
		float f,
		float g,
		boolean bl,
		int i,
		SoundInstance.AttenuationType attenuationType,
		float h,
		float j,
		float k
	) {
		this(soundEvent.getId(), soundCategory, f, g, bl, i, attenuationType, h, j, k, false);
	}

	public PositionedSoundInstance(
		Identifier identifier,
		SoundCategory soundCategory,
		float f,
		float g,
		boolean bl,
		int i,
		SoundInstance.AttenuationType attenuationType,
		float h,
		float j,
		float k,
		boolean bl2
	) {
		super(identifier, soundCategory);
		this.volume = f;
		this.pitch = g;
		this.x = h;
		this.y = j;
		this.z = k;
		this.repeat = bl;
		this.repeatDelay = i;
		this.attenuationType = attenuationType;
		this.field_18936 = bl2;
	}
}
