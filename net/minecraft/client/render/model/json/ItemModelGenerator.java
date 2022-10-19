/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class ItemModelGenerator {
    public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");
    private static final float field_32806 = 7.5f;
    private static final float field_32807 = 8.5f;

    public JsonUnbakedModel create(Function<SpriteIdentifier, Sprite> textureGetter, JsonUnbakedModel blockModel) {
        String string;
        HashMap<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
        ArrayList<ModelElement> list = Lists.newArrayList();
        for (int i = 0; i < LAYERS.size() && blockModel.textureExists(string = LAYERS.get(i)); ++i) {
            SpriteIdentifier spriteIdentifier = blockModel.resolveSprite(string);
            map.put(string, Either.left(spriteIdentifier));
            SpriteContents spriteContents = textureGetter.apply(spriteIdentifier).getContents();
            list.addAll(this.addLayerElements(i, string, spriteContents));
        }
        map.put("particle", blockModel.textureExists("particle") ? Either.left(blockModel.resolveSprite("particle")) : (Either)map.get("layer0"));
        JsonUnbakedModel jsonUnbakedModel = new JsonUnbakedModel(null, list, map, false, blockModel.getGuiLight(), blockModel.getTransformations(), blockModel.getOverrides());
        jsonUnbakedModel.id = blockModel.id;
        return jsonUnbakedModel;
    }

    private List<ModelElement> addLayerElements(int layer, String key, SpriteContents sprite) {
        HashMap<Direction, ModelElementFace> map = Maps.newHashMap();
        map.put(Direction.SOUTH, new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{0.0f, 0.0f, 16.0f, 16.0f}, 0)));
        map.put(Direction.NORTH, new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{16.0f, 0.0f, 0.0f, 16.0f}, 0)));
        ArrayList<ModelElement> list = Lists.newArrayList();
        list.add(new ModelElement(new Vec3f(0.0f, 0.0f, 7.5f), new Vec3f(16.0f, 16.0f, 8.5f), map, null, true));
        list.addAll(this.addSubComponents(sprite, key, layer));
        return list;
    }

    private List<ModelElement> addSubComponents(SpriteContents sprite, String key, int layer) {
        float f = sprite.getWidth();
        float g = sprite.getHeight();
        ArrayList<ModelElement> list = Lists.newArrayList();
        for (Frame frame : this.getFrames(sprite)) {
            float h = 0.0f;
            float i = 0.0f;
            float j = 0.0f;
            float k = 0.0f;
            float l = 0.0f;
            float m = 0.0f;
            float n = 0.0f;
            float o = 0.0f;
            float p = 16.0f / f;
            float q = 16.0f / g;
            float r = frame.getMin();
            float s = frame.getMax();
            float t = frame.getLevel();
            Side side = frame.getSide();
            switch (side) {
                case UP: {
                    h = l = r;
                    j = m = s + 1.0f;
                    i = n = t;
                    k = t;
                    o = t + 1.0f;
                    break;
                }
                case DOWN: {
                    n = t;
                    o = t + 1.0f;
                    h = l = r;
                    j = m = s + 1.0f;
                    i = t + 1.0f;
                    k = t + 1.0f;
                    break;
                }
                case LEFT: {
                    h = l = t;
                    j = t;
                    m = t + 1.0f;
                    i = o = r;
                    k = n = s + 1.0f;
                    break;
                }
                case RIGHT: {
                    l = t;
                    m = t + 1.0f;
                    h = t + 1.0f;
                    j = t + 1.0f;
                    i = o = r;
                    k = n = s + 1.0f;
                }
            }
            h *= p;
            j *= p;
            i *= q;
            k *= q;
            i = 16.0f - i;
            k = 16.0f - k;
            HashMap<Direction, ModelElementFace> map = Maps.newHashMap();
            map.put(side.getDirection(), new ModelElementFace(null, layer, key, new ModelElementTexture(new float[]{l *= p, n *= q, m *= p, o *= q}, 0)));
            switch (side) {
                case UP: {
                    list.add(new ModelElement(new Vec3f(h, i, 7.5f), new Vec3f(j, i, 8.5f), map, null, true));
                    break;
                }
                case DOWN: {
                    list.add(new ModelElement(new Vec3f(h, k, 7.5f), new Vec3f(j, k, 8.5f), map, null, true));
                    break;
                }
                case LEFT: {
                    list.add(new ModelElement(new Vec3f(h, i, 7.5f), new Vec3f(h, k, 8.5f), map, null, true));
                    break;
                }
                case RIGHT: {
                    list.add(new ModelElement(new Vec3f(j, i, 7.5f), new Vec3f(j, k, 8.5f), map, null, true));
                }
            }
        }
        return list;
    }

    private List<Frame> getFrames(SpriteContents sprite) {
        int i = sprite.getWidth();
        int j = sprite.getHeight();
        ArrayList<Frame> list = Lists.newArrayList();
        sprite.getDistinctFrameCount().forEach(frame -> {
            for (int k = 0; k < j; ++k) {
                for (int l = 0; l < i; ++l) {
                    boolean bl = !this.isPixelTransparent(sprite, frame, l, k, i, j);
                    this.buildCube(Side.UP, list, sprite, frame, l, k, i, j, bl);
                    this.buildCube(Side.DOWN, list, sprite, frame, l, k, i, j, bl);
                    this.buildCube(Side.LEFT, list, sprite, frame, l, k, i, j, bl);
                    this.buildCube(Side.RIGHT, list, sprite, frame, l, k, i, j, bl);
                }
            }
        });
        return list;
    }

    private void buildCube(Side side, List<Frame> cubes, SpriteContents sprite, int frame, int x, int y, int width, int height, boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = this.isPixelTransparent(sprite, frame, x + side.getOffsetX(), y + side.getOffsetY(), width, height) && bl;
        if (bl2) {
            this.buildCube(cubes, side, x, y);
        }
    }

    private void buildCube(List<Frame> cubes, Side side, int x, int y) {
        int k;
        Frame frame = null;
        for (Frame frame2 : cubes) {
            int i;
            if (frame2.getSide() != side) continue;
            int n = i = side.isVertical() ? y : x;
            if (frame2.getLevel() != i) continue;
            frame = frame2;
            break;
        }
        int j = side.isVertical() ? y : x;
        int n = k = side.isVertical() ? x : y;
        if (frame == null) {
            cubes.add(new Frame(side, k, j));
        } else {
            frame.expand(k);
        }
    }

    private boolean isPixelTransparent(SpriteContents sprite, int frame, int x, int y, int width, int height) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return true;
        }
        return sprite.isPixelTransparent(frame, x, y);
    }

    @Environment(value=EnvType.CLIENT)
    static class Frame {
        private final Side side;
        private int min;
        private int max;
        private final int level;

        public Frame(Side side, int width, int depth) {
            this.side = side;
            this.min = width;
            this.max = width;
            this.level = depth;
        }

        public void expand(int newValue) {
            if (newValue < this.min) {
                this.min = newValue;
            } else if (newValue > this.max) {
                this.max = newValue;
            }
        }

        public Side getSide() {
            return this.side;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        public int getLevel() {
            return this.level;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Side {
        UP(Direction.UP, 0, -1),
        DOWN(Direction.DOWN, 0, 1),
        LEFT(Direction.EAST, -1, 0),
        RIGHT(Direction.WEST, 1, 0);

        private final Direction direction;
        private final int offsetX;
        private final int offsetY;

        private Side(Direction direction, int offsetX, int offsetY) {
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public Direction getDirection() {
            return this.direction;
        }

        public int getOffsetX() {
            return this.offsetX;
        }

        public int getOffsetY() {
            return this.offsetY;
        }

        boolean isVertical() {
            return this == DOWN || this == UP;
        }
    }
}

