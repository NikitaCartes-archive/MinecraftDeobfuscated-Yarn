package net.minecraft.block;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class WoodButtonBlock extends AbstractButtonBlock {
	protected WoodButtonBlock(AbstractBlock.Settings settings) {
		super(true, settings);
	}

	@Override
	protected SoundEvent getClickSound(boolean powered) {
		return powered ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
	}
}
