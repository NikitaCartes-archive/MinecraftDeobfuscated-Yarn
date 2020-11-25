package net.minecraft.client.gui;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
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
	private static final RenderLayer field_21688 = RenderLayer.getText(MAP_ICONS_TEXTURE);
	private final TextureManager textureManager;
	private final Int2ObjectMap<MapRenderer.MapTexture> mapTextures = new Int2ObjectOpenHashMap<>();

	public MapRenderer(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	public void updateTexture(int i, MapState mapState) {
		this.method_32601(i, mapState).updateTexture();
	}

	public void draw(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, MapState mapState, boolean bl, int j) {
		this.method_32601(i, mapState).draw(matrixStack, vertexConsumerProvider, bl, j);
	}

	private MapRenderer.MapTexture method_32601(int i, MapState mapState) {
		return this.mapTextures.computeIfAbsent(i, ix -> new MapRenderer.MapTexture(ix, mapState));
	}

	@Nullable
	public MapState method_32599(int i) {
		MapRenderer.MapTexture mapTexture = this.mapTextures.get(i);
		return mapTexture != null ? mapTexture.mapState : null;
	}

	public void clearStateTextures() {
		for (MapRenderer.MapTexture mapTexture : this.mapTextures.values()) {
			mapTexture.close();
		}

		this.mapTextures.clear();
	}

	public void close() {
		this.clearStateTextures();
	}

	@Environment(EnvType.CLIENT)
	class MapTexture implements AutoCloseable {
		private final MapState mapState;
		private final NativeImageBackedTexture texture;
		private final RenderLayer field_21689;

		private MapTexture(int i, MapState mapState) {
			this.mapState = mapState;
			this.texture = new NativeImageBackedTexture(128, 128, true);
			Identifier identifier = MapRenderer.this.textureManager.registerDynamicTexture("map/" + i, this.texture);
			this.field_21689 = RenderLayer.getText(identifier);
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

		private void draw(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, boolean bl, int i) {
			int j = 0;
			int k = 0;
			float f = 0.0F;
			Matrix4f matrix4f = matrixStack.peek().getModel();
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.field_21689);
			vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(i).next();
			vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(i).next();
			vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(i).next();
			vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(i).next();
			int l = 0;

			for (MapIcon mapIcon : this.mapState.method_32373()) {
				if (!bl || mapIcon.isAlwaysRendered()) {
					matrixStack.push();
					matrixStack.translate((double)(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F), (double)(0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F), -0.02F);
					matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)(mapIcon.getRotation() * 360) / 16.0F));
					matrixStack.scale(4.0F, 4.0F, 3.0F);
					matrixStack.translate(-0.125, 0.125, 0.0);
					byte b = mapIcon.getTypeId();
					float g = (float)(b % 16 + 0) / 16.0F;
					float h = (float)(b / 16 + 0) / 16.0F;
					float m = (float)(b % 16 + 1) / 16.0F;
					float n = (float)(b / 16 + 1) / 16.0F;
					Matrix4f matrix4f2 = matrixStack.peek().getModel();
					float o = -0.001F;
					VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(MapRenderer.field_21688);
					vertexConsumer2.vertex(matrix4f2, -1.0F, 1.0F, (float)l * -0.001F).color(255, 255, 255, 255).texture(g, h).light(i).next();
					vertexConsumer2.vertex(matrix4f2, 1.0F, 1.0F, (float)l * -0.001F).color(255, 255, 255, 255).texture(m, h).light(i).next();
					vertexConsumer2.vertex(matrix4f2, 1.0F, -1.0F, (float)l * -0.001F).color(255, 255, 255, 255).texture(m, n).light(i).next();
					vertexConsumer2.vertex(matrix4f2, -1.0F, -1.0F, (float)l * -0.001F).color(255, 255, 255, 255).texture(g, n).light(i).next();
					matrixStack.pop();
					if (mapIcon.getText() != null) {
						TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
						Text text = mapIcon.getText();
						float p = (float)textRenderer.getWidth(text);
						float q = MathHelper.clamp(25.0F / p, 0.0F, 6.0F / 9.0F);
						matrixStack.push();
						matrixStack.translate(
							(double)(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F - p * q / 2.0F), (double)(0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F + 4.0F), -0.025F
						);
						matrixStack.scale(q, q, 1.0F);
						matrixStack.translate(0.0, 0.0, -0.1F);
						textRenderer.draw(text, 0.0F, 0.0F, -1, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, Integer.MIN_VALUE, i);
						matrixStack.pop();
					}

					l++;
				}
			}
		}

		public void close() {
			this.texture.close();
		}
	}
}
