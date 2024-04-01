package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.chunk.light.LightingProvider;

public class class_9516 extends LightingProvider {
	public static final class_9516 field_50527 = new class_9516();

	private class_9516() {
		super(new ChunkProvider() {
			@Nullable
			@Override
			public LightSourceView getChunk(int chunkX, int chunkZ) {
				return null;
			}

			@Override
			public BlockView getWorld() {
				return EmptyBlockView.INSTANCE;
			}
		}, false, false);
	}

	@Override
	public ChunkLightingView get(LightType lightType) {
		return lightType == LightType.BLOCK ? ChunkLightingView.Empty.MIN : ChunkLightingView.Empty.MAX;
	}
}
