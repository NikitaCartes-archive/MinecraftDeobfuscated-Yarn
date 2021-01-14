package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class MapRenderer implements AutoCloseable {
	private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
	private static final RenderLayer MAP_ICONS_RENDER_LAYER = RenderLayer.getText(MAP_ICONS_TEXTURE);
	private final TextureManager textureManager;
	private final Map<String, MapRenderer.MapTexture> mapTextures = Maps.<String, MapRenderer.MapTexture>newHashMap();

	public MapRenderer(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	public void updateTexture(MapState mapState) {
		this.getMapTexture(mapState).updateTexture();
	}

	public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, MapState mapState, boolean bl, int light) {
		this.getMapTexture(mapState).draw(matrices, vertexConsumers, bl, light);
	}

	private MapRenderer.MapTexture getMapTexture(MapState mapState) {
		MapRenderer.MapTexture mapTexture = (MapRenderer.MapTexture)this.mapTextures.get(mapState.getId());
		if (mapTexture == null) {
			mapTexture = new MapRenderer.MapTexture(mapState);
			this.mapTextures.put(mapState.getId(), mapTexture);
		}

		return mapTexture;
	}

	@Nullable
	public MapRenderer.MapTexture getTexture(String string) {
		return (MapRenderer.MapTexture)this.mapTextures.get(string);
	}

	public void clearStateTextures() {
		for (MapRenderer.MapTexture mapTexture : this.mapTextures.values()) {
			mapTexture.close();
		}

		this.mapTextures.clear();
	}

	@Nullable
	public MapState getState(@Nullable MapRenderer.MapTexture texture) {
		return texture != null ? texture.mapState : null;
	}

	public void close() {
		this.clearStateTextures();
	}

	@Environment(EnvType.CLIENT)
	class MapTexture implements AutoCloseable {
		private final MapState mapState;
		private final NativeImageBackedTexture texture;
		private final RenderLayer renderLayer;

		private MapTexture(MapState state) {
			this.mapState = state;
			this.texture = new NativeImageBackedTexture(128, 128, true);
			Identifier identifier = MapRenderer.this.textureManager.registerDynamicTexture("map/" + state.getId(), this.texture);
			this.renderLayer = RenderLayer.getText(identifier);
		}

		private void updateTexture() {
			for (int i = 0; i < 128; i++) {
				for (int j = 0; j < 128; j++) {
					int k = j + i * 128;
					int l = this.mapState.colors[k] & 255;
					if (l / 4 == 0) {
						this.texture.getImage().setPixelColor(j, i, 0);
					} else {
						this.texture.getImage().setPixelColor(j, i, MapColor.COLORS[l / 4].getRenderColor(l & 3));
					}
				}
			}

			this.texture.upload();
		}

		private void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean bl, int light) {
			int i = 0;
			int j = 0;
			float f = 0.0F;
			Matrix4f matrix4f = matrices.peek().getModel();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.renderLayer);
			vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
			vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
			vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
			vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
			int k = 0;

			for (MapIcon mapIcon : this.mapState.icons.values()) {
				if (!bl || mapIcon.isAlwaysRendered()) {
					matrices.push();
					matrices.translate((double)(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F), (double)(0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F), -0.02F);
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)(mapIcon.getRotation() * 360) / 16.0F));
					matrices.scale(4.0F, 4.0F, 3.0F);
					matrices.translate(-0.125, 0.125, 0.0);
					byte b = mapIcon.getTypeId();
					float g = (float)(b % 16 + 0) / 16.0F;
					float h = (float)(b / 16 + 0) / 16.0F;
					float l = (float)(b % 16 + 1) / 16.0F;
					float m = (float)(b / 16 + 1) / 16.0F;
					Matrix4f matrix4f2 = matrices.peek().getModel();
					float n = -0.001F;
					VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(MapRenderer.MAP_ICONS_RENDER_LAYER);
					vertexConsumer2.vertex(matrix4f2, -1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).texture(g, h).light(light).next();
					vertexConsumer2.vertex(matrix4f2, 1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).texture(l, h).light(light).next();
					vertexConsumer2.vertex(matrix4f2, 1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).texture(l, m).light(light).next();
					vertexConsumer2.vertex(matrix4f2, -1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).texture(g, m).light(light).next();
					matrices.pop();
					if (mapIcon.getText() != null) {
						TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
						Text text = mapIcon.getText();
						float o = (float)textRenderer.getWidth(text);
						float p = MathHelper.clamp(25.0F / o, 0.0F, 6.0F / 9.0F);
						matrices.push();
						matrices.translate(
							(double)(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F - o * p / 2.0F), (double)(0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F + 4.0F), -0.025F
						);
						matrices.scale(p, p, 1.0F);
						matrices.translate(0.0, 0.0, -0.1F);
						textRenderer.draw(text, 0.0F, 0.0F, -1, false, matrices.peek().getModel(), vertexConsumers, false, Integer.MIN_VALUE, light);
						matrices.pop();
					}

					k++;
				}
			}
		}

		public void close() {
			this.texture.close();
		}
	}
}
