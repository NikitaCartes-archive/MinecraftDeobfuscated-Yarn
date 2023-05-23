package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	private final PressurePlateBlock.ActivationRule type;

	protected PressurePlateBlock(PressurePlateBlock.ActivationRule type, AbstractBlock.Settings settings, BlockSetType blockSetType) {
		super(settings, blockSetType);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, Boolean.valueOf(false)));
		this.type = type;
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWERED, Boolean.valueOf(rsOut > 0));
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		Class class_ = switch (this.type) {
			case EVERYTHING -> Entity.class;
			case MOBS -> LivingEntity.class;
		};
		return getEntityCount(world, BOX.offset(pos), class_) > 0 ? 15 : 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	public static enum ActivationRule {
		EVERYTHING,
		MOBS;
	}
}
