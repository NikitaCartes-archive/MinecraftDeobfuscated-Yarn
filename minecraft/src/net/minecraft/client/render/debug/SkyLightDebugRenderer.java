package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
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

	public SkyLightDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		Camera camera = this.client.gameRenderer.getCamera();
		World world = this.client.world;
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE_MINUS_SRC_ALPHA, class_4493.class_4535.ONE, class_4493.class_4534.ZERO
		);
		RenderSystem.disableTexture();
		BlockPos blockPos = new BlockPos(camera.getPos());
		LongSet longSet = new LongOpenHashSet();

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
			int i = world.getLightLevel(LightType.SKY, blockPos2);
			float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = MathHelper.hsvToRgb(f, 0.9F, 0.9F);
			long m = ChunkSectionPos.toChunkLong(blockPos2.asLong());
			if (longSet.add(m)) {
				DebugRenderer.method_19429(
					world.getChunkManager().getLightingProvider().method_15564(LightType.SKY, ChunkSectionPos.from(m)),
					(double)(ChunkSectionPos.unpackLongX(m) * 16 + 8),
					(double)(ChunkSectionPos.unpackLongY(m) * 16 + 8),
					(double)(ChunkSectionPos.unpackLongZ(m) * 16 + 8),
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				DebugRenderer.method_3714(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, j);
			}
		}

		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}
}
