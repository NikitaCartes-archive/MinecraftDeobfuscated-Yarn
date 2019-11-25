/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class WorldChunk
implements Chunk {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ChunkSection EMPTY_SECTION = null;
    private final ChunkSection[] sections = new ChunkSection[16];
    private BiomeArray biomeArray;
    private final Map<BlockPos, CompoundTag> pendingBlockEntityTags = Maps.newHashMap();
    private boolean loadedToWorld;
    private final World world;
    private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private final UpgradeData upgradeData;
    private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    private final TypeFilterableList<Entity>[] entitySections;
    private final Map<String, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<String, LongSet> structureReferences = Maps.newHashMap();
    private final ShortList[] postProcessingLists = new ShortList[16];
    private TickScheduler<Block> blockTickScheduler;
    private TickScheduler<Fluid> fluidTickScheduler;
    private boolean unsaved;
    private long lastSaveTime;
    private volatile boolean shouldSave;
    private long inhabitedTime;
    @Nullable
    private Supplier<ChunkHolder.LevelType> levelTypeProvider;
    @Nullable
    private Consumer<WorldChunk> loadToWorldConsumer;
    private final ChunkPos pos;
    private volatile boolean isLightOn;

    public WorldChunk(World world, ChunkPos chunkPos, BiomeArray biomeArray) {
        this(world, chunkPos, biomeArray, UpgradeData.NO_UPGRADE_DATA, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null, null);
    }

    public WorldChunk(World world, ChunkPos chunkPos, BiomeArray biomeArray, UpgradeData upgradeData, TickScheduler<Block> tickScheduler, TickScheduler<Fluid> tickScheduler2, long l, @Nullable ChunkSection[] chunkSections, @Nullable Consumer<WorldChunk> consumer) {
        this.entitySections = new TypeFilterableList[16];
        this.world = world;
        this.pos = chunkPos;
        this.upgradeData = upgradeData;
        for (Heightmap.Type type : Heightmap.Type.values()) {
            if (!ChunkStatus.FULL.getHeightmapTypes().contains((Object)type)) continue;
            this.heightmaps.put(type, new Heightmap(this, type));
        }
        for (int i = 0; i < this.entitySections.length; ++i) {
            this.entitySections[i] = new TypeFilterableList<Entity>(Entity.class);
        }
        this.biomeArray = biomeArray;
        this.blockTickScheduler = tickScheduler;
        this.fluidTickScheduler = tickScheduler2;
        this.inhabitedTime = l;
        this.loadToWorldConsumer = consumer;
        if (chunkSections != null) {
            if (this.sections.length == chunkSections.length) {
                System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)chunkSections.length, (Object)this.sections.length);
            }
        }
    }

    public WorldChunk(World world, ProtoChunk protoChunk) {
        this(world, protoChunk.getPos(), protoChunk.getBiomeArray(), protoChunk.getUpgradeData(), protoChunk.getBlockTickScheduler(), protoChunk.getFluidTickScheduler(), protoChunk.getInhabitedTime(), protoChunk.getSectionArray(), null);
        for (CompoundTag compoundTag : protoChunk.getEntities()) {
            EntityType.loadEntityWithPassengers(compoundTag, world, entity -> {
                this.addEntity((Entity)entity);
                return entity;
            });
        }
        for (BlockEntity blockEntity : protoChunk.getBlockEntities().values()) {
            this.addBlockEntity(blockEntity);
        }
        this.pendingBlockEntityTags.putAll(protoChunk.getBlockEntityTags());
        for (int i = 0; i < protoChunk.getPostProcessingLists().length; ++i) {
            this.postProcessingLists[i] = protoChunk.getPostProcessingLists()[i];
        }
        this.setStructureStarts(protoChunk.getStructureStarts());
        this.setStructureReferences(protoChunk.getStructureReferences());
        for (Map.Entry<Heightmap.Type, Heightmap> entry : protoChunk.getHeightmaps()) {
            if (!ChunkStatus.FULL.getHeightmapTypes().contains((Object)entry.getKey())) continue;
            this.getHeightmap(entry.getKey()).setTo(entry.getValue().asLongArray());
        }
        this.setLightOn(protoChunk.isLightOn());
        this.shouldSave = true;
    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type type2) {
        return this.heightmaps.computeIfAbsent(type2, type -> new Heightmap(this, (Heightmap.Type)((Object)type)));
    }

    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        HashSet<BlockPos> set = Sets.newHashSet(this.pendingBlockEntityTags.keySet());
        set.addAll(this.blockEntities.keySet());
        return set;
    }

    @Override
    public ChunkSection[] getSectionArray() {
        return this.sections;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        if (this.world.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            BlockState blockState = null;
            if (j == 60) {
                blockState = Blocks.BARRIER.getDefaultState();
            }
            if (j == 70) {
                blockState = DebugChunkGenerator.getBlockState(i, k);
            }
            return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
        }
        try {
            ChunkSection chunkSection;
            if (j >= 0 && j >> 4 < this.sections.length && !ChunkSection.isEmpty(chunkSection = this.sections[j >> 4])) {
                return chunkSection.getBlockState(i & 0xF, j & 0xF, k & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString(i, j, k));
            throw new CrashException(crashReport);
        }
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return this.getFluidState(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public FluidState getFluidState(int i, int j, int k) {
        try {
            ChunkSection chunkSection;
            if (j >= 0 && j >> 4 < this.sections.length && !ChunkSection.isEmpty(chunkSection = this.sections[j >> 4])) {
                return chunkSection.getFluidState(i & 0xF, j & 0xF, k & 0xF);
            }
            return Fluids.EMPTY.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting fluid state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString(i, j, k));
            throw new CrashException(crashReport);
        }
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
        BlockEntity blockEntity;
        int i = blockPos.getX() & 0xF;
        int j = blockPos.getY();
        int k = blockPos.getZ() & 0xF;
        ChunkSection chunkSection = this.sections[j >> 4];
        if (chunkSection == EMPTY_SECTION) {
            if (blockState.isAir()) {
                return null;
            }
            this.sections[j >> 4] = chunkSection = new ChunkSection(j >> 4 << 4);
        }
        boolean bl2 = chunkSection.isEmpty();
        BlockState blockState2 = chunkSection.setBlockState(i, j & 0xF, k, blockState);
        if (blockState2 == blockState) {
            return null;
        }
        Block block = blockState.getBlock();
        Block block2 = blockState2.getBlock();
        this.heightmaps.get((Object)Heightmap.Type.MOTION_BLOCKING).trackUpdate(i, j, k, blockState);
        this.heightmaps.get((Object)Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).trackUpdate(i, j, k, blockState);
        this.heightmaps.get((Object)Heightmap.Type.OCEAN_FLOOR).trackUpdate(i, j, k, blockState);
        this.heightmaps.get((Object)Heightmap.Type.WORLD_SURFACE).trackUpdate(i, j, k, blockState);
        boolean bl3 = chunkSection.isEmpty();
        if (bl2 != bl3) {
            this.world.getChunkManager().getLightingProvider().updateSectionStatus(blockPos, bl3);
        }
        if (!this.world.isClient) {
            blockState2.onBlockRemoved(this.world, blockPos, blockState, bl);
        } else if (block2 != block && block2 instanceof BlockEntityProvider) {
            this.world.removeBlockEntity(blockPos);
        }
        if (chunkSection.getBlockState(i, j & 0xF, k).getBlock() != block) {
            return null;
        }
        if (block2 instanceof BlockEntityProvider && (blockEntity = this.getBlockEntity(blockPos, CreationType.CHECK)) != null) {
            blockEntity.resetBlock();
        }
        if (!this.world.isClient) {
            blockState.onBlockAdded(this.world, blockPos, blockState2, bl);
        }
        if (block instanceof BlockEntityProvider) {
            blockEntity = this.getBlockEntity(blockPos, CreationType.CHECK);
            if (blockEntity == null) {
                blockEntity = ((BlockEntityProvider)((Object)block)).createBlockEntity(this.world);
                this.world.setBlockEntity(blockPos, blockEntity);
            } else {
                blockEntity.resetBlock();
            }
        }
        this.shouldSave = true;
        return blockState2;
    }

    @Nullable
    public LightingProvider getLightingProvider() {
        return this.world.getChunkManager().getLightingProvider();
    }

    @Override
    public void addEntity(Entity entity) {
        int k;
        this.unsaved = true;
        int i = MathHelper.floor(entity.getX() / 16.0);
        int j = MathHelper.floor(entity.getZ() / 16.0);
        if (i != this.pos.x || j != this.pos.z) {
            LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", (Object)i, (Object)j, (Object)this.pos.x, (Object)this.pos.z, (Object)entity);
            entity.removed = true;
        }
        if ((k = MathHelper.floor(entity.getY() / 16.0)) < 0) {
            k = 0;
        }
        if (k >= this.entitySections.length) {
            k = this.entitySections.length - 1;
        }
        entity.updateNeeded = true;
        entity.chunkX = this.pos.x;
        entity.chunkY = k;
        entity.chunkZ = this.pos.z;
        this.entitySections[k].add(entity);
    }

    @Override
    public void setHeightmap(Heightmap.Type type, long[] ls) {
        this.heightmaps.get((Object)type).setTo(ls);
    }

    public void remove(Entity entity) {
        this.remove(entity, entity.chunkY);
    }

    public void remove(Entity entity, int i) {
        if (i < 0) {
            i = 0;
        }
        if (i >= this.entitySections.length) {
            i = this.entitySections.length - 1;
        }
        this.entitySections[i].remove(entity);
    }

    @Override
    public int sampleHeightmap(Heightmap.Type type, int i, int j) {
        return this.heightmaps.get((Object)type).get(i & 0xF, j & 0xF) - 1;
    }

    @Nullable
    private BlockEntity createBlockEntity(BlockPos blockPos) {
        BlockState blockState = this.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (!block.hasBlockEntity()) {
            return null;
        }
        return ((BlockEntityProvider)((Object)block)).createBlockEntity(this.world);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return this.getBlockEntity(blockPos, CreationType.CHECK);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos, CreationType creationType) {
        BlockEntity blockEntity2;
        CompoundTag compoundTag;
        BlockEntity blockEntity = this.blockEntities.get(blockPos);
        if (blockEntity == null && (compoundTag = this.pendingBlockEntityTags.remove(blockPos)) != null && (blockEntity2 = this.loadBlockEntity(blockPos, compoundTag)) != null) {
            return blockEntity2;
        }
        if (blockEntity == null) {
            if (creationType == CreationType.IMMEDIATE) {
                blockEntity = this.createBlockEntity(blockPos);
                this.world.setBlockEntity(blockPos, blockEntity);
            }
        } else if (blockEntity.isRemoved()) {
            this.blockEntities.remove(blockPos);
            return null;
        }
        return blockEntity;
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        this.setBlockEntity(blockEntity.getPos(), blockEntity);
        if (this.loadedToWorld || this.world.isClient()) {
            this.world.setBlockEntity(blockEntity.getPos(), blockEntity);
        }
    }

    @Override
    public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
        if (!(this.getBlockState(blockPos).getBlock() instanceof BlockEntityProvider)) {
            return;
        }
        blockEntity.setWorld(this.world, blockPos);
        blockEntity.cancelRemoval();
        BlockEntity blockEntity2 = this.blockEntities.put(blockPos.toImmutable(), blockEntity);
        if (blockEntity2 != null && blockEntity2 != blockEntity) {
            blockEntity2.markRemoved();
        }
    }

    @Override
    public void addPendingBlockEntityTag(CompoundTag compoundTag) {
        this.pendingBlockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
    }

    @Override
    @Nullable
    public CompoundTag method_20598(BlockPos blockPos) {
        BlockEntity blockEntity = this.getBlockEntity(blockPos);
        if (blockEntity != null && !blockEntity.isRemoved()) {
            CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
            compoundTag.putBoolean("keepPacked", false);
            return compoundTag;
        }
        CompoundTag compoundTag = this.pendingBlockEntityTags.get(blockPos);
        if (compoundTag != null) {
            compoundTag = compoundTag.copy();
            compoundTag.putBoolean("keepPacked", true);
        }
        return compoundTag;
    }

    @Override
    public void removeBlockEntity(BlockPos blockPos) {
        BlockEntity blockEntity;
        if ((this.loadedToWorld || this.world.isClient()) && (blockEntity = this.blockEntities.remove(blockPos)) != null) {
            blockEntity.markRemoved();
        }
    }

    public void loadToWorld() {
        if (this.loadToWorldConsumer != null) {
            this.loadToWorldConsumer.accept(this);
            this.loadToWorldConsumer = null;
        }
    }

    public void markDirty() {
        this.shouldSave = true;
    }

    public void getEntities(@Nullable Entity entity, Box box, List<Entity> list, @Nullable Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((box.y1 - 2.0) / 16.0);
        int j = MathHelper.floor((box.y2 + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);
        for (int k = i; k <= j; ++k) {
            if (this.entitySections[k].isEmpty()) continue;
            for (Entity entity2 : this.entitySections[k]) {
                if (!entity2.getBoundingBox().intersects(box) || entity2 == entity) continue;
                if (predicate == null || predicate.test(entity2)) {
                    list.add(entity2);
                }
                if (!(entity2 instanceof EnderDragonEntity)) continue;
                for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity2).method_5690()) {
                    if (enderDragonPart == entity || !enderDragonPart.getBoundingBox().intersects(box) || predicate != null && !predicate.test(enderDragonPart)) continue;
                    list.add(enderDragonPart);
                }
            }
        }
    }

    public <T extends Entity> void getEntities(@Nullable EntityType<?> entityType, Box box, List<? super T> list, Predicate<? super T> predicate) {
        int i = MathHelper.floor((box.y1 - 2.0) / 16.0);
        int j = MathHelper.floor((box.y2 + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);
        for (int k = i; k <= j; ++k) {
            for (Entity entity : this.entitySections[k].getAllOfType(Entity.class)) {
                if (entityType != null && entity.getType() != entityType) continue;
                Entity entity2 = entity;
                if (!entity.getBoundingBox().intersects(box) || !predicate.test(entity2)) continue;
                list.add(entity2);
            }
        }
    }

    public <T extends Entity> void getEntities(Class<? extends T> class_, Box box, List<T> list, @Nullable Predicate<? super T> predicate) {
        int i = MathHelper.floor((box.y1 - 2.0) / 16.0);
        int j = MathHelper.floor((box.y2 + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);
        for (int k = i; k <= j; ++k) {
            for (Entity entity : this.entitySections[k].getAllOfType(class_)) {
                if (!entity.getBoundingBox().intersects(box) || predicate != null && !predicate.test(entity)) continue;
                list.add(entity);
            }
        }
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public ChunkPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public void loadFromPacket(@Nullable BiomeArray biomeArray, PacketByteBuf packetByteBuf, CompoundTag compoundTag, int i) {
        boolean bl = biomeArray != null;
        Predicate<BlockPos> predicate = bl ? blockPos -> true : blockPos -> (i & 1 << (blockPos.getY() >> 4)) != 0;
        Sets.newHashSet(this.blockEntities.keySet()).stream().filter(predicate).forEach(this.world::removeBlockEntity);
        for (int j = 0; j < this.sections.length; ++j) {
            ChunkSection chunkSection = this.sections[j];
            if ((i & 1 << j) == 0) {
                if (!bl || chunkSection == EMPTY_SECTION) continue;
                this.sections[j] = EMPTY_SECTION;
                continue;
            }
            if (chunkSection == EMPTY_SECTION) {
                this.sections[j] = chunkSection = new ChunkSection(j << 4);
            }
            chunkSection.fromPacket(packetByteBuf);
        }
        if (biomeArray != null) {
            this.biomeArray = biomeArray;
        }
        for (Heightmap.Type type : Heightmap.Type.values()) {
            String string = type.getName();
            if (!compoundTag.contains(string, 12)) continue;
            this.setHeightmap(type, compoundTag.getLongArray(string));
        }
        for (BlockEntity blockEntity : this.blockEntities.values()) {
            blockEntity.resetBlock();
        }
    }

    @Override
    public BiomeArray getBiomeArray() {
        return this.biomeArray;
    }

    public void setLoadedToWorld(boolean bl) {
        this.loadedToWorld = bl;
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public TypeFilterableList<Entity>[] getEntitySectionArray() {
        return this.entitySections;
    }

    @Override
    public CompoundTag getBlockEntityTagAt(BlockPos blockPos) {
        return this.pendingBlockEntityTags.get(blockPos);
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return StreamSupport.stream(BlockPos.iterate(this.pos.getStartX(), 0, this.pos.getStartZ(), this.pos.getEndX(), 255, this.pos.getEndZ()).spliterator(), false).filter(blockPos -> this.getBlockState((BlockPos)blockPos).getLuminance() != 0);
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public void setShouldSave(boolean bl) {
        this.shouldSave = bl;
    }

    @Override
    public boolean needsSaving() {
        return this.shouldSave || this.unsaved && this.world.getTime() != this.lastSaveTime;
    }

    public void setUnsaved(boolean bl) {
        this.unsaved = bl;
    }

    @Override
    public void setLastSaveTime(long l) {
        this.lastSaveTime = l;
    }

    @Override
    @Nullable
    public StructureStart getStructureStart(String string) {
        return this.structureStarts.get(string);
    }

    @Override
    public void setStructureStart(String string, StructureStart structureStart) {
        this.structureStarts.put(string, structureStart);
    }

    @Override
    public Map<String, StructureStart> getStructureStarts() {
        return this.structureStarts;
    }

    @Override
    public void setStructureStarts(Map<String, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
    }

    @Override
    public LongSet getStructureReferences(String string2) {
        return this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet());
    }

    @Override
    public void addStructureReference(String string2, long l) {
        this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet()).add(l);
    }

    @Override
    public Map<String, LongSet> getStructureReferences() {
        return this.structureReferences;
    }

    @Override
    public void setStructureReferences(Map<String, LongSet> map) {
        this.structureReferences.clear();
        this.structureReferences.putAll(map);
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void setInhabitedTime(long l) {
        this.inhabitedTime = l;
    }

    public void runPostProcessing() {
        ChunkPos chunkPos = this.getPos();
        for (int i = 0; i < this.postProcessingLists.length; ++i) {
            if (this.postProcessingLists[i] == null) continue;
            for (Short short_ : this.postProcessingLists[i]) {
                BlockPos blockPos = ProtoChunk.joinBlockPos(short_, i, chunkPos);
                BlockState blockState = this.getBlockState(blockPos);
                BlockState blockState2 = Block.getRenderingState(blockState, this.world, blockPos);
                this.world.setBlockState(blockPos, blockState2, 20);
            }
            this.postProcessingLists[i].clear();
        }
        this.method_20530();
        for (BlockPos blockPos2 : Sets.newHashSet(this.pendingBlockEntityTags.keySet())) {
            this.getBlockEntity(blockPos2);
        }
        this.pendingBlockEntityTags.clear();
        this.upgradeData.method_12356(this);
    }

    @Nullable
    private BlockEntity loadBlockEntity(BlockPos blockPos, CompoundTag compoundTag) {
        BlockEntity blockEntity;
        if ("DUMMY".equals(compoundTag.getString("id"))) {
            Block block = this.getBlockState(blockPos).getBlock();
            if (block instanceof BlockEntityProvider) {
                blockEntity = ((BlockEntityProvider)((Object)block)).createBlockEntity(this.world);
            } else {
                blockEntity = null;
                LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", (Object)blockPos, (Object)this.getBlockState(blockPos));
            }
        } else {
            blockEntity = BlockEntity.createFromTag(compoundTag);
        }
        if (blockEntity != null) {
            blockEntity.setWorld(this.world, blockPos);
            this.addBlockEntity(blockEntity);
        } else {
            LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", (Object)this.getBlockState(blockPos), (Object)blockPos);
        }
        return blockEntity;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    @Override
    public ShortList[] getPostProcessingLists() {
        return this.postProcessingLists;
    }

    public void method_20530() {
        if (this.blockTickScheduler instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.blockTickScheduler).tick(this.world.getBlockTickScheduler(), blockPos -> this.getBlockState((BlockPos)blockPos).getBlock());
            this.blockTickScheduler = DummyClientTickScheduler.get();
        } else if (this.blockTickScheduler instanceof SimpleTickScheduler) {
            this.world.getBlockTickScheduler().scheduleAll(((SimpleTickScheduler)this.blockTickScheduler).stream());
            this.blockTickScheduler = DummyClientTickScheduler.get();
        }
        if (this.fluidTickScheduler instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.fluidTickScheduler).tick(this.world.getFluidTickScheduler(), blockPos -> this.getFluidState((BlockPos)blockPos).getFluid());
            this.fluidTickScheduler = DummyClientTickScheduler.get();
        } else if (this.fluidTickScheduler instanceof SimpleTickScheduler) {
            this.world.getFluidTickScheduler().scheduleAll(((SimpleTickScheduler)this.fluidTickScheduler).stream());
            this.fluidTickScheduler = DummyClientTickScheduler.get();
        }
    }

    public void method_20471(ServerWorld serverWorld) {
        if (this.blockTickScheduler == DummyClientTickScheduler.get()) {
            this.blockTickScheduler = new SimpleTickScheduler<Block>(Registry.BLOCK::getId, ((ServerTickScheduler)serverWorld.getBlockTickScheduler()).getScheduledTicksInChunk(this.pos, true, false));
            this.setShouldSave(true);
        }
        if (this.fluidTickScheduler == DummyClientTickScheduler.get()) {
            this.fluidTickScheduler = new SimpleTickScheduler<Fluid>(Registry.FLUID::getId, ((ServerTickScheduler)serverWorld.getFluidTickScheduler()).getScheduledTicksInChunk(this.pos, true, false));
            this.setShouldSave(true);
        }
    }

    @Override
    public ChunkStatus getStatus() {
        return ChunkStatus.FULL;
    }

    public ChunkHolder.LevelType getLevelType() {
        if (this.levelTypeProvider == null) {
            return ChunkHolder.LevelType.BORDER;
        }
        return this.levelTypeProvider.get();
    }

    public void setLevelTypeProvider(Supplier<ChunkHolder.LevelType> supplier) {
        this.levelTypeProvider = supplier;
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

    public static enum CreationType {
        IMMEDIATE,
        QUEUED,
        CHECK;

    }
}

