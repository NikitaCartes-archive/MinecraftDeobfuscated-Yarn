package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticle;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneOreBlock extends Block {
	public static final BooleanProperty field_11392 = RedstoneTorchBlock.field_11446;

	public RedstoneOreBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(field_11392, Boolean.valueOf(false)));
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return blockState.get(field_11392) ? super.getLuminance(blockState) : 0;
	}

	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		method_10441(blockState, world, blockPos);
		super.onBlockBreakStart(blockState, world, blockPos, playerEntity);
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		method_10441(world.getBlockState(blockPos), world, blockPos);
		super.onSteppedOn(world, blockPos, entity);
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		method_10441(blockState, world, blockPos);
		return super.method_9534(blockState, world, blockPos, playerEntity, hand, direction, f, g, h);
	}

	private static void method_10441(BlockState blockState, World world, BlockPos blockPos) {
		method_10440(world, blockPos);
		if (!(Boolean)blockState.get(field_11392)) {
			world.setBlockState(blockPos, blockState.with(field_11392, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(field_11392)) {
			world.setBlockState(blockPos, blockState.with(field_11392, Boolean.valueOf(false)), 3);
		}
	}

	@Override
	public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		super.onStacksDropped(blockState, world, blockPos, itemStack);
		if (EnchantmentHelper.getLevel(Enchantments.field_9099, itemStack) == 0) {
			int i = 1 + world.random.nextInt(5);
			this.dropExperience(world, blockPos, i);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(field_11392)) {
			method_10440(world, blockPos);
		}
	}

	private static void method_10440(World world, BlockPos blockPos) {
		double d = 0.5625;
		Random random = world.random;

		for (Direction direction : Direction.values()) {
			BlockPos blockPos2 = blockPos.method_10093(direction);
			if (!world.getBlockState(blockPos2).method_11598(world, blockPos2)) {
				Direction.Axis axis = direction.getAxis();
				double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getOffsetX() : (double)random.nextFloat();
				double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getOffsetY() : (double)random.nextFloat();
				double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getOffsetZ() : (double)random.nextFloat();
				world.method_8406(DustParticle.field_11188, (double)blockPos.getX() + e, (double)blockPos.getY() + f, (double)blockPos.getZ() + g, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11392);
	}
}
