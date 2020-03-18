package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;

public class RespawnAnchorBlock extends Block {
	public static final IntProperty CHARGES = Properties.CHARGES;

	public RespawnAnchorBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(CHARGES, Integer.valueOf(0)));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		int i = (Integer)state.get(CHARGES);
		if (player.getStackInHand(hand).getItem() == Items.GLOWSTONE) {
			if (i < 4) {
				world.setBlockState(pos, state.with(CHARGES, Integer.valueOf(i + 1)), 3);
				world.playSound(
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
					SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
					SoundCategory.BLOCKS,
					1.0F,
					1.0F,
					false
				);
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.CONSUME;
			}
		} else if (i == 0) {
			return ActionResult.CONSUME;
		} else {
			if (world.dimension.getType() == DimensionType.THE_NETHER) {
				if (!world.isClient) {
					((ServerPlayerEntity)player).setSpawnPoint(world.dimension.getType(), pos, false, true);
				}

				world.playSound(
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
					SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,
					SoundCategory.BLOCKS,
					1.0F,
					1.0F,
					false
				);
			} else {
				world.removeBlock(pos, false);
				world.createExplosion(
					null,
					DamageSource.netherBed(),
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
					5.0F,
					true,
					Explosion.DestructionType.DESTROY
				);
			}

			return ActionResult.SUCCESS;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Integer)state.get(CHARGES) != 0) {
			if (random.nextInt(100) == 0) {
				world.playSound(
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
					SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT,
					SoundCategory.BLOCKS,
					0.5F,
					random.nextFloat() * 0.4F + 0.8F,
					false
				);
			}

			double d = (double)pos.getX() + 0.5 + (double)(0.5F - random.nextFloat());
			double e = (double)pos.getY() + 1.0;
			double f = (double)pos.getZ() + 0.5 + (double)(0.5F - random.nextFloat());
			double g = (double)random.nextFloat() * 0.04;
			world.addParticle(ParticleTypes.REVERSE_PORTAL, d, e, f, 0.0, g, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(CHARGES);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	public static int getLightLevel(BlockState state, int maxLevel) {
		return MathHelper.floor((float)((Integer)state.get(CHARGES) - 0) / 4.0F * (float)maxLevel);
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return getLightLevel(state, 15);
	}

	public static Optional<Vec3d> findRespawnPosition(WorldView world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
			BlockPos blockPos2 = blockPos.down();
			BlockPos blockPos3 = blockPos.up();
			if (world.getBlockState(blockPos2).isSideSolidFullSquare(world, blockPos2, Direction.DOWN)
				&& world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()
				&& world.getBlockState(blockPos3).getCollisionShape(world, blockPos3).isEmpty()) {
				return Optional.of(Vec3d.method_24955(blockPos));
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
