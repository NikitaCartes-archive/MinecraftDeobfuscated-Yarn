package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class PassiveBeeSoundInstance extends AbstractBeeSoundInstance {
	public PassiveBeeSoundInstance(BeeEntity beeEntity) {
		super(beeEntity, SoundEvents.ENTITY_BEE_LOOP, SoundCategory.NEUTRAL);
	}

	@Override
	protected MovingSoundInstance method_22135() {
		return new AggressiveBeeSoundInstance(this.bee);
	}

	@Override
	protected boolean method_22136() {
		return this.bee.isAngry();
	}
}
