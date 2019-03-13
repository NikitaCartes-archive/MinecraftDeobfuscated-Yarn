package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
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
	private static final VoxelShape field_11712 = Block.method_9541(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
	private static final VoxelShape field_11709 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
	public static final IntegerProperty field_11711 = Properties.field_12530;
	public static final IntegerProperty field_11710 = Properties.field_12509;

	public TurtleEggBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11711, Integer.valueOf(0)).method_11657(field_11710, Integer.valueOf(1)));
	}

	@Override
	public void method_9591(World world, BlockPos blockPos, Entity entity) {
		this.method_10834(world, blockPos, entity, 100);
		super.method_9591(world, blockPos, entity);
	}

	@Override
	public void method_9554(World world, BlockPos blockPos, Entity entity, float f) {
		if (!(entity instanceof ZombieEntity)) {
			this.method_10834(world, blockPos, entity, 3);
		}

		super.method_9554(world, blockPos, entity, f);
	}

	private void method_10834(World world, BlockPos blockPos, Entity entity, int i) {
		if (!this.method_10835(world, entity)) {
			super.method_9591(world, blockPos, entity);
		} else {
			if (!world.isClient && world.random.nextInt(i) == 0) {
				this.method_10833(world, blockPos, world.method_8320(blockPos));
			}
		}
	}

	private void method_10833(World world, BlockPos blockPos, BlockState blockState) {
		world.method_8396(null, blockPos, SoundEvents.field_14687, SoundCategory.field_15245, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		int i = (Integer)blockState.method_11654(field_11710);
		if (i <= 1) {
			world.method_8651(blockPos, false);
		} else {
			world.method_8652(blockPos, blockState.method_11657(field_11710, Integer.valueOf(i - 1)), 2);
			world.method_8535(2001, blockPos, Block.method_9507(blockState));
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (this.method_10832(world) && this.method_10831(world, blockPos)) {
			int i = (Integer)blockState.method_11654(field_11711);
			if (i < 2) {
				world.method_8396(null, blockPos, SoundEvents.field_15109, SoundCategory.field_15245, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.method_8652(blockPos, blockState.method_11657(field_11711, Integer.valueOf(i + 1)), 2);
			} else {
				world.method_8396(null, blockPos, SoundEvents.field_14902, SoundCategory.field_15245, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.method_8650(blockPos);
				if (!world.isClient) {
					for (int j = 0; j < blockState.method_11654(field_11710); j++) {
						world.method_8535(2001, blockPos, Block.method_9507(blockState));
						TurtleEntity turtleEntity = EntityType.TURTLE.method_5883(world);
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
		return blockView.method_8320(blockPos.down()).getBlock() == Blocks.field_10102;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (this.method_10831(world, blockPos) && !world.isClient) {
			world.method_8535(2005, blockPos, 0);
		}
	}

	private boolean method_10832(World world) {
		float f = world.getSkyAngle(1.0F);
		return (double)f < 0.69 && (double)f > 0.65 ? true : world.random.nextInt(500) == 0;
	}

	@Override
	public void method_9556(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.method_9556(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
		this.method_10833(world, blockPos, blockState);
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getItemStack().getItem() == this.getItem() && blockState.method_11654(field_11710) < 4
			? true
			: super.method_9616(blockState, itemPlacementContext);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.method_8045().method_8320(itemPlacementContext.method_8037());
		return blockState.getBlock() == this
			? blockState.method_11657(field_11710, Integer.valueOf(Math.min(4, (Integer)blockState.method_11654(field_11710) + 1)))
			: super.method_9605(itemPlacementContext);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return blockState.method_11654(field_11710) > 1 ? field_11709 : field_11712;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11711, field_11710);
	}

	private boolean method_10835(World world, Entity entity) {
		if (entity instanceof TurtleEntity) {
			return false;
		} else {
			return entity instanceof LivingEntity && !(entity instanceof PlayerEntity) ? world.getGameRules().getBoolean("mobGriefing") : true;
		}
	}
}
