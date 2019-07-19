/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity>
extends EntityModel<T> {
    private final ModelPart field_3553;
    private final ModelPart field_3555;
    private final ModelPart field_3554;

    public ShulkerEntityModel() {
        this.textureHeight = 64;
        this.textureWidth = 64;
        this.field_3555 = new ModelPart(this);
        this.field_3553 = new ModelPart(this);
        this.field_3554 = new ModelPart(this);
        this.field_3555.setTextureOffset(0, 0).addCuboid(-8.0f, -16.0f, -8.0f, 16, 12, 16);
        this.field_3555.setPivot(0.0f, 24.0f, 0.0f);
        this.field_3553.setTextureOffset(0, 28).addCuboid(-8.0f, -8.0f, -8.0f, 16, 8, 16);
        this.field_3553.setPivot(0.0f, 24.0f, 0.0f);
        this.field_3554.setTextureOffset(0, 52).addCuboid(-3.0f, 0.0f, -3.0f, 6, 6, 6);
        this.field_3554.setPivot(0.0f, 12.0f, 0.0f);
    }

    @Override
    public void setAngles(T shulkerEntity, float f, float g, float h, float i, float j, float k) {
        float l = h - (float)((ShulkerEntity)shulkerEntity).age;
        float m = (0.5f + ((ShulkerEntity)shulkerEntity).method_7116(l)) * (float)Math.PI;
        float n = -1.0f + MathHelper.sin(m);
        float o = 0.0f;
        if (m > (float)Math.PI) {
            o = MathHelper.sin(h * 0.1f) * 0.7f;
        }
        this.field_3555.setPivot(0.0f, 16.0f + MathHelper.sin(m) * 8.0f + o, 0.0f);
        this.field_3555.yaw = ((ShulkerEntity)shulkerEntity).method_7116(l) > 0.3f ? n * n * n * n * (float)Math.PI * 0.125f : 0.0f;
        this.field_3554.pitch = j * ((float)Math.PI / 180);
        this.field_3554.yaw = i * ((float)Math.PI / 180);
    }

    @Override
    public void render(T shulkerEntity, float f, float g, float h, float i, float j, float k) {
        this.field_3553.render(k);
        this.field_3555.render(k);
    }

    public ModelPart method_2831() {
        return this.field_3553;
    }

    public ModelPart method_2829() {
        return this.field_3555;
    }

    public ModelPart method_2830() {
        return this.field_3554;
    }
}

