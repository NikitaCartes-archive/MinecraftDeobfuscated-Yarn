package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_6568;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a scoped, modifiable view of biomes, block states, fluid states and block entities.
 */
public abstract class Chunk implements BlockView, BiomeAccess.Storage, StructureHolder {
	private static final Logger field_34548 = LogManager.getLogger();
	protected final ShortList[] field_34536;
	protected volatile boolean field_34537;
	private volatile boolean field_34549;
	protected final ChunkPos field_34538;
	private long field_34550;
	@Nullable
	@Deprecated
	private Biome field_34551;
	@Nullable
	protected class_6568 field_34539;
	protected final UpgradeData field_34540;
	protected final Map<Heightmap.Type, Heightmap> field_34541 = Maps.newEnumMap(Heightmap.Type.class);
	private final Map<StructureFeature<?>, StructureStart<?>> field_34552 = Maps.<StructureFeature<?>, StructureStart<?>>newHashMap();
	private final Map<StructureFeature<?>, LongSet> field_34553 = Maps.<StructureFeature<?>, LongSet>newHashMap();
	protected final Map<BlockPos, NbtCompound> field_34542 = Maps.<BlockPos, NbtCompound>newHashMap();
	protected final Map<BlockPos, BlockEntity> field_34543 = Maps.<BlockPos, BlockEntity>newHashMap();
	protected final HeightLimitView field_34544;
	protected final ChunkSection[] field_34545;
	protected TickScheduler<Block> field_34546;
	protected TickScheduler<Fluid> field_34547;

	public Chunk(
		ChunkPos chunkPos,
		UpgradeData upgradeData,
		HeightLimitView heightLimitView,
		Registry<Biome> registry,
		long l,
		@Nullable ChunkSection[] chunkSections,
		TickScheduler<Block> tickScheduler,
		TickScheduler<Fluid> tickScheduler2
	) {
		this.field_34538 = chunkPos;
		this.field_34540 = upgradeData;
		this.field_34544 = heightLimitView;
		this.field_34545 = new ChunkSection[heightLimitView.countVerticalSections()];
		this.field_34550 = l;
		this.field_34536 = new ShortList[heightLimitView.countVerticalSections()];
		this.field_34546 = tickScheduler;
		this.field_34547 = tickScheduler2;
		if (chunkSections != null) {
			if (this.field_34545.length == chunkSections.length) {
				System.arraycopy(chunkSections, 0, this.field_34545, 0, this.field_34545.length);
			} else {
				field_34548.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.field_34545.length);
			}
		}

