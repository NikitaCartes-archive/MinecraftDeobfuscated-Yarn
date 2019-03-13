package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.block.BlockItem;
import net.minecraft.sound.SoundEvent;

public class class_4177 extends BlockItem {
	private final SoundEvent field_18671;

	public class_4177(Block block, SoundEvent soundEvent, Item.Settings settings) {
		super(block, settings);
		this.field_18671 = soundEvent;
	}

	@Override
	protected SoundEvent method_19260(BlockState blockState) {
		return this.field_18671;
	}
}
