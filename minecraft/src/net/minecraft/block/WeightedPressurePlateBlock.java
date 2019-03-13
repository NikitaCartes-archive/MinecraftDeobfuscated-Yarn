package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final IntegerProperty field_11739 = Properties.field_12511;
	private final int weight;

	protected WeightedPressurePlateBlock(int i, Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11739, Integer.valueOf(0)));
		this.weight = i;
	}

	@Override
	protected int method_9434(World world, BlockPos blockPos) {
		int i = Math.min(world.method_18467(Entity.class, field_9941.method_996(blockPos)).size(), this.weight);
		if (i > 0) {
			float f = (float)Math.min(this.weight, i) / (float)this.weight;
			return MathHelper.ceil(f * 15.0F);
		} else {
			return 0;
		}
	}

	@Override
	protected void method_9436(IWorld iWorld, BlockPos blockPos) {
		iWorld.method_8396(null, blockPos, SoundEvents.field_14988, SoundCategory.field_15245, 0.3F, 0.90000004F);
	}

	@Override
	protected void method_9438(IWorld iWorld, BlockPos blockPos) {
		iWorld.method_8396(null, blockPos, SoundEvents.field_15100, SoundCategory.field_15245, 0.3F, 0.75F);
	}

	@Override
	protected int method_9435(BlockState blockState) {
		return (Integer)blockState.method_11654(field_11739);
	}

	@Override
	protected BlockState method_9432(BlockState blockState, int i) {
		return blockState.method_11657(field_11739, Integer.valueOf(i));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 10;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11739);
	}
}
