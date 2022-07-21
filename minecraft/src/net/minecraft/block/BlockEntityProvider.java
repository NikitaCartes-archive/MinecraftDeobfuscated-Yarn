package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;

/**
 * A block with a {@link BlockEntity}. If a block has a corresponding block entity,
 * it must implement this interface. Multiple blocks can share a block entity type.
 * 
 * <p>The {@link #createBlockEntity} method is responsible for creating an instance
 * of your block entity; no other code should instantiate it.
 * 
 * <p>See the documentation of {@link BlockEntity} for more information on what a
 * block entity is. See the documentation of {@link
 * net.minecraft.block.entity.BlockEntityType} for how to create a block entity type.
 * 
 * @see BlockEntity
 * @see BlockWithEntity
 */
public interface BlockEntityProvider {
	/**
	 * {@return a new block entity instance}
	 * 
	 * <p>For example:
	 * <pre>{@code
	 * @Override
	 * public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
	 *   return new MyBlockEntity(pos, state);
	 * }
	 * }</pre>
	 * 
	 * @implNote While this is marked as nullable, in practice this should never return
	 * {@code null}. {@link PistonExtensionBlock} is the only block in vanilla that
	 * returns {@code null} inside the implementation.
	 */
	@Nullable
	BlockEntity createBlockEntity(BlockPos pos, BlockState state);

	/**
	 * {@return the "ticker" for the block's block entity, or {@code null} if
	 * the block entity does not need to be ticked}
	 * 
	 * <p>Ticker is a functional interface called every tick to tick the block entity
	 * on both the client and the server.
	 * 
	 * <p>Tickers should validate that the passed {@code type} is the one this block expects,
	 * and return {@code null} if it isn't. This is to prevent crashes in rare cases where a
	 * mismatch occurs between the position's block and block entity. {@link
	 * BlockWithEntity#checkType} can be used to implement the check.
	 * 
	 * <p>Example:
	 * 
	 * <pre>{@code
	 * public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
	 *   if (type != YourMod.MY_BLOCK_ENTITY_TYPE) return null;
	 *   // This should be a static method usable as a BlockEntityTicker.
	 *   return YourBlockEntity::tick;
	 * }
	 * }</pre>
	 */
	@Nullable
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return null;
	}

	/**
	 * {@return the game event listener for the block's block entity,
	 * or {@code null} if the block entity does not listen to game events}
	 * 
	 * <p>Listeners should validate that the passed {@code blockEntity} is the block entity
	 * for this block, and return {@code null} if it isn't. This is to prevent crashes in
	 * rare cases where a mismatch occurs between the position's block and block entity.
	 */
	@Nullable
	default <T extends BlockEntity> GameEventListener getGameEventListener(ServerWorld world, T blockEntity) {
		return null;
	}
}
