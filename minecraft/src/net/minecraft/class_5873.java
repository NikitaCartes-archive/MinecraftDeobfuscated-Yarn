package net.minecraft;

import net.minecraft.world.gen.chunk.ChunkGenerator;

public class class_5873 implements class_5868 {
	private final ChunkGenerator field_29059;

	public class_5873(ChunkGenerator chunkGenerator) {
		this.field_29059 = chunkGenerator;
	}

	@Override
	public int getMinY() {
		return this.field_29059.getMinimumY();
	}

	@Override
	public int getMaxY() {
		return this.field_29059.getWorldHeight();
	}
}
