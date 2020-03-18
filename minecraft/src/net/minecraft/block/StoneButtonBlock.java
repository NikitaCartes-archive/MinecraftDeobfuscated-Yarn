package net.minecraft.block;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class StoneButtonBlock extends AbstractButtonBlock {
	protected StoneButtonBlock(AbstractBlock.Settings settings) {
		super(false, settings);
	}

	@Override
	protected SoundEvent getClickSound(boolean powered) {
		return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
	}
}
