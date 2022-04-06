package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Optional;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class RespawnAnchorBlock extends Block {
	public static final int NO_CHARGES = 0;
	public static final int MAX_CHARGES = 4;
	public static final IntProperty CHARGES = Properties.CHARGES;
	private static final ImmutableList<Vec3i> VALID_HORIZONTAL_SPAWN_OFFSETS = ImmutableList.of(
		new Vec3i(0, 0, -1),
		new Vec3i(-1, 0, 0),
		new Vec3i(0, 0, 1),
		new Vec3i(1, 0, 0),
		new Vec3i(-1, 0, -1),
		new Vec3i(1, 0, -1),
		new Vec3i(-1, 0, 1),
		new Vec3i(1, 0, 1)
	);
	private static final ImmutableList<Vec3i> VALID_SPAWN_OFFSETS = new Builder<Vec3i>()
		.addAll(VALID_HORIZONTAL_SPAWN_OFFSETS)
		.addAll(VALID_HORIZONTAL_SPAWN_OFFSETS.stream().map(Vec3i::down).iterator())
		.addAll(VALID_HORIZONTAL_SPAWN_OFFSETS.stream().map(Vec3i::up).iterator())
		.add(new Vec3i(0, 1, 0))
		.build();

	public RespawnAnchorBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(CHARGES, Integer.valueOf(0)));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (hand == Hand.MAIN_HAND && !isChargeItem(itemStack) && isChargeItem(player.getStackInHand(Hand.OFF_HAND))) {
			return ActionResult.PASS;
		} else if (isChargeItem(itemStack) && canCharge(state)) {
			charge(world, pos, state);
			if (!player.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

			return ActionResult.success(world.isClient);
		} else if ((Integer)state.get(CHARGES) == 0) {
			return ActionResult.PASS;
		} else if (!isNether(world)) {
			if (!world.isClient) {
				this.explode(state, world, pos);
			}

			return ActionResult.success(world.isClient);
		} else {
			if (!world.isClient) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
				if (serverPlayerEntity.getSpawnPointDimension() != world.getRegistryKey() || !pos.equals(serverPlayerEntity.getSpawnPointPosition())) {
					serverPlayerEntity.setSpawnPoint(world.getRegistryKey(), pos, 0.0F, false, true);
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

			return ActionResult.CONSUME;
		}
	}

	private static boolean isChargeItem(ItemStack stack) {
		return stack.isOf(Items.GLOWSTONE);
	}

	private static boolean canCharge(BlockState state) {
		return (Integer)state.get(CHARGES) < 4;
	}

	private static boolean hasStillWater(BlockPos pos, World world) {
		FluidState fluidState = world.getFluidState(pos);
		if (!fluidState.isIn(FluidTags.WATER)) {
			return false;
		} else if (fluidState.isStill()) {
			return true;
		} else {
			float f = (float)fluidState.getLevel();
			if (f < 2.0F) {
				return false;
			} else {
				FluidState fluidState2 = world.getFluidState(pos.down());
				return !fluidState2.isIn(FluidTags.WATER);
			}
		}
	}

	private void explode(BlockState state, World world, BlockPos explodedPos) {
		world.removeBlock(explodedPos, false);
		boolean bl = Direction.Type.HORIZONTAL.stream().map(explodedPos::offset).anyMatch(pos -> hasStillWater(pos, world));
		final boolean bl2 = bl || world.getFluidState(explodedPos.up()).isIn(FluidTags.WATER);
		ExplosionBehavior explosionBehavior = new ExplosionBehavior() {
			@Override
			public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
				return pos.equals(explodedPos) && bl2
					? Optional.of(Blocks.WATER.getBlastResistance())
					: super.getBlastResistance(explosion, world, pos, blockState, fluidState);
			}
		};
		world.createExplosion(
			null,
			DamageSource.badRespawnPoint(),
			explosionBehavior,
			(double)explodedPos.getX() + 0.5,
			(double)explodedPos.getY() + 0.5,
			(double)explodedPos.getZ() + 0.5,
			5.0F,
			true,
			Explosion.DestructionType.DESTROY
		);
	}

	public static boolean isNether(World world) {
		return world.getDimension().respawnAnchorWorks();
	}

	public static void charge(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(CHARGES, Integer.valueOf((Integer)state.get(CHARGES) + 1)), Block.NOTIFY_ALL);
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

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, AbstractRandom random) {
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

			double d = (double)pos.getX() + 0.5 + (0.5 - random.nextDouble());
			double e = (double)pos.getY() + 1.0;
			double f = (double)pos.getZ() + 0.5 + (0.5 - random.nextDouble());
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

	public static Optional<Vec3d> findRespawnPosition(EntityType<?> entity, CollisionView world, BlockPos pos) {
		Optional<Vec3d> optional = findRespawnPosition(entity, world, pos, true);
		return optional.isPresent() ? optional : findRespawnPosition(entity, world, pos, false);
	}

	private static Optional<Vec3d> findRespawnPosition(EntityType<?> entity, CollisionView world, BlockPos pos, boolean ignoreInvalidPos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Vec3i vec3i : VALID_SPAWN_OFFSETS) {
			mutable.set(pos).move(vec3i);
			Vec3d vec3d = Dismounting.findRespawnPos(entity, world, mutable, ignoreInvalidPos);
			if (vec3d != null) {
				return Optional.of(vec3d);
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
