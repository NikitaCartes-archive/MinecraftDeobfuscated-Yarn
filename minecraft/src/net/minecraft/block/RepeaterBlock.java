package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RepeaterBlock extends AbstractRedstoneGateBlock {
	public static final BooleanProperty field_11452 = Properties.field_12502;
	public static final IntProperty field_11451 = Properties.field_12494;

	protected RepeaterBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, Direction.field_11043)
				.method_11657(field_11451, Integer.valueOf(1))
				.method_11657(field_11452, Boolean.valueOf(false))
				.method_11657(field_10911, Boolean.valueOf(false))
		);
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!playerEntity.abilities.allowModifyWorld) {
			return false;
		} else {
			world.method_8652(blockPos, blockState.method_11572(field_11451), 3);
			return true;
		}
	}

	@Override
	protected int method_9992(BlockState blockState) {
		return (Integer)blockState.method_11654(field_11451) * 2;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = super.method_9605(itemPlacementContext);
		return blockState.method_11657(
			field_11452, Boolean.valueOf(this.method_9996(itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos(), blockState))
		);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return !iWorld.isClient() && direction.getAxis() != ((Direction)blockState.method_11654(field_11177)).getAxis()
			? blockState.method_11657(field_11452, Boolean.valueOf(this.method_9996(iWorld, blockPos, blockState)))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9996(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		return this.method_10000(viewableWorld, blockPos, blockState) > 0;
	}

	@Override
	protected boolean method_9989(BlockState blockState) {
		return method_9999(blockState);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_10911)) {
			Direction direction = blockState.method_11654(field_11177);
			double d = (double)((float)blockPos.getX() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double e = (double)((float)blockPos.getY() + 0.4F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			double f = (double)((float)blockPos.getZ() + 0.5F) + (double)(random.nextFloat() - 0.5F) * 0.2;
			float g = -5.0F;
			if (random.nextBoolean()) {
				g = (float)((Integer)blockState.method_11654(field_11451) * 2 - 1);
			}

			g /= 16.0F;
			double h = (double)(g * (float)direction.getOffsetX());
			double i = (double)(g * (float)direction.getOffsetZ());
			world.addParticle(DustParticleEffect.RED, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177, field_11451, field_11452, field_10911);
	}
}
