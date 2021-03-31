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
     * interacting with the world in this way.</p>
     * 
     * <p>See {@link #setBlockState(BlockPos, BlockState, int)} for a list of accepted flags.
     * 
     * @param pos the target position
     * @param state the block state to set
     * @param flags the bitwise flag combination, as described above
     * @param maxUpdateDepth the limit for the cascading block updates
     */
    public boolean setBlockState(BlockPos var1, BlockState var2, int var3, int var4);

    /**
     * Updates the block state at a position, calling appropriate callbacks.
     * 
     * <p>When called on the server, the new block state is stored and propagated to clients and listeners as dictated
     * by the supplied flags. Note that calling this on the client will update the world locally, but may not see the
     * change persisted across loads. It's recommended to check whether this world is client before
     * interacting with the world in this way.</p>
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
     * @param state the block state to set
     * @param flags the bitwise flag combination, as described above
     */
    default public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        return this.setBlockState(pos, state, flags, 512);
    }

    public boolean removeBlock(BlockPos var1, boolean var2);

    default public boolean breakBlock(BlockPos pos, boolean drop) {
        return this.breakBlock(pos, drop, null);
    }

    default public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity) {
        return this.breakBlock(pos, drop, breakingEntity, 512);
    }

    public boolean breakBlock(BlockPos var1, boolean var2, @Nullable Entity var3, int var4);

    default public boolean spawnEntity(Entity entity) {
        return false;
    }
}

