package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;

@Environment(EnvType.CLIENT)
public class LightmapCoordinatesRetriever<S extends BlockEntity> implements DoubleBlockProperties.PropertyRetriever<S, Int2IntFunction> {
	public Int2IntFunction getFromBoth(S blockEntity, S blockEntity2) {
		return i -> {
			int j = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos());
			int k = WorldRenderer.getLightmapCoordinates(blockEntity2.getWorld(), blockEntity2.getPos());
			int l = LightmapTextureManager.getBlockLightCoordinates(j);
			int m = LightmapTextureManager.getBlockLightCoordinates(k);
			int n = LightmapTextureManager.getSkyLightCoordinates(j);
			int o = LightmapTextureManager.getSkyLightCoordinates(k);
			return LightmapTextureManager.pack(Math.max(l, m), Math.max(n, o));
		};
	}

	public Int2IntFunction getFrom(S blockEntity) {
		return i -> i;
	}

	public Int2IntFunction getFallback() {
		return i -> i;
	}
}
