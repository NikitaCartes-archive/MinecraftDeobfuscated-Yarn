/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.LunarWorldView;
import net.minecraft.world.RegistryWorldView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.Nullable;

public interface WorldAccess
extends RegistryWorldView,
LunarWorldView {
    @Override
    default public long getLunarTime() {
        return this.getLevelProperties().getTimeOfDay();
    }

    public long getTickOrder();

    public QueryableTickScheduler<Block> getBlockTickScheduler();

    private <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay, TickPriority priority) {
        return new OrderedTick<T>(type, pos, this.getLevelProperties().getTime() + (long)delay, priority, this.getTickOrder());
    }

    private <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay) {
        return new OrderedTick<T>(type, pos, this.getLevelProperties().getTime() + (long)delay, this.getTickOrder());
    }

    default public void createAndScheduleBlockTick(BlockPos pos, Block block, int delay, TickPriority priority) {
        this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay, priority));
    }

    default public void createAndScheduleBlockTick(BlockPos pos, Block block, int delay) {
        this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay));
    }

    public QueryableTickScheduler<Fluid> getFluidTickScheduler();

    default public void createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {
        this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay, priority));
    }

    default public void createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay) {
        this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay));
    }

    public WorldProperties getLevelProperties();

    public LocalDifficulty getLocalDifficulty(BlockPos var1);

    @Nullable
    public MinecraftServer getServer();

    default public Difficulty getDifficulty() {
        return this.getLevelProperties().getDifficulty();
    }

    public ChunkManager getChunkManager();

    @Override
    default public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return this.getChunkManager().isChunkLoaded(chunkX, chunkZ);
    }

    public Random getRandom();

    default public void updateNeighbors(BlockPos pos, Block block) {
    }

    default public void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        NeighborUpdater.replaceWithStateForNeighborUpdate(this, direction, neighborState, pos, neighborPos, flags, maxUpdateDepth - 1);
    }

    public void playSound(@Nullable PlayerEntity var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void addParticle(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void syncWorldEvent(@Nullable PlayerEntity var1, int var2, BlockPos var3, int var4);

    default public void syncWorldEvent(int eventId, BlockPos pos, int data) {
        this.syncWorldEvent(null, eventId, pos, data);
    }

    /**
     * Emits a game event.
     */
    public void emitGameEvent(GameEvent var1, Vec3d var2, GameEvent.Emitter var3);

    default public void emitGameEvent(@Nullable Entity entity, GameEvent event, Vec3d pos) {
        this.emitGameEvent(event, pos, new GameEvent.Emitter(entity, null));
    }

    default public void emitGameEvent(@Nullable Entity entity, GameEvent event, BlockPos pos) {
        this.emitGameEvent(event, pos, new GameEvent.Emitter(entity, null));
    }

    default public void emitGameEvent(GameEvent event, BlockPos pos, GameEvent.Emitter emitter) {
        this.emitGameEvent(event, Vec3d.ofCenter(pos), emitter);
    }
}

