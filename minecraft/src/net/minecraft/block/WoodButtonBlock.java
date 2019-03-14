package net.minecraft.block;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class WoodButtonBlock extends AbstractButtonBlock {
	protected WoodButtonBlock(Block.Settings settings) {
		super(true, settings);
	}

	@Override
	protected SoundEvent getClickSound(boolean bl) {
		return bl ? SoundEvents.field_14699 : SoundEvents.field_15105;
	}
}
