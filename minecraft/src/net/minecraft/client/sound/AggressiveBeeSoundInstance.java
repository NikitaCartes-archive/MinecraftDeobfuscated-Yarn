package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class AggressiveBeeSoundInstance extends AbstractBeeSoundInstance {
	public AggressiveBeeSoundInstance(BeeEntity beeEntity) {
		super(beeEntity, SoundEvents.field_20604, SoundCategory.field_15254);
		this.repeatDelay = 0;
	}

	@Override
	protected MovingSoundInstance getReplacement() {
		return new PassiveBeeSoundInstance(this.bee);
	}

	@Override
	protected boolean shouldReplace() {
		return !this.bee.hasAngerTime();
	}
}
