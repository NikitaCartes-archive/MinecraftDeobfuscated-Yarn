/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
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

@Environment(value=EnvType.CLIENT)
public class MapRenderer
implements AutoCloseable {
    private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
    static final RenderLayer MAP_ICONS_RENDER_LAYER = RenderLayer.getText(MAP_ICONS_TEXTURE);
    private static final int DEFAULT_IMAGE_WIDTH = 128;
    private static final int DEFAULT_IMAGE_HEIGHT = 128;
    final TextureManager textureManager;
    private final Int2ObjectMap<MapTexture> mapTextures = new Int2ObjectOpenHashMap<MapTexture>();

    public MapRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void updateTexture(int id, MapState state) {
        this.getMapTexture(id, state).setNeedsUpdate();
    }

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int id, MapState state, boolean hidePlayerIcons, int light) {
        this.getMapTexture(id, state).draw(matrices, vertexConsumers, hidePlayerIcons, light);
    }

    private MapTexture getMapTexture(int id, MapState state) {
        return this.mapTextures.compute(id, (integer, mapTexture) -> {
            if (mapTexture == null) {
                return new MapTexture((int)integer, state);
            }
            mapTexture.setState(state);
            return mapTexture;
        });
    }

    public void clearStateTextures() {
        for (MapTexture mapTexture : this.mapTextures.values()) {
            mapTexture.close();
        }
        this.mapTextures.clear();
    }

    @Override
    public void close() {
        this.clearStateTextures();
    }

    @Environment(value=EnvType.CLIENT)
    class MapTexture
    implements AutoCloseable {
        private MapState state;
        private final NativeImageBackedTexture texture;
        private final RenderLayer renderLayer;
        private boolean needsUpdate = true;

        MapTexture(int id, MapState state) {
            this.state = state;
            this.texture = new NativeImageBackedTexture(128, 128, true);
            Identifier identifier = MapRenderer.this.textureManager.registerDynamicTexture("map/" + id, this.texture);
            this.renderLayer = RenderLayer.getText(identifier);
        }

        void setState(MapState state) {
            boolean bl = this.state != state;
            this.state = state;
            this.needsUpdate |= bl;
        }

        public void setNeedsUpdate() {
            this.needsUpdate = true;
        }

        private void updateTexture() {
            for (int i = 0; i < 128; ++i) {
                for (int j = 0; j < 128; ++j) {
                    int k = j + i * 128;
                    int l = this.state.colors[k] & 0xFF;
                    if (l / 4 == 0) {
                        this.texture.getImage().setPixelColor(j, i, 0);
                        continue;
                    }
                    this.texture.getImage().setPixelColor(j, i, MapColor.COLORS[l / 4].getRenderColor(l & 3));
                }
            }
            this.texture.upload();
        }

        void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean hidePlayerIcons, int light) {
            if (this.needsUpdate) {
                this.updateTexture();
                this.needsUpdate = false;
            }
            boolean i = false;
            boolean j = false;
            float f = 0.0f;
            Matrix4f matrix4f = matrices.peek().getModel();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.renderLayer);
            vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(light).next();
            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(light).next();
            int k = 0;
            for (MapIcon mapIcon : this.state.getIcons()) {
                if (hidePlayerIcons && !mapIcon.isAlwaysRendered()) continue;
                matrices.push();
                matrices.translate(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f, -0.02f);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)(mapIcon.getRotation() * 360) / 16.0f));
                matrices.scale(4.0f, 4.0f, 3.0f);
                matrices.translate(-0.125, 0.125, 0.0);
                byte b = mapIcon.getTypeId();
                float g = (float)(b % 16 + 0) / 16.0f;
                float h = (float)(b / 16 + 0) / 16.0f;
                float l = (float)(b % 16 + 1) / 16.0f;
                float m = (float)(b / 16 + 1) / 16.0f;
                Matrix4f matrix4f2 = matrices.peek().getModel();
                float n = -0.001f;
                VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(MAP_ICONS_RENDER_LAYER);
                vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(g, h).light(light).next();
                vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(l, h).light(light).next();
                vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(l, m).light(light).next();
                vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, (float)k * -0.001f).color(255, 255, 255, 255).texture(g, m).light(light).next();
                matrices.pop();
                if (mapIcon.getText() != null) {
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    Text text = mapIcon.getText();
                    float o = textRenderer.getWidth(text);
                    float f2 = 25.0f / o;
                    Objects.requireNonNull(textRenderer);
                    float p = MathHelper.clamp(f2, 0.0f, 6.0f / 9.0f);
                    matrices.push();
                    matrices.translate(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f - o * p / 2.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f + 4.0f, -0.025f);
                    matrices.scale(p, p, 1.0f);
                    matrices.translate(0.0, 0.0, -0.1f);
                    textRenderer.draw(text, 0.0f, 0.0f, -1, false, matrices.peek().getModel(), vertexConsumers, false, Integer.MIN_VALUE, light);
                    matrices.pop();
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

