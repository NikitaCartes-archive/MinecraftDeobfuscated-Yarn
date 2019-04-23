package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.sound.SoundEvents;

public class SittingAttackingPhase extends AbstractSittingPhase {
	private int field_7049;

	public SittingAttackingPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_6853() {
		this.dragon
			.world
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
	public void method_6855() {
		if (this.field_7049++ >= 40) {
			this.dragon.getPhaseManager().setPhase(PhaseType.field_7072);
		}
	}

	@Override
	public void beginPhase() {
		this.field_7049 = 0;
	}

	@Override
	public PhaseType<SittingAttackingPhase> getType() {
		return PhaseType.field_7073;
	}
}
