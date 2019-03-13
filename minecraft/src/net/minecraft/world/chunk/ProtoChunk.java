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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ChunkPos pos;
	private boolean shouldSave;
	private Biome[] biomeArray;
	@Nullable
	private LightingProvider field_17105;
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
	private final UpgradeData field_12916;
	private final ChunkTickScheduler<Block> field_12911;
	private final ChunkTickScheduler<Fluid> field_12923;
	private long inhabitedTime;
	private final Map<GenerationStep.Carver, BitSet> carvingMasks = Maps.<GenerationStep.Carver, BitSet>newHashMap();
	private volatile boolean isLightOn;

	public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData) {
		this(
			chunkPos,
			upgradeData,
			null,
			new ChunkTickScheduler<>(block -> block == null || block.method_9564().isAir(), Registry.BLOCK::method_10221, Registry.BLOCK::method_10223, chunkPos),
			new ChunkTickScheduler<>(fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::method_10221, Registry.FLUID::method_10223, chunkPos)
		);
	}

	public ProtoChunk(
		ChunkPos chunkPos,
		UpgradeData upgradeData,
		@Nullable ChunkSection[] chunkSections,
		ChunkTickScheduler<Block> chunkTickScheduler,
		ChunkTickScheduler<Fluid> chunkTickScheduler2
	) {
		this.pos = chunkPos;
		this.field_12916 = upgradeData;
		this.field_12911 = chunkTickScheduler;
		this.field_12923 = chunkTickScheduler2;
		if (chunkSections != null) {
			if (this.sections.length == chunkSections.length) {
				System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
			} else {
				LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.sections.length);
			}
		}
	}

	@Override
	public BlockState method_8320(BlockPos blockPos) {
		int i = blockPos.getY();
		if (World.isHeightInvalid(i)) {
			return Blocks.field_10243.method_9564();
		} else {
			ChunkSection chunkSection = this.method_12006()[i >> 4];
			return ChunkSection.isEmpty(chunkSection)
				? Blocks.field_10124.method_9564()
				: chunkSection.getBlockState(blockPos.getX() & 15, i & 15, blockPos.getZ() & 15);
		}
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		int i = blockPos.getY();
		if (World.isHeightInvalid(i)) {
			return Fluids.EMPTY.method_15785();
		} else {
			ChunkSection chunkSection = this.method_12006()[i >> 4];
			return ChunkSection.isEmpty(chunkSection) ? Fluids.EMPTY.method_15785() : chunkSection.method_12255(blockPos.getX() & 15, i & 15, blockPos.getZ() & 15);
		}
	}

	@Override
	public int method_8317(BlockPos blockPos) {
		return this.field_17105 == null ? 0 : this.method_8320(blockPos).getLuminance();
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.lightSources.stream();
	}

	public ShortList[] getLightSourcesBySection() {
		ShortList[] shortLists = new ShortList[16];

		for (BlockPos blockPos : this.lightSources) {
			Chunk.getList(shortLists, blockPos.getY() >> 4).add(method_12300(blockPos));
		}

		return shortLists;
	}

	public void addLightSource(short s, int i) {
		this.method_12315(method_12314(s, i, this.pos));
	}

	public void method_12315(BlockPos blockPos) {
		this.lightSources.add(blockPos.toImmutable());
	}

	@Nullable
	@Override
	public BlockState method_12010(BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (j >= 0 && j < 256) {
			if (this.sections[j >> 4] == WorldChunk.field_12852 && blockState.getBlock() == Blocks.field_10124) {
				return blockState;
			} else {
				if (blockState.getLuminance() > 0) {
					this.lightSources.add(new BlockPos((i & 15) + this.getPos().getStartX(), j, (k & 15) + this.getPos().getStartZ()));
				}

				ChunkSection chunkSection = this.getSection(j >> 4);
				BlockState blockState2 = chunkSection.setBlockState(i & 15, j & 15, k & 15, blockState);
				if (!this.method_12009().isSurfaceGenerated()) {
					Heightmap heightmap = (Heightmap)this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR_WG);
					Heightmap heightmap2 = (Heightmap)this.heightmaps.get(Heightmap.Type.WORLD_SURFACE_WG);
					if (heightmap == null || heightmap2 == null) {
						EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);
						if (heightmap == null) {
							enumSet.add(Heightmap.Type.OCEAN_FLOOR_WG);
						}

						if (heightmap2 == null) {
							enumSet.add(Heightmap.Type.WORLD_SURFACE_WG);
						}

						Heightmap.populateHeightmaps(this, enumSet);
						heightmap = (Heightmap)this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR_WG);
						heightmap2 = (Heightmap)this.heightmaps.get(Heightmap.Type.WORLD_SURFACE_WG);
					}

					heightmap.trackUpdate(i & 15, j, k & 15, blockState);
					heightmap2.trackUpdate(i & 15, j, k & 15, blockState);
				} else {
					Heightmap heightmap = (Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING);
					Heightmap heightmap2 = (Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
					Heightmap heightmap3 = (Heightmap)this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR);
					Heightmap heightmap4 = (Heightmap)this.heightmaps.get(Heightmap.Type.WORLD_SURFACE);
					if (heightmap == null || heightmap2 == null || heightmap3 == null || heightmap4 == null) {
						EnumSet<Heightmap.Type> enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
						if (heightmap == null) {
							enumSet2.add(Heightmap.Type.MOTION_BLOCKING);
						}

						if (heightmap2 == null) {
							enumSet2.add(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
						}

						if (heightmap3 == null) {
							enumSet2.add(Heightmap.Type.OCEAN_FLOOR);
						}

						if (heightmap4 == null) {
							enumSet2.add(Heightmap.Type.WORLD_SURFACE);
						}

						Heightmap.populateHeightmaps(this, enumSet2);
						heightmap = (Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING);
						heightmap2 = (Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
						heightmap3 = (Heightmap)this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR);
						heightmap4 = (Heightmap)this.heightmaps.get(Heightmap.Type.WORLD_SURFACE);
					}

					heightmap.trackUpdate(i & 15, j, k & 15, blockState);
					heightmap2.trackUpdate(i & 15, j, k & 15, blockState);
					heightmap3.trackUpdate(i & 15, j, k & 15, blockState);
					heightmap4.trackUpdate(i & 15, j, k & 15, blockState);
				}

				return blockState2;
			}
		} else {
			return Blocks.field_10243.method_9564();
		}
	}

	public ChunkSection getSection(int i) {
		if (this.sections[i] == WorldChunk.field_12852) {
			this.sections[i] = new ChunkSection(i << 4);
		}

		return this.sections[i];
	}

	@Override
	public void method_12007(BlockPos blockPos, BlockEntity blockEntity) {
		blockEntity.method_10998(blockPos);
		this.blockEntities.put(blockPos, blockEntity);
	}

	@Override
	public Set<BlockPos> getBlockEntityPositions() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet(this.blockEntityTags.keySet());
		set.addAll(this.blockEntities.keySet());
		return set;
	}

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		return (BlockEntity)this.blockEntities.get(blockPos);
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public void method_12302(CompoundTag compoundTag) {
		this.entities.add(compoundTag);
	}

	@Override
	public void addEntity(Entity entity) {
		CompoundTag compoundTag = new CompoundTag();
		entity.method_5662(compoundTag);
		this.method_12302(compoundTag);
	}

	public List<CompoundTag> getEntities() {
		return this.entities;
	}

	@Override
	public void setBiomeArray(Biome[] biomes) {
		this.biomeArray = biomes;
	}

	@Override
	public Biome[] getBiomeArray() {
		return this.biomeArray;
	}

	@Override
	public void setShouldSave(boolean bl) {
		this.shouldSave = bl;
	}

	@Override
	public boolean needsSaving() {
		return this.shouldSave;
	}

	@Override
	public ChunkStatus method_12009() {
		return this.status;
	}

	public void setStatus(ChunkStatus chunkStatus) {
		this.status = chunkStatus;
		this.setShouldSave(true);
	}

	@Override
	public ChunkSection[] method_12006() {
		return this.sections;
	}

	@Nullable
	@Override
	public LightingProvider method_12023() {
		return this.field_17105;
	}

	@Override
	public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
		return Collections.unmodifiableSet(this.heightmaps.entrySet());
	}

	@Override
	public void method_12037(Heightmap.Type type, long[] ls) {
		this.method_12032(type).setTo(ls);
	}

	@Override
	public Heightmap method_12032(Heightmap.Type type) {
		return (Heightmap)this.heightmaps.computeIfAbsent(type, typex -> new Heightmap(this, typex));
	}

	@Override
	public int method_12005(Heightmap.Type type, int i, int j) {
		Heightmap heightmap = (Heightmap)this.heightmaps.get(type);
		if (heightmap == null) {
			Heightmap.populateHeightmaps(this, EnumSet.of(type));
			heightmap = (Heightmap)this.heightmaps.get(type);
		}

		return heightmap.get(i & 15, j & 15) - 1;
	}

	@Override
	public ChunkPos getPos() {
		return this.pos;
	}

	@Override
	public void setLastSaveTime(long l) {
	}

	@Nullable
	@Override
	public StructureStart method_12181(String string) {
		return (StructureStart)this.structureStarts.get(string);
	}

	@Override
	public void method_12184(String string, StructureStart structureStart) {
		this.structureStarts.put(string, structureStart);
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
	public LongSet getStructureReferences(String string) {
		return (LongSet)this.structureReferences.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void addStructureReference(String string, long l) {
		((LongSet)this.structureReferences.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
		this.shouldSave = true;
	}

	@Override
	public Map<String, LongSet> getStructureReferences() {
		return Collections.unmodifiableMap(this.structureReferences);
	}

	@Override
	public void setStructureReferences(Map<String, LongSet> map) {
		this.structureReferences.clear();
		this.structureReferences.putAll(map);
		this.shouldSave = true;
	}

	public static short method_12300(BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		int l = i & 15;
		int m = j & 15;
		int n = k & 15;
		return (short)(l | m << 4 | n << 8);
	}

	public static BlockPos method_12314(short s, int i, ChunkPos chunkPos) {
		int j = (s & 15) + (chunkPos.x << 4);
		int k = (s >>> 4 & 15) + (i << 4);
		int l = (s >>> 8 & 15) + (chunkPos.z << 4);
		return new BlockPos(j, k, l);
	}

	@Override
	public void method_12039(BlockPos blockPos) {
		if (!World.method_8518(blockPos)) {
			Chunk.getList(this.postProcessingLists, blockPos.getY() >> 4).add(method_12300(blockPos));
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

	public ChunkTickScheduler<Block> method_12303() {
		return this.field_12911;
	}

	public ChunkTickScheduler<Fluid> method_12313() {
		return this.field_12923;
	}

	@Override
	public UpgradeData method_12003() {
		return this.field_12916;
	}

	@Override
	public void setInhabitedTime(long l) {
		this.inhabitedTime = l;
	}

	@Override
	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	@Override
	public void method_12042(CompoundTag compoundTag) {
		this.blockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
	}

	public Map<BlockPos, CompoundTag> getBlockEntityTags() {
		return Collections.unmodifiableMap(this.blockEntityTags);
	}

	@Override
	public CompoundTag method_12024(BlockPos blockPos) {
		return (CompoundTag)this.blockEntityTags.get(blockPos);
	}

	@Override
	public void method_12041(BlockPos blockPos) {
		this.blockEntities.remove(blockPos);
		this.blockEntityTags.remove(blockPos);
	}

	@Override
	public BitSet method_12025(GenerationStep.Carver carver) {
		return (BitSet)this.carvingMasks.computeIfAbsent(carver, carverx -> new BitSet(65536));
	}

	public void method_12307(GenerationStep.Carver carver, BitSet bitSet) {
		this.carvingMasks.put(carver, bitSet);
	}

	@Override
	public void method_17032(LightingProvider lightingProvider) {
		this.field_17105 = lightingProvider;
	}

	@Override
	public boolean isLightOn() {
		return this.isLightOn;
	}

	@Override
	public void setLightOn(boolean bl) {
		this.isLightOn = bl;
		this.setShouldSave(true);
	}
}
