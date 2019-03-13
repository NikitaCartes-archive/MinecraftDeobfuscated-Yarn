package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class class_4207 implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_18786;
	private final Map<BlockPos, class_4158> field_18787 = Maps.<BlockPos, class_4158>newHashMap();
	private final Set<ChunkSectionPos> field_18788 = Sets.<ChunkSectionPos>newHashSet();

	public void method_19432(BlockPos blockPos, Identifier identifier) {
		this.field_18787.put(blockPos, Registry.field_18792.method_10223(identifier));
	}

	public void method_19434(BlockPos blockPos, Identifier identifier) {
		this.field_18787.remove(blockPos);
	}

	public void method_19433(ChunkSectionPos chunkSectionPos) {
		this.field_18788.add(chunkSectionPos);
	}

	public void method_19435(ChunkSectionPos chunkSectionPos) {
		this.field_18788.remove(chunkSectionPos);
	}

	public class_4207(MinecraftClient minecraftClient) {
		this.field_18786 = minecraftClient;
	}

	@Override
	public void render(long l) {
		class_4184 lv = this.field_18786.field_1773.method_19418();
		double d = lv.method_19326().x;
		double e = lv.method_19326().y;
		double f = lv.method_19326().z;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(lv.method_19326().x, 0.0, lv.method_19326().z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(5, VertexFormats.field_1576);

		for (BlockPos blockPos2 : this.field_18787.keySet()) {
			if (blockPos.distanceTo(blockPos2) < 160.0) {
				WorldRenderer.buildBox(
					bufferBuilder,
					(double)blockPos2.getX() - d,
					(double)blockPos2.getY() - e,
					(double)blockPos2.getZ() - f,
					(double)((float)blockPos2.getX() + 1.0F) - d,
					(double)((float)blockPos2.getY() + 1.0F) - e,
					(double)((float)blockPos2.getZ() + 1.0F) - f,
					0.2F,
					0.2F,
					1.0F,
					0.5F
				);
			}
		}

		for (ChunkSectionPos chunkSectionPos : this.field_18788) {
			int i = ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkX()) + 8;
			int j = ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkY()) + 8;
			int k = ChunkSectionPos.fromChunkCoord(chunkSectionPos.getChunkZ()) + 8;
			if (blockPos.distanceTo(i, j, k) < 160.0) {
				WorldRenderer.buildBox(
					bufferBuilder,
					(double)(i - 2) - d,
					(double)(j - 2) - e,
					(double)(k - 2) - f,
					(double)(i + 2) - d,
					(double)(j + 2) - e,
					(double)(k + 2) - f,
					0.2F,
					1.0F,
					0.2F,
					0.2F
				);
			}
		}

		tessellator.draw();

		for (BlockPos blockPos2x : this.field_18787.keySet()) {
			if (blockPos.distanceTo(blockPos2x) < 160.0) {
				DebugRenderer.method_3714(
					((class_4158)this.field_18787.get(blockPos2x)).method_19155(),
					(double)blockPos2x.getX() + 0.5,
					(double)blockPos2x.getY() + 1.5,
					(double)blockPos2x.getZ() + 0.5,
					-1
				);
			}
		}

		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
