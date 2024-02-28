package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class DragonEggBlock extends FallingBlock {
	public static final MapCodec<DragonEggBlock> CODEC = createCodec(DragonEggBlock::new);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	@Override
	public MapCodec<DragonEggBlock> getCodec() {
		return CODEC;
	}

	public DragonEggBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		this.teleport(state, world, pos);
		return ActionResult.success(world.isClient);
	}

	@Override
	protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		this.teleport(state, world, pos);
	}

	private void teleport(BlockState state, World world, BlockPos pos) {
		WorldBorder worldBorder = world.getWorldBorder();

		for (int i = 0; i < 1000; i++) {
			BlockPos blockPos = pos.add(
				world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16)
			);
			if (world.getBlockState(blockPos).isAir() && worldBorder.contains(blockPos)) {
				if (world.isClient) {
					for (int j = 0; j < 128; j++) {
						double d = world.random.nextDouble();
						float f = (world.random.nextFloat() - 0.5F) * 0.2F;
						float g = (world.random.nextFloat() - 0.5F) * 0.2F;
						float h = (world.random.nextFloat() - 0.5F) * 0.2F;
						double e = MathHelper.lerp(d, (double)blockPos.getX(), (double)pos.getX()) + (world.random.nextDouble() - 0.5) + 0.5;
						double k = MathHelper.lerp(d, (double)blockPos.getY(), (double)pos.getY()) + world.random.nextDouble() - 0.5;
						double l = MathHelper.lerp(d, (double)blockPos.getZ(), (double)pos.getZ()) + (world.random.nextDouble() - 0.5) + 0.5;
						world.addParticle(ParticleTypes.PORTAL, e, k, l, (double)f, (double)g, (double)h);
					}
				} else {
					world.setBlockState(blockPos, state, Block.NOTIFY_LISTENERS);
					world.removeBlock(pos, false);
				}

				return;
			}
		}
	}

	@Override
	protected int getFallDelay() {
		return 5;
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
