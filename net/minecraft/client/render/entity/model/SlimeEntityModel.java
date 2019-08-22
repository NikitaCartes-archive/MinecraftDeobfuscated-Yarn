/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3571;
    private final ModelPart field_3573;
    private final ModelPart field_3572;
    private final ModelPart field_3570;

    public SlimeEntityModel(int i) {
        if (i > 0) {
            this.field_3571 = new ModelPart(this, 0, i);
            this.field_3571.addCuboid(-3.0f, 17.0f, -3.0f, 6, 6, 6);
            this.field_3573 = new ModelPart(this, 32, 0);
            this.field_3573.addCuboid(-3.25f, 18.0f, -3.5f, 2, 2, 2);
            this.field_3572 = new ModelPart(this, 32, 4);
            this.field_3572.addCuboid(1.25f, 18.0f, -3.5f, 2, 2, 2);
            this.field_3570 = new ModelPart(this, 32, 8);
            this.field_3570.addCuboid(0.0f, 21.0f, -3.5f, 1, 1, 1);
        } else {
            this.field_3571 = new ModelPart(this, 0, i);
            this.field_3571.addCuboid(-4.0f, 16.0f, -4.0f, 8, 8, 8);
            this.field_3573 = null;
            this.field_3572 = null;
            this.field_3570 = null;
        }
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        RenderSystem.translatef(0.0f, 0.001f, 0.0f);
        this.field_3571.render(k);
        if (this.field_3573 != null) {
            this.field_3573.render(k);
            this.field_3572.render(k);
            this.field_3570.render(k);
        }
    }
}

