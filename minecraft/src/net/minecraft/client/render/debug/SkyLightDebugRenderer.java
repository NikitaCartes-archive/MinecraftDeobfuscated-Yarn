package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SkyLightDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public SkyLightDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(long limitTime) {
		Camera camera = this.client.gameRenderer.getCamera();
		World world = this.client.world;
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		BlockPos blockPos = new BlockPos(camera.getPos());
		LongSet longSet = new LongOpenHashSet();

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			int i = world.getLightLevel(LightType.SKY, blockPos2);
			float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = MathHelper.hsvToRgb(f, 0.9F, 0.9F);
			long l = ChunkSectionPos.fromGlobalPos(blockPos2.asLong());
			if (longSet.add(l)) {
				DebugRenderer.drawString(
					world.getChunkManager().getLightingProvider().method_22876(LightType.SKY, ChunkSectionPos.from(l)),
					(double)(ChunkSectionPos.getX(l) * 16 + 8),
					(double)(ChunkSectionPos.getY(l) * 16 + 8),
					(double)(ChunkSectionPos.getZ(l) * 16 + 8),
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				DebugRenderer.drawString(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, j);
			}
		}

		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}
}
