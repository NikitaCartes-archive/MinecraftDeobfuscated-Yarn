package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final IntProperty POWER = Properties.POWER;
	private final int weight;

	protected WeightedPressurePlateBlock(int i, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(POWER, Integer.valueOf(0)));
		this.weight = i;
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos blockPos) {
		int i = Math.min(world.getEntities(Entity.class, BOX.offset(blockPos)).size(), this.weight);
		if (i > 0) {
			float f = (float)Math.min(this.weight, i) / (float)this.weight;
			return MathHelper.ceil(f * 15.0F);
		} else {
			return 0;
		}
	}

	@Override
	protected void playPressSound(IWorld iWorld, BlockPos blockPos) {
		iWorld.playSound(null, blockPos, SoundEvents.field_14988, SoundCategory.field_15245, 0.3F, 0.90000004F);
	}

	@Override
	protected void playDepressSound(IWorld iWorld, BlockPos blockPos) {
		iWorld.playSound(null, blockPos, SoundEvents.field_15100, SoundCategory.field_15245, 0.3F, 0.75F);
	}

	@Override
	protected int getRedstoneOutput(BlockState blockState) {
		return (Integer)blockState.get(POWER);
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState blockState, int i) {
		return blockState.with(POWER, Integer.valueOf(i));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 10;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}
}
