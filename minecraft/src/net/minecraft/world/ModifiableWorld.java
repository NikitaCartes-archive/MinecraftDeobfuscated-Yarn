package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld {
	/**
	 * Updates the block state at a position, calling appropriate callbacks.
	 * 
	 * <p>When called on the server, the new block state is stored and propagated to clients and listeners as dictated
	 * by the supplied flags. Note that calling this on the client will update the world locally, but may not see the
	 * change persisted across loads. It's recommended to check whether this world is client before
	 * interacting with the world in this way.</p>
	 * 
	 * <p>Accepted values of the flags are a bitset combination of the following:</p>
	 * <ul>
	 * <li>0b0000001 // 1 - PROPAGATE_CHANGE - Propagates a change event to surrounding blocks.</li>
	 * <li>0b0000010 // 2 - NOTIFY_LISTENERS - Notifies listeners and clients who need to react when the block changes</li>
	 * <li>0b0000100 // 4 - NO_REDRAW - Used in conjunction with NOTIFY_LISTENERS to suppress the render pass on clients.</li>
	 * <li>0b0001000 // 8 - REDRAW_ON_MAIN_THREAD - Forces a synchronous redraw on clients.</li>
	 * <li>0b0010000 // 16 - FORCE_STATE - Bypass virtual blockstate changes and forces the passed state to be stored as-is.</li>
	 * <li>0b0100000 // 32 - SKIP_DROPS - Prevents the previous block (container) from dropping items when destroyed.</li>
	 * <li>0b1000000 // 64 - MECHANICAL_UPDATE - Signals that this is a mechanical update, usually caused by pistons moving blocks.</li>
	 * </ul>
	 * 
	 * @param pos the target position
	 * @param state the block state to set
	 * @param flags the bitwise flag combination, as described above
	 */
	boolean setBlockState(BlockPos pos, BlockState state, int flags);

	boolean removeBlock(BlockPos pos, boolean move);

	boolean breakBlock(BlockPos pos, boolean bl);

	default boolean spawnEntity(Entity entity) {
		return false;
	}
}
