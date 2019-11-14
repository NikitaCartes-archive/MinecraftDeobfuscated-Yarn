/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MapRenderer
implements AutoCloseable {
    public static final Identifier field_21056 = new Identifier("textures/map/map_background.png");
    private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
    private final TextureManager textureManager;
    private final Map<String, MapTexture> mapTextures = Maps.newHashMap();

    public MapRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void updateTexture(MapState mapState) {
        this.getMapTexture(mapState).updateTexture();
    }

    public void draw(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, MapState mapState, boolean bl, int i) {
        this.getMapTexture(mapState).draw(matrixStack, vertexConsumerProvider, bl, i);
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
                        this.texture.getImage().setPixelRgba(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
                        continue;
                    }
                    this.texture.getImage().setPixelRgba(j, i, MaterialColor.COLORS[l / 4].getRenderColor(l & 3));
                }
            }
            this.texture.upload();
        }

        private void draw(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, boolean bl, int i) {
            boolean j = false;
            boolean k = false;
            float f = 0.0f;
            Matrix4f matrix4f = matrixStack.peek().getModel();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getText(this.id));
            vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(i).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(i).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(i).next();
            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(i).next();
            int l = 0;
            for (MapIcon mapIcon : this.mapState.icons.values()) {
                if (bl && !mapIcon.isAlwaysRendered()) continue;
                matrixStack.push();
                matrixStack.translate(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f, -0.02f);
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)(mapIcon.getRotation() * 360) / 16.0f));
                matrixStack.scale(4.0f, 4.0f, 3.0f);
                matrixStack.translate(-0.125, 0.125, 0.0);
                byte b = mapIcon.getTypeId();
                float g = (float)(b % 16 + 0) / 16.0f;
                float h = (float)(b / 16 + 0) / 16.0f;
                float m = (float)(b % 16 + 1) / 16.0f;
                float n = (float)(b / 16 + 1) / 16.0f;
                Matrix4f matrix4f2 = matrixStack.peek().getModel();
                float o = -0.001f;
                VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getText(MAP_ICONS_TEXTURE));
                vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, (float)l * -0.001f).color(255, 255, 255, 255).texture(g, h).light(i).next();
                vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, (float)l * -0.001f).color(255, 255, 255, 255).texture(m, h).light(i).next();
                vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, (float)l * -0.001f).color(255, 255, 255, 255).texture(m, n).light(i).next();
                vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, (float)l * -0.001f).color(255, 255, 255, 255).texture(g, n).light(i).next();
                matrixStack.pop();
                if (mapIcon.getText() != null) {
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    String string = mapIcon.getText().asFormattedString();
                    float p = textRenderer.getStringWidth(string);
                    float f2 = 25.0f / p;
                    textRenderer.getClass();
                    float q = MathHelper.clamp(f2, 0.0f, 6.0f / 9.0f);
                    matrixStack.push();
                    matrixStack.translate(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f - p * q / 2.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f + 4.0f, -0.025f);
                    matrixStack.scale(q, q, 1.0f);
                    matrixStack.translate(0.0, 0.0, -0.1f);
                    textRenderer.draw(string, 0.0f, 0.0f, -1, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, Integer.MIN_VALUE, i);
                    matrixStack.pop();
                }
                ++l;
            }
        }

        @Override
        public void close() {
            this.texture.close();
        }
    }
}

