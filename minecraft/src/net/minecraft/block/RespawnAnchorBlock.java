package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.GLOWSTONE && (Integer)state.get(CHARGES) < 4) {
			charge(world, pos, state);
			if (!player.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			return ActionResult.SUCCESS;
		} else if ((Integer)state.get(CHARGES) == 0) {
			return ActionResult.PASS;
		} else if (!method_27353(world)) {
			if (!world.isClient) {
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
		} else {
			if (!world.isClient) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
				if (serverPlayerEntity.getSpawnPointDimension() != world.dimension.getType() || !serverPlayerEntity.getSpawnPointPosition().equals(pos)) {
					serverPlayerEntity.setSpawnPoint(world.dimension.getType(), pos, false, true);
					world.playSound(
						null,
						(double)pos.getX() + 0.5,
						(double)pos.getY() + 0.5,
						(double)pos.getZ() + 0.5,
						SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,
						SoundCategory.BLOCKS,
						1.0F,
						1.0F
					);
					return ActionResult.SUCCESS;
				}
			}

			return state.get(CHARGES) < 4 ? ActionResult.PASS : ActionResult.CONSUME;
		}
	}

	public static boolean method_27353(World world) {
		return world.dimension.getType() == DimensionType.THE_NETHER;
	}

	public static void charge(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(CHARGES, Integer.valueOf((Integer)state.get(CHARGES) + 1)), 3);
		world.playSound(
			null,
			(double)pos.getX() + 0.5,
			(double)pos.getY() + 0.5,
			(double)pos.getZ() + 0.5,
			SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
			SoundCategory.BLOCKS,
			1.0F,
			1.0F
		);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Integer)state.get(CHARGES) != 0) {
			if (random.nextInt(100) == 0) {
				world.playSound(
					null,
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
					SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT,
					SoundCategory.BLOCKS,
					1.0F,
					1.0F
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

	public static Optional<Vec3d> findRespawnPosition(EntityType<?> entity, WorldView world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
			Optional<Vec3d> optional = BedBlock.canWakeUpAt(entity, world, blockPos);
			if (optional.isPresent()) {
				return optional;
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
