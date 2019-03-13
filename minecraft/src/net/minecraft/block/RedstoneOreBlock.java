package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneOreBlock extends Block {
	public static final BooleanProperty field_11392 = RedstoneTorchBlock.field_11446;

	public RedstoneOreBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.method_9564().method_11657(field_11392, Boolean.valueOf(false)));
	}

	@Override
	public int method_9593(BlockState blockState) {
		return blockState.method_11654(field_11392) ? super.method_9593(blockState) : 0;
	}

	@Override
	public void method_9606(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		method_10441(blockState, world, blockPos);
		super.method_9606(blockState, world, blockPos, playerEntity);
	}

	@Override
	public void method_9591(World world, BlockPos blockPos, Entity entity) {
		method_10441(world.method_8320(blockPos), world, blockPos);
		super.method_9591(world, blockPos, entity);
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		method_10441(blockState, world, blockPos);
		return super.method_9534(blockState, world, blockPos, playerEntity, hand, blockHitResult);
	}

	private static void method_10441(BlockState blockState, World world, BlockPos blockPos) {
		method_10440(world, blockPos);
		if (!(Boolean)blockState.method_11654(field_11392)) {
			world.method_8652(blockPos, blockState.method_11657(field_11392, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11392)) {
			world.method_8652(blockPos, blockState.method_11657(field_11392, Boolean.valueOf(false)), 3);
		}
	}

	@Override
	public void method_9565(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		super.method_9565(blockState, world, blockPos, itemStack);
		if (EnchantmentHelper.getLevel(Enchantments.field_9099, itemStack) == 0) {
			int i = 1 + world.random.nextInt(5);
			this.method_9583(world, blockPos, i);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11392)) {
			method_10440(world, blockPos);
		}
	}

	private static void method_10440(World world, BlockPos blockPos) {
		double d = 0.5625;
		Random random = world.random;

		for (Direction direction : Direction.values()) {
			BlockPos blockPos2 = blockPos.method_10093(direction);
			if (!world.method_8320(blockPos2).method_11598(world, blockPos2)) {
				Direction.Axis axis = direction.getAxis();
				double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getOffsetX() : (double)random.nextFloat();
				double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getOffsetY() : (double)random.nextFloat();
				double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getOffsetZ() : (double)random.nextFloat();
				world.method_8406(DustParticleParameters.RED, (double)blockPos.getX() + e, (double)blockPos.getY() + f, (double)blockPos.getZ() + g, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11392);
	}
}
