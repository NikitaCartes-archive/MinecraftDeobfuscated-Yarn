package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class TransparentBlock extends TranslucentBlock {
	public static final MapCodec<TransparentBlock> CODEC = createCodec(TransparentBlock::new);

	protected TransparentBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends TransparentBlock> getCodec() {
		return CODEC;
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}
}
