package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class VoxelDebugRenderer implements RenderDebug.DebugRenderer {
	private final MinecraftClient field_4540;
	private double field_4541 = Double.MIN_VALUE;
	private List<VoxelShape> field_4542 = Collections.emptyList();

	public VoxelDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4540 = minecraftClient;
	}

	@Override
	public void render(float f, long l) {
		PlayerEntity playerEntity = this.field_4540.player;
		double d = (double)SystemUtil.getMeasuringTimeNano();
		if (d - this.field_4541 > 1.0E8) {
			this.field_4541 = d;
			this.field_4542 = (List<VoxelShape>)playerEntity.world.method_8607(playerEntity, playerEntity.getBoundingBox().expand(6.0)).collect(Collectors.toList());
		}

		double e = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
		double g = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
		double h = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (VoxelShape voxelShape : this.field_4542) {
			WorldRenderer.drawDebugShapeOutline(voxelShape, -e, -g, -h, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
