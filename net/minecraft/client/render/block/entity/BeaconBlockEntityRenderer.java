/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderer
extends BlockEntityRenderer<BeaconBlockEntity> {
    public static final Identifier BEAM_TEX = new Identifier("textures/entity/beacon_beam.png");

    public BeaconBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_3541(BeaconBlockEntity beaconBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
        long l = beaconBlockEntity.getWorld().getTime();
        List<BeaconBlockEntity.BeamSegment> list = beaconBlockEntity.getBeamSegments();
        int j = 0;
        for (int k = 0; k < list.size(); ++k) {
            BeaconBlockEntity.BeamSegment beamSegment = list.get(k);
            BeaconBlockEntityRenderer.render(arg, arg2, g, l, j, k == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
            j += beamSegment.getHeight();
        }
    }

    private static void render(class_4587 arg, class_4597 arg2, float f, long l, int i, int j, float[] fs) {
        BeaconBlockEntityRenderer.renderLightBeam(arg, arg2, BEAM_TEX, f, 1.0f, l, i, j, fs, 0.2f, 0.25f);
    }

    public static void renderLightBeam(class_4587 arg, class_4597 arg2, Identifier identifier, float f, float g, long l, int i, int j, float[] fs, float h, float k) {
        int m = i + j;
        arg.method_22903();
        arg.method_22904(0.5, 0.0, 0.5);
        float n = (float)Math.floorMod(l, 40L) + f;
        float o = j < 0 ? n : -n;
        float p = MathHelper.method_22450(o * 0.2f - (float)MathHelper.floor(o * 0.1f));
        float q = fs[0];
        float r = fs[1];
        float s = fs[2];
        arg.method_22903();
        arg.method_22907(Vector3f.field_20705.method_23214(n * 2.25f - 45.0f, true));
        float t = 0.0f;
        float u = h;
        float v = h;
        float w = 0.0f;
        float x = -h;
        float y = 0.0f;
        float z = 0.0f;
        float aa = -h;
        float ab = 0.0f;
        float ac = 1.0f;
        float ad = -1.0f + p;
        float ae = (float)j * g * (0.5f / h) + ad;
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(identifier));
        class_4608.method_23211(lv);
        BeaconBlockEntityRenderer.method_22741(arg, lv, q, r, s, 1.0f, i, m, 0.0f, u, v, 0.0f, x, 0.0f, 0.0f, aa, 0.0f, 1.0f, ae, ad);
        lv.method_22923();
        arg.method_22909();
        t = -k;
        u = -k;
        v = k;
        w = -k;
        x = -k;
        y = k;
        z = k;
        aa = k;
        ab = 0.0f;
        ac = 1.0f;
        ad = -1.0f + p;
        ae = (float)j * g + ad;
        BeaconBlockEntityRenderer.method_22741(arg, arg2.getBuffer(BlockRenderLayer.BEACON_BEAM), q, r, s, 0.125f, i, m, t, u, v, w, x, y, z, aa, 0.0f, 1.0f, ae, ad);
        arg.method_22909();
    }

    private static void method_22741(class_4587 arg, class_4588 arg2, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u, float v, float w) {
        Matrix4f matrix4f = arg.method_22910();
        BeaconBlockEntityRenderer.method_22740(matrix4f, arg2, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, arg2, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, arg2, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, arg2, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
    }

    private static void method_22740(Matrix4f matrix4f, class_4588 arg, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s) {
        BeaconBlockEntityRenderer.method_23076(matrix4f, arg, f, g, h, i, k, l, m, q, r);
        BeaconBlockEntityRenderer.method_23076(matrix4f, arg, f, g, h, i, j, l, m, q, s);
        BeaconBlockEntityRenderer.method_23076(matrix4f, arg, f, g, h, i, j, n, o, p, s);
        BeaconBlockEntityRenderer.method_23076(matrix4f, arg, f, g, h, i, k, n, o, p, r);
    }

    private static void method_23076(Matrix4f matrix4f, class_4588 arg, float f, float g, float h, float i, int j, float k, float l, float m, float n) {
        arg.method_22918(matrix4f, k, j, l).method_22915(f, g, h, i).texture(m, n).method_22916(0xF000F0).method_22914(0.0f, 1.0f, 0.0f).next();
    }

    public boolean method_3542(BeaconBlockEntity beaconBlockEntity) {
        return true;
    }
}

