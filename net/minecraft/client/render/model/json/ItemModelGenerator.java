/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ItemModelGenerator {
    public static final List<String> LAYERS = Lists.newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

    public JsonUnbakedModel create(Function<Identifier, Sprite> function, JsonUnbakedModel jsonUnbakedModel) {
        String string;
        HashMap<String, String> map = Maps.newHashMap();
        ArrayList<ModelElement> list = Lists.newArrayList();
        for (int i = 0; i < LAYERS.size() && jsonUnbakedModel.textureExists(string = LAYERS.get(i)); ++i) {
            String string2 = jsonUnbakedModel.resolveTexture(string);
            map.put(string, string2);
            Sprite sprite = function.apply(new Identifier(string2));
            list.addAll(this.method_3480(i, string, sprite));
        }
        map.put("particle", jsonUnbakedModel.textureExists("particle") ? jsonUnbakedModel.resolveTexture("particle") : (String)map.get("layer0"));
        JsonUnbakedModel jsonUnbakedModel2 = new JsonUnbakedModel(null, list, map, false, false, jsonUnbakedModel.getTransformations(), jsonUnbakedModel.getOverrides());
        jsonUnbakedModel2.id = jsonUnbakedModel.id;
        return jsonUnbakedModel2;
    }

    private List<ModelElement> method_3480(int i, String string, Sprite sprite) {
        HashMap<Direction, ModelElementFace> map = Maps.newHashMap();
        map.put(Direction.SOUTH, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{0.0f, 0.0f, 16.0f, 16.0f}, 0)));
        map.put(Direction.NORTH, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{16.0f, 0.0f, 0.0f, 16.0f}, 0)));
        ArrayList<ModelElement> list = Lists.newArrayList();
        list.add(new ModelElement(new Vector3f(0.0f, 0.0f, 7.5f), new Vector3f(16.0f, 16.0f, 8.5f), map, null, true));
        list.addAll(this.method_3481(sprite, string, i));
        return list;
    }

    private List<ModelElement> method_3481(Sprite sprite, String string, int i) {
        float f = sprite.getWidth();
        float g = sprite.getHeight();
        ArrayList<ModelElement> list = Lists.newArrayList();
        for (class_802 lv : this.method_3478(sprite)) {
            float h = 0.0f;
            float j = 0.0f;
            float k = 0.0f;
            float l = 0.0f;
            float m = 0.0f;
            float n = 0.0f;
            float o = 0.0f;
            float p = 0.0f;
            float q = 16.0f / f;
            float r = 16.0f / g;
            float s = lv.method_3487();
            float t = lv.method_3485();
            float u = lv.method_3486();
            class_803 lv2 = lv.method_3484();
            switch (lv2) {
                case UP: {
                    h = m = s;
                    k = n = t + 1.0f;
                    j = o = u;
                    l = u;
                    p = u + 1.0f;
                    break;
                }
                case DOWN: {
                    o = u;
                    p = u + 1.0f;
                    h = m = s;
                    k = n = t + 1.0f;
                    j = u + 1.0f;
                    l = u + 1.0f;
                    break;
                }
                case LEFT: {
                    h = m = u;
                    k = u;
                    n = u + 1.0f;
                    j = p = s;
                    l = o = t + 1.0f;
                    break;
                }
                case RIGHT: {
                    m = u;
                    n = u + 1.0f;
                    h = u + 1.0f;
                    k = u + 1.0f;
                    j = p = s;
                    l = o = t + 1.0f;
                }
            }
            h *= q;
            k *= q;
            j *= r;
            l *= r;
            j = 16.0f - j;
            l = 16.0f - l;
            HashMap<Direction, ModelElementFace> map = Maps.newHashMap();
            map.put(lv2.method_3488(), new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{m *= q, o *= r, n *= q, p *= r}, 0)));
            switch (lv2) {
                case UP: {
                    list.add(new ModelElement(new Vector3f(h, j, 7.5f), new Vector3f(k, j, 8.5f), map, null, true));
                    break;
                }
                case DOWN: {
                    list.add(new ModelElement(new Vector3f(h, l, 7.5f), new Vector3f(k, l, 8.5f), map, null, true));
                    break;
                }
                case LEFT: {
                    list.add(new ModelElement(new Vector3f(h, j, 7.5f), new Vector3f(h, l, 8.5f), map, null, true));
                    break;
                }
                case RIGHT: {
                    list.add(new ModelElement(new Vector3f(k, j, 7.5f), new Vector3f(k, l, 8.5f), map, null, true));
                }
            }
        }
        return list;
    }

    private List<class_802> method_3478(Sprite sprite) {
        int i = sprite.getWidth();
        int j = sprite.getHeight();
        ArrayList<class_802> list = Lists.newArrayList();
        for (int k = 0; k < sprite.getFrameCount(); ++k) {
            for (int l = 0; l < j; ++l) {
                for (int m = 0; m < i; ++m) {
                    boolean bl = !this.method_3477(sprite, k, m, l, i, j);
                    this.method_3476(class_803.UP, list, sprite, k, m, l, i, j, bl);
                    this.method_3476(class_803.DOWN, list, sprite, k, m, l, i, j, bl);
                    this.method_3476(class_803.LEFT, list, sprite, k, m, l, i, j, bl);
                    this.method_3476(class_803.RIGHT, list, sprite, k, m, l, i, j, bl);
                }
            }
        }
        return list;
    }

    private void method_3476(class_803 arg, List<class_802> list, Sprite sprite, int i, int j, int k, int l, int m, boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = this.method_3477(sprite, i, j + arg.method_3490(), k + arg.method_3489(), l, m) && bl;
        if (bl2) {
            this.method_3482(list, arg, j, k);
        }
    }

    private void method_3482(List<class_802> list, class_803 arg, int i, int j) {
        int m;
        class_802 lv = null;
        for (class_802 lv2 : list) {
            int k;
            if (lv2.method_3484() != arg) continue;
            int n = k = arg.method_3491() ? j : i;
            if (lv2.method_3486() != k) continue;
            lv = lv2;
            break;
        }
        int l = arg.method_3491() ? j : i;
        int n = m = arg.method_3491() ? i : j;
        if (lv == null) {
            list.add(new class_802(arg, m, l));
        } else {
            lv.method_3483(m);
        }
    }

    private boolean method_3477(Sprite sprite, int i, int j, int k, int l, int m) {
        if (j < 0 || k < 0 || j >= l || k >= m) {
            return true;
        }
        return sprite.isPixelTransparent(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    static class class_802 {
        private final class_803 field_4271;
        private int field_4274;
        private int field_4273;
        private final int field_4272;

        public class_802(class_803 arg, int i, int j) {
            this.field_4271 = arg;
            this.field_4274 = i;
            this.field_4273 = i;
            this.field_4272 = j;
        }

        public void method_3483(int i) {
            if (i < this.field_4274) {
                this.field_4274 = i;
            } else if (i > this.field_4273) {
                this.field_4273 = i;
            }
        }

        public class_803 method_3484() {
            return this.field_4271;
        }

        public int method_3487() {
            return this.field_4274;
        }

        public int method_3485() {
            return this.field_4273;
        }

        public int method_3486() {
            return this.field_4272;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum class_803 {
        UP(Direction.UP, 0, -1),
        DOWN(Direction.DOWN, 0, 1),
        LEFT(Direction.EAST, -1, 0),
        RIGHT(Direction.WEST, 1, 0);

        private final Direction field_4276;
        private final int field_4280;
        private final int field_4279;

        private class_803(Direction direction, int j, int k) {
            this.field_4276 = direction;
            this.field_4280 = j;
            this.field_4279 = k;
        }

        public Direction method_3488() {
            return this.field_4276;
        }

        public int method_3490() {
            return this.field_4280;
        }

        public int method_3489() {
            return this.field_4279;
        }

        private boolean method_3491() {
            return this == DOWN || this == UP;
        }
    }
}

