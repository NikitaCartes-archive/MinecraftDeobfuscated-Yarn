package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class BambooSaplingBlock extends Block implements Fertilizable {
	protected static final VoxelShape field_9897 = Block.method_9541(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);

	public BambooSaplingBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Vec3d vec3d = blockState.method_11599(blockView, blockPos);
		return field_9897.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(3) == 0 && world.method_8623(blockPos.up()) && world.method_8624(blockPos.up(), 0) >= 9) {
			this.method_9351(world, blockPos);
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.method_8320(blockPos.down()).method_11602(BlockTags.field_15497);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.method_11591(iWorld, blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			if (direction == Direction.UP && blockState2.getBlock() == Blocks.field_10211) {
				iWorld.method_8652(blockPos, Blocks.field_10211.method_9564(), 2);
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.BAMBOO);
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return blockView.method_8320(blockPos.up()).isAir();
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		this.method_9351(world, blockPos);
	}

	@Override
	public float method_9594(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return playerEntity.method_6047().getItem() instanceof SwordItem ? 1.0F : super.method_9594(blockState, playerEntity, blockView, blockPos);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	protected void method_9351(World world, BlockPos blockPos) {
		world.method_8652(blockPos.up(), Blocks.field_10211.method_9564().method_11657(BambooBlock.field_9917, BambooLeaves.field_12466), 3);
	}
}
