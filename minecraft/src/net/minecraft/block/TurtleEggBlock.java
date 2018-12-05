package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TurtleEggBlock extends Block {
	private static final VoxelShape field_11712 = Block.createCubeShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
	private static final VoxelShape field_11709 = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
	public static final IntegerProperty field_11711 = Properties.HATCH;
	public static final IntegerProperty field_11710 = Properties.EGGS;

	public TurtleEggBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11711, Integer.valueOf(0)).with(field_11710, Integer.valueOf(1)));
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		this.method_10834(world, blockPos, entity, 100);
		super.onSteppedOn(world, blockPos, entity);
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		if (!(entity instanceof ZombieEntity)) {
			this.method_10834(world, blockPos, entity, 3);
		}

		super.onLandedUpon(world, blockPos, entity, f);
	}

	private void method_10834(World world, BlockPos blockPos, Entity entity, int i) {
		if (!this.method_10835(world, entity)) {
			super.onSteppedOn(world, blockPos, entity);
		} else {
			if (!world.isRemote && world.random.nextInt(i) == 0) {
				this.method_10833(world, blockPos, world.getBlockState(blockPos));
			}
		}
	}

	private void method_10833(World world, BlockPos blockPos, BlockState blockState) {
		world.playSound(null, blockPos, SoundEvents.field_14687, SoundCategory.field_15245, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		int i = (Integer)blockState.get(field_11710);
		if (i <= 1) {
			world.breakBlock(blockPos, false);
		} else {
			world.setBlockState(blockPos, blockState.with(field_11710, Integer.valueOf(i - 1)), 2);
			world.fireWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (this.method_10832(world) && this.method_10831(world, blockPos)) {
			int i = (Integer)blockState.get(field_11711);
			if (i < 2) {
				world.playSound(null, blockPos, SoundEvents.field_15109, SoundCategory.field_15245, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.setBlockState(blockPos, blockState.with(field_11711, Integer.valueOf(i + 1)), 2);
			} else {
				world.playSound(null, blockPos, SoundEvents.field_14902, SoundCategory.field_15245, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.clearBlockState(blockPos);
				if (!world.isRemote) {
					for (int j = 0; j < blockState.get(field_11710); j++) {
						world.fireWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
						TurtleEntity turtleEntity = new TurtleEntity(world);
						turtleEntity.setBreedingAge(-24000);
						turtleEntity.method_6683(blockPos);
						turtleEntity.setPositionAndAngles((double)blockPos.getX() + 0.3 + (double)j * 0.2, (double)blockPos.getY(), (double)blockPos.getZ() + 0.3, 0.0F, 0.0F);
						world.spawnEntity(turtleEntity);
					}
				}
			}
		}
	}

	private boolean method_10831(BlockView blockView, BlockPos blockPos) {
		return blockView.getBlockState(blockPos.down()).getBlock() == Blocks.field_10102;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (this.method_10831(world, blockPos) && !world.isRemote) {
			world.fireWorldEvent(2005, blockPos, 0);
		}
	}

	private boolean method_10832(World world) {
		float f = world.method_8400(1.0F);
		return (double)f < 0.69 && (double)f > 0.65 ? true : world.random.nextInt(500) == 0;
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
		this.method_10833(world, blockPos, blockState);
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getItemStack().getItem() == this.getItem() && blockState.get(field_11710) < 4
			? true
			: super.method_9616(blockState, itemPlacementContext);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos());
		return blockState.getBlock() == this
			? blockState.with(field_11710, Integer.valueOf(Math.min(4, (Integer)blockState.get(field_11710) + 1)))
			: super.getPlacementState(itemPlacementContext);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_11710) > 1 ? field_11709 : field_11712;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11711, field_11710);
	}

	private boolean method_10835(World world, Entity entity) {
		if (entity instanceof TurtleEntity) {
			return false;
		} else {
			return entity instanceof LivingEntity && !(entity instanceof PlayerEntity) ? world.getGameRules().getBoolean("mobGriefing") : true;
		}
	}
}
