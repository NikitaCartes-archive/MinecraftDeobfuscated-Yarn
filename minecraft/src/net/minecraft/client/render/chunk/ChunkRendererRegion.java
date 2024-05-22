package net.minecraft.client.render.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;

@Environment(EnvType.CLIENT)
public class ChunkRendererRegion implements BlockRenderView {
	public static final int field_52160 = 1;
	public static final int field_52161 = 3;
	private final int field_52162;
	private final int field_52163;
	protected final RenderedChunk[] chunks;
	protected final World world;

	ChunkRendererRegion(World world, int chunkX, int chunkZ, RenderedChunk[] chunks) {
		this.world = world;
		this.field_52162 = chunkX;
		this.field_52163 = chunkZ;
		this.chunks = chunks;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.method_60898(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.method_60898(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockState(pos).getFluidState();
	}

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return this.world.getBrightness(direction, shaded);
	}

	@Override
	public LightingProvider getLightingProvider() {
		return this.world.getLightingProvider();
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.method_60898(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockEntity(pos);
	}

	private RenderedChunk method_60898(int i, int j) {
		return this.chunks[method_60899(this.field_52162, this.field_52163, i, j)];
	}

	@Override
	public int getColor(BlockPos pos, ColorResolver colorResolver) {
		return this.world.getColor(pos, colorResolver);
	}

	@Override
	public int getBottomY() {
		return this.world.getBottomY();
	}

	@Override
	public int getHeight() {
		return this.world.getHeight();
	}

	public static int method_60899(int i, int j, int k, int l) {
		return k - i + (l - j) * 3;
	}
}
