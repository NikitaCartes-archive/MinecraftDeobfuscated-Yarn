package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public abstract class FacingBlock extends Block {
	public static final EnumProperty<Direction> FACING = Properties.FACING;

	protected FacingBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected abstract MapCodec<? extends FacingBlock> getCodec();
}