		method_38256(heightLimitView, registry, this.field_34545);
	}

	private static void method_38256(HeightLimitView heightLimitView, Registry<Biome> registry, ChunkSection[] chunkSections) {
		for (int i = 0; i < chunkSections.length; i++) {
			if (chunkSections[i] == null) {
				chunkSections[i] = new ChunkSection(heightLimitView.sectionIndexToCoord(i), registry);
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

	@Nullable
	public ChunkSection getHighestNonEmptySection() {
		ChunkSection[] chunkSections = this.getSectionArray();

		for (int i = chunkSections.length - 1; i >= 0; i--) {
			ChunkSection chunkSection = chunkSections[i];
			if (!chunkSection.isEmpty()) {
				return chunkSection;
			}
		}

		return null;
	}

	public int getHighestNonEmptySectionYOffset() {
		ChunkSection chunkSection = this.getHighestNonEmptySection();
		return chunkSection == null ? this.getBottomY() : chunkSection.getYOffset();
	}

	public Set<BlockPos> getBlockEntityPositions() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.field_34542.keySet());
		set.addAll(this.field_34543.keySet());
		return set;
	}

	public ChunkSection[] getSectionArray() {
		return this.field_34545;
	}

	public ChunkSection getSection(int yIndex) {
		return this.getSectionArray()[yIndex];
	}

	public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
		return Collections.unmodifiableSet(this.field_34541.entrySet());
	}

	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
		this.getHeightmap(type).setTo(this, type, heightmap);
	}

	public Heightmap getHeightmap(Heightmap.Type type) {
		return (Heightmap)this.field_34541.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		Heightmap heightmap = (Heightmap)this.field_34541.get(type);
		if (heightmap == null) {
			if (SharedConstants.isDevelopment && this instanceof WorldChunk) {
				field_34548.error("Unprimed heightmap: " + type + " " + x + " " + z);
			}

			Heightmap.populateHeightmaps(this, EnumSet.of(type));
			heightmap = (Heightmap)this.field_34541.get(type);
		}

		return heightmap.get(x & 15, z & 15) - 1;
	}

	public BlockPos sampleMaxHeightMap(Heightmap.Type type) {
		int i = this.getBottomY();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = this.field_34538.getStartX(); j <= this.field_34538.getEndX(); j++) {
			for (int k = this.field_34538.getStartZ(); k <= this.field_34538.getEndZ(); k++) {
				int l = this.sampleHeightmap(type, j & 15, k & 15);
				if (l > i) {
					i = l;
					mutable.set(j, l, k);
				}
			}
		}

		return mutable.toImmutable();
	}

	public ChunkPos getPos() {
		return this.field_34538;
	}

	@Nullable
	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
		return (StructureStart<?>)this.field_34552.get(structure);
	}

	@Override
	public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
		this.field_34552.put(structure, start);
		this.field_34537 = true;
	}

	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return Collections.unmodifiableMap(this.field_34552);
	}

	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
		this.field_34552.clear();
		this.field_34552.putAll(structureStarts);
		this.field_34537 = true;
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> structure) {
		return (LongSet)this.field_34553.computeIfAbsent(structure, structureFeature -> new LongOpenHashSet());
	}

	@Override
	public void addStructureReference(StructureFeature<?> structure, long reference) {
		((LongSet)this.field_34553.computeIfAbsent(structure, structureFeature -> new LongOpenHashSet())).add(reference);
		this.field_34537 = true;
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return Collections.unmodifiableMap(this.field_34553);
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
		this.field_34553.clear();
		this.field_34553.putAll(structureReferences);
		this.field_34537 = true;
	}

	public boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
		if (lowerHeight < this.getBottomY()) {
			lowerHeight = this.getBottomY();
		}

		if (upperHeight >= this.getTopY()) {
			upperHeight = this.getTopY() - 1;
		}

		for (int i = lowerHeight; i <= upperHeight; i += 16) {
			if (!this.getSection(this.getSectionIndex(i)).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void setShouldSave(boolean shouldSave) {
		this.field_34537 = shouldSave;
	}

	public boolean needsSaving() {
		return this.field_34537;
	}

	public abstract ChunkStatus getStatus();

	public abstract void removeBlockEntity(BlockPos pos);

	public void markBlockForPostProcessing(BlockPos pos) {
		LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", pos);
	}

	public ShortList[] getPostProcessingLists() {
		return this.field_34536;
	}

	public void markBlockForPostProcessing(short packedPos, int index) {
		getList(this.getPostProcessingLists(), index).add(packedPos);
	}

	public void addPendingBlockEntityNbt(NbtCompound nbt) {
		this.field_34542.put(BlockEntity.posFromNbt(nbt), nbt);
	}

	@Nullable
	public NbtCompound getBlockEntityNbt(BlockPos pos) {
		return (NbtCompound)this.field_34542.get(pos);
	}

	@Nullable
	public abstract NbtCompound getPackedBlockEntityNbt(BlockPos pos);

	public abstract Stream<BlockPos> getLightSourcesStream();

	public TickScheduler<Block> getBlockTickScheduler() {
		return this.field_34546;
	}

	public TickScheduler<Fluid> getFluidTickScheduler() {
		return this.field_34547;
	}

	public UpgradeData getUpgradeData() {
		return this.field_34540;
	}

	public long getInhabitedTime() {
		return this.field_34550;
	}

	public void setInhabitedTime(long inhabitedTime) {
		this.field_34550 = inhabitedTime;
	}

	public static ShortList getList(ShortList[] lists, int index) {
		if (lists[index] == null) {
			lists[index] = new ShortArrayList();
		}

		return lists[index];
	}

	public boolean isLightOn() {
		return this.field_34549;
	}

	public void setLightOn(boolean lightOn) {
		this.field_34549 = lightOn;
		this.setShouldSave(true);
	}

	@Override
	public int getBottomY() {
		return this.field_34544.getBottomY();
	}

	@Override
	public int getHeight() {
		return this.field_34544.getHeight();
	}

	public class_6568 method_38255(
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		NoiseColumnSampler noiseColumnSampler,
		Supplier<class_6568.class_6572> supplier,
		Supplier<ChunkGeneratorSettings> supplier2,
		AquiferSampler.class_6565 arg
	) {
		if (this.field_34539 == null) {
			this.field_34539 = new class_6568(m, n, 16 / m, j, i, noiseColumnSampler, k, l, (class_6568.class_6572)supplier.get(), supplier2, arg);
		}

		return this.field_34539;
	}

	@Deprecated
	public Biome method_38258(Supplier<Biome> supplier) {
		if (this.field_34551 == null) {
			this.field_34551 = (Biome)supplier.get();
		}

		return this.field_34551;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		try {
			int i = BiomeCoords.fromBlock(this.getBottomY());
			int j = i + BiomeCoords.fromBlock(this.getHeight()) - 1;
			int k = MathHelper.clamp(biomeY, i, j);
			int l = this.getSectionIndex(BiomeCoords.toBlock(k));
			return this.field_34545[l].method_38293(biomeX & 3, k & 3, biomeZ & 3);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Getting biome");
			CrashReportSection crashReportSection = crashReport.addElement("Biome being got");
			crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this, biomeX, biomeY, biomeZ)));
			throw new CrashException(crashReport);
		}
	}

	public void method_38257(BiomeSource biomeSource, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
		ChunkPos chunkPos = this.getPos();
		int i = BiomeCoords.fromBlock(chunkPos.getStartX());
		int j = BiomeCoords.fromBlock(chunkPos.getStartZ());

		for (int k = 0; k < this.countVerticalSections(); k++) {
			ChunkSection chunkSection = this.getSection(k);
			chunkSection.method_38291(biomeSource, multiNoiseSampler, i, j);
		}
	}
}
