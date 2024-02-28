package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public abstract class AbstractBannerBlock extends BlockWithEntity {
	private final DyeColor color;

	protected AbstractBannerBlock(DyeColor color, AbstractBlock.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Override
	protected abstract MapCodec<? extends AbstractBannerBlock> getCodec();

	@Override
	public boolean canMobSpawnInside(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BannerBlockEntity(pos, state, this.color);
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return world.getBlockEntity(pos) instanceof BannerBlockEntity bannerBlockEntity ? bannerBlockEntity.getPickStack() : super.getPickStack(world, pos, state);
	}

	public DyeColor getColor() {
		return this.color;
	}
}
