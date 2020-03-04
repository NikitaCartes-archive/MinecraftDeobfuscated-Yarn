package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ChunkPos pos;
	private volatile boolean shouldSave;
	@Nullable
	private BiomeArray biomes;
	@Nullable
	private volatile LightingProvider lightingProvider;
	private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
	private volatile ChunkStatus status = ChunkStatus.EMPTY;
	private final Map<BlockPos, BlockEntity> blockEntities = Maps.<BlockPos, BlockEntity>newHashMap();
	private final Map<BlockPos, CompoundTag> blockEntityTags = Maps.<BlockPos, CompoundTag>newHashMap();
	private final ChunkSection[] sections = new ChunkSection[16];
	private final List<CompoundTag> entities = Lists.<CompoundTag>newArrayList();
	private final List<BlockPos> lightSources = Lists.<BlockPos>newArrayList();
	private final ShortList[] postProcessingLists = new ShortList[16];
	private final Map<String, StructureStart> structureStarts = Maps.<String, StructureStart>newHashMap();
	private final Map<String, LongSet> structureReferences = Maps.<String, LongSet>newHashMap();
	private final UpgradeData upgradeData;
	private final ChunkTickScheduler<Block> blockTickScheduler;
	private final ChunkTickScheduler<Fluid> fluidTickScheduler;
	private long inhabitedTime;
	private final Map<GenerationStep.Carver, BitSet> carvingMasks = Maps.<GenerationStep.Carver, BitSet>newHashMap();
	private volatile boolean isLightOn;

	public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData) {
		this(
			chunkPos,
			upgradeData,
			null,
			new ChunkTickScheduler<>(block -> block == null || block.getDefaultState().isAir(), chunkPos),
			new ChunkTickScheduler<>(fluid -> fluid == null || fluid == Fluids.EMPTY, chunkPos)
		);
	}

	public ProtoChunk(
		ChunkPos chunkPos,
		UpgradeData upgradeData,
		@Nullable ChunkSection[] chunkSections,
		ChunkTickScheduler<Block> blockTickScheduler,
		ChunkTickScheduler<Fluid> fluidTickScheduler
	) {
		this.pos = chunkPos;
		this.upgradeData = upgradeData;
		this.blockTickScheduler = blockTickScheduler;
		this.fluidTickScheduler = fluidTickScheduler;
		if (chunkSections != null) {
			if (this.sections.length == chunkSections.length) {
				System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.sections.length);
			}
		}
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = pos.getY();
		if (World.isHeightInvalid(i)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSectionArray()[i >> 4];
			return ChunkSection.isEmpty(chunkSection) ? Blocks.AIR.getDefaultState() : chunkSection.getBlockState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		int i = pos.getY();
		if (World.isHeightInvalid(i)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSectionArray()[i >> 4];
			return ChunkSection.isEmpty(chunkSection) ? Fluids.EMPTY.getDefaultState() : chunkSection.getFluidState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.lightSources.stream();
	}

	public ShortList[] getLightSourcesBySection() {
		ShortList[] shortLists = new ShortList[16];

		for (BlockPos blockPos : this.lightSources) {
			Chunk.getList(shortLists, blockPos.getY() >> 4).add(getPackedSectionRelative(blockPos));
		}

		return shortLists;
	}

	public void addLightSource(short chunkSliceRel, int sectionY) {
		this.addLightSource(joinBlockPos(chunkSliceRel, sectionY, this.pos));
	}

	public void addLightSource(BlockPos pos) {
		this.lightSources.add(pos.toImmutable());
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		if (j >= 0 && j < 256) {
			if (this.sections[j >> 4] == WorldChunk.EMPTY_SECTION && state.getBlock() == Blocks.AIR) {
				return state;
			} else {
				if (state.getLuminance() > 0) {
					this.lightSources.add(new BlockPos((i & 15) + this.getPos().getStartX(), j, (k & 15) + this.getPos().getStartZ()));
				}

				ChunkSection chunkSection = this.getSection(j >> 4);
				BlockState blockState = chunkSection.setBlockState(i & 15, j & 15, k & 15, state);
				if (this.status.isAtLeast(ChunkStatus.FEATURES)
					&& state != blockState
					&& (
						state.getOpacity(this, pos) != blockState.getOpacity(this, pos)
							|| state.getLuminance() != blockState.getLuminance()
							|| state.hasSidedTransparency()
							|| blockState.hasSidedTransparency()
					)) {
					LightingProvider lightingProvider = this.getLightingProvider();
					lightingProvider.checkBlock(pos);
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
					((Heightmap)this.heightmaps.get(typex)).trackUpdate(i & 15, j, k & 15, state);
				}

				return blockState;
			}
		} else {
			return Blocks.VOID_AIR.getDefaultState();
		}
	}

	public ChunkSection getSection(int y) {
		if (this.sections[y] == WorldChunk.EMPTY_SECTION) {
			this.sections[y] = new ChunkSection(y << 4);
		}

		return this.sections[y];
	}

	@Override
	public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
		blockEntity.setPos(pos);
		this.blockEntities.put(pos, blockEntity);
	}

	@Override
	public Set<BlockPos> getBlockEntityPositions() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.blockEntityTags.keySet());
		set.addAll(this.blockEntities.keySet());
		return set;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return (BlockEntity)this.blockEntities.get(pos);
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public void addEntity(CompoundTag entityTag) {
		this.entities.add(entityTag);
	}

	@Override
	public void addEntity(Entity entity) {
		CompoundTag compoundTag = new CompoundTag();
		entity.saveToTag(compoundTag);
		this.addEntity(compoundTag);
	}

	public List<CompoundTag> getEntities() {
		return this.entities;
	}

	public void setBiomes(BiomeArray biomeArray) {
		this.biomes = biomeArray;
	}

	@Nullable
	@Override
	public BiomeArray getBiomeArray() {
		return this.biomes;
	}

	@Override
	public void setShouldSave(boolean shouldSave) {
		this.shouldSave = shouldSave;
	}

	@Override
	public boolean needsSaving() {
		return this.shouldSave;
	}

	@Override
	public ChunkStatus getStatus() {
		return this.status;
	}

	public void setStatus(ChunkStatus chunkStatus) {
		this.status = chunkStatus;
		this.setShouldSave(true);
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.sections;
	}

	@Nullable
	public LightingProvider getLightingProvider() {
		return this.lightingProvider;
	}

	@Override
	public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
		return Collections.unmodifiableSet(this.heightmaps.entrySet());
	}

	@Override
	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
		this.getHeightmap(type).setTo(heightmap);
	}

	@Override
	public Heightmap getHeightmap(Heightmap.Type type) {
		return (Heightmap)this.heightmaps.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		Heightmap heightmap = (Heightmap)this.heightmaps.get(type);
		if (heightmap == null) {
			Heightmap.populateHeightmaps(this, EnumSet.of(type));
			heightmap = (Heightmap)this.heightmaps.get(type);
		}

		return heightmap.get(x & 15, z & 15) - 1;
	}

	@Override
	public ChunkPos getPos() {
		return this.pos;
	}

	@Override
	public void setLastSaveTime(long lastSaveTime) {
	}

	@Nullable
	@Override
	public StructureStart getStructureStart(String structure) {
		return (StructureStart)this.structureStarts.get(structure);
	}

	@Override
	public void setStructureStart(String structure, StructureStart start) {
		this.structureStarts.put(structure, start);
		this.shouldSave = true;
	}

	@Override
	public Map<String, StructureStart> getStructureStarts() {
		return Collections.unmodifiableMap(this.structureStarts);
	}

	@Override
	public void setStructureStarts(Map<String, StructureStart> map) {
		this.structureStarts.clear();
		this.structureStarts.putAll(map);
		this.shouldSave = true;
	}

	@Override
	public LongSet getStructureReferences(String structure) {
		return (LongSet)this.structureReferences.computeIfAbsent(structure, string -> new LongOpenHashSet());
	}

	@Override
	public void addStructureReference(String structure, long reference) {
		((LongSet)this.structureReferences.computeIfAbsent(structure, string -> new LongOpenHashSet())).add(reference);
		this.shouldSave = true;
	}

	@Override
	public Map<String, LongSet> getStructureReferences() {
		return Collections.unmodifiableMap(this.structureReferences);
	}

	@Override
	public void setStructureReferences(Map<String, LongSet> structureReferences) {
		this.structureReferences.clear();
		this.structureReferences.putAll(structureReferences);
		this.shouldSave = true;
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
		int i = (sectionRel & 15) + (chunkPos.x << 4);
		int j = (sectionRel >>> 4 & 15) + (sectionY << 4);
		int k = (sectionRel >>> 8 & 15) + (chunkPos.z << 4);
		return new BlockPos(i, j, k);
	}

	@Override
	public void markBlockForPostProcessing(BlockPos blockPos) {
		if (!World.isHeightInvalid(blockPos)) {
			Chunk.getList(this.postProcessingLists, blockPos.getY() >> 4).add(getPackedSectionRelative(blockPos));
		}
	}

	@Override
	public ShortList[] getPostProcessingLists() {
		return this.postProcessingLists;
	}

	@Override
	public void markBlockForPostProcessing(short s, int i) {
		Chunk.getList(this.postProcessingLists, i).add(s);
	}

	public ChunkTickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
		return this.fluidTickScheduler;
	}

	@Override
	public UpgradeData getUpgradeData() {
		return this.upgradeData;
	}

	@Override
	public void setInhabitedTime(long inhabitedTime) {
		this.inhabitedTime = inhabitedTime;
	}

	@Override
	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	@Override
	public void addPendingBlockEntityTag(CompoundTag compoundTag) {
		this.blockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
	}

	public Map<BlockPos, CompoundTag> getBlockEntityTags() {
		return Collections.unmodifiableMap(this.blockEntityTags);
	}

	@Override
	public CompoundTag getBlockEntityTagAt(BlockPos pos) {
		return (CompoundTag)this.blockEntityTags.get(pos);
	}

	@Nullable
	@Override
	public CompoundTag method_20598(BlockPos blockPos) {
		BlockEntity blockEntity = this.getBlockEntity(blockPos);
		return blockEntity != null ? blockEntity.toTag(new CompoundTag()) : (CompoundTag)this.blockEntityTags.get(blockPos);
	}

	@Override
	public void removeBlockEntity(BlockPos blockPos) {
		this.blockEntities.remove(blockPos);
		this.blockEntityTags.remove(blockPos);
	}

	@Override
	public BitSet getCarvingMask(GenerationStep.Carver carver) {
		return (BitSet)this.carvingMasks.computeIfAbsent(carver, carverx -> new BitSet(65536));
	}

	public void setCarvingMask(GenerationStep.Carver carver, BitSet mask) {
		this.carvingMasks.put(carver, mask);
	}

	public void setLightingProvider(LightingProvider lightingProvider) {
		this.lightingProvider = lightingProvider;
	}

	@Override
	public boolean isLightOn() {
		return this.isLightOn;
	}

	@Override
	public void setLightOn(boolean lightOn) {
		this.isLightOn = lightOn;
		this.setShouldSave(true);
	}
}
