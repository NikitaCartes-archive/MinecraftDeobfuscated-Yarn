package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallWitherSkullBlock extends WallSkullBlock {
	public static final MapCodec<WallWitherSkullBlock> CODEC = createCodec(WallWitherSkullBlock::new);

	@Override
	public MapCodec<WallWitherSkullBlock> getCodec() {
		return CODEC;
	}

	protected WallWitherSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.WITHER_SKELETON, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		WitherSkullBlock.onPlaced(world, pos);
	}
}
