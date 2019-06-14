package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractBannerBlock extends BlockWithEntity {
	private final DyeColor color;

	protected AbstractBannerBlock(DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new BannerBlockEntity(this.color);
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof BannerBlockEntity) {
				((BannerBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockEntity = blockView.method_8321(blockPos);
		return blockEntity instanceof BannerBlockEntity
			? ((BannerBlockEntity)blockEntity).method_10907(blockState)
			: super.method_9574(blockView, blockPos, blockState);
	}

	public DyeColor getColor() {
		return this.color;
	}
}
