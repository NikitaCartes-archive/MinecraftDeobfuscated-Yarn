package net.minecraft.client.network;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class PendingUpdateManager implements AutoCloseable {
	private final Long2ObjectOpenHashMap<PendingUpdateManager.PendingUpdate> blockPosToPendingUpdate = new Long2ObjectOpenHashMap<>();
	private int sequence;
	private boolean pendingSequence;

	public void addPendingUpdate(BlockPos pos, BlockState state, ClientPlayerEntity player) {
		this.blockPosToPendingUpdate
			.compute(
				pos.asLong(),
				(posLong, pendingUpdate) -> pendingUpdate != null
						? pendingUpdate.withSequence(this.sequence)
						: new PendingUpdateManager.PendingUpdate(this.sequence, state, player.getPos())
			);
	}

	public boolean hasPendingUpdate(BlockPos pos, BlockState state) {
		PendingUpdateManager.PendingUpdate pendingUpdate = this.blockPosToPendingUpdate.get(pos.asLong());
		if (pendingUpdate == null) {
			return false;
		} else {
			pendingUpdate.setBlockState(state);
			return true;
		}
	}

	public void processPendingUpdates(int maxProcessableSequence, ClientWorld world) {
		ObjectIterator<Entry<PendingUpdateManager.PendingUpdate>> objectIterator = this.blockPosToPendingUpdate.long2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<PendingUpdateManager.PendingUpdate> entry = (Entry<PendingUpdateManager.PendingUpdate>)objectIterator.next();
			PendingUpdateManager.PendingUpdate pendingUpdate = (PendingUpdateManager.PendingUpdate)entry.getValue();
			if (pendingUpdate.sequence <= maxProcessableSequence) {
				BlockPos blockPos = BlockPos.fromLong(entry.getLongKey());
				objectIterator.remove();
				world.processPendingUpdate(blockPos, pendingUpdate.blockState, pendingUpdate.playerPos);
			}
		}
	}

	public PendingUpdateManager incrementSequence() {
		this.sequence++;
		this.pendingSequence = true;
		return this;
	}

	public void close() {
		this.pendingSequence = false;
	}

	public int getSequence() {
		return this.sequence;
	}

	public boolean hasPendingSequence() {
		return this.pendingSequence;
	}

	@Environment(EnvType.CLIENT)
	static class PendingUpdate {
		final Vec3d playerPos;
		int sequence;
		BlockState blockState;

		PendingUpdate(int sequence, BlockState blockState, Vec3d playerPos) {
			this.sequence = sequence;
			this.blockState = blockState;
			this.playerPos = playerPos;
		}

		PendingUpdateManager.PendingUpdate withSequence(int sequence) {
			this.sequence = sequence;
			return this;
		}

		void setBlockState(BlockState state) {
			this.blockState = state;
		}
	}
}
