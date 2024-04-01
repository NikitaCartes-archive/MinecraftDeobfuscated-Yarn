package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PotatoSproutsBlock extends PlantBlock implements Fertilizable {
	public static final MapCodec<PotatoSproutsBlock> CODEC = createCodec(PotatoSproutsBlock::new);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	@Override
	public MapCodec<PotatoSproutsBlock> getCodec() {
		return CODEC;
	}

	public PotatoSproutsBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d vec3d = state.getModelOffset(world, pos);
		return SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.CORRUPTED_PEELGRASS_BLOCK) || floor.isOf(Blocks.PEELGRASS_BLOCK);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return (double)world.random.nextFloat() < 0.25;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		SaplingGenerator.POTATO.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random);
	}
}
