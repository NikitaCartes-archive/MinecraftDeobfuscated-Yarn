package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class PartiallyBrokenBlockEntry {
	private final int breakingEntityId;
	private final BlockPos pos;
	private int stage;
	private int field_13857;

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

	public void method_13989(int i) {
		this.field_13857 = i;
	}

	public int method_13990() {
		return this.field_13857;
	}
}
