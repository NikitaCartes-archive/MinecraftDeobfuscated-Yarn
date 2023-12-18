package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class TurtleEggBlock extends Block {
	public static final MapCodec<TurtleEggBlock> CODEC = createCodec(TurtleEggBlock::new);
	public static final int field_31272 = 2;
	public static final int field_31273 = 1;
	public static final int field_31274 = 4;
	private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
	private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
	public static final IntProperty HATCH = Properties.HATCH;
	public static final IntProperty EGGS = Properties.EGGS;

	@Override
	public MapCodec<TurtleEggBlock> getCodec() {
		return CODEC;
	}

	public TurtleEggBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HATCH, Integer.valueOf(0)).with(EGGS, Integer.valueOf(1)));
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!entity.bypassesSteppingEffects()) {
			this.tryBreakEgg(world, state, pos, entity, 100);
		}

		super.onSteppedOn(world, pos, state, entity);
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (!(entity instanceof ZombieEntity)) {
			this.tryBreakEgg(world, state, pos, entity, 3);
		}

		super.onLandedUpon(world, state, pos, entity, fallDistance);
	}

	private void tryBreakEgg(World world, BlockState state, BlockPos pos, Entity entity, int inverseChance) {
		if (this.breaksEgg(world, entity)) {
			if (!world.isClient && world.random.nextInt(inverseChance) == 0 && state.isOf(Blocks.TURTLE_EGG)) {
				this.breakEgg(world, pos, state);
			}
		}
	}

	private void breakEgg(World world, BlockPos pos, BlockState state) {
		world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		int i = (Integer)state.get(EGGS);
		if (i <= 1) {
			world.breakBlock(pos, false);
		} else {
			world.setBlockState(pos, state.with(EGGS, Integer.valueOf(i - 1)), Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(state));
			world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
		}
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (this.shouldHatchProgress(world) && isSandBelow(world, pos)) {
			int i = (Integer)state.get(HATCH);
			if (i < 2) {
				world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.setBlockState(pos, state.with(HATCH, Integer.valueOf(i + 1)), Block.NOTIFY_LISTENERS);
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
			} else {
				world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.removeBlock(pos, false);
				world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(state));

				for (int j = 0; j < state.get(EGGS); j++) {
					world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
					TurtleEntity turtleEntity = EntityType.TURTLE.create(world);
					if (turtleEntity != null) {
						turtleEntity.setBreedingAge(-24000);
						turtleEntity.setHomePos(pos);
						turtleEntity.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, (double)pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
						world.spawnEntity(turtleEntity);
					}
				}
			}
		}
	}

	public static boolean isSandBelow(BlockView world, BlockPos pos) {
		return isSand(world, pos.down());
	}

	public static boolean isSand(BlockView world, BlockPos pos) {
		return world.getBlockState(pos).isIn(BlockTags.SAND);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (isSandBelow(world, pos) && !world.isClient) {
			world.syncWorldEvent(2012, pos, 15);
		}
	}

	private boolean shouldHatchProgress(World world) {
		float f = world.getSkyAngle(1.0F);
		return (double)f < 0.69 && (double)f > 0.65 ? true : world.random.nextInt(500) == 0;
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.afterBreak(world, player, pos, state, blockEntity, tool);
		this.breakEgg(world, pos, state);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(EGGS) < 4 ? true : super.canReplace(state, context);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		return blockState.isOf(this) ? blockState.with(EGGS, Integer.valueOf(Math.min(4, (Integer)blockState.get(EGGS) + 1))) : super.getPlacementState(ctx);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(EGGS) > 1 ? LARGE_SHAPE : SMALL_SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HATCH, EGGS);
	}

	private boolean breaksEgg(World world, Entity entity) {
		if (entity instanceof TurtleEntity || entity instanceof BatEntity) {
			return false;
		} else {
			return !(entity instanceof LivingEntity) ? false : entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
		}
	}
}
