/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public interface ChunkRendererFactory {
    public ChunkRenderer create(World var1, WorldRenderer var2);
}

