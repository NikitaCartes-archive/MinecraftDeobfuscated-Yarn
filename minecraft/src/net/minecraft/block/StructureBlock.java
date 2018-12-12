package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StructureBlock extends BlockWithEntity {
	public static final EnumProperty<StructureMode> field_11586 = Properties.STRUCTURE_MODE;

	protected StructureBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new StructureBlockBlockEntity();
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		return blockEntity instanceof StructureBlockBlockEntity ? ((StructureBlockBlockEntity)blockEntity).openGui(playerEntity) : false;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		if (!world.isClient) {
			if (livingEntity != null) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof StructureBlockBlockEntity) {
					((StructureBlockBlockEntity)blockEntity).setAuthor(livingEntity);
				}
			}
		}
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_11586, StructureMode.field_12696);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11586);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof StructureBlockBlockEntity) {
				StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
				boolean bl = world.isReceivingRedstonePower(blockPos);
				boolean bl2 = structureBlockBlockEntity.isPowered();
				if (bl && !bl2) {
					structureBlockBlockEntity.setPowered(true);
					this.method_10703(structureBlockBlockEntity);
				} else if (!bl && bl2) {
					structureBlockBlockEntity.setPowered(false);
				}
			}
		}
	}

	private void method_10703(StructureBlockBlockEntity structureBlockBlockEntity) {
		switch (structureBlockBlockEntity.getMode()) {
			case field_12695:
				structureBlockBlockEntity.method_11366(false);
				break;
			case field_12697:
				structureBlockBlockEntity.method_11368(false);
				break;
			case field_12699:
				structureBlockBlockEntity.method_11361();
			case field_12696:
		}
	}
}
