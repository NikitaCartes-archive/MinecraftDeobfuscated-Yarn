package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

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
	 * <p>For the accepted values of the flags, see {@link net.fabricmc.yarn.constants.SetBlockStateFlags}.
	 * 
	 * @see net.fabricmc.yarn.constants.SetBlockStateFlags
	 * 
	 * @param pos the target position
	 * @param state the block state to set
	 * @param flags the bitwise flag combination, as described above
	 * @param maxUpdateDepth the limit for the cascading block updates
	 */
	boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth);

	/**
	 * Updates the block state at a position, calling appropriate callbacks.
	 * 
	 * <p>When called on the server, the new block state is stored and propagated to clients and listeners as dictated
	 * by the supplied flags. Note that calling this on the client will update the world locally, but may not see the
	 * change persisted across loads. It's recommended to check whether this world is client before
	 * interacting with the world in this way.</p>
	 * 
	 * <p>For the accepted values of the flags, see {@link net.fabricmc.yarn.constants.SetBlockStateFlags}.
	 * 
	 * @see #setBlockState(BlockPos, BlockState, int, int)
	 * @see net.fabricmc.yarn.constants.SetBlockStateFlags
	 * 
	 * @param pos the target position
	 * @param state the block state to set
	 * @param flags the bitwise flag combination, as described above
	 */
	default boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		return this.setBlockState(pos, state, flags, 512);
	}

	boolean removeBlock(BlockPos pos, boolean move);

	default boolean breakBlock(BlockPos pos, boolean drop) {
		return this.breakBlock(pos, drop, null);
	}

	default boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity) {
		return this.breakBlock(pos, drop, breakingEntity, 512);
	}

	boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth);

	default boolean spawnEntity(Entity entity) {
		return false;
	}
}
