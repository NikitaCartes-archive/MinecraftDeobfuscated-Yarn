/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ProtoChunk
implements Chunk {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkPos pos;
    private volatile boolean shouldSave;
    @Nullable
    private BiomeArray biomes;
    @Nullable
    private volatile LightingProvider lightingProvider;
    private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private volatile ChunkStatus status = ChunkStatus.EMPTY;
    private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    private final Map<BlockPos, CompoundTag> blockEntityTags = Maps.newHashMap();
    private final ChunkSection[] sections;
    private final List<CompoundTag> entities = Lists.newArrayList();
    private final List<BlockPos> lightSources = Lists.newArrayList();
    private final ShortList[] postProcessingLists;
    private final Map<StructureFeature<?>, StructureStart<?>> structureStarts = Maps.newHashMap();
    private final Map<StructureFeature<?>, LongSet> structureReferences = Maps.newHashMap();
    private final UpgradeData upgradeData;
    private final ChunkTickScheduler<Block> blockTickScheduler;
    private final ChunkTickScheduler<Fluid> fluidTickScheduler;
    private final HeightLimitView world;
    private long inhabitedTime;
    private final Map<GenerationStep.Carver, BitSet> carvingMasks = new Object2ObjectArrayMap<GenerationStep.Carver, BitSet>();
    private volatile boolean lightOn;

    public ProtoChunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView world) {
        this(pos, upgradeData, null, new ChunkTickScheduler<Block>(block -> block == null || block.getDefaultState().isAir(), pos, world), new ChunkTickScheduler<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, pos, world), world);
    }

    public ProtoChunk(ChunkPos pos, UpgradeData upgradeData, @Nullable ChunkSection[] chunkSections, ChunkTickScheduler<Block> blockTickScheduler, ChunkTickScheduler<Fluid> fluidTickScheduler, HeightLimitView world) {
        this.pos = pos;
        this.upgradeData = upgradeData;
        this.blockTickScheduler = blockTickScheduler;
        this.fluidTickScheduler = fluidTickScheduler;
        this.world = world;
        this.sections = new ChunkSection[world.countVerticalSections()];
        if (chunkSections != null) {
            if (this.sections.length == chunkSections.length) {
                System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)chunkSections.length, (Object)this.sections.length);
            }
        }
        this.postProcessingLists = new ShortList[world.countVerticalSections()];
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getY();
        if (this.isOutOfHeightLimit(i)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        ChunkSection chunkSection = this.getSectionArray()[this.getSectionIndex(i)];
        if (ChunkSection.isEmpty(chunkSection)) {
            return Blocks.AIR.getDefaultState();
        }
        return chunkSection.getBlockState(pos.getX() & 0xF, i & 0xF, pos.getZ() & 0xF);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        int i = pos.getY();
        if (this.isOutOfHeightLimit(i)) {
            return Fluids.EMPTY.getDefaultState();
        }
        ChunkSection chunkSection = this.getSectionArray()[this.getSectionIndex(i)];
        if (ChunkSection.isEmpty(chunkSection)) {
            return Fluids.EMPTY.getDefaultState();
        }
        return chunkSection.getFluidState(pos.getX() & 0xF, i & 0xF, pos.getZ() & 0xF);
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.lightSources.stream();
    }

    public ShortList[] getLightSourcesBySection() {
        ShortList[] shortLists = new ShortList[this.countVerticalSections()];
        for (BlockPos blockPos : this.lightSources) {
            Chunk.getList(shortLists, this.getSectionIndex(blockPos.getY())).add(ProtoChunk.getPackedSectionRelative(blockPos));
        }
        return shortLists;
    }

    public void addLightSource(short chunkSliceRel, int sectionY) {
        this.addLightSource(ProtoChunk.joinBlockPos(chunkSliceRel, this.sectionIndexToCoord(sectionY), this.pos));
    }

    public void addLightSource(BlockPos pos) {
        this.lightSources.add(pos.toImmutable());
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (j < this.getBottomY() || j >= this.getTopY()) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        int l = this.getSectionIndex(j);
        if (this.sections[l] == WorldChunk.EMPTY_SECTION && state.isOf(Blocks.AIR)) {
            return state;
        }
        if (state.getLuminance() > 0) {
            this.lightSources.add(new BlockPos((i & 0xF) + this.getPos().getStartX(), j, (k & 0xF) + this.getPos().getStartZ()));
        }
        ChunkSection chunkSection = this.getSection(l);
        BlockState blockState = chunkSection.setBlockState(i & 0xF, j & 0xF, k & 0xF, state);
        if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockState && (state.getOpacity(this, pos) != blockState.getOpacity(this, pos) || state.getLuminance() != blockState.getLuminance() || state.hasSidedTransparency() || blockState.hasSidedTransparency())) {
            this.lightingProvider.checkBlock(pos);
        }
        EnumSet<Heightmap.Type> enumSet = this.getStatus().getHeightmapTypes();
        EnumSet<Heightmap.Type> enumSet2 = null;
        for (Heightmap.Type type : enumSet) {
            Heightmap heightmap = this.heightmaps.get(type);
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
            this.heightmaps.get(type).trackUpdate(i & 0xF, j, k & 0xF, state);
        }
        return blockState;
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
        this.blockEntities.put(blockEntity.getPos(), blockEntity);
    }

    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        HashSet<BlockPos> set = Sets.newHashSet(this.blockEntityTags.keySet());
        set.addAll(this.blockEntities.keySet());
        return set;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.blockEntities.get(pos);
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addEntity(CompoundTag entityTag) {
        this.entities.add(entityTag);
    }

    @Override
    public void addEntity(Entity entity) {
        if (entity.hasVehicle()) {
            return;
        }
        CompoundTag compoundTag = new CompoundTag();
        entity.saveToTag(compoundTag);
        this.addEntity(compoundTag);
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }

    public void setBiomes(BiomeArray biomes) {
        this.biomes = biomes;
    }

    @Override
    @Nullable
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
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    @Override
    public void setHeightmap(Heightmap.Type type, long[] heightmap) {
        this.getHeightmap(type).setTo(heightmap);
    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type type2) {
        return this.heightmaps.computeIfAbsent(type2, type -> new Heightmap(this, (Heightmap.Type)type));
    }

    @Override
    public int sampleHeightmap(Heightmap.Type type, int x, int z) {
        Heightmap heightmap = this.heightmaps.get(type);
        if (heightmap == null) {
            Heightmap.populateHeightmaps(this, EnumSet.of(type));
            heightmap = this.heightmaps.get(type);
        }
        return heightmap.get(x & 0xF, z & 0xF) - 1;
    }

    @Override
    public ChunkPos getPos() {
        return this.pos;
    }

    @Override
    @Nullable
    public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
        return this.structureStarts.get(structure);
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
    public LongSet getStructureReferences(StructureFeature<?> structure2) {
        return this.structureReferences.computeIfAbsent(structure2, structure -> new LongOpenHashSet());
    }

    @Override
    public void addStructureReference(StructureFeature<?> structure2, long reference) {
        this.structureReferences.computeIfAbsent(structure2, structure -> new LongOpenHashSet()).add(reference);
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
        int l = i & 0xF;
        int m = j & 0xF;
        int n = k & 0xF;
        return (short)(l | m << 4 | n << 8);
    }

    public static BlockPos joinBlockPos(short sectionRel, int sectionY, ChunkPos chunkPos) {
        int i = ChunkSectionPos.getOffsetPos(chunkPos.x, sectionRel & 0xF);
        int j = ChunkSectionPos.getOffsetPos(sectionY, sectionRel >>> 4 & 0xF);
        int k = ChunkSectionPos.getOffsetPos(chunkPos.z, sectionRel >>> 8 & 0xF);
        return new BlockPos(i, j, k);
    }

    @Override
    public void markBlockForPostProcessing(BlockPos pos) {
        if (!this.isOutOfHeightLimit(pos)) {
            Chunk.getList(this.postProcessingLists, this.getSectionIndex(pos.getY())).add(ProtoChunk.getPackedSectionRelative(pos));
        }
    }

    @Override
    public ShortList[] getPostProcessingLists() {
        return this.postProcessingLists;
    }

    @Override
    public void markBlockForPostProcessing(short packedPos, int index) {
        Chunk.getList(this.postProcessingLists, index).add(packedPos);
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
        return this.blockEntityTags.get(pos);
    }

    @Override
    @Nullable
    public CompoundTag getPackedBlockEntityTag(BlockPos pos) {
        BlockEntity blockEntity = this.getBlockEntity(pos);
        if (blockEntity != null) {
            return blockEntity.toTag(new CompoundTag());
        }
        return this.blockEntityTags.get(pos);
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
        this.blockEntities.remove(pos);
        this.blockEntityTags.remove(pos);
    }

    @Nullable
    public BitSet getCarvingMask(GenerationStep.Carver carver) {
        return this.carvingMasks.get(carver);
    }

    public BitSet getOrCreateCarvingMask(GenerationStep.Carver carver2) {
        return this.carvingMasks.computeIfAbsent(carver2, carver -> new BitSet(98304));
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
    public int getBottomY() {
        return this.world.getBottomY();
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    public /* synthetic */ TickScheduler getFluidTickScheduler() {
        return this.getFluidTickScheduler();
    }

    public /* synthetic */ TickScheduler getBlockTickScheduler() {
        return this.getBlockTickScheduler();
    }
}

