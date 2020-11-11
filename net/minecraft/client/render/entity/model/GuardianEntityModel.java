/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityModel
extends SinglePartEntityModel<GuardianEntity> {
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
        this.body = modelPart.getChild("head");
        for (int i = 0; i < this.field_3380.length; ++i) {
            this.field_3380[i] = this.body.getChild(GuardianEntityModel.method_32003(i));
        }
        this.eye = this.body.getChild("eye");
        this.field_3378 = new ModelPart[3];
        this.field_3378[0] = this.body.getChild("tail0");
        this.field_3378[1] = this.field_3378[0].getChild("tail1");
        this.field_3378[2] = this.field_3378[1].getChild("tail2");
    }

    private static String method_32003(int i) {
        return "spike" + i;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, 10.0f, -8.0f, 12.0f, 12.0f, 16.0f).uv(0, 28).cuboid(-8.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f).uv(0, 28).cuboid(6.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f, true).uv(16, 40).cuboid(-6.0f, 8.0f, -6.0f, 12.0f, 2.0f, 12.0f).uv(16, 40).cuboid(-6.0f, 22.0f, -6.0f, 12.0f, 2.0f, 12.0f), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -4.5f, -1.0f, 2.0f, 9.0f, 2.0f);
        for (int i = 0; i < 12; ++i) {
            float f = GuardianEntityModel.method_32005(i, 0.0f, 0.0f);
            float g = GuardianEntityModel.method_32006(i, 0.0f, 0.0f);
            float h = GuardianEntityModel.method_32007(i, 0.0f, 0.0f);
            float j = (float)Math.PI * field_17131[i];
            float k = (float)Math.PI * field_17132[i];
            float l = (float)Math.PI * field_17133[i];
            modelPartData2.addChild(GuardianEntityModel.method_32003(i), modelPartBuilder, ModelTransform.of(f, g, h, j, k, l));
        }
        modelPartData2.addChild("eye", ModelPartBuilder.create().uv(8, 0).cuboid(-1.0f, 15.0f, 0.0f, 2.0f, 2.0f, 1.0f), ModelTransform.pivot(0.0f, 0.0f, -8.25f));
        ModelPartData modelPartData3 = modelPartData2.addChild("tail0", ModelPartBuilder.create().uv(40, 0).cuboid(-2.0f, 14.0f, 7.0f, 4.0f, 4.0f, 8.0f), ModelTransform.NONE);
        ModelPartData modelPartData4 = modelPartData3.addChild("tail1", ModelPartBuilder.create().uv(0, 54).cuboid(0.0f, 14.0f, 0.0f, 3.0f, 3.0f, 7.0f), ModelTransform.pivot(-1.5f, 0.5f, 14.0f));
        modelPartData4.addChild("tail2", ModelPartBuilder.create().uv(41, 32).cuboid(0.0f, 14.0f, 0.0f, 2.0f, 2.0f, 6.0f).uv(25, 19).cuboid(1.0f, 10.5f, 3.0f, 1.0f, 9.0f, 9.0f), ModelTransform.pivot(0.5f, 0.5f, 6.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
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

