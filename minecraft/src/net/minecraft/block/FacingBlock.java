package net.minecraft.block;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;

public abstract class FacingBlock extends Block {
	public static final DirectionProperty field_10927 = Properties.field_12525;

	protected FacingBlock(Block.Settings settings) {
		super(settings);
	}
}
