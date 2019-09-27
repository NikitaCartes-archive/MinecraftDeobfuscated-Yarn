package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class PartiallyBrokenBlockEntry implements Comparable<PartiallyBrokenBlockEntry> {
	private final int breakingEntityId;
	private final BlockPos pos;
	private int stage;
	private int lastUpdateTicks;

	public PartiallyBrokenBlockEntry(int i, BlockPos blockPos) {
		this.breakingEntityId = i;
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

	public void setLastUpdateTicks(int i) {
		this.lastUpdateTicks = i;
	}

	public int getLastUpdateTicks() {
		return this.lastUpdateTicks;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)object;
			return this.breakingEntityId == partiallyBrokenBlockEntry.breakingEntityId;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Integer.hashCode(this.breakingEntityId);
	}

	public int method_23269(PartiallyBrokenBlockEntry partiallyBrokenBlockEntry) {
		return this.stage != partiallyBrokenBlockEntry.stage
			? Integer.compare(this.stage, partiallyBrokenBlockEntry.stage)
			: Integer.compare(this.breakingEntityId, partiallyBrokenBlockEntry.breakingEntityId);
	}
}
