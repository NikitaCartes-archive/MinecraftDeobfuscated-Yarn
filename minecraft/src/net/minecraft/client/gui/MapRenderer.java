package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MapRenderer implements AutoCloseable {
	private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
	private final TextureManager textureManager;
	private final Map<String, MapRenderer.MapTexture> mapTextures = Maps.<String, MapRenderer.MapTexture>newHashMap();

	public MapRenderer(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	public void updateTexture(MapState mapState) {
		this.getMapTexture(mapState).updateTexture();
	}

	public void draw(MapState mapState, boolean bl) {
		this.getMapTexture(mapState).draw(bl);
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
	public MapState getState(@Nullable MapRenderer.MapTexture mapTexture) {
		return mapTexture != null ? mapTexture.mapState : null;
	}

	public void close() {
		this.clearStateTextures();
	}

	@Environment(EnvType.CLIENT)
	class MapTexture implements AutoCloseable {
		private final MapState mapState;
		private final NativeImageBackedTexture texture;
		private final Identifier id;

		private MapTexture(MapState mapState) {
			this.mapState = mapState;
			this.texture = new NativeImageBackedTexture(128, 128, true);
			this.id = MapRenderer.this.textureManager.registerDynamicTexture("map/" + mapState.getId(), this.texture);
		}

		private void updateTexture() {
			for (int i = 0; i < 128; i++) {
				for (int j = 0; j < 128; j++) {
					int k = j + i * 128;
					int l = this.mapState.colors[k] & 255;
					if (l / 4 == 0) {
						this.texture.getImage().setPixelRGBA(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
					} else {
						this.texture.getImage().setPixelRGBA(j, i, MaterialColor.COLORS[l / 4].getRenderColor(l & 3));
					}
				}
			}

			this.texture.upload();
		}

		private void draw(boolean bl) {
			int i = 0;
			int j = 0;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			float f = 0.0F;
			MapRenderer.this.textureManager.bindTexture(this.id);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(class_4493.class_4535.ONE, class_4493.class_4534.ONE_MINUS_SRC_ALPHA, class_4493.class_4535.ZERO, class_4493.class_4534.ONE);
			RenderSystem.disableAlphaTest();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
			bufferBuilder.vertex(0.0, 128.0, -0.01F).texture(0.0, 1.0).next();
			bufferBuilder.vertex(128.0, 128.0, -0.01F).texture(1.0, 1.0).next();
			bufferBuilder.vertex(128.0, 0.0, -0.01F).texture(1.0, 0.0).next();
			bufferBuilder.vertex(0.0, 0.0, -0.01F).texture(0.0, 0.0).next();
			tessellator.draw();
			RenderSystem.enableAlphaTest();
			RenderSystem.disableBlend();
			int k = 0;

			for (MapIcon mapIcon : this.mapState.icons.values()) {
				if (!bl || mapIcon.isAlwaysRendered()) {
					MapRenderer.this.textureManager.bindTexture(MapRenderer.MAP_ICONS_TEXTURE);
					RenderSystem.pushMatrix();
					RenderSystem.translatef(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F, 0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F, -0.02F);
					RenderSystem.rotatef((float)(mapIcon.getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
					RenderSystem.scalef(4.0F, 4.0F, 3.0F);
					RenderSystem.translatef(-0.125F, 0.125F, 0.0F);
					byte b = mapIcon.getTypeId();
					float g = (float)(b % 16 + 0) / 16.0F;
					float h = (float)(b / 16 + 0) / 16.0F;
					float l = (float)(b % 16 + 1) / 16.0F;
					float m = (float)(b / 16 + 1) / 16.0F;
					bufferBuilder.begin(7, VertexFormats.POSITION_UV);
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					float n = -0.001F;
					bufferBuilder.vertex(-1.0, 1.0, (double)((float)k * -0.001F)).texture((double)g, (double)h).next();
					bufferBuilder.vertex(1.0, 1.0, (double)((float)k * -0.001F)).texture((double)l, (double)h).next();
					bufferBuilder.vertex(1.0, -1.0, (double)((float)k * -0.001F)).texture((double)l, (double)m).next();
					bufferBuilder.vertex(-1.0, -1.0, (double)((float)k * -0.001F)).texture((double)g, (double)m).next();
					tessellator.draw();
					RenderSystem.popMatrix();
					if (mapIcon.getText() != null) {
						TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
						String string = mapIcon.getText().asFormattedString();
						float o = (float)textRenderer.getStringWidth(string);
						float p = MathHelper.clamp(25.0F / o, 0.0F, 6.0F / 9.0F);
						RenderSystem.pushMatrix();
						RenderSystem.translatef(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F - o * p / 2.0F, 0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F + 4.0F, -0.025F);
						RenderSystem.scalef(p, p, 1.0F);
						DrawableHelper.fill(-1, -1, (int)o, 9 - 1, Integer.MIN_VALUE);
						RenderSystem.translatef(0.0F, 0.0F, -0.1F);
						textRenderer.draw(string, 0.0F, 0.0F, -1);
						RenderSystem.popMatrix();
					}

					k++;
				}
			}

			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 0.0F, -0.04F);
			RenderSystem.scalef(1.0F, 1.0F, 1.0F);
			RenderSystem.popMatrix();
		}

		public void close() {
			this.texture.close();
		}
	}
}
