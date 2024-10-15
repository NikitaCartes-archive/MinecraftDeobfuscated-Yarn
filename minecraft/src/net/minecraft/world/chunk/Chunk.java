package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.light.ChunkSkyLight;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.Tick;
import org.slf4j.Logger;

/**
 * Represents a scoped, modifiable view of biomes, block states, fluid states and block entities.
 */
public abstract class Chunk implements BiomeAccess.Storage, LightSourceView, StructureHolder {
	public static final int MISSING_SECTION = -1;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final LongSet EMPTY_STRUCTURE_REFERENCES = new LongOpenHashSet();
	protected final ShortList[] postProcessingLists;
	private volatile boolean needsSaving;
	private volatile boolean lightOn;
	protected final ChunkPos pos;
	private long inhabitedTime;
	@Nullable
	@Deprecated
	private GenerationSettings generationSettings;
	@Nullable
	protected ChunkNoiseSampler chunkNoiseSampler;
	protected final UpgradeData upgradeData;
	@Nullable
	protected BlendingData blendingData;
	protected final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
	protected ChunkSkyLight chunkSkyLight;
	private final Map<Structure, StructureStart> structureStarts = Maps.<Structure, StructureStart>newHashMap();
	private final Map<Structure, LongSet> structureReferences = Maps.<Structure, LongSet>newHashMap();
	protected final Map<BlockPos, NbtCompound> blockEntityNbts = Maps.<BlockPos, NbtCompound>newHashMap();
	protected final Map<BlockPos, BlockEntity> blockEntities = new Object2ObjectOpenHashMap<>();
	protected final HeightLimitView heightLimitView;
	protected final ChunkSection[] sectionArray;

	public Chunk(
		ChunkPos pos,
		UpgradeData upgradeData,
		HeightLimitView heightLimitView,
		Registry<Biome> biomeRegistry,
		long inhabitedTime,
		@Nullable ChunkSection[] sectionArray,
		@Nullable BlendingData blendingData
	) {
		this.pos = pos;
		this.upgradeData = upgradeData;
		this.heightLimitView = heightLimitView;
		this.sectionArray = new ChunkSection[heightLimitView.countVerticalSections()];
		this.inhabitedTime = inhabitedTime;
		this.postProcessingLists = new ShortList[heightLimitView.countVerticalSections()];
		this.blendingData = blendingData;
		this.chunkSkyLight = new ChunkSkyLight(heightLimitView);
		if (sectionArray != null) {
			if (this.sectionArray.length == sectionArray.length) {
				System.arraycopy(sectionArray, 0, this.sectionArray, 0, this.sectionArray.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", sectionArray.length, this.sectionArray.length);
			}
		}

		fillSectionArray(biomeRegistry, this.sectionArray);
	}

	private static void fillSectionArray(Registry<Biome> biomeRegistry, ChunkSection[] sectionArray) {
		for (int i = 0; i < sectionArray.length; i++) {
			if (sectionArray[i] == null) {
				sectionArray[i] = new ChunkSection(biomeRegistry);
			}
		}
	}

	public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
		return GameEventDispatcher.EMPTY;
	}

	@Nullable
	public abstract BlockState setBlockState(BlockPos pos, BlockState state, boolean moved);

	public abstract void setBlockEntity(BlockEntity blockEntity);

	public abstract void addEntity(Entity entity);

