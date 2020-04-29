package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CampfireBlock extends BlockWithEntity implements Waterloggable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	private static final VoxelShape field_21580 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	private final boolean field_23881;

	public CampfireBlock(boolean bl, AbstractBlock.Settings settings) {
		super(settings);
		this.field_23881 = bl;
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(LIT, Boolean.valueOf(true))
				.with(SIGNAL_FIRE, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
				.with(FACING, Direction.NORTH)
		);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if ((Boolean)state.get(LIT)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CampfireBlockEntity) {
				CampfireBlockEntity campfireBlockEntity = (CampfireBlockEntity)blockEntity;
				ItemStack itemStack = player.getStackInHand(hand);
				Optional<CampfireCookingRecipe> optional = campfireBlockEntity.getRecipeFor(itemStack);
				if (optional.isPresent()) {
					if (!world.isClient
						&& campfireBlockEntity.addItem(player.abilities.creativeMode ? itemStack.copy() : itemStack, ((CampfireCookingRecipe)optional.get()).getCookTime())) {
						player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
						return ActionResult.SUCCESS;
					}

					return ActionResult.CONSUME;
				}
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!entity.isFireImmune() && (Boolean)state.get(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
			entity.damage(DamageSource.IN_FIRE, 1.0F);
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CampfireBlockEntity) {
				ItemScatterer.spawn(world, pos, ((CampfireBlockEntity)blockEntity).getItemsBeingCooked());
			}

			super.onBlockRemoved(state, world, pos, newState, notify);
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		IWorld iWorld = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		boolean bl = iWorld.getFluidState(blockPos).getFluid() == Fluids.WATER;
		return this.getDefaultState()
			.with(WATERLOGGED, Boolean.valueOf(bl))
			.with(SIGNAL_FIRE, Boolean.valueOf(this.doesBlockCauseSignalFire(iWorld.getBlockState(blockPos.down()))))
			.with(LIT, Boolean.valueOf(!bl))
			.with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return direction == Direction.DOWN
			? state.with(SIGNAL_FIRE, Boolean.valueOf(this.doesBlockCauseSignalFire(newState)))
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	private boolean doesBlockCauseSignalFire(BlockState state) {
		return state.isOf(Blocks.HAY_BLOCK);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(LIT)) {
			if (random.nextInt(10) == 0) {
				world.playSound(
					(double)((float)pos.getX() + 0.5F),
					(double)((float)pos.getY() + 0.5F),
					(double)((float)pos.getZ() + 0.5F),
					SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
					SoundCategory.BLOCKS,
					0.5F + random.nextFloat(),
					random.nextFloat() * 0.7F + 0.6F,
					false
				);
			}

			if (this.field_23881 && random.nextInt(5) == 0) {
				for (int i = 0; i < random.nextInt(1) + 1; i++) {
					world.addParticle(
						ParticleTypes.LAVA,
						(double)((float)pos.getX() + 0.5F),
						(double)((float)pos.getY() + 0.5F),
						(double)((float)pos.getZ() + 0.5F),
						(double)(random.nextFloat() / 2.0F),
						5.0E-5,
						(double)(random.nextFloat() / 2.0F)
					);
				}
			}
		}
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			boolean bl = (Boolean)state.get(LIT);
			if (bl) {
				if (world.isClient()) {
					for (int i = 0; i < 20; i++) {
						spawnSmokeParticle(world.getWorld(), pos, (Boolean)state.get(SIGNAL_FIRE), true);
					}
				} else {
					world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof CampfireBlockEntity) {
					((CampfireBlockEntity)blockEntity).spawnItemsBeingCooked();
				}
			}

			world.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)).with(LIT, Boolean.valueOf(false)), 3);
			world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		if (!world.isClient && projectile.isOnFire()) {
			Entity entity = projectile.getOwner();
			boolean bl = entity == null || entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
			if (bl && !(Boolean)state.get(LIT) && !(Boolean)state.get(WATERLOGGED)) {
				BlockPos blockPos = hit.getBlockPos();
				world.setBlockState(blockPos, state.with(Properties.LIT, Boolean.valueOf(true)), 11);
			}
		}
	}

	public static void spawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke) {
		Random random = world.getRandom();
		DefaultParticleType defaultParticleType = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
		world.addImportantParticle(
			defaultParticleType,
			true,
			(double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			(double)pos.getY() + random.nextDouble() + random.nextDouble(),
			(double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			0.0,
			0.07,
			0.0
		);
		if (lotsOfSmoke) {
			world.addParticle(
				ParticleTypes.SMOKE,
				(double)pos.getX() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				(double)pos.getY() + 0.4,
				(double)pos.getZ() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				0.0,
				0.005,
				0.0
			);
		}
	}

	public static boolean isLitCampfireInRange(World world, BlockPos pos, int range) {
		for (int i = 1; i <= range; i++) {
			BlockPos blockPos = pos.down(i);
			BlockState blockState = world.getBlockState(blockPos);
			if (isLitCampfire(blockState)) {
				return true;
			}

			boolean bl = VoxelShapes.matchesAnywhere(field_21580, blockState.getCollisionShape(world, pos, ShapeContext.absent()), BooleanBiFunction.AND);
			if (bl) {
				BlockState blockState2 = world.getBlockState(blockPos.down());
				return isLitCampfire(blockState2);
			}
		}

		return false;
	}

	public static boolean isLitCampfire(BlockState state) {
		return state.getBlock().isIn(BlockTags.CAMPFIRES) && state.contains(LIT) && (Boolean)state.get(LIT);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new CampfireBlockEntity();
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
