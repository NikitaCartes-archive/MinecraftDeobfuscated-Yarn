package net.minecraft.client.render;

import net.minecraft.util.math.BlockPos;

public class BlockBreakingInfo implements Comparable<BlockBreakingInfo> {
	private final int actorNetworkId;
	private final BlockPos pos;
	private int stage;
	private int lastUpdateTick;

	public BlockBreakingInfo(int breakingEntityId, BlockPos pos) {
		this.actorNetworkId = breakingEntityId;
		this.pos = pos;
	}

	public int getActorId() {
		return this.actorNetworkId;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public void setStage(int stage) {
		if (stage > 10) {
			stage = 10;
		}

		this.stage = stage;
	}

	public int getStage() {
		return this.stage;
	}

	public void setLastUpdateTick(int lastUpdateTick) {
		this.lastUpdateTick = lastUpdateTick;
	}

	public int getLastUpdateTick() {
		return this.lastUpdateTick;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)o;
			return this.actorNetworkId == blockBreakingInfo.actorNetworkId;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Integer.hashCode(this.actorNetworkId);
	}

	public int compareTo(BlockBreakingInfo blockBreakingInfo) {
		return this.stage != blockBreakingInfo.stage
			? Integer.compare(this.stage, blockBreakingInfo.stage)
			: Integer.compare(this.actorNetworkId, blockBreakingInfo.actorNetworkId);
	}
}
