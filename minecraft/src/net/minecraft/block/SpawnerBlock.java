package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SpawnerBlock extends BlockWithEntity {
	protected SpawnerBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new MobSpawnerBlockEntity();
	}

	@Override
	public void method_9565(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		super.method_9565(blockState, world, blockPos, itemStack);
		int i = 15 + world.random.nextInt(15) + world.random.nextInt(15);
		this.dropExperience(world, blockPos, i);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}
}
