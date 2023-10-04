package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;

public abstract class FacingBlock extends Block {
	public static final DirectionProperty FACING = Properties.FACING;

	protected FacingBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected abstract MapCodec<? extends FacingBlock> getCodec();
}