	public int getHighestNonEmptySection() {
		ChunkSection[] chunkSections = this.getSectionArray();

		for (int i = chunkSections.length - 1; i >= 0; i--) {
			ChunkSection chunkSection = chunkSections[i];
			if (!chunkSection.isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	@Deprecated(
		forRemoval = true
	)
	public int getHighestNonEmptySectionYOffset() {
		int i = this.getHighestNonEmptySection();
		return i == -1 ? this.getBottomY() : ChunkSectionPos.getBlockCoord(this.sectionIndexToCoord(i));
	}

	public Set<BlockPos> getBlockEntityPositions() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.blockEntityNbts.keySet());
		set.addAll(this.blockEntities.keySet());
		return set;
	}

	public ChunkSection[] getSectionArray() {
		return this.sectionArray;
	}

	public ChunkSection getSection(int yIndex) {
		return this.getSectionArray()[yIndex];
	}

	public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
		return Collections.unmodifiableSet(this.heightmaps.entrySet());
	}

	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
		this.getHeightmap(type).setTo(this, type, heightmap);
	}

	public Heightmap getHeightmap(Heightmap.Type type) {
		return (Heightmap)this.heightmaps.computeIfAbsent(type, type2 -> new Heightmap(this, type2));
	}

	public boolean hasHeightmap(Heightmap.Type type) {
		return this.heightmaps.get(type) != null;
	}

	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		Heightmap heightmap = (Heightmap)this.heightmaps.get(type);
		if (heightmap == null) {
			if (SharedConstants.isDevelopment && this instanceof WorldChunk) {
				LOGGER.error("Unprimed heightmap: " + type + " " + x + " " + z);
			}

			Heightmap.populateHeightmaps(this, EnumSet.of(type));
			heightmap = (Heightmap)this.heightmaps.get(type);
		}

		return heightmap.get(x & 15, z & 15) - 1;
	}

	public ChunkPos getPos() {
		return this.pos;
	}

	@Nullable
	@Override
	public StructureStart getStructureStart(Structure structure) {
		return (StructureStart)this.structureStarts.get(structure);
	}

	@Override
	public void setStructureStart(Structure structure, StructureStart start) {
		this.structureStarts.put(structure, start);
		this.markNeedsSaving();
	}

	public Map<Structure, StructureStart> getStructureStarts() {
		return Collections.unmodifiableMap(this.structureStarts);
	}

	public void setStructureStarts(Map<Structure, StructureStart> structureStarts) {
		this.structureStarts.clear();
		this.structureStarts.putAll(structureStarts);
		this.markNeedsSaving();
	}

	@Override
	public LongSet getStructureReferences(Structure structure) {
		return (LongSet)this.structureReferences.getOrDefault(structure, EMPTY_STRUCTURE_REFERENCES);
	}

	@Override
	public void addStructureReference(Structure structure, long reference) {
		((LongSet)this.structureReferences.computeIfAbsent(structure, type2 -> new LongOpenHashSet())).add(reference);
		this.markNeedsSaving();
	}

	@Override
	public Map<Structure, LongSet> getStructureReferences() {
		return Collections.unmodifiableMap(this.structureReferences);
	}

	@Override
	public void setStructureReferences(Map<Structure, LongSet> structureReferences) {
		this.structureReferences.clear();
		this.structureReferences.putAll(structureReferences);
		this.markNeedsSaving();
	}

	public boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
		if (lowerHeight < this.getBottomY()) {
			lowerHeight = this.getBottomY();
		}

		if (upperHeight > this.getTopYInclusive()) {
			upperHeight = this.getTopYInclusive();
		}

		for (int i = lowerHeight; i <= upperHeight; i += 16) {
			if (!this.getSection(this.getSectionIndex(i)).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public boolean isSectionEmpty(int sectionCoord) {
		return this.getSection(this.sectionCoordToIndex(sectionCoord)).isEmpty();
	}

	public void markNeedsSaving() {
		this.needsSaving = true;
	}

	public boolean tryMarkSaved() {
		if (this.needsSaving) {
			this.needsSaving = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean needsSaving() {
		return this.needsSaving;
	}

	public abstract ChunkStatus getStatus();

	/**
	 * {@return the chunk status or the target status for the retrogen, whichever is later}
	 */
	public ChunkStatus getMaxStatus() {
		ChunkStatus chunkStatus = this.getStatus();
		BelowZeroRetrogen belowZeroRetrogen = this.getBelowZeroRetrogen();
		if (belowZeroRetrogen != null) {
			ChunkStatus chunkStatus2 = belowZeroRetrogen.getTargetStatus();
			return ChunkStatus.max(chunkStatus2, chunkStatus);
		} else {
			return chunkStatus;
		}
	}

	public abstract void removeBlockEntity(BlockPos pos);

	public void markBlockForPostProcessing(BlockPos pos) {
		LOGGER.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", pos);
	}

	public ShortList[] getPostProcessingLists() {
		return this.postProcessingLists;
	}

	public void markBlocksForPostProcessing(ShortList packedPositions, int index) {
		getList(this.getPostProcessingLists(), index).addAll(packedPositions);
	}

	public void addPendingBlockEntityNbt(NbtCompound nbt) {
		this.blockEntityNbts.put(BlockEntity.posFromNbt(nbt), nbt);
	}

	@Nullable
	public NbtCompound getBlockEntityNbt(BlockPos pos) {
		return (NbtCompound)this.blockEntityNbts.get(pos);
	}

	@Nullable
	public abstract NbtCompound getPackedBlockEntityNbt(BlockPos pos, RegistryWrapper.WrapperLookup registries);

	@Override
	public final void forEachLightSource(BiConsumer<BlockPos, BlockState> callback) {
		this.forEachBlockMatchingPredicate(blockState -> blockState.getLuminance() != 0, callback);
	}

	public void forEachBlockMatchingPredicate(Predicate<BlockState> predicate, BiConsumer<BlockPos, BlockState> consumer) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = this.getBottomSectionCoord(); i <= this.getTopSectionCoord(); i++) {
			ChunkSection chunkSection = this.getSection(this.sectionCoordToIndex(i));
			if (chunkSection.hasAny(predicate)) {
				BlockPos blockPos = ChunkSectionPos.from(this.pos, i).getMinPos();

				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						for (int l = 0; l < 16; l++) {
							BlockState blockState = chunkSection.getBlockState(l, j, k);
							if (predicate.test(blockState)) {
								consumer.accept(mutable.set(blockPos, l, j, k), blockState);
							}
						}
					}
				}
			}
		}
	}

	public abstract BasicTickScheduler<Block> getBlockTickScheduler();

	public abstract BasicTickScheduler<Fluid> getFluidTickScheduler();

	public boolean isSerializable() {
		return true;
	}

	public abstract Chunk.TickSchedulers getTickSchedulers(long time);

	public UpgradeData getUpgradeData() {
		return this.upgradeData;
	}

	public boolean usesOldNoise() {
		return this.blendingData != null;
	}

	@Nullable
	public BlendingData getBlendingData() {
		return this.blendingData;
	}

	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	public void increaseInhabitedTime(long delta) {
		this.inhabitedTime += delta;
	}

	public void setInhabitedTime(long inhabitedTime) {
		this.inhabitedTime = inhabitedTime;
	}

	public static ShortList getList(ShortList[] lists, int index) {
		if (lists[index] == null) {
			lists[index] = new ShortArrayList();
		}

		return lists[index];
	}

	public boolean isLightOn() {
		return this.lightOn;
	}

	public void setLightOn(boolean lightOn) {
		this.lightOn = lightOn;
		this.markNeedsSaving();
	}

	@Override
	public int getBottomY() {
		return this.heightLimitView.getBottomY();
	}

	@Override
	public int getHeight() {
		return this.heightLimitView.getHeight();
	}

	public ChunkNoiseSampler getOrCreateChunkNoiseSampler(Function<Chunk, ChunkNoiseSampler> chunkNoiseSamplerCreator) {
		if (this.chunkNoiseSampler == null) {
			this.chunkNoiseSampler = (ChunkNoiseSampler)chunkNoiseSamplerCreator.apply(this);
		}

		return this.chunkNoiseSampler;
	}

	@Deprecated
	public GenerationSettings getOrCreateGenerationSettings(Supplier<GenerationSettings> generationSettingsCreator) {
		if (this.generationSettings == null) {
			this.generationSettings = (GenerationSettings)generationSettingsCreator.get();
		}

		return this.generationSettings;
	}

	@Override
	public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		try {
			int i = BiomeCoords.fromBlock(this.getBottomY());
			int j = i + BiomeCoords.fromBlock(this.getHeight()) - 1;
			int k = MathHelper.clamp(biomeY, i, j);
			int l = this.getSectionIndex(BiomeCoords.toBlock(k));
			return this.sectionArray[l].getBiome(biomeX & 3, k & 3, biomeZ & 3);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Getting biome");
			CrashReportSection crashReportSection = crashReport.addElement("Biome being got");
			crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this, biomeX, biomeY, biomeZ)));
			throw new CrashException(crashReport);
		}
	}

	public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
		ChunkPos chunkPos = this.getPos();
		int i = BiomeCoords.fromBlock(chunkPos.getStartX());
		int j = BiomeCoords.fromBlock(chunkPos.getStartZ());
		HeightLimitView heightLimitView = this.getHeightLimitView();

		for (int k = heightLimitView.getBottomSectionCoord(); k <= heightLimitView.getTopSectionCoord(); k++) {
			ChunkSection chunkSection = this.getSection(this.sectionCoordToIndex(k));
			int l = BiomeCoords.fromChunk(k);
			chunkSection.populateBiomes(biomeSupplier, sampler, i, l, j);
		}
	}

	public boolean hasStructureReferences() {
		return !this.getStructureReferences().isEmpty();
	}

	@Nullable
	public BelowZeroRetrogen getBelowZeroRetrogen() {
		return null;
	}

	public boolean hasBelowZeroRetrogen() {
		return this.getBelowZeroRetrogen() != null;
	}

	public HeightLimitView getHeightLimitView() {
		return this;
	}

	public void refreshSurfaceY() {
		this.chunkSkyLight.refreshSurfaceY(this);
	}

	@Override
	public ChunkSkyLight getChunkSkyLight() {
		return this.chunkSkyLight;
	}

	public static record TickSchedulers(List<Tick<Block>> blocks, List<Tick<Fluid>> fluids) {
	}
}
