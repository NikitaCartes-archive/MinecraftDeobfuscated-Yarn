package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class TurtleEggBlock extends Block {
	private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
	private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
	public static final IntProperty HATCH = Properties.HATCH;
	public static final IntProperty EGGS = Properties.EGGS;

	public TurtleEggBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HATCH, Integer.valueOf(0)).with(EGGS, Integer.valueOf(1)));
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		this.tryBreakEgg(world, pos, entity, 100);
		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		if (!(entity instanceof ZombieEntity)) {
			this.tryBreakEgg(world, pos, entity, 3);
		}

		super.onLandedUpon(world, pos, entity, distance);
	}

	private void tryBreakEgg(World world, BlockPos pos, Entity entity, int inverseChance) {
		if (!this.breaksEgg(world, entity)) {
			super.onSteppedOn(world, pos, entity);
		} else {
			if (!world.isClient && world.random.nextInt(inverseChance) == 0) {
				this.breakEgg(world, pos, world.getBlockState(pos));
			}
		}
	}

	private void breakEgg(World world, BlockPos pos, BlockState state) {
		world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		int i = (Integer)state.get(EGGS);
		if (i <= 1) {
			world.breakBlock(pos, false);
		} else {
			world.setBlockState(pos, state.with(EGGS, Integer.valueOf(i - 1)), 2);
			world.playLevelEvent(2001, pos, Block.getRawIdFromState(state));
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (this.shouldHatchProgress(world) && this.isSand(world, pos)) {
			int i = (Integer)state.get(HATCH);
			if (i < 2) {
				world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.setBlockState(pos, state.with(HATCH, Integer.valueOf(i + 1)), 2);
			} else {
				world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.removeBlock(pos, false);

				for (int j = 0; j < state.get(EGGS); j++) {
					world.playLevelEvent(2001, pos, Block.getRawIdFromState(state));
					TurtleEntity turtleEntity = EntityType.TURTLE.create(world);
					turtleEntity.setBreedingAge(-24000);
					turtleEntity.setHomePos(pos);
					turtleEntity.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, (double)pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
					world.spawnEntity(turtleEntity);
				}
			}
		}
	}

	private boolean isSand(BlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).getBlock() == Blocks.SAND;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
		if (this.isSand(world, pos) && !world.isClient) {
			world.playLevelEvent(2005, pos, 0);
		}
	}

	private boolean shouldHatchProgress(World world) {
		float f = world.getSkyAngle(1.0F);
		return (double)f < 0.69 && (double)f > 0.65 ? true : world.random.nextInt(500) == 0;
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, state, blockEntity, stack);
		this.breakEgg(world, pos, state);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		return ctx.getStack().getItem() == this.asItem() && state.get(EGGS) < 4 ? true : super.canReplace(state, ctx);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		return blockState.getBlock() == this ? blockState.with(EGGS, Integer.valueOf(Math.min(4, (Integer)blockState.get(EGGS) + 1))) : super.getPlacementState(ctx);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return state.get(EGGS) > 1 ? LARGE_SHAPE : SMALL_SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HATCH, EGGS);
	}

	private boolean breaksEgg(World world, Entity entity) {
		if (entity instanceof TurtleEntity) {
			return false;
		} else {
			return entity instanceof LivingEntity && !(entity instanceof PlayerEntity) ? world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) : true;
		}
	}
}
