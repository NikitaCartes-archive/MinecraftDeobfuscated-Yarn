package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TallPlantBlock extends PlantBlock {
	public static final EnumProperty<DoubleBlockHalf> field_10929 = Properties.field_12533;

	public TallPlantBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10929, DoubleBlockHalf.field_12607));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		DoubleBlockHalf doubleBlockHalf = blockState.method_11654(field_10929);
		if (direction.getAxis() != Direction.Axis.Y
			|| doubleBlockHalf == DoubleBlockHalf.field_12607 != (direction == Direction.field_11036)
			|| blockState2.getBlock() == this && blockState2.method_11654(field_10929) != doubleBlockHalf) {
			return doubleBlockHalf == DoubleBlockHalf.field_12607 && direction == Direction.field_11033 && !blockState.canPlaceAt(iWorld, blockPos)
				? Blocks.field_10124.method_9564()
				: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return Blocks.field_10124.method_9564();
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return blockPos.getY() < 255 && itemPlacementContext.method_8045().method_8320(blockPos.up()).canReplace(itemPlacementContext)
			? super.method_9605(itemPlacementContext)
			: null;
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.method_8652(blockPos.up(), this.method_9564().method_11657(field_10929, DoubleBlockHalf.field_12609), 3);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		if (blockState.method_11654(field_10929) != DoubleBlockHalf.field_12609) {
			return super.method_9558(blockState, viewableWorld, blockPos);
		} else {
			BlockState blockState2 = viewableWorld.method_8320(blockPos.down());
			return blockState2.getBlock() == this && blockState2.method_11654(field_10929) == DoubleBlockHalf.field_12607;
		}
	}

	public void placeAt(IWorld iWorld, BlockPos blockPos, int i) {
		iWorld.method_8652(blockPos, this.method_9564().method_11657(field_10929, DoubleBlockHalf.field_12607), i);
		iWorld.method_8652(blockPos.up(), this.method_9564().method_11657(field_10929, DoubleBlockHalf.field_12609), i);
	}

	@Override
	public void method_9556(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.method_9556(world, playerEntity, blockPos, Blocks.field_10124.method_9564(), blockEntity, itemStack);
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		DoubleBlockHalf doubleBlockHalf = blockState.method_11654(field_10929);
		BlockPos blockPos2 = doubleBlockHalf == DoubleBlockHalf.field_12607 ? blockPos.up() : blockPos.down();
		BlockState blockState2 = world.method_8320(blockPos2);
		if (blockState2.getBlock() == this && blockState2.method_11654(field_10929) != doubleBlockHalf) {
			world.method_8652(blockPos2, Blocks.field_10124.method_9564(), 35);
			world.playLevelEvent(playerEntity, 2001, blockPos2, Block.method_9507(blockState2));
			if (!world.isClient && !playerEntity.isCreative()) {
				method_9511(blockState, world, blockPos, null, playerEntity, playerEntity.getMainHandStack());
				method_9511(blockState2, world, blockPos2, null, playerEntity, playerEntity.getMainHandStack());
			}
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10929);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.field_10657;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long method_9535(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(
			blockPos.getX(), blockPos.down(blockState.method_11654(field_10929) == DoubleBlockHalf.field_12607 ? 0 : 1).getY(), blockPos.getZ()
		);
	}
}
