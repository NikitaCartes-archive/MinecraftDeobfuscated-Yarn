package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_6643;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Represents a read only view of a world chunk used in world generation.
 */
public class ReadOnlyChunk extends ProtoChunk {
	private final WorldChunk wrapped;
	private final boolean field_34554;

	public ReadOnlyChunk(WorldChunk wrapped, boolean bl) {
		super(wrapped.getPos(), UpgradeData.NO_UPGRADE_DATA, wrapped.heightLimitView, wrapped.getWorld().getRegistryManager().get(Registry.BIOME_KEY));
		this.wrapped = wrapped;
		this.field_34554 = bl;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.wrapped.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.wrapped.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.wrapped.getFluidState(pos);
	}

	@Override
	public int getMaxLightLevel() {
		return this.wrapped.getMaxLightLevel();
	}

	@Override
	public ChunkSection getSection(int yIndex) {
		return this.field_34554 ? this.wrapped.getSection(yIndex) : super.getSection(yIndex);
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return this.field_34554 ? this.wrapped.setBlockState(pos, state, moved) : null;
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		if (this.field_34554) {
			this.wrapped.setBlockEntity(blockEntity);
		}
	}

	@Override
	public void addEntity(Entity entity) {
		if (this.field_34554) {
			this.wrapped.addEntity(entity);
		}
	}

	@Override
	public void setStatus(ChunkStatus status) {
		if (this.field_34554) {
			super.setStatus(status);
		}
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.wrapped.getSectionArray();
	}

	@Override
	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
	}

	private Heightmap.Type transformHeightmapType(Heightmap.Type type) {
		if (type == Heightmap.Type.WORLD_SURFACE_WG) {
			return Heightmap.Type.WORLD_SURFACE;
		} else {
			return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
		}
	}

	@Override
	public Heightmap getHeightmap(Heightmap.Type type) {
		return this.wrapped.getHeightmap(type);
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		return this.wrapped.sampleHeightmap(this.transformHeightmapType(type), x, z);
	}

	@Override
	public BlockPos sampleMaxHeightMap(Heightmap.Type type) {
		return this.wrapped.sampleMaxHeightMap(this.transformHeightmapType(type));
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.wrapped.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
	}

	@Override
	public ChunkPos getPos() {
		return this.wrapped.getPos();
	}

	@Nullable
	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
		return this.wrapped.getStructureStart(structure);
	}

	@Override
	public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
	}

	@Override
	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return this.wrapped.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> structure) {
		return this.wrapped.getStructureReferences(structure);
	}

	@Override
	public void addStructureReference(StructureFeature<?> structure, long reference) {
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return this.wrapped.getStructureReferences();
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
	}

	@Override
	public void setShouldSave(boolean shouldSave) {
	}

	@Override
	public boolean needsSaving() {
		return false;
	}

	@Override
	public ChunkStatus getStatus() {
		return this.wrapped.getStatus();
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
	}

	@Override
	public void markBlockForPostProcessing(BlockPos pos) {
	}

	@Override
	public void addPendingBlockEntityNbt(NbtCompound nbt) {
	}

	@Nullable
	@Override
	public NbtCompound getBlockEntityNbt(BlockPos pos) {
		return this.wrapped.getBlockEntityNbt(pos);
	}

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
		return this.wrapped.getPackedBlockEntityNbt(pos);
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.wrapped.getLightSourcesStream();
	}

	@Override
	public TickScheduler<Block> getBlockTickScheduler() {
		return new ChunkTickScheduler<>(block -> block.getDefaultState().isAir(), this.getPos(), this);
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return new ChunkTickScheduler<>(fluid -> fluid == Fluids.EMPTY, this.getPos(), this);
	}

	@Override
	public class_6643 getCarvingMask(GenerationStep.Carver carver) {
		if (this.field_34554) {
			return super.getCarvingMask(carver);
		} else {
			throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("Meaningless in this context"));
		}
	}

	@Override
	public class_6643 getOrCreateCarvingMask(GenerationStep.Carver carver) {
		if (this.field_34554) {
			return super.getOrCreateCarvingMask(carver);
		} else {
			throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("Meaningless in this context"));
		}
	}

	public WorldChunk getWrappedChunk() {
		return this.wrapped;
	}

	@Override
	public boolean isLightOn() {
		return this.wrapped.isLightOn();
	}

	@Override
	public void setLightOn(boolean lightOn) {
		this.wrapped.setLightOn(lightOn);
	}

	@Override
	public void method_38257(BiomeSource source, MultiNoiseUtil.MultiNoiseSampler sampler) {
		if (this.field_34554) {
			this.wrapped.method_38257(source, sampler);
		}
	}
}
