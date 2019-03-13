package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.block.BlockItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class class_4178 extends BlockItem {
	private final SoundEvent field_18675;

	public class_4178(Block block, SoundEvent soundEvent, Item.Settings settings) {
		super(block, settings);
		this.field_18675 = soundEvent;
	}

	public class_4178(Block block, Item.Settings settings) {
		this(block, SoundEvents.field_17611, settings);
	}

	@Override
	protected SoundEvent method_19260(BlockState blockState) {
		return this.field_18675;
	}
}
