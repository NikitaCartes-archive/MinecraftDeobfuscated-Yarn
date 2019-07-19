/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3374 = new ModelPart(this, 0, 0);
    private final ModelPart field_3376;
    private final ModelPart field_3375;

    public EvokerFangsEntityModel() {
        this.field_3374.setPivot(-5.0f, 22.0f, -5.0f);
        this.field_3374.addCuboid(0.0f, 0.0f, 0.0f, 10, 12, 10);
        this.field_3376 = new ModelPart(this, 40, 0);
        this.field_3376.setPivot(1.5f, 22.0f, -4.0f);
        this.field_3376.addCuboid(0.0f, 0.0f, 0.0f, 4, 14, 8);
        this.field_3375 = new ModelPart(this, 40, 0);
        this.field_3375.setPivot(-1.5f, 22.0f, 4.0f);
        this.field_3375.addCuboid(0.0f, 0.0f, 0.0f, 4, 14, 8);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        float l = f * 2.0f;
        if (l > 1.0f) {
            l = 1.0f;
        }
        l = 1.0f - l * l * l;
        this.field_3376.roll = (float)Math.PI - l * 0.35f * (float)Math.PI;
        this.field_3375.roll = (float)Math.PI + l * 0.35f * (float)Math.PI;
        this.field_3375.yaw = (float)Math.PI;
        float m = (f + MathHelper.sin(f * 2.7f)) * 0.6f * 12.0f;
        this.field_3375.pivotY = this.field_3376.pivotY = 24.0f - m;
        this.field_3374.pivotY = this.field_3376.pivotY;
        this.field_3374.render(k);
        this.field_3376.render(k);
        this.field_3375.render(k);
    }
}

