/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.class_6603;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class WorldChunk
extends Chunk {
    static final Logger LOGGER = LogManager.getLogger();
    private static final BlockEntityTickInvoker EMPTY_BLOCK_ENTITY_TICKER = new BlockEntityTickInvoker(){

        @Override
        public void tick() {
        }

        @Override
        public boolean isRemoved() {
            return true;
        }

        @Override
        public BlockPos getPos() {
            return BlockPos.ORIGIN;
        }

        @Override
        public String getName() {
            return "<null>";
        }
    };
    private final Map<BlockPos, WrappedBlockEntityTickInvoker> blockEntityTickers = Maps.newHashMap();
    private boolean loadedToWorld;
    final World world;
    @Nullable
    private Supplier<ChunkHolder.LevelType> levelTypeProvider;
    @Nullable
    private Consumer<WorldChunk> loadToWorldConsumer;
    private final Int2ObjectMap<GameEventDispatcher> gameEventDispatchers;

    public WorldChunk(World world, ChunkPos pos) {
        this(world, pos, UpgradeData.NO_UPGRADE_DATA, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null, null);
    }

    public WorldChunk(World world, ChunkPos pos, UpgradeData upgradeData, TickScheduler<Block> tickScheduler, TickScheduler<Fluid> blockTickScheduler, long l, @Nullable ChunkSection[] chunkSections, @Nullable Consumer<WorldChunk> consumer) {
        super(pos, upgradeData, world, world.getRegistryManager().get(Registry.BIOME_KEY), l, chunkSections, tickScheduler, blockTickScheduler);
        this.world = world;
        this.gameEventDispatchers = new Int2ObjectOpenHashMap<GameEventDispatcher>();
        for (Heightmap.Type type : Heightmap.Type.values()) {
            if (!ChunkStatus.FULL.getHeightmapTypes().contains(type)) continue;
            this.field_34541.put(type, new Heightmap(this, type));
        }
        this.loadToWorldConsumer = consumer;
    }

    public WorldChunk(ServerWorld serverWorld, ProtoChunk protoChunk, @Nullable Consumer<WorldChunk> consumer) {
        this(serverWorld, protoChunk.getPos(), protoChunk.getUpgradeData(), protoChunk.getBlockTickScheduler(), protoChunk.getFluidTickScheduler(), protoChunk.getInhabitedTime(), protoChunk.getSectionArray(), consumer);
        for (BlockEntity blockEntity : protoChunk.getBlockEntities().values()) {
            this.setBlockEntity(blockEntity);
        }
        this.field_34542.putAll(protoChunk.getBlockEntityNbts());
        for (int i = 0; i < protoChunk.getPostProcessingLists().length; ++i) {
            this.field_34536[i] = protoChunk.getPostProcessingLists()[i];
        }
        this.setStructureStarts(protoChunk.getStructureStarts());
        this.setStructureReferences(protoChunk.getStructureReferences());
        for (Map.Entry<Heightmap.Type, Heightmap> entry : protoChunk.getHeightmaps()) {
            if (!ChunkStatus.FULL.getHeightmapTypes().contains(entry.getKey())) continue;
            this.setHeightmap(entry.getKey(), entry.getValue().asLongArray());
        }
        this.setLightOn(protoChunk.isLightOn());
        this.field_34537 = true;
    }

    @Override
    public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
        return this.gameEventDispatchers.computeIfAbsent(ySectionCoord, sectionCoord -> new SimpleGameEventDispatcher(this.world));
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (this.world.isDebugWorld()) {
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
            int l = this.getSectionIndex(j);
            if (l >= 0 && l < this.field_34545.length && !(chunkSection = this.field_34545[l]).isEmpty()) {
                return chunkSection.getBlockState(i & 0xF, j & 0xF, k & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString((HeightLimitView)this, i, j, k));
            throw new CrashException(crashReport);
        }
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }

    public FluidState getFluidState(int x, int y, int z) {
        try {
            ChunkSection chunkSection;
            int i = this.getSectionIndex(y);
            if (i >= 0 && i < this.field_34545.length && !(chunkSection = this.field_34545[i]).isEmpty()) {
                return chunkSection.getFluidState(x & 0xF, y & 0xF, z & 0xF);
            }
            return Fluids.EMPTY.getDefaultState();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Getting fluid state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString((HeightLimitView)this, x, y, z));
            throw new CrashException(crashReport);
        }
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        int l;
        int k;
        int i = pos.getY();
        ChunkSection chunkSection = this.getSection(this.getSectionIndex(i));
        boolean bl = chunkSection.isEmpty();
        if (bl && state.isAir()) {
            return null;
        }
        int j = pos.getX() & 0xF;
        BlockState blockState = chunkSection.setBlockState(j, k = i & 0xF, l = pos.getZ() & 0xF, state);
        if (blockState == state) {
            return null;
        }
        Block block = state.getBlock();
        ((Heightmap)this.field_34541.get(Heightmap.Type.MOTION_BLOCKING)).trackUpdate(j, i, l, state);
        ((Heightmap)this.field_34541.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).trackUpdate(j, i, l, state);
        ((Heightmap)this.field_34541.get(Heightmap.Type.OCEAN_FLOOR)).trackUpdate(j, i, l, state);
        ((Heightmap)this.field_34541.get(Heightmap.Type.WORLD_SURFACE)).trackUpdate(j, i, l, state);
        boolean bl2 = chunkSection.isEmpty();
        if (bl != bl2) {
            this.world.getChunkManager().getLightingProvider().setSectionStatus(pos, bl2);
        }
        boolean bl3 = blockState.hasBlockEntity();
        if (!this.world.isClient) {
            blockState.onStateReplaced(this.world, pos, state, moved);
        } else if (!blockState.isOf(block) && bl3) {
            this.removeBlockEntity(pos);
        }
        if (!chunkSection.getBlockState(j, k, l).isOf(block)) {
            return null;
        }
        if (!this.world.isClient) {
            state.onBlockAdded(this.world, pos, blockState, moved);
        }
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = this.getBlockEntity(pos, CreationType.CHECK);
            if (blockEntity == null) {
                blockEntity = ((BlockEntityProvider)((Object)block)).createBlockEntity(pos, state);
                if (blockEntity != null) {
                    this.addBlockEntity(blockEntity);
                }
            } else {
                blockEntity.setCachedState(state);
                this.updateTicker(blockEntity);
            }
        }
        this.field_34537 = true;
        return blockState;
    }

    @Override
    @Deprecated
    public void addEntity(Entity entity) {
    }

    @Nullable
    private BlockEntity createBlockEntity(BlockPos pos) {
        BlockState blockState = this.getBlockState(pos);
        if (!blockState.hasBlockEntity()) {
            return null;
        }
        return ((BlockEntityProvider)((Object)blockState.getBlock())).createBlockEntity(pos, blockState);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.getBlockEntity(pos, CreationType.CHECK);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos, CreationType creationType) {
        BlockEntity blockEntity2;
        NbtCompound nbtCompound;
        BlockEntity blockEntity = (BlockEntity)this.field_34543.get(pos);
        if (blockEntity == null && (nbtCompound = (NbtCompound)this.field_34542.remove(pos)) != null && (blockEntity2 = this.loadBlockEntity(pos, nbtCompound)) != null) {
            return blockEntity2;
        }
        if (blockEntity == null) {
            if (creationType == CreationType.IMMEDIATE && (blockEntity = this.createBlockEntity(pos)) != null) {
                this.addBlockEntity(blockEntity);
            }
        } else if (blockEntity.isRemoved()) {
            this.field_34543.remove(pos);
            return null;
        }
        return blockEntity;
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        this.setBlockEntity(blockEntity);
        if (this.canTickBlockEntities()) {
            this.updateGameEventListener(blockEntity);
            this.updateTicker(blockEntity);
        }
    }

    private boolean canTickBlockEntities() {
        return this.loadedToWorld || this.world.isClient();
    }

    boolean canTickBlockEntity(BlockPos pos) {
        if (!this.world.getWorldBorder().contains(pos)) {
            return false;
        }
        if (this.world instanceof ServerWorld) {
            return this.getLevelType().isAfter(ChunkHolder.LevelType.TICKING) && ((ServerWorld)this.world).method_37116(ChunkPos.toLong(pos));
        }
        return true;
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
        BlockPos blockPos = blockEntity.getPos();
        if (!this.getBlockState(blockPos).hasBlockEntity()) {
            return;
        }
        blockEntity.setWorld(this.world);
        blockEntity.cancelRemoval();
        BlockEntity blockEntity2 = this.field_34543.put(blockPos.toImmutable(), blockEntity);
        if (blockEntity2 != null && blockEntity2 != blockEntity) {
            blockEntity2.markRemoved();
        }
    }

    @Override
    @Nullable
    public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
        BlockEntity blockEntity = this.getBlockEntity(pos);
        if (blockEntity != null && !blockEntity.isRemoved()) {
            NbtCompound nbtCompound = blockEntity.createNbtWithIdentifyingData();
            nbtCompound.putBoolean("keepPacked", false);
            return nbtCompound;
        }
        NbtCompound nbtCompound = (NbtCompound)this.field_34542.get(pos);
        if (nbtCompound != null) {
            nbtCompound = nbtCompound.copy();
            nbtCompound.putBoolean("keepPacked", true);
        }
        return nbtCompound;
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
        BlockEntity blockEntity;
        if (this.canTickBlockEntities() && (blockEntity = (BlockEntity)this.field_34543.remove(pos)) != null) {
            this.removeGameEventListener(blockEntity);
            blockEntity.markRemoved();
        }
        this.removeBlockEntityTicker(pos);
    }

    private <T extends BlockEntity> void removeGameEventListener(T blockEntity) {
        GameEventListener gameEventListener;
        if (this.world.isClient) {
            return;
        }
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof BlockEntityProvider && (gameEventListener = ((BlockEntityProvider)((Object)block)).getGameEventListener(this.world, blockEntity)) != null) {
            int i = ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY());
            GameEventDispatcher gameEventDispatcher = this.getGameEventDispatcher(i);
            gameEventDispatcher.removeListener(gameEventListener);
            if (gameEventDispatcher.isEmpty()) {
                this.gameEventDispatchers.remove(i);
            }
        }
    }

    private void removeBlockEntityTicker(BlockPos pos) {
        WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker = this.blockEntityTickers.remove(pos);
        if (wrappedBlockEntityTickInvoker != null) {
            wrappedBlockEntityTickInvoker.setWrapped(EMPTY_BLOCK_ENTITY_TICKER);
        }
    }

    public void loadToWorld() {
        if (this.loadToWorldConsumer != null) {
            this.loadToWorldConsumer.accept(this);
            this.loadToWorldConsumer = null;
        }
    }

    public boolean isEmpty() {
        return false;
    }

    public void loadFromPacket(PacketByteBuf packetByteBuf, NbtCompound nbtCompound2, Consumer<class_6603.class_6605> consumer) {
        this.method_38289();
        for (ChunkSection chunkSection : this.field_34545) {
            chunkSection.fromPacket(packetByteBuf);
        }
        for (Heightmap.Type type : Heightmap.Type.values()) {
            String string = type.getName();
            if (!nbtCompound2.contains(string, 12)) continue;
            this.setHeightmap(type, nbtCompound2.getLongArray(string));
        }
        consumer.accept((blockPos, blockEntityType, nbtCompound) -> {
            BlockEntity blockEntity = this.getBlockEntity(blockPos, CreationType.IMMEDIATE);
            if (blockEntity != null && nbtCompound != null && blockEntity.getType() == blockEntityType) {
                blockEntity.readNbt(nbtCompound);
            }
        });
    }

    public void setLoadedToWorld(boolean loaded) {
        this.loadedToWorld = loaded;
    }

    public World getWorld() {
        return this.world;
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.field_34543;
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return StreamSupport.stream(BlockPos.iterate(this.field_34538.getStartX(), this.getBottomY(), this.field_34538.getStartZ(), this.field_34538.getEndX(), this.getTopY() - 1, this.field_34538.getEndZ()).spliterator(), false).filter(blockPos -> this.getBlockState((BlockPos)blockPos).getLuminance() != 0);
    }

    public void runPostProcessing() {
        ChunkPos chunkPos = this.getPos();
        for (int i = 0; i < this.field_34536.length; ++i) {
            if (this.field_34536[i] == null) continue;
            for (Short short_ : this.field_34536[i]) {
                BlockPos blockPos = ProtoChunk.joinBlockPos(short_, this.sectionIndexToCoord(i), chunkPos);
                BlockState blockState = this.getBlockState(blockPos);
                BlockState blockState2 = Block.postProcessState(blockState, this.world, blockPos);
                this.world.setBlockState(blockPos, blockState2, Block.NO_REDRAW | Block.FORCE_STATE);
            }
            this.field_34536[i].clear();
        }
        this.disableTickSchedulers();
        for (BlockPos blockPos2 : ImmutableList.copyOf(this.field_34542.keySet())) {
            this.getBlockEntity(blockPos2);
        }
        this.field_34542.clear();
        this.field_34540.upgrade(this);
    }

    @Nullable
    private BlockEntity loadBlockEntity(BlockPos pos, NbtCompound nbt) {
        BlockEntity blockEntity;
        BlockState blockState = this.getBlockState(pos);
        if ("DUMMY".equals(nbt.getString("id"))) {
            if (blockState.hasBlockEntity()) {
                blockEntity = ((BlockEntityProvider)((Object)blockState.getBlock())).createBlockEntity(pos, blockState);
            } else {
                blockEntity = null;
                LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", (Object)pos, (Object)blockState);
            }
        } else {
            blockEntity = BlockEntity.createFromNbt(pos, blockState, nbt);
        }
        if (blockEntity != null) {
            blockEntity.setWorld(this.world);
            this.addBlockEntity(blockEntity);
        } else {
            LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", (Object)blockState, (Object)pos);
        }
        return blockEntity;
    }

    public void disableTickSchedulers() {
        if (this.field_34546 instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.field_34546).tick(this.world.getBlockTickScheduler(), pos -> this.getBlockState((BlockPos)pos).getBlock());
            this.field_34546 = DummyClientTickScheduler.get();
        } else if (this.field_34546 instanceof SimpleTickScheduler) {
            ((SimpleTickScheduler)this.field_34546).scheduleTo(this.world.getBlockTickScheduler());
            this.field_34546 = DummyClientTickScheduler.get();
        }
        if (this.field_34547 instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.field_34547).tick(this.world.getFluidTickScheduler(), pos -> this.getFluidState((BlockPos)pos).getFluid());
            this.field_34547 = DummyClientTickScheduler.get();
        } else if (this.field_34547 instanceof SimpleTickScheduler) {
            ((SimpleTickScheduler)this.field_34547).scheduleTo(this.world.getFluidTickScheduler());
            this.field_34547 = DummyClientTickScheduler.get();
        }
    }

    public void enableTickSchedulers(ServerWorld world) {
        if (this.field_34546 == DummyClientTickScheduler.get()) {
            this.field_34546 = new SimpleTickScheduler<Block>(Registry.BLOCK::getId, ((ServerTickScheduler)world.getBlockTickScheduler()).getScheduledTicksInChunk(this.field_34538, true, false), world.getTime());
            this.setShouldSave(true);
        }
        if (this.field_34547 == DummyClientTickScheduler.get()) {
            this.field_34547 = new SimpleTickScheduler<Fluid>(Registry.FLUID::getId, ((ServerTickScheduler)world.getFluidTickScheduler()).getScheduledTicksInChunk(this.field_34538, true, false), world.getTime());
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

    public void setLevelTypeProvider(Supplier<ChunkHolder.LevelType> levelTypeProvider) {
        this.levelTypeProvider = levelTypeProvider;
    }

    public void method_38289() {
        this.field_34543.values().forEach(BlockEntity::markRemoved);
        this.field_34543.clear();
        this.blockEntityTickers.values().forEach(wrappedBlockEntityTickInvoker -> wrappedBlockEntityTickInvoker.setWrapped(EMPTY_BLOCK_ENTITY_TICKER));
        this.blockEntityTickers.clear();
    }

    public void updateAllBlockEntities() {
        this.field_34543.values().forEach(blockEntity -> {
            this.updateGameEventListener(blockEntity);
            this.updateTicker(blockEntity);
        });
    }

    private <T extends BlockEntity> void updateGameEventListener(T blockEntity) {
        GameEventListener gameEventListener;
        if (this.world.isClient) {
            return;
        }
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof BlockEntityProvider && (gameEventListener = ((BlockEntityProvider)((Object)block)).getGameEventListener(this.world, blockEntity)) != null) {
            GameEventDispatcher gameEventDispatcher = this.getGameEventDispatcher(ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY()));
            gameEventDispatcher.addListener(gameEventListener);
        }
    }

    private <T extends BlockEntity> void updateTicker(T blockEntity) {
        BlockState blockState = blockEntity.getCachedState();
        BlockEntityTicker<?> blockEntityTicker = blockState.getBlockEntityTicker(this.world, blockEntity.getType());
        if (blockEntityTicker == null) {
            this.removeBlockEntityTicker(blockEntity.getPos());
        } else {
            this.blockEntityTickers.compute(blockEntity.getPos(), (pos, wrappedBlockEntityTickInvoker) -> {
                BlockEntityTickInvoker blockEntityTickInvoker = this.wrapTicker(blockEntity, blockEntityTicker);
                if (wrappedBlockEntityTickInvoker != null) {
                    wrappedBlockEntityTickInvoker.setWrapped(blockEntityTickInvoker);
                    return wrappedBlockEntityTickInvoker;
                }
                if (this.canTickBlockEntities()) {
                    WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker2 = new WrappedBlockEntityTickInvoker(blockEntityTickInvoker);
                    this.world.addBlockEntityTicker(wrappedBlockEntityTickInvoker2);
                    return wrappedBlockEntityTickInvoker2;
                }
                return null;
            });
        }
    }

    private <T extends BlockEntity> BlockEntityTickInvoker wrapTicker(T blockEntity, BlockEntityTicker<T> blockEntityTicker) {
        return new DirectBlockEntityTickInvoker(this, blockEntity, blockEntityTicker);
    }

    public static enum CreationType {
        IMMEDIATE,
        QUEUED,
        CHECK;

    }

    class WrappedBlockEntityTickInvoker
    implements BlockEntityTickInvoker {
        private BlockEntityTickInvoker wrapped;

        WrappedBlockEntityTickInvoker(BlockEntityTickInvoker wrapped) {
            this.wrapped = wrapped;
        }

        void setWrapped(BlockEntityTickInvoker wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void tick() {
            this.wrapped.tick();
        }

        @Override
        public boolean isRemoved() {
            return this.wrapped.isRemoved();
        }

        @Override
        public BlockPos getPos() {
            return this.wrapped.getPos();
        }

        @Override
        public String getName() {
            return this.wrapped.getName();
        }

        public String toString() {
            return this.wrapped.toString() + " <wrapped>";
        }
    }

    static class DirectBlockEntityTickInvoker<T extends BlockEntity>
    implements BlockEntityTickInvoker {
        private final T blockEntity;
        private final BlockEntityTicker<T> ticker;
        private boolean hasWarned;
        final /* synthetic */ WorldChunk worldChunk;

        DirectBlockEntityTickInvoker(T blockEntity, BlockEntityTicker<T> ticker) {
            this.worldChunk = worldChunk;
            this.blockEntity = blockEntity;
            this.ticker = ticker;
        }

        @Override
        public void tick() {
            BlockPos blockPos;
            if (!((BlockEntity)this.blockEntity).isRemoved() && ((BlockEntity)this.blockEntity).hasWorld() && this.worldChunk.canTickBlockEntity(blockPos = ((BlockEntity)this.blockEntity).getPos())) {
                try {
                    Profiler profiler = this.worldChunk.world.getProfiler();
                    profiler.push(this::getName);
                    BlockState blockState = this.worldChunk.getBlockState(blockPos);
                    if (((BlockEntity)this.blockEntity).getType().supports(blockState)) {
                        this.ticker.tick(this.worldChunk.world, ((BlockEntity)this.blockEntity).getPos(), blockState, this.blockEntity);
                        this.hasWarned = false;
                    } else if (!this.hasWarned) {
                        this.hasWarned = true;
                        LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", this::getName, this::getPos, () -> blockState);
                    }
                    profiler.pop();
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Ticking block entity");
                    CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
                    ((BlockEntity)this.blockEntity).populateCrashReport(crashReportSection);
                    throw new CrashException(crashReport);
                }
            }
        }

        @Override
        public boolean isRemoved() {
            return ((BlockEntity)this.blockEntity).isRemoved();
        }

        @Override
        public BlockPos getPos() {
            return ((BlockEntity)this.blockEntity).getPos();
        }

        @Override
        public String getName() {
            return BlockEntityType.getId(((BlockEntity)this.blockEntity).getType()).toString();
        }

        public String toString() {
            return "Level ticker for " + this.getName() + "@" + this.getPos();
        }
    }
}

