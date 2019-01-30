package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class GuardianAttackSoundInstance extends MovingSoundInstance {
	private final GuardianEntity guardian;

	public GuardianAttackSoundInstance(GuardianEntity guardianEntity) {
		super(SoundEvents.field_14880, SoundCategory.field_15251);
		this.guardian = guardianEntity;
		this.attenuationType = SoundInstance.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public void tick() {
		if (!this.guardian.invalid && this.guardian.hasBeamTarget()) {
			this.x = (float)this.guardian.x;
			this.y = (float)this.guardian.y;
			this.z = (float)this.guardian.z;
			float f = this.guardian.getBeamProgress(0.0F);
			this.volume = 0.0F + 1.0F * f * f;
			this.pitch = 0.7F + 0.5F * f;
		} else {
			this.done = true;
		}
	}
}
