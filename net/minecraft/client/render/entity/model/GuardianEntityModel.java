/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityModel
extends class_5597<GuardianEntity> {
    private static final float[] field_17131 = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
    private static final float[] field_17132 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] field_17133 = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
    private static final float[] field_17134 = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
    private static final float[] field_17135 = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
    private static final float[] field_17136 = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
    private final ModelPart field_27420;
    private final ModelPart body;
    private final ModelPart eye;
    private final ModelPart[] field_3380;
    private final ModelPart[] field_3378;

    public GuardianEntityModel(ModelPart modelPart) {
        this.field_27420 = modelPart;
        this.field_3380 = new ModelPart[12];
        this.body = modelPart.method_32086("head");
        for (int i = 0; i < this.field_3380.length; ++i) {
            this.field_3380[i] = this.body.method_32086(GuardianEntityModel.method_32003(i));
        }
        this.eye = this.body.method_32086("eye");
        this.field_3378 = new ModelPart[3];
        this.field_3378[0] = this.body.method_32086("tail0");
        this.field_3378[1] = this.field_3378[0].method_32086("tail1");
        this.field_3378[2] = this.field_3378[1].method_32086("tail2");
    }

    private static String method_32003(int i) {
        return "spike" + i;
    }

    public static class_5607 method_32002() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-6.0f, 10.0f, -8.0f, 12.0f, 12.0f, 16.0f).method_32101(0, 28).method_32097(-8.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f).method_32101(0, 28).method_32100(6.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f, true).method_32101(16, 40).method_32097(-6.0f, 8.0f, -6.0f, 12.0f, 2.0f, 12.0f).method_32101(16, 40).method_32097(-6.0f, 22.0f, -6.0f, 12.0f, 2.0f, 12.0f), class_5603.field_27701);
        class_5606 lv4 = class_5606.method_32108().method_32101(0, 0).method_32097(-1.0f, -4.5f, -1.0f, 2.0f, 9.0f, 2.0f);
        for (int i = 0; i < 12; ++i) {
            float f = GuardianEntityModel.method_32005(i, 0.0f, 0.0f);
            float g = GuardianEntityModel.method_32006(i, 0.0f, 0.0f);
            float h = GuardianEntityModel.method_32007(i, 0.0f, 0.0f);
            float j = (float)Math.PI * field_17131[i];
            float k = (float)Math.PI * field_17132[i];
            float l = (float)Math.PI * field_17133[i];
            lv3.method_32117(GuardianEntityModel.method_32003(i), lv4, class_5603.method_32091(f, g, h, j, k, l));
        }
        lv3.method_32117("eye", class_5606.method_32108().method_32101(8, 0).method_32097(-1.0f, 15.0f, 0.0f, 2.0f, 2.0f, 1.0f), class_5603.method_32090(0.0f, 0.0f, -8.25f));
        class_5610 lv5 = lv3.method_32117("tail0", class_5606.method_32108().method_32101(40, 0).method_32097(-2.0f, 14.0f, 7.0f, 4.0f, 4.0f, 8.0f), class_5603.field_27701);
        class_5610 lv6 = lv5.method_32117("tail1", class_5606.method_32108().method_32101(0, 54).method_32097(0.0f, 14.0f, 0.0f, 3.0f, 3.0f, 7.0f), class_5603.method_32090(-1.5f, 0.5f, 14.0f));
        lv6.method_32117("tail2", class_5606.method_32108().method_32101(41, 32).method_32097(0.0f, 14.0f, 0.0f, 2.0f, 2.0f, 6.0f).method_32101(25, 19).method_32097(1.0f, 10.5f, 3.0f, 1.0f, 9.0f, 9.0f), class_5603.method_32090(0.5f, 0.5f, 6.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27420;
    }

    @Override
    public void setAngles(GuardianEntity guardianEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)guardianEntity.age;
        this.body.yaw = i * ((float)Math.PI / 180);
        this.body.pitch = j * ((float)Math.PI / 180);
        float l = (1.0f - guardianEntity.getTailAngle(k)) * 0.55f;
        this.method_24185(h, l);
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (guardianEntity.hasBeamTarget()) {
            entity = guardianEntity.getBeamTarget();
        }
        if (entity != null) {
            Vec3d vec3d = entity.getCameraPosVec(0.0f);
            Vec3d vec3d2 = guardianEntity.getCameraPosVec(0.0f);
            double d = vec3d.y - vec3d2.y;
            this.eye.pivotY = d > 0.0 ? 0.0f : 1.0f;
            Vec3d vec3d3 = guardianEntity.getRotationVec(0.0f);
            vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
            Vec3d vec3d4 = new Vec3d(vec3d2.x - vec3d.x, 0.0, vec3d2.z - vec3d.z).normalize().rotateY(1.5707964f);
            double e = vec3d3.dotProduct(vec3d4);
            this.eye.pivotX = MathHelper.sqrt((float)Math.abs(e)) * 2.0f * (float)Math.signum(e);
        }
        this.eye.visible = true;
        float m = guardianEntity.getSpikesExtension(k);
        this.field_3378[0].yaw = MathHelper.sin(m) * (float)Math.PI * 0.05f;
        this.field_3378[1].yaw = MathHelper.sin(m) * (float)Math.PI * 0.1f;
        this.field_3378[2].yaw = MathHelper.sin(m) * (float)Math.PI * 0.15f;
    }

    private void method_24185(float f, float g) {
        for (int i = 0; i < 12; ++i) {
            this.field_3380[i].pivotX = GuardianEntityModel.method_32005(i, f, g);
            this.field_3380[i].pivotY = GuardianEntityModel.method_32006(i, f, g);
            this.field_3380[i].pivotZ = GuardianEntityModel.method_32007(i, f, g);
        }
    }

    private static float method_32004(int i, float f, float g) {
        return 1.0f + MathHelper.cos(f * 1.5f + (float)i) * 0.01f - g;
    }

    private static float method_32005(int i, float f, float g) {
        return field_17134[i] * GuardianEntityModel.method_32004(i, f, g);
    }

    private static float method_32006(int i, float f, float g) {
        return 16.0f + field_17135[i] * GuardianEntityModel.method_32004(i, f, g);
    }

    private static float method_32007(int i, float f, float g) {
        return field_17136[i] * GuardianEntityModel.method_32004(i, f, g);
    }
}

