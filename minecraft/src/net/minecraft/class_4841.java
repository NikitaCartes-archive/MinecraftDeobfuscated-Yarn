package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

@Environment(EnvType.CLIENT)
public class class_4841 implements DebugRenderer.Renderer {
	private final Set<ChunkSectionPos> field_22409 = Sets.<ChunkSectionPos>newHashSet();

	@Override
	public void clear() {
		this.field_22409.clear();
	}

	public void method_24808(ChunkSectionPos chunkSectionPos) {
		this.field_22409.add(chunkSectionPos);
	}

	public void method_24809(ChunkSectionPos chunkSectionPos) {
		this.field_22409.remove(chunkSectionPos);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		this.method_24806(cameraX, cameraY, cameraZ);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}

	private void method_24806(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		this.field_22409.forEach(chunkSectionPos -> {
			if (blockPos.isWithinDistance(chunkSectionPos.getCenterPos(), 60.0)) {
				method_24810(chunkSectionPos);
			}
		});
	}

	private static void method_24810(ChunkSectionPos chunkSectionPos) {
		float f = 1.0F;
		BlockPos blockPos = chunkSectionPos.getCenterPos();
		BlockPos blockPos2 = blockPos.add(-1.0, -1.0, -1.0);
		BlockPos blockPos3 = blockPos.add(1.0, 1.0, 1.0);
		DebugRenderer.drawBox(blockPos2, blockPos3, 0.2F, 1.0F, 0.2F, 0.15F);
	}
}
