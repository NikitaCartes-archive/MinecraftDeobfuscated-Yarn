package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.sound.SoundEvents;

public class SittingAttackingPhase extends AbstractSittingPhase {
	private int ticks;

	public SittingAttackingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void clientTick() {
		this.dragon
			.field_6002
			.playSound(
				this.dragon.x,
				this.dragon.y,
				this.dragon.z,
				SoundEvents.field_14671,
				this.dragon.getSoundCategory(),
				2.5F,
				0.8F + this.dragon.getRand().nextFloat() * 0.3F,
				false
			);
	}

	@Override
	public void serverTick() {
		if (this.ticks++ >= 40) {
			this.dragon.getPhaseManager().setPhase(PhaseType.field_7072);
		}
	}

	@Override
	public void beginPhase() {
		this.ticks = 0;
	}

	@Override
	public PhaseType<SittingAttackingPhase> getType() {
		return PhaseType.field_7073;
	}
}
