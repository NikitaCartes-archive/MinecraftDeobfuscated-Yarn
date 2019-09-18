/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class GhastEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3373;
    private final ModelPart[] field_3372 = new ModelPart[9];

    public GhastEntityModel() {
        int i = -16;
        this.field_3373 = new ModelPart(this, 0, 0);
        this.field_3373.addCuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        this.field_3373.rotationPointY += 8.0f;
        Random random = new Random(1660L);
        for (int j = 0; j < this.field_3372.length; ++j) {
            this.field_3372[j] = new ModelPart(this, 0, 0);
            float f = (((float)(j % 3) - (float)(j / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float g = ((float)(j / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int k = random.nextInt(7) + 8;
            this.field_3372[j].addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, k, 2.0f);
            this.field_3372[j].rotationPointX = f;
            this.field_3372[j].rotationPointZ = g;
            this.field_3372[j].rotationPointY = 15.0f;
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        for (int l = 0; l < this.field_3372.length; ++l) {
            this.field_3372[l].pitch = 0.2f * MathHelper.sin(h * 0.3f + (float)l) + 0.4f;
        }
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.6f, 0.0f);
        this.field_3373.render(k);
        for (ModelPart modelPart : this.field_3372) {
            modelPart.render(k);
        }
        RenderSystem.popMatrix();
    }
}

