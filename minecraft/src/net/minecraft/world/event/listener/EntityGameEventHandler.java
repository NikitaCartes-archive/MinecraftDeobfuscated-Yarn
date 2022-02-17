package net.minecraft.world.event.listener;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

/**
 * A game event handler for an entity so that the listener stored can be
 * moved to the correct dispatcher or unregistered as the entity moves or
 * gets removed.
 * 
 * @apiNote This implementation is currently unused by vanilla as vanilla
 * doesn't have any entity that listens to game events.
 * 
 * @see net.minecraft.entity.Entity#getGameEventHandler()
 */
public class EntityGameEventHandler {
	private final GameEventListener listener;
	@Nullable
	private ChunkSectionPos sectionPos;

	public EntityGameEventHandler(GameEventListener listener) {
		this.listener = listener;
	}

	public void onEntityRemoval(World world) {
		this.updateDispatcher(world, this.sectionPos, dispatcher -> dispatcher.removeListener(this.listener));
	}

	public void onEntitySetPos(World world) {
		Optional<BlockPos> optional = this.listener.getPositionSource().getPos(world).map(BlockPos::new);
		if (optional.isPresent()) {
			long l = ChunkSectionPos.fromBlockPos(((BlockPos)optional.get()).asLong());
			if (this.sectionPos == null || this.sectionPos.asLong() != l) {
				ChunkSectionPos chunkSectionPos = this.sectionPos;
				this.sectionPos = ChunkSectionPos.from(l);
				this.updateDispatcher(world, chunkSectionPos, dispatcher -> dispatcher.removeListener(this.listener));
				this.updateDispatcher(world, this.sectionPos, dispatcher -> dispatcher.addListener(this.listener));
			}
		}
	}

	private void updateDispatcher(World world, @Nullable ChunkSectionPos sectionPos, Consumer<GameEventDispatcher> action) {
		if (sectionPos != null) {
			Chunk chunk = world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.FULL, false);
			if (chunk != null) {
				action.accept(chunk.getGameEventDispatcher(sectionPos.getSectionY()));
			}
		}
	}
}
