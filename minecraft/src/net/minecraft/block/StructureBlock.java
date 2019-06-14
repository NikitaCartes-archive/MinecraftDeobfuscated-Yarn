package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StructureBlock extends BlockWithEntity {
	public static final EnumProperty<StructureBlockMode> field_11586 = Properties.field_12547;

	protected StructureBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new StructureBlockBlockEntity();
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		return blockEntity instanceof StructureBlockBlockEntity ? ((StructureBlockBlockEntity)blockEntity).openScreen(playerEntity) : false;
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		if (!world.isClient) {
			if (livingEntity != null) {
				BlockEntity blockEntity = world.method_8321(blockPos);
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
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_11586, StructureBlockMode.field_12696);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11586);
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof StructureBlockBlockEntity) {
				StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
				boolean bl2 = world.isReceivingRedstonePower(blockPos);
				boolean bl3 = structureBlockBlockEntity.isPowered();
				if (bl2 && !bl3) {
					structureBlockBlockEntity.setPowered(true);
					this.method_10703(structureBlockBlockEntity);
				} else if (!bl2 && bl3) {
					structureBlockBlockEntity.setPowered(false);
				}
			}
		}
	}

	private void method_10703(StructureBlockBlockEntity structureBlockBlockEntity) {
		switch (structureBlockBlockEntity.method_11374()) {
			case field_12695:
				structureBlockBlockEntity.saveStructure(false);
				break;
			case field_12697:
				structureBlockBlockEntity.loadStructure(false);
				break;
			case field_12699:
				structureBlockBlockEntity.unloadStructure();
			case field_12696:
		}
	}
}
