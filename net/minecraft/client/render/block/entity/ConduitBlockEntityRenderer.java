/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.class_4576;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderer
extends class_4576<ConduitBlockEntity> {
    public static final Identifier BASE_TEX = new Identifier("entity/conduit/base");
    public static final Identifier CAGE_TEX = new Identifier("entity/conduit/cage");
    public static final Identifier WIND_TEX = new Identifier("entity/conduit/wind");
    public static final Identifier WIND_VERTICAL_TEX = new Identifier("entity/conduit/wind_vertical");
    public static final Identifier OPEN_EYE_TEX = new Identifier("entity/conduit/open_eye");
    public static final Identifier CLOSED_EYE_TEX = new Identifier("entity/conduit/closed_eye");
    private final ModelPart field_20823 = new ModelPart(8, 8, 0, 0);
    private final ModelPart field_20824;
    private final ModelPart field_20825;
    private final ModelPart field_20826;

    public ConduitBlockEntityRenderer() {
        this.field_20823.addCuboid(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, 0.01f);
        this.field_20824 = new ModelPart(64, 32, 0, 0);
        this.field_20824.addCuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        this.field_20825 = new ModelPart(32, 16, 0, 0);
        this.field_20825.addCuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.field_20826 = new ModelPart(32, 16, 0, 0);
        this.field_20826.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
    }

    protected void method_22750(ConduitBlockEntity conduitBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k) {
        float h = (float)conduitBlockEntity.ticks + g;
        if (!conduitBlockEntity.isActive()) {
            float l = conduitBlockEntity.getRotation(0.0f);
            bufferBuilder.method_22629();
            bufferBuilder.method_22626(0.5, 0.5, 0.5);
            bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, l, true));
            this.field_20825.method_22698(bufferBuilder, 0.0625f, j, k, this.method_22739(BASE_TEX));
            bufferBuilder.method_22630();
            return;
        }
        float l = conduitBlockEntity.getRotation(g) * 57.295776f;
        float m = MathHelper.sin(h * 0.1f) / 2.0f + 0.5f;
        m = m * m + m;
        bufferBuilder.method_22629();
        bufferBuilder.method_22626(0.5, 0.3f + m * 0.2f, 0.5);
        Vector3f vector3f = new Vector3f(0.5f, 1.0f, 0.5f);
        vector3f.reciprocal();
        bufferBuilder.method_22622(new Quaternion(vector3f, l, true));
        this.field_20826.method_22698(bufferBuilder, 0.0625f, j, k, this.method_22739(CAGE_TEX));
        bufferBuilder.method_22630();
        int n = conduitBlockEntity.ticks / 66 % 3;
        bufferBuilder.method_22629();
        bufferBuilder.method_22626(0.5, 0.5, 0.5);
        if (n == 1) {
            bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 90.0f, true));
        } else if (n == 2) {
            bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 90.0f, true));
        }
        this.field_20824.method_22698(bufferBuilder, 0.0625f, j, k, this.method_22739(n == 1 ? WIND_VERTICAL_TEX : WIND_TEX));
        bufferBuilder.method_22630();
        bufferBuilder.method_22629();
        bufferBuilder.method_22626(0.5, 0.5, 0.5);
        bufferBuilder.method_22627(0.875f, 0.875f, 0.875f);
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 180.0f, true));
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 180.0f, true));
        this.field_20824.render(0.0625f);
        bufferBuilder.method_22630();
        Camera camera = this.renderManager.cameraEntity;
        bufferBuilder.method_22629();
        bufferBuilder.method_22626(0.5, 0.3f + m * 0.2f, 0.5);
        bufferBuilder.method_22627(0.5f, 0.5f, 0.5f);
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -camera.getYaw(), true));
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, camera.getPitch(), true));
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 180.0f, true));
        this.field_20823.method_22698(bufferBuilder, 0.083333336f, k, j, this.method_22739(conduitBlockEntity.isEyeOpen() ? OPEN_EYE_TEX : CLOSED_EYE_TEX));
        bufferBuilder.method_22630();
    }
}

