package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
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
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
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
	private final ChunkSection[] sections;
	private final List<CompoundTag> entities = Lists.<CompoundTag>newArrayList();
	private final List<BlockPos> lightSources = Lists.<BlockPos>newArrayList();
	private final ShortList[] postProcessingLists;
	private final Map<StructureFeature<?>, StructureStart<?>> structureStarts = Maps.<StructureFeature<?>, StructureStart<?>>newHashMap();
	private final Map<StructureFeature<?>, LongSet> structureReferences = Maps.<StructureFeature<?>, LongSet>newHashMap();
	private final UpgradeData upgradeData;
	private final ChunkTickScheduler<Block> blockTickScheduler;
	private final ChunkTickScheduler<Fluid> fluidTickScheduler;
	private final HeightLimitView field_27229;
	private long inhabitedTime;
	private final Map<GenerationStep.Carver, BitSet> carvingMasks = new Object2ObjectArrayMap<>();
	private volatile boolean lightOn;

	public ProtoChunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView) {
		this(
			pos,
			upgradeData,
			null,
			new ChunkTickScheduler<>(block -> block == null || block.getDefaultState().isAir(), pos, heightLimitView),
			new ChunkTickScheduler<>(fluid -> fluid == null || fluid == Fluids.EMPTY, pos, heightLimitView),
			heightLimitView
		);
	}

	public ProtoChunk(
		ChunkPos pos,
		UpgradeData upgradeData,
		@Nullable ChunkSection[] chunkSections,
		ChunkTickScheduler<Block> blockTickScheduler,
		ChunkTickScheduler<Fluid> fluidTickScheduler,
		HeightLimitView heightLimitView
	) {
		this.pos = pos;
		this.upgradeData = upgradeData;
		this.blockTickScheduler = blockTickScheduler;
		this.fluidTickScheduler = fluidTickScheduler;
		this.field_27229 = heightLimitView;
		this.sections = new ChunkSection[heightLimitView.method_32890()];
		if (chunkSections != null) {
			if (this.sections.length == chunkSections.length) {
				System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.sections.length);
			}
		}

		this.postProcessingLists = new ShortList[heightLimitView.method_32890()];
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = pos.getY();
		if (this.isOutOfHeightLimit(i)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSectionArray()[this.getSectionIndex(i)];
			return ChunkSection.isEmpty(chunkSection) ? Blocks.AIR.getDefaultState() : chunkSection.getBlockState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		int i = pos.getY();
		if (this.isOutOfHeightLimit(i)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			ChunkSection chunkSection = this.getSectionArray()[this.getSectionIndex(i)];
			return ChunkSection.isEmpty(chunkSection) ? Fluids.EMPTY.getDefaultState() : chunkSection.getFluidState(pos.getX() & 15, i & 15, pos.getZ() & 15);
		}
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.lightSources.stream();
	}

	public ShortList[] getLightSourcesBySection() {
		ShortList[] shortLists = new ShortList[this.method_32890()];

		for (BlockPos blockPos : this.lightSources) {
			Chunk.getList(shortLists, this.getSectionIndex(blockPos.getY())).add(getPackedSectionRelative(blockPos));
		}

		return shortLists;
	}

	public void addLightSource(short chunkSliceRel, int sectionY) {
		this.addLightSource(joinBlockPos(chunkSliceRel, this.getSection(sectionY), this.pos));
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
		if (j >= this.getSectionCount() && j < this.getTopHeightLimit()) {
			int l = this.getSectionIndex(j);
			if (this.sections[l] == WorldChunk.EMPTY_SECTION && state.isOf(Blocks.AIR)) {
				return state;
			} else {
				if (state.getLuminance() > 0) {
					this.lightSources.add(new BlockPos((i & 15) + this.getPos().getStartX(), j, (k & 15) + this.getPos().getStartZ()));
				}

				ChunkSection chunkSection = this.getSection(l);
				BlockState blockState = chunkSection.setBlockState(i & 15, j & 15, k & 15, state);
				if (this.status.isAtLeast(ChunkStatus.FEATURES)
					&& state != blockState
					&& (
						state.getOpacity(this, pos) != blockState.getOpacity(this, pos)
							|| state.getLuminance() != blockState.getLuminance()
							|| state.hasSidedTransparency()
							|| blockState.hasSidedTransparency()
					)) {
					this.lightingProvider.checkBlock(pos);
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
			this.sections[y] = new ChunkSection(this.getSection(y));
		}

		return this.sections[y];
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		this.blockEntities.put(blockEntity.getPos(), blockEntity);
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
		if (!entity.hasVehicle()) {
			CompoundTag compoundTag = new CompoundTag();
			entity.saveToTag(compoundTag);
			this.addEntity(compoundTag);
		}
	}

	public List<CompoundTag> getEntities() {
		return this.entities;
	}

	public void setBiomes(BiomeArray biomes) {
		this.biomes = biomes;
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

	public void setStatus(ChunkStatus status) {
		this.status = status;
		this.setShouldSave(true);
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.sections;
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

	@Nullable
	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
		return (StructureStart<?>)this.structureStarts.get(structure);
	}

	@Override
	public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
		this.structureStarts.put(structure, start);
		this.shouldSave = true;
	}

	@Override
	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return Collections.unmodifiableMap(this.structureStarts);
	}

	@Override
	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
		this.structureStarts.clear();
		this.structureStarts.putAll(structureStarts);
		this.shouldSave = true;
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> structure) {
		return (LongSet)this.structureReferences.computeIfAbsent(structure, structurex -> new LongOpenHashSet());
	}

	@Override
	public void addStructureReference(StructureFeature<?> structure, long reference) {
		((LongSet)this.structureReferences.computeIfAbsent(structure, structurex -> new LongOpenHashSet())).add(reference);
		this.shouldSave = true;
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return Collections.unmodifiableMap(this.structureReferences);
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
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
		int i = ChunkSectionPos.method_32205(chunkPos.x, sectionRel & 15);
		int j = ChunkSectionPos.method_32205(sectionY, sectionRel >>> 4 & 15);
		int k = ChunkSectionPos.method_32205(chunkPos.z, sectionRel >>> 8 & 15);
		return new BlockPos(i, j, k);
	}

	@Override
	public void markBlockForPostProcessing(BlockPos pos) {
		if (!this.isOutOfHeightLimit(pos)) {
			Chunk.getList(this.postProcessingLists, this.getSectionIndex(pos.getY())).add(getPackedSectionRelative(pos));
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
	public void addPendingBlockEntityTag(CompoundTag tag) {
		this.blockEntityTags.put(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), tag);
	}

	public Map<BlockPos, CompoundTag> getBlockEntityTags() {
		return Collections.unmodifiableMap(this.blockEntityTags);
	}

	@Override
	public CompoundTag getBlockEntityTag(BlockPos pos) {
		return (CompoundTag)this.blockEntityTags.get(pos);
	}

	@Nullable
	@Override
	public CompoundTag getPackedBlockEntityTag(BlockPos pos) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		return blockEntity != null ? blockEntity.toTag(new CompoundTag()) : (CompoundTag)this.blockEntityTags.get(pos);
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
		this.blockEntities.remove(pos);
		this.blockEntityTags.remove(pos);
	}

	@Nullable
	public BitSet getCarvingMask(GenerationStep.Carver carver) {
		return (BitSet)this.carvingMasks.get(carver);
	}

	public BitSet getOrCreateCarvingMask(GenerationStep.Carver carver) {
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
		return this.lightOn;
	}

	@Override
	public void setLightOn(boolean lightOn) {
		this.lightOn = lightOn;
		this.setShouldSave(true);
	}

	@Override
	public int getSectionCount() {
		return this.field_27229.getSectionCount();
	}

	@Override
	public int getBottomSectionLimit() {
		return this.field_27229.getBottomSectionLimit();
	}
}
