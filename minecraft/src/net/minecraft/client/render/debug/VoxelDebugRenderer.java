package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class VoxelDebugRenderer implements DebugRenderer.DebugRenderer {
	private final MinecraftClient field_4540;
	private double field_4541 = Double.MIN_VALUE;
	private List<VoxelShape> field_4542 = Collections.emptyList();

	public VoxelDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4540 = minecraftClient;
	}

	@Override
	public void render(long l) {
		class_4184 lv = this.field_4540.field_1773.method_19418();
		double d = (double)SystemUtil.getMeasuringTimeNano();
		if (d - this.field_4541 > 1.0E8) {
			this.field_4541 = d;
			this.field_4542 = (List<VoxelShape>)lv.method_19331()
				.field_6002
				.method_8600(lv.method_19331(), lv.method_19331().method_5829().expand(6.0), Collections.emptySet())
				.collect(Collectors.toList());
		}

		double e = lv.method_19326().x;
		double f = lv.method_19326().y;
		double g = lv.method_19326().z;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (VoxelShape voxelShape : this.field_4542) {
			WorldRenderer.drawDebugShapeOutline(voxelShape, -e, -f, -g, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
