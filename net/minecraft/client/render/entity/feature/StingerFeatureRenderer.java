/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StickingOutThingsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StingerFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends StickingOutThingsFeatureRenderer<T, M> {
    private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

    public StingerFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    @Override
    protected int getThingCount(T livingEntity) {
        return ((LivingEntity)livingEntity).getStingerCount();
    }

    @Override
    protected void renderThing(class_4587 arg, class_4597 arg2, Entity entity, float f, float g, float h, float i) {
        float j = MathHelper.sqrt(f * f + h * h);
        float k = (float)(Math.atan2(f, h) * 57.2957763671875);
        float l = (float)(Math.atan2(g, j) * 57.2957763671875);
        arg.method_22904(0.0, 0.0, 0.0);
        arg.method_22907(Vector3f.field_20705.method_23214(k - 90.0f, true));
        arg.method_22907(Vector3f.field_20707.method_23214(l, true));
        float m = 0.0f;
        float n = 0.125f;
        float o = 0.0f;
        float p = 0.0625f;
        float q = 0.03125f;
        arg.method_22907(Vector3f.field_20703.method_23214(45.0f, true));
        arg.method_22905(0.03125f, 0.03125f, 0.03125f);
        arg.method_22904(2.5, 0.0, 0.0);
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(field_20529));
        class_4608.method_23211(lv);
        for (int r = 0; r < 4; ++r) {
            arg.method_22907(Vector3f.field_20703.method_23214(90.0f, true));
            Matrix4f matrix4f = arg.method_22910();
            lv.method_22918(matrix4f, -4.5f, -1.0f, 0.0f).texture(0.0f, 0.0f).next();
            lv.method_22918(matrix4f, 4.5f, -1.0f, 0.0f).texture(0.125f, 0.0f).next();
            lv.method_22918(matrix4f, 4.5f, 1.0f, 0.0f).texture(0.125f, 0.0625f).next();
            lv.method_22918(matrix4f, -4.5f, 1.0f, 0.0f).texture(0.0f, 0.0625f).next();
        }
        lv.method_22923();
    }
}

