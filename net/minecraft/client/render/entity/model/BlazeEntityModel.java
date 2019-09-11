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
public class BlazeEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart[] rods = new ModelPart[12];
    private final ModelPart head;

    public BlazeEntityModel() {
        for (int i = 0; i < this.rods.length; ++i) {
            this.rods[i] = new ModelPart(this, 0, 16);
            this.rods[i].addCuboid(0.0f, 0.0f, 0.0f, 2, 8, 2);
        }
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -4.0f, -4.0f, 8, 8, 8);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.head.render(k);
        for (ModelPart modelPart : this.rods) {
            modelPart.render(k);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        int m;
        float l = h * (float)Math.PI * -0.1f;
        for (m = 0; m < 4; ++m) {
            this.rods[m].rotationPointY = -2.0f + MathHelper.cos(((float)(m * 2) + h) * 0.25f);
            this.rods[m].rotationPointX = MathHelper.cos(l) * 9.0f;
            this.rods[m].rotationPointZ = MathHelper.sin(l) * 9.0f;
            l += 1.5707964f;
        }
        l = 0.7853982f + h * (float)Math.PI * 0.03f;
        for (m = 4; m < 8; ++m) {
            this.rods[m].rotationPointY = 2.0f + MathHelper.cos(((float)(m * 2) + h) * 0.25f);
            this.rods[m].rotationPointX = MathHelper.cos(l) * 7.0f;
            this.rods[m].rotationPointZ = MathHelper.sin(l) * 7.0f;
            l += 1.5707964f;
        }
        l = 0.47123894f + h * (float)Math.PI * -0.05f;
        for (m = 8; m < 12; ++m) {
            this.rods[m].rotationPointY = 11.0f + MathHelper.cos(((float)m * 1.5f + h) * 0.5f);
            this.rods[m].rotationPointX = MathHelper.cos(l) * 5.0f;
            this.rods[m].rotationPointZ = MathHelper.sin(l) * 5.0f;
            l += 1.5707964f;
        }
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
    }
}

