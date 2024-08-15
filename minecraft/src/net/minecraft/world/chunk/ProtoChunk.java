package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.SimpleTickScheduler;

public class ProtoChunk extends Chunk {
	@Nullable
	private volatile LightingProvider lightingProvider;
	private volatile ChunkStatus status = ChunkStatus.EMPTY;
	private final List<NbtCompound> entities = Lists.<NbtCompound>newArrayList();
	@Nullable
	private CarvingMask carvingMask;
	@Nullable
	private BelowZeroRetrogen belowZeroRetrogen;
	private final SimpleTickScheduler<Block> blockTickScheduler;
	private final SimpleTickScheduler<Fluid> fluidTickScheduler;

	public ProtoChunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView world, Registry<Biome> biomeRegistry, @Nullable BlendingData blendingData) {
		this(pos, upgradeData, null, new SimpleTickScheduler<>(), new SimpleTickScheduler<>(), world, biomeRegistry, blendingData);
	}

	public ProtoChunk(
		ChunkPos pos,
		UpgradeData upgradeData,
		@Nullable ChunkSection[] sections,
		SimpleTickScheduler<Block> blockTickScheduler,
		SimpleTickScheduler<Fluid> fluidTickScheduler,
		HeightLimitView world,
		Registry<Biome> biomeRegistry,
		@Nullable BlendingData blendingData
	) {
		super(pos, upgradeData, world, biomeRegistry, 0L, sections, blendingData);
		this.blockTickScheduler = blockTickScheduler;
		this.fluidTickScheduler = fluidTickScheduler;
	}

	@Override
	public BasicTickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	@Override
	public BasicTickScheduler<Fluid> getFluidTickScheduler() {
		return this.fluidTickScheduler;
	}

	@Override
	public Chunk.TickSchedulers getTickSchedulers(long time) {
		return new Chunk.TickSchedulers(this.blockTickScheduler.collectTicks(time), this.fluidTickScheduler.collectTicks(time));
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = pos.getY();
		if (this.isOutOfHeightLimit(i)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSection(this.getSectionIndex(i));
			return chunkSection.isEmpty() ? Blocks.AIR.getDefaultState() : chunkSection.getBlockState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		int i = pos.getY();
		if (this.isOutOfHeightLimit(i)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSection(this.getSectionIndex(i));
			return chunkSection.isEmpty() ? Fluids.EMPTY.getDefaultState() : chunkSection.getFluidState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		if (j >= this.getBottomY() && j < this.getTopY()) {
			int l = this.getSectionIndex(j);
			ChunkSection chunkSection = this.getSection(l);
			boolean bl = chunkSection.isEmpty();
			if (bl && state.isOf(Blocks.AIR)) {
				return state;
			} else {
				int m = ChunkSectionPos.getLocalCoord(i);
				int n = ChunkSectionPos.getLocalCoord(j);
				int o = ChunkSectionPos.getLocalCoord(k);
				BlockState blockState = chunkSection.setBlockState(m, n, o, state);
				if (this.status.isAtLeast(ChunkStatus.INITIALIZE_LIGHT)) {
					boolean bl2 = chunkSection.isEmpty();
					if (bl2 != bl) {
						this.lightingProvider.setSectionStatus(pos, bl2);
					}

					if (ChunkLightProvider.needsLightUpdate(blockState, state)) {
						this.chunkSkyLight.isSkyLightAccessible(this, m, j, o);
						this.lightingProvider.checkBlock(pos);
					}
				}

				EnumSet<Heightmap.Type> enumSet = this.getStatus().getHeightmapTypes();
				EnumSet<Heightmap.Type> enumSet2 = null;

				for (Heightmap.Type type : enumSet) {
					Heightmap heightmap = (Heightmap)this.heightmaps.get(type);
					if (heightmap == null) {
						if (enumSet2 == null) {
							enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
						}

						enumSet2.add(type);
					}
				}

				if (enumSet2 != null) {
					Heightmap.populateHeightmaps(this, enumSet2);
				}

				for (Heightmap.Type typex : enumSet) {
					((Heightmap)this.heightmaps.get(typex)).trackUpdate(m, j, o, state);
				}

				return blockState;
			}
		} else {
			return Blocks.VOID_AIR.getDefaultState();
		}
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		this.blockEntities.put(blockEntity.getPos(), blockEntity);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return (BlockEntity)this.blockEntities.get(pos);
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public void addEntity(NbtCompound entityNbt) {
		this.entities.add(entityNbt);
	}

	@Override
	public void addEntity(Entity entity) {
		if (!entity.hasVehicle()) {
			NbtCompound nbtCompound = new NbtCompound();
			entity.saveNbt(nbtCompound);
			this.addEntity(nbtCompound);
		}
	}

	@Override
	public void setStructureStart(Structure structure, StructureStart start) {
		BelowZeroRetrogen belowZeroRetrogen = this.getBelowZeroRetrogen();
		if (belowZeroRetrogen != null && start.hasChildren()) {
			BlockBox blockBox = start.getBoundingBox();
			HeightLimitView heightLimitView = this.getHeightLimitView();
			if (blockBox.getMinY() < heightLimitView.getBottomY() || blockBox.getMaxY() >= heightLimitView.getTopY()) {
				return;
			}
		}

		super.setStructureStart(structure, start);
	}

	public List<NbtCompound> getEntities() {
		return this.entities;
	}

	@Override
	public ChunkStatus getStatus() {
		return this.status;
	}

	public void setStatus(ChunkStatus status) {
		this.status = status;
		if (this.belowZeroRetrogen != null && status.isAtLeast(this.belowZeroRetrogen.getTargetStatus())) {
			this.setBelowZeroRetrogen(null);
		}

		this.setNeedsSaving(true);
	}

	@Override
	public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		if (this.getMaxStatus().isAtLeast(ChunkStatus.BIOMES)) {
			return super.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
		} else {
			throw new IllegalStateException("Asking for biomes before we have biomes");
		}
	}

	public static short getPackedSectionRelative(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		int l = i & 15;
		int m = j & 15;
		int n = k & 15;
		return (short)(l | m << 4 | n << 8);
	}

	public static BlockPos joinBlockPos(short sectionRel, int sectionY, ChunkPos chunkPos) {
		int i = ChunkSectionPos.getOffsetPos(chunkPos.x, sectionRel & 15);
		int j = ChunkSectionPos.getOffsetPos(sectionY, sectionRel >>> 4 & 15);
		int k = ChunkSectionPos.getOffsetPos(chunkPos.z, sectionRel >>> 8 & 15);
		return new BlockPos(i, j, k);
	}

	@Override
	public void markBlockForPostProcessing(BlockPos pos) {
		if (!this.isOutOfHeightLimit(pos)) {
			Chunk.getList(this.postProcessingLists, this.getSectionIndex(pos.getY())).add(getPackedSectionRelative(pos));
		}
	}

	@Override
	public void markBlocksForPostProcessing(ShortList packedPositions, int index) {
		Chunk.getList(this.postProcessingLists, index).addAll(packedPositions);
	}

	public Map<BlockPos, NbtCompound> getBlockEntityNbts() {
		return Collections.unmodifiableMap(this.blockEntityNbts);
	}

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos, RegistryWrapper.WrapperLookup registryLookup) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		return blockEntity != null ? blockEntity.createNbtWithIdentifyingData(registryLookup) : (NbtCompound)this.blockEntityNbts.get(pos);
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
		this.blockEntities.remove(pos);
		this.blockEntityNbts.remove(pos);
	}

	@Nullable
	public CarvingMask getCarvingMask() {
		return this.carvingMask;
	}

	public CarvingMask getOrCreateCarvingMask() {
		if (this.carvingMask == null) {
			this.carvingMask = new CarvingMask(this.getHeight(), this.getBottomY());
		}

		return this.carvingMask;
	}

	public void setCarvingMask(CarvingMask carvingMask) {
		this.carvingMask = carvingMask;
	}

	public void setLightingProvider(LightingProvider lightingProvider) {
		this.lightingProvider = lightingProvider;
	}

	public void setBelowZeroRetrogen(@Nullable BelowZeroRetrogen belowZeroRetrogen) {
		this.belowZeroRetrogen = belowZeroRetrogen;
	}

	@Nullable
	@Override
	public BelowZeroRetrogen getBelowZeroRetrogen() {
		return this.belowZeroRetrogen;
	}

	private static <T> ChunkTickScheduler<T> createProtoTickScheduler(SimpleTickScheduler<T> tickScheduler) {
		return new ChunkTickScheduler<>(tickScheduler.getTicks());
	}

	public ChunkTickScheduler<Block> getBlockProtoTickScheduler() {
		return createProtoTickScheduler(this.blockTickScheduler);
	}

	public ChunkTickScheduler<Fluid> getFluidProtoTickScheduler() {
		return createProtoTickScheduler(this.fluidTickScheduler);
	}

	@Override
	public HeightLimitView getHeightLimitView() {
		return (HeightLimitView)(this.hasBelowZeroRetrogen() ? BelowZeroRetrogen.BELOW_ZERO_VIEW : this);
	}
}
