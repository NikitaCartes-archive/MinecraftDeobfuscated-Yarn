package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public interface ChunkRendererFactory {
	ChunkRenderer create(World world, WorldRenderer worldRenderer);
}
