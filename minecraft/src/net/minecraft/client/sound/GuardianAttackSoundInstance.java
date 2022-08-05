package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class GuardianAttackSoundInstance extends MovingSoundInstance {
	private static final float BASE_VOLUME = 0.0F;
	private static final float BEAM_PROGRESS_VOLUME_MULTIPLIER = 1.0F;
	private static final float BASE_PITCH = 0.7F;
	private static final float BEAM_PROGRESS_PITCH_MULTIPLIER = 0.5F;
	private final GuardianEntity guardian;

	public GuardianAttackSoundInstance(GuardianEntity guardian) {
		super(SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE, SoundInstance.createRandom());
		this.guardian = guardian;
		this.attenuationType = SoundInstance.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public boolean canPlay() {
		return !this.guardian.isSilent();
	}

	@Override
	public void tick() {
		if (!this.guardian.isRemoved() && this.guardian.getTarget() == null) {
			this.x = (double)((float)this.guardian.getX());
			this.y = (double)((float)this.guardian.getY());
			this.z = (double)((float)this.guardian.getZ());
			float f = this.guardian.getBeamProgress(0.0F);
			this.volume = 0.0F + 1.0F * f * f;
			this.pitch = 0.7F + 0.5F * f;
		} else {
			this.setDone();
		}
	}
}
