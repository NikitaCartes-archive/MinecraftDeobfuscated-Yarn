package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneOreBlock extends Block {
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public RedstoneOreBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)));
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return blockState.get(LIT) ? super.getLuminance(blockState) : 0;
	}

	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		light(blockState, world, blockPos);
		super.onBlockBreakStart(blockState, world, blockPos, playerEntity);
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		light(world.getBlockState(blockPos), world, blockPos);
		super.onSteppedOn(world, blockPos, entity);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		light(blockState, world, blockPos);
		return super.activate(blockState, world, blockPos, playerEntity, hand, blockHitResult);
	}

	private static void light(BlockState blockState, World world, BlockPos blockPos) {
		spawnParticles(world, blockPos);
		if (!(Boolean)blockState.get(LIT)) {
			world.setBlockState(blockPos, blockState.with(LIT, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(LIT)) {
			serverWorld.setBlockState(blockPos, blockState.with(LIT, Boolean.valueOf(false)), 3);
		}
	}

	@Override
	public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		super.onStacksDropped(blockState, world, blockPos, itemStack);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
			int i = 1 + world.random.nextInt(5);
			this.dropExperience(world, blockPos, i);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(LIT)) {
			spawnParticles(world, blockPos);
		}
	}

	private static void spawnParticles(World world, BlockPos blockPos) {
		double d = 0.5625;
		Random random = world.random;

		for (Direction direction : Direction.values()) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (!world.getBlockState(blockPos2).isFullOpaque(world, blockPos2)) {
				Direction.Axis axis = direction.getAxis();
				double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getOffsetX() : (double)random.nextFloat();
				double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getOffsetY() : (double)random.nextFloat();
				double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getOffsetZ() : (double)random.nextFloat();
				world.addParticle(DustParticleEffect.RED, (double)blockPos.getX() + e, (double)blockPos.getY() + f, (double)blockPos.getZ() + g, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
}
