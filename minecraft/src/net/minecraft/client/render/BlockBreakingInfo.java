package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BlockBreakingInfo implements Comparable<BlockBreakingInfo> {
	private final int actorNetworkId;
	private final BlockPos pos;
	private int stage;
	private int lastUpdateTick;

	public BlockBreakingInfo(int i, BlockPos blockPos) {
		this.actorNetworkId = i;
		this.pos = blockPos;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public void setStage(int i) {
		if (i > 10) {
			i = 10;
		}

		this.stage = i;
	}

	public int getStage() {
		return this.stage;
	}

	public void setLastUpdateTick(int i) {
		this.lastUpdateTick = i;
	}

	public int getLastUpdateTick() {
		return this.lastUpdateTick;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)object;
			return this.actorNetworkId == blockBreakingInfo.actorNetworkId;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Integer.hashCode(this.actorNetworkId);
	}

	public int method_23269(BlockBreakingInfo blockBreakingInfo) {
		return this.stage != blockBreakingInfo.stage
			? Integer.compare(this.stage, blockBreakingInfo.stage)
			: Integer.compare(this.actorNetworkId, blockBreakingInfo.actorNetworkId);
	}
}
