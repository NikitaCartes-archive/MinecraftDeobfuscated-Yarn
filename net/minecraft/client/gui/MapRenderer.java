/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MapRenderer
implements AutoCloseable {
    private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
    private final TextureManager textureManager;
    private final Map<String, MapTexture> mapTextures = Maps.newHashMap();

    public MapRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void updateTexture(MapState mapState) {
        this.getMapTexture(mapState).updateTexture();
    }

    public void draw(class_4587 arg, class_4597 arg2, MapState mapState, boolean bl) {
        this.getMapTexture(mapState).draw(arg, arg2, bl);
    }

    private MapTexture getMapTexture(MapState mapState) {
        MapTexture mapTexture = this.mapTextures.get(mapState.getId());
        if (mapTexture == null) {
            mapTexture = new MapTexture(mapState);
            this.mapTextures.put(mapState.getId(), mapTexture);
        }
        return mapTexture;
    }

    @Nullable
    public MapTexture getTexture(String string) {
        return this.mapTextures.get(string);
    }

    public void clearStateTextures() {
        for (MapTexture mapTexture : this.mapTextures.values()) {
            mapTexture.close();
        }
        this.mapTextures.clear();
    }

    @Nullable
    public MapState getState(@Nullable MapTexture mapTexture) {
        if (mapTexture != null) {
            return mapTexture.mapState;
        }
        return null;
    }

    @Override
    public void close() {
        this.clearStateTextures();
    }

    @Environment(value=EnvType.CLIENT)
    class MapTexture
    implements AutoCloseable {
        private final MapState mapState;
        private final NativeImageBackedTexture texture;
        private final Identifier id;

        private MapTexture(MapState mapState) {
            this.mapState = mapState;
            this.texture = new NativeImageBackedTexture(128, 128, true);
            this.id = MapRenderer.this.textureManager.registerDynamicTexture("map/" + mapState.getId(), this.texture);
        }

        private void updateTexture() {
            for (int i = 0; i < 128; ++i) {
                for (int j = 0; j < 128; ++j) {
                    int k = j + i * 128;
                    int l = this.mapState.colors[k] & 0xFF;
                    if (l / 4 == 0) {
                        this.texture.getImage().setPixelRGBA(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
                        continue;
                    }
                    this.texture.getImage().setPixelRGBA(j, i, MaterialColor.COLORS[l / 4].getRenderColor(l & 3));
                }
            }
            this.texture.upload();
        }

        private void draw(class_4587 arg, class_4597 arg2, boolean bl) {
            boolean i = false;
            boolean j = false;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
            float f = 0.0f;
            Matrix4f matrix4f = arg.method_22910();
            MapRenderer.this.textureManager.bindTexture(this.id);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
            RenderSystem.disableAlphaTest();
            bufferBuilder.begin(7, VertexFormats.POSITION_UV);
            bufferBuilder.method_22918(matrix4f, 0.0f, 128.0f, -0.01f).texture(0.0f, 1.0f).next();
            bufferBuilder.method_22918(matrix4f, 128.0f, 128.0f, -0.01f).texture(1.0f, 1.0f).next();
            bufferBuilder.method_22918(matrix4f, 128.0f, 0.0f, -0.01f).texture(1.0f, 0.0f).next();
            bufferBuilder.method_22918(matrix4f, 0.0f, 0.0f, -0.01f).texture(0.0f, 0.0f).next();
            tessellator.draw();
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
            int k = 0;
            for (MapIcon mapIcon : this.mapState.icons.values()) {
                if (bl && !mapIcon.isAlwaysRendered()) continue;
                MapRenderer.this.textureManager.bindTexture(MAP_ICONS_TEXTURE);
                arg.method_22903();
                arg.method_22904(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f, -0.02f);
                arg.method_22907(Vector3f.field_20707.method_23214((float)(mapIcon.getRotation() * 360) / 16.0f, true));
                arg.method_22905(4.0f, 4.0f, 3.0f);
                arg.method_22904(-0.125, 0.125, 0.0);
                byte b = mapIcon.getTypeId();
                float g = (float)(b % 16 + 0) / 16.0f;
                float h = (float)(b / 16 + 0) / 16.0f;
                float l = (float)(b % 16 + 1) / 16.0f;
                float m = (float)(b / 16 + 1) / 16.0f;
                Matrix4f matrix4f2 = arg.method_22910();
                bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
                float n = -0.001f;
                bufferBuilder.method_22918(matrix4f2, -1.0f, 1.0f, (float)k * -0.001f).texture(g, h).color(255, 255, 255, 255).next();
                bufferBuilder.method_22918(matrix4f2, 1.0f, 1.0f, (float)k * -0.001f).texture(l, h).color(255, 255, 255, 255).next();
                bufferBuilder.method_22918(matrix4f2, 1.0f, -1.0f, (float)k * -0.001f).texture(l, m).color(255, 255, 255, 255).next();
                bufferBuilder.method_22918(matrix4f2, -1.0f, -1.0f, (float)k * -0.001f).texture(g, m).color(255, 255, 255, 255).next();
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
                arg.method_22909();
                if (mapIcon.getText() != null) {
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    String string = mapIcon.getText().asFormattedString();
                    float o = textRenderer.getStringWidth(string);
                    float f2 = 25.0f / o;
                    textRenderer.getClass();
                    float p = MathHelper.clamp(f2, 0.0f, 6.0f / 9.0f);
                    arg.method_22903();
                    arg.method_22904(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f - o * p / 2.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f + 4.0f, -0.025f);
                    arg.method_22905(p, p, 1.0f);
                    DrawableHelper.fill(arg.method_22910(), -1, -1, (int)o, textRenderer.fontHeight - 1, Integer.MIN_VALUE);
                    arg.method_22904(0.0, 0.0, -0.1f);
                    RenderSystem.enableAlphaTest();
                    textRenderer.method_22942(string, 0.0f, 0.0f, -1, false, arg.method_22910(), arg2, false, 0, 0xF000F0);
                    arg.method_22909();
                }
                ++k;
            }
        }

        @Override
        public void close() {
            this.texture.close();
        }
    }
}

