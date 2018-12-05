package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_330 implements AutoCloseable {
	private static final Identifier field_2044 = new Identifier("textures/map/map_icons.png");
	private final TextureManager field_2043;
	private final Map<String, class_330.class_331> field_2045 = Maps.<String, class_330.class_331>newHashMap();

	public class_330(TextureManager textureManager) {
		this.field_2043 = textureManager;
	}

	public void method_1769(MapState mapState) {
		this.method_1774(mapState).method_1776();
	}

	public void method_1773(MapState mapState, boolean bl) {
		this.method_1774(mapState).method_1777(bl);
	}

	private class_330.class_331 method_1774(MapState mapState) {
		class_330.class_331 lv = (class_330.class_331)this.field_2045.get(mapState.getKey());
		if (lv == null) {
			lv = new class_330.class_331(mapState);
			this.field_2045.put(mapState.getKey(), lv);
		}

		return lv;
	}

	@Nullable
	public class_330.class_331 method_1768(String string) {
		return (class_330.class_331)this.field_2045.get(string);
	}

	public void method_1771() {
		for (class_330.class_331 lv : this.field_2045.values()) {
			lv.close();
		}

		this.field_2045.clear();
	}

	@Nullable
	public MapState method_1772(@Nullable class_330.class_331 arg) {
		return arg != null ? arg.field_2046 : null;
	}

	public void close() {
		this.method_1771();
	}

	@Environment(EnvType.CLIENT)
	class class_331 implements AutoCloseable {
		private final MapState field_2046;
		private final NativeImageBackedTexture field_2048;
		private final Identifier field_2049;

		private class_331(MapState mapState) {
			this.field_2046 = mapState;
			this.field_2048 = new NativeImageBackedTexture(128, 128, true);
			this.field_2049 = class_330.this.field_2043.registerDynamicTexture("map/" + mapState.getKey(), this.field_2048);
		}

		private void method_1776() {
			for (int i = 0; i < 128; i++) {
				for (int j = 0; j < 128; j++) {
					int k = j + i * 128;
					int l = this.field_2046.colorArray[k] & 255;
					if (l / 4 == 0) {
						this.field_2048.getImage().setPixelRGBA(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
					} else {
						this.field_2048.getImage().setPixelRGBA(j, i, MaterialColor.COLORS[l / 4].getRenderColor(l & 3));
					}
				}
			}

			this.field_2048.method_4524();
		}

		private void method_1777(boolean bl) {
			int i = 0;
			int j = 0;
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
			float f = 0.0F;
			class_330.this.field_2043.bindTexture(this.field_2049);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcBlendFactor.ZERO, GlStateManager.DstBlendFactor.ONE
			);
			GlStateManager.disableAlphaTest();
			vertexBuffer.begin(7, VertexFormats.POSITION_UV);
			vertexBuffer.vertex(0.0, 128.0, -0.01F).texture(0.0, 1.0).next();
			vertexBuffer.vertex(128.0, 128.0, -0.01F).texture(1.0, 1.0).next();
			vertexBuffer.vertex(128.0, 0.0, -0.01F).texture(1.0, 0.0).next();
			vertexBuffer.vertex(0.0, 0.0, -0.01F).texture(0.0, 0.0).next();
			tessellator.draw();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			int k = 0;

			for (MapIcon mapIcon : this.field_2046.icons.values()) {
				if (!bl || mapIcon.method_94()) {
					class_330.this.field_2043.bindTexture(class_330.field_2044);
					GlStateManager.pushMatrix();
					GlStateManager.translatef(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F, 0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F, -0.02F);
					GlStateManager.rotatef((float)(mapIcon.getType() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.scalef(4.0F, 4.0F, 3.0F);
					GlStateManager.translatef(-0.125F, 0.125F, 0.0F);
					byte b = mapIcon.getDirection();
					float g = (float)(b % 16 + 0) / 16.0F;
					float h = (float)(b / 16 + 0) / 16.0F;
					float l = (float)(b % 16 + 1) / 16.0F;
					float m = (float)(b / 16 + 1) / 16.0F;
					vertexBuffer.begin(7, VertexFormats.POSITION_UV);
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					float n = -0.001F;
					vertexBuffer.vertex(-1.0, 1.0, (double)((float)k * -0.001F)).texture((double)g, (double)h).next();
					vertexBuffer.vertex(1.0, 1.0, (double)((float)k * -0.001F)).texture((double)l, (double)h).next();
					vertexBuffer.vertex(1.0, -1.0, (double)((float)k * -0.001F)).texture((double)l, (double)m).next();
					vertexBuffer.vertex(-1.0, -1.0, (double)((float)k * -0.001F)).texture((double)g, (double)m).next();
					tessellator.draw();
					GlStateManager.popMatrix();
					if (mapIcon.method_88() != null) {
						FontRenderer fontRenderer = MinecraftClient.getInstance().fontRenderer;
						String string = mapIcon.method_88().getFormattedText();
						float o = (float)fontRenderer.getStringWidth(string);
						float p = MathHelper.clamp(25.0F / o, 0.0F, 6.0F / (float)fontRenderer.FONT_HEIGHT);
						GlStateManager.pushMatrix();
						GlStateManager.translatef(0.0F + (float)mapIcon.getX() / 2.0F + 64.0F - o * p / 2.0F, 0.0F + (float)mapIcon.getZ() / 2.0F + 64.0F + 4.0F, -0.025F);
						GlStateManager.scalef(p, p, 1.0F);
						InGameHud.drawRect(-1, -1, (int)o, fontRenderer.FONT_HEIGHT - 1, Integer.MIN_VALUE);
						GlStateManager.translatef(0.0F, 0.0F, -0.1F);
						fontRenderer.draw(string, 0.0F, 0.0F, -1);
						GlStateManager.popMatrix();
					}

					k++;
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, -0.04F);
			GlStateManager.scalef(1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}

		public void close() {
			this.field_2048.close();
		}
	}
}
