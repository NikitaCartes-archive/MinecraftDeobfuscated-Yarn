/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
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
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ProtoChunk
implements Chunk {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkPos pos;
    private volatile boolean shouldSave;
    private Biome[] biomeArray;
    @Nullable
    private volatile LightingProvider lightingProvider;
    private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private volatile ChunkStatus status = ChunkStatus.EMPTY;
    private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    private final Map<BlockPos, CompoundTag> blockEntityTags = Maps.newHashMap();
    private final ChunkSection[] sections = new ChunkSection[16];
    private final List<CompoundTag> entities = Lists.newArrayList();
    private final List<BlockPos> lightSources = Lists.newArrayList();
    private final ShortList[] postProcessingLists = new ShortList[16];
    private final Map<String, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<String, LongSet> structureReferences = Maps.newHashMap();
    private final UpgradeData upgradeData;
    private final ChunkTickScheduler<Block> blockTickScheduler;
    private final ChunkTickScheduler<Fluid> fluidTickScheduler;
    private long inhabitedTime;
    private final Map<GenerationStep.Carver, BitSet> carvingMasks = Maps.newHashMap();
    private volatile boolean isLightOn;

    public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData) {
        this(chunkPos, upgradeData, null, new ChunkTickScheduler<Block>(block -> block == null || block.getDefaultState().isAir(), chunkPos), new ChunkTickScheduler<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, chunkPos));
    }

    public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData, @Nullable ChunkSection[] chunkSections, ChunkTickScheduler<Block> chunkTickScheduler, ChunkTickScheduler<Fluid> chunkTickScheduler2) {
        this.pos = chunkPos;
        this.upgradeData = upgradeData;
        this.blockTickScheduler = chunkTickScheduler;
        this.fluidTickScheduler = chunkTickScheduler2;
        if (chunkSections != null) {
            if (this.sections.length == chunkSections.length) {
                System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)chunkSections.length, (Object)this.sections.length);
            }
        }
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        int i = blockPos.getY();
        if (World.isHeightInvalid(i)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        ChunkSection chunkSection = this.getSectionArray()[i >> 4];
        if (ChunkSection.isEmpty(chunkSection)) {
            return Blocks.AIR.getDefaultState();
        }
        return chunkSection.getBlockState(blockPos.getX() & 0xF, i & 0xF, blockPos.getZ() & 0xF);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        int i = blockPos.getY();
        if (World.isHeightInvalid(i)) {
            return Fluids.EMPTY.getDefaultState();
        }
        ChunkSection chunkSection = this.getSectionArray()[i >> 4];
        if (ChunkSection.isEmpty(chunkSection)) {
            return Fluids.EMPTY.getDefaultState();
        }
        return chunkSection.getFluidState(blockPos.getX() & 0xF, i & 0xF, blockPos.getZ() & 0xF);
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.lightSources.stream();
    }

    public ShortList[] getLightSourcesBySection() {
        ShortList[] shortLists = new ShortList[16];
        for (BlockPos blockPos : this.lightSources) {
            Chunk.getList(shortLists, blockPos.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(blockPos));
        }
        return shortLists;
    }

    public void addLightSource(short s, int i) {
        this.addLightSource(ProtoChunk.joinBlockPos(s, i, this.pos));
    }

    public void addLightSource(BlockPos blockPos) {
        this.lightSources.add(blockPos.toImmutable());
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        if (j < 0 || j >= 256) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        if (this.sections[j >> 4] == WorldChunk.EMPTY_SECTION && blockState.getBlock() == Blocks.AIR) {
            return blockState;
        }
        if (blockState.getLuminance() > 0) {
            this.lightSources.add(new BlockPos((i & 0xF) + this.getPos().getStartX(), j, (k & 0xF) + this.getPos().getStartZ()));
        }
        ChunkSection chunkSection = this.getSection(j >> 4);
        BlockState blockState2 = chunkSection.setBlockState(i & 0xF, j & 0xF, k & 0xF, blockState);
        if (this.status.isAtLeast(ChunkStatus.FEATURES) && blockState != blockState2 && (blockState.getLightSubtracted(this, blockPos) != blockState2.getLightSubtracted(this, blockPos) || blockState.getLuminance() != blockState2.getLuminance() || blockState.hasSidedTransparency() || blockState2.hasSidedTransparency())) {
            LightingProvider lightingProvider = this.getLightingProvider();
            lightingProvider.enqueueLightUpdate(blockPos);
        }
        EnumSet<Heightmap.Type> enumSet = this.getStatus().isSurfaceGenerated();
        EnumSet<Heightmap.Type> enumSet2 = null;
        for (Heightmap.Type type : enumSet) {
            Heightmap heightmap = this.heightmaps.get((Object)type);
            if (heightmap != null) continue;
            if (enumSet2 == null) {
                enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
            }
            enumSet2.add(type);
        }
        if (enumSet2 != null) {
            Heightmap.populateHeightmaps(this, enumSet2);
        }
        for (Heightmap.Type type : enumSet) {
            this.heightmaps.get((Object)type).trackUpdate(i & 0xF, j, k & 0xF, blockState);
        }
        return blockState2;
    }

    public ChunkSection getSection(int i) {
        if (this.sections[i] == WorldChunk.EMPTY_SECTION) {
            this.sections[i] = new ChunkSection(i << 4);
        }
        return this.sections[i];
    }

    @Override
    public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
        blockEntity.setPos(blockPos);
        this.blockEntities.put(blockPos, blockEntity);
    }

    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        HashSet<BlockPos> set = Sets.newHashSet(this.blockEntityTags.keySet());
        set.addAll(this.blockEntities.keySet());
        return set;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return this.blockEntities.get(blockPos);
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addEntity(CompoundTag compoundTag) {
        this.entities.add(compoundTag);
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
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    @Override
    public void setHeightmap(Heightmap.Type type, long[] ls) {
        this.getHeightmap(type).setTo(ls);
    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type type2) {
        return this.heightmaps.computeIfAbsent(type2, type -> new Heightmap(this, (Heightmap.Type)((Object)type)));
    }

    @Override
    public int sampleHeightmap(Heightmap.Type type, int i, int j) {
        Heightmap heightmap = this.heightmaps.get((Object)type);
        if (heightmap == null) {
            Heightmap.populateHeightmaps(this, EnumSet.of(type));
            heightmap = this.heightmaps.get((Object)type);
        }
        return heightmap.get(i & 0xF, j & 0xF) - 1;
    }

    @Override
    public ChunkPos getPos() {
        return this.pos;
    }

    @Override
    public void setLastSaveTime(long l) {
    }

    @Override
    @Nullable
    public StructureStart getStructureStart(String string) {
        return this.structureStarts.get(string);
    }

    @Override
    public void setStructureStart(String string, StructureStart structureStart) {
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
    public LongSet getStructureReferences(String string2) {
        return this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet());
    }

    @Override
    public void addStructureReference(String string2, long l) {
        this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet()).add(l);
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

    public static short getPackedSectionRelative(BlockPos blockPos) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        int l = i & 0xF;
        int m = j & 0xF;
        int n = k & 0xF;
        return (short)(l | m << 4 | n << 8);
    }

    public static BlockPos joinBlockPos(short s, int i, ChunkPos chunkPos) {
        int j = (s & 0xF) + (chunkPos.x << 4);
        int k = (s >>> 4 & 0xF) + (i << 4);
        int l = (s >>> 8 & 0xF) + (chunkPos.z << 4);
        return new BlockPos(j, k, l);
    }

    @Override
    public void markBlockForPostProcessing(BlockPos blockPos) {
        if (!World.isHeightInvalid(blockPos)) {
            Chunk.getList(this.postProcessingLists, blockPos.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(blockPos));
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
        return this.blockTickScheduler;
    }

    public ChunkTickScheduler<Fluid> method_12313() {
        return this.fluidTickScheduler;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
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
    public void addPendingBlockEntityTag(CompoundTag compoundTag) {
        this.blockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
    }

    public Map<BlockPos, CompoundTag> getBlockEntityTags() {
        return Collections.unmodifiableMap(this.blockEntityTags);
    }

    @Override
    public CompoundTag getBlockEntityTagAt(BlockPos blockPos) {
        return this.blockEntityTags.get(blockPos);
    }

    @Override
    @Nullable
    public CompoundTag method_20598(BlockPos blockPos) {
        BlockEntity blockEntity = this.getBlockEntity(blockPos);
        if (blockEntity != null) {
            return blockEntity.toTag(new CompoundTag());
        }
        return this.blockEntityTags.get(blockPos);
    }

    @Override
    public void removeBlockEntity(BlockPos blockPos) {
        this.blockEntities.remove(blockPos);
        this.blockEntityTags.remove(blockPos);
    }

    @Override
    public BitSet getCarvingMask(GenerationStep.Carver carver2) {
        return this.carvingMasks.computeIfAbsent(carver2, carver -> new BitSet(65536));
    }

    public void setCarvingMask(GenerationStep.Carver carver, BitSet bitSet) {
        this.carvingMasks.put(carver, bitSet);
    }

    public void setLightingProvider(LightingProvider lightingProvider) {
        this.lightingProvider = lightingProvider;
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

    public /* synthetic */ TickScheduler getFluidTickScheduler() {
        return this.method_12313();
    }

    public /* synthetic */ TickScheduler getBlockTickScheduler() {
        return this.method_12303();
    }
}

