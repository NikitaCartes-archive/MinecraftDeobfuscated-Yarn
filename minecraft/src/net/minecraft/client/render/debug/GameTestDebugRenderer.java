package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class GameTestDebugRenderer implements DebugRenderer.Renderer {
	private static final float MARKER_BOX_SIZE = 0.02F;
	private final Map<BlockPos, GameTestDebugRenderer.Marker> markers = Maps.<BlockPos, GameTestDebugRenderer.Marker>newHashMap();

	public void addMarker(BlockPos pos, int color, String message, int duration) {
		this.markers.put(pos, new GameTestDebugRenderer.Marker(color, message, Util.getMeasuringTimeMs() + (long)duration));
	}

	@Override
	public void clear() {
		this.markers.clear();
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		long l = Util.getMeasuringTimeMs();
		this.markers.entrySet().removeIf(entry -> l > ((GameTestDebugRenderer.Marker)entry.getValue()).removalTime);
		this.markers.forEach((pos, marker) -> this.renderMarker(matrices, vertexConsumers, pos, marker));
	}

	private void renderMarker(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, GameTestDebugRenderer.Marker marker) {
		DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.02F, marker.getRed(), marker.getBlue(), marker.getGreen(), marker.getAlpha() * 0.75F);
		if (!marker.message.isEmpty()) {
			double d = (double)pos.getX() + 0.5;
			double e = (double)pos.getY() + 1.2;
			double f = (double)pos.getZ() + 0.5;
			DebugRenderer.drawString(matrices, vertexConsumers, marker.message, d, e, f, Colors.WHITE, 0.01F, true, 0.0F, true);
		}
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

		public float getRed() {
			return (float)(this.color >> 16 & 0xFF) / 255.0F;
		}

		public float getBlue() {
			return (float)(this.color >> 8 & 0xFF) / 255.0F;
		}

		public float getGreen() {
			return (float)(this.color & 0xFF) / 255.0F;
		}

		public float getAlpha() {
			return (float)(this.color >> 24 & 0xFF) / 255.0F;
		}
	}
}
