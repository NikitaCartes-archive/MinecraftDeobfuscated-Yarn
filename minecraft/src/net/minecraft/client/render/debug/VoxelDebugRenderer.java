package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
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
	public void render(long l) {
		Camera camera = this.field_4540.gameRenderer.getCamera();
		double d = (double)SystemUtil.getMeasuringTimeNano();
		if (d - this.field_4541 > 1.0E8) {
			this.field_4541 = d;
			this.field_4542 = (List<VoxelShape>)camera.getFocusedEntity()
				.world
				.getCollisionShapes(camera.getFocusedEntity(), camera.getFocusedEntity().getBoundingBox().expand(6.0), Collections.emptySet())
				.collect(Collectors.toList());
		}

		double e = camera.getPos().x;
		double f = camera.getPos().y;
		double g = camera.getPos().z;
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE_MINUS_SRC_ALPHA, class_4493.class_4535.ONE, class_4493.class_4534.ZERO
		);
		RenderSystem.lineWidth(2.0F);
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);

		for (VoxelShape voxelShape : this.field_4542) {
			WorldRenderer.drawDebugShapeOutline(voxelShape, -e, -f, -g, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
