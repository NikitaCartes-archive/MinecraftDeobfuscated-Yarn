/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a modifiable world where block states can be changed and entities spawned.
 */
public interface ModifiableWorld {
    /**
     * Updates the block state at a position, calling appropriate callbacks.
     * 
     * <p>When called on the server, the new block state is stored and propagated to clients and listeners as dictated
     * by the supplied flags. Note that calling this on the client will update the world locally, but may not see the
     * change persisted across loads. It's recommended to check whether this world is client before
     * interacting with the world in this way.
     * 
     * <p>See {@link #setBlockState(BlockPos, BlockState, int)} for a list of accepted flags.
     * 
     * @param maxUpdateDepth the limit for the cascading block updates
     * @param flags the bitwise flag combination, as described above
     * @param state the block state to set
     * @param pos the target position
     */
    public boolean setBlockState(BlockPos var1, BlockState var2, int var3, int var4);

    /**
     * Updates the block state at a position, calling appropriate callbacks.
     * 
     * <p>When called on the server, the new block state is stored and propagated to clients and listeners as dictated
     * by the supplied flags. Note that calling this on the client will update the world locally, but may not see the
     * change persisted across loads. It's recommended to check whether this world is client before
     * interacting with the world in this way.
     * 
     * <p>The accepted values of these flags are:
     * <ul>
     * <li>{@link net.minecraft.block.Block#NOTIFY_ALL Block.NOTIFY_ALL}</li>
     * <li>{@link net.minecraft.block.Block#NOTIFY_NEIGHBORS Block.NOTIFY_NEIGHBORS}</li>
     * <li>{@link net.minecraft.block.Block#NOTIFY_LISTENERS Block.NOTIFY_LISTENERS}</li>
     * <li>{@link net.minecraft.block.Block#NO_REDRAW Block.NO_REDRAW}</li>
     * <li>{@link net.minecraft.block.Block#REDRAW_ON_MAIN_THREAD Block.REDRAW_ON_MAIN_THREAD}</li>
     * <li>{@link net.minecraft.block.Block#FORCE_STATE Block.FORCE_STATE}</li>
     * <li>{@link net.minecraft.block.Block#SKIP_DROPS Block.SKIP_DROPS}</li>
     * <li>{@link net.minecraft.block.Block#MOVED Block.MOVED}</li>
     * <li>{@link net.minecraft.block.Block#SKIP_LIGHTING_UPDATES Block.SKIP_LIGHTING_UPDATES}</li>
     * </ul>
     * 
     * @see #setBlockState(BlockPos, BlockState, int, int)
     * 
     * @param pos the target position
     * @param flags the bitwise flag combination, as described above
     * @param state the block state to set
     */
    default public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        return this.setBlockState(pos, state, flags, 512);
    }

    /**
     * Removes the block and replaces it with the fluid occupying the block
     * (such as water inside kelp), or air if it does not exist.
     * 
     * @implNote This does not emit the {@linkplain WorldEvents#BLOCK_BROKEN world event}
     * or the {@linkplain net.minecraft.world.event.GameEvent#BLOCK_DESTROY game event}.
     * 
     * @return whether the block was removed successfully
     * 
     * @see #breakBlock(BlockPos, boolean)
     * 
     * @param move whether the block was removed as part of piston moving blocks
     */
    public boolean removeBlock(BlockPos var1, boolean var2);

    /**
     * Removes the block and replaces it with the fluid occupying the block
     * (such as water inside kelp), or air if it does not exist. Additionally,
     * this emits the {@linkplain WorldEvents#BLOCK_BROKEN world event}
     * and the {@linkplain net.minecraft.world.event.GameEvent#BLOCK_DESTROY game event}.
     * 
     * @return whether the block was removed successfully
     * 
     * @see #removeBlock(BlockPos, boolean)
     * @see #breakBlock(BlockPos, boolean, Entity)
     */
    default public boolean breakBlock(BlockPos pos, boolean drop) {
        return this.breakBlock(pos, drop, null);
    }

    /**
     * Removes the block and replaces it with the fluid occupying the block
     * (such as water inside kelp), or air if it does not exist. Additionally,
     * this emits the {@linkplain WorldEvents#BLOCK_BROKEN world event}
     * and the {@linkplain net.minecraft.world.event.GameEvent#BLOCK_DESTROY game event}.
     * 
     * @return whether the block was removed successfully
     * 
     * @see #breakBlock(BlockPos, boolean)
     * @see #breakBlock(BlockPos, boolean, Entity, int)
     */
    default public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity) {
        return this.breakBlock(pos, drop, breakingEntity, 512);
    }

    /**
     * Removes the block and replaces it with the fluid occupying the block
     * (such as water inside kelp), or air if it does not exist. Additionally,
     * this emits the {@linkplain WorldEvents#BLOCK_BROKEN world event}
     * and the {@linkplain net.minecraft.world.event.GameEvent#BLOCK_DESTROY game event}.
     * 
     * @return whether the block was removed successfully
     * 
     * @see #breakBlock(BlockPos, boolean)
     * @see #breakBlock(BlockPos, boolean, Entity)
     */
    public boolean breakBlock(BlockPos var1, boolean var2, @Nullable Entity var3, int var4);

    /**
     * Spawns an entity.
     * 
     * @apiNote To spawn an entity with passengers, use
     * {@link ServerWorldAccess#spawnEntityAndPassengers}.
     * 
     * @see ServerWorldAccess#spawnEntityAndPassengers
     */
    default public boolean spawnEntity(Entity entity) {
        return false;
    }
}

