package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class GameTestDebugRenderer implements DebugRenderer.Renderer {
	private final Map<BlockPos, GameTestDebugRenderer.Marker> markers = Maps.<BlockPos, GameTestDebugRenderer.Marker>newHashMap();

	public void addMarker(BlockPos pos, int color, String message, int duration) {
		this.markers.put(pos, new GameTestDebugRenderer.Marker(color, message, Util.getMeasuringTimeMs() + (long)duration));
	}

	@Override
	public void clear() {
		this.markers.clear();
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
		long m = Util.getMeasuringTimeMs();
		this.markers.entrySet().removeIf(entry -> m > ((GameTestDebugRenderer.Marker)entry.getValue()).removalTime);
		this.markers.forEach(this::method_23111);
	}

	private void method_23111(BlockPos blockPos, GameTestDebugRenderer.Marker marker) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		RenderSystem.disableTexture();
		DebugRenderer.drawBox(blockPos, 0.02F, marker.method_23112(), marker.method_23113(), marker.method_23114(), marker.method_23115());
		if (!marker.message.isEmpty()) {
			double d = (double)blockPos.getX() + 0.5;
			double e = (double)blockPos.getY() + 1.2;
			double f = (double)blockPos.getZ() + 0.5;
			DebugRenderer.drawString(marker.message, d, e, f, -1, 0.01F, true, 0.0F, true);
		}

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}

	@Environment(EnvType.CLIENT)
	static class Marker {
		public int color;
		public String message;
		public long removalTime;

		public Marker(int color, String message, long removalTime) {
			this.color = color;
			this.message = message;
			this.removalTime = removalTime;
		}

		public float method_23112() {
			return (float)(this.color >> 16 & 0xFF) / 255.0F;
		}

		public float method_23113() {
			return (float)(this.color >> 8 & 0xFF) / 255.0F;
		}

		public float method_23114() {
			return (float)(this.color & 0xFF) / 255.0F;
		}

		public float method_23115() {
			return (float)(this.color >> 24 & 0xFF) / 255.0F;
		}
	}
}