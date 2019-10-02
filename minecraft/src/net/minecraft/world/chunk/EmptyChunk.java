package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeArray;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.light.LightingProvider;

public class EmptyChunk extends WorldChunk {
	private static final Biome[] BIOMES = SystemUtil.consume(new Biome[BiomeArray.DEFAULT_LENGTH], biomes -> Arrays.fill(biomes, Biomes.PLAINS));

	public EmptyChunk(World world, ChunkPos chunkPos) {
		super(world, chunkPos, new BiomeArray(BIOMES));
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return Blocks.VOID_AIR.getDefaultState();
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
		return null;
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return Fluids.EMPTY.getDefaultState();
	}

	@Nullable
	@Override
	public LightingProvider getLightingProvider() {
		return null;
	}

	@Override
	public int getLuminance(BlockPos blockPos) {
		return 0;
	}

	@Override
	public void addEntity(Entity entity) {
	}

	@Override
	public void remove(Entity entity) {
	}

	@Override
	public void remove(Entity entity, int i) {
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos, WorldChunk.CreationType creationType) {
		return null;
	}

	@Override
	public void addBlockEntity(BlockEntity blockEntity) {
	}

	@Override
	public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
	}

	@Override
	public void removeBlockEntity(BlockPos blockPos) {
	}

	@Override
	public void markDirty() {
	}

	@Override
	public void getEntities(@Nullable Entity entity, Box box, List<Entity> list, Predicate<? super Entity> predicate) {
	}

	@Override
	public <T extends Entity> void getEntities(Class<? extends T> class_, Box box, List<T> list, Predicate<? super T> predicate) {
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean method_12228(int i, int j) {
		return true;
	}

	@Override
	public ChunkHolder.LevelType getLevelType() {
		return ChunkHolder.LevelType.BORDER;
	}
}
