package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BlockBreakingInfo {
	private final int actorNetworkId;
	private final BlockPos pos;
	private int stage;
	private int lastUpdateTick;

	public BlockBreakingInfo(int breakingEntityId, BlockPos pos) {
		this.actorNetworkId = breakingEntityId;
		this.pos = pos;
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
}
