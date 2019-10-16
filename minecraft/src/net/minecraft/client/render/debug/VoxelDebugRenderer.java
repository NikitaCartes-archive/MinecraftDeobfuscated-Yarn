package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class VoxelDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_4540;
	private double field_4541 = Double.MIN_VALUE;
	private List<VoxelShape> field_4542 = Collections.emptyList();

	public VoxelDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4540 = minecraftClient;
	}

	@Override
	public void method_23109(long l) {
		Camera camera = this.field_4540.gameRenderer.getCamera();
		double d = (double)SystemUtil.getMeasuringTimeNano();
		if (d - this.field_4541 > 1.0E8) {
			this.field_4541 = d;
			this.field_4542 = (List<VoxelShape>)camera.getFocusedEntity()
				.world
				.getCollisions(camera.getFocusedEntity(), camera.getFocusedEntity().getBoundingBox().expand(6.0), Collections.emptySet())
				.collect(Collectors.toList());
		}

		double e = camera.getPos().x;
		double f = camera.getPos().y;
		double g = camera.getPos().z;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(2.0F);
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		LayeredVertexConsumerStorage.Drawer drawer = LayeredVertexConsumerStorage.makeDrawer(Tessellator.getInstance().getBufferBuilder());
		VertexConsumer vertexConsumer = drawer.getBuffer(RenderLayer.getLines());
		MatrixStack matrixStack = new MatrixStack();

		for (VoxelShape voxelShape : this.field_4542) {
			WorldRenderer.method_22983(matrixStack, vertexConsumer, voxelShape, -e, -f, -g, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		drawer.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
