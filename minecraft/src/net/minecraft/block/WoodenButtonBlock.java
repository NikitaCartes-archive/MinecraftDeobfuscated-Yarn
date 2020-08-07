package net.minecraft.block;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class WoodenButtonBlock extends AbstractButtonBlock {
	protected WoodenButtonBlock(AbstractBlock.Settings settings) {
		super(true, settings);
	}

	@Override
	protected SoundEvent getClickSound(boolean powered) {
		return powered ? SoundEvents.field_14699 : SoundEvents.field_15105;
	}
}
