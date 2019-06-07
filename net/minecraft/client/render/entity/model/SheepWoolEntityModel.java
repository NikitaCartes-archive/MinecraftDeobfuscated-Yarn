/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Environment(value=EnvType.CLIENT)
public class SheepWoolEntityModel<T extends SheepEntity>
extends QuadrupedEntityModel<T> {
    private float field_3541;

    public SheepWoolEntityModel() {
        super(12, 0.0f);
        this.head = new Cuboid(this, 0, 0);
        this.head.addBox(-3.0f, -4.0f, -4.0f, 6, 6, 6, 0.6f);
        this.head.setRotationPoint(0.0f, 6.0f, -8.0f);
        this.body = new Cuboid(this, 28, 8);
        this.body.addBox(-4.0f, -10.0f, -7.0f, 8, 16, 6, 1.75f);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
        float f = 0.5f;
        this.leg1 = new Cuboid(this, 0, 16);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg1.setRotationPoint(-3.0f, 12.0f, 7.0f);
        this.leg2 = new Cuboid(this, 0, 16);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg2.setRotationPoint(3.0f, 12.0f, 7.0f);
        this.leg3 = new Cuboid(this, 0, 16);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg3.setRotationPoint(-3.0f, 12.0f, -5.0f);
        this.leg4 = new Cuboid(this, 0, 16);
        this.leg4.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg4.setRotationPoint(3.0f, 12.0f, -5.0f);
    }

    public void method_17118(T sheepEntity, float f, float g, float h) {
        super.animateModel(sheepEntity, f, g, h);
        this.head.rotationPointY = 6.0f + ((SheepEntity)sheepEntity).method_6628(h) * 9.0f;
        this.field_3541 = ((SheepEntity)sheepEntity).method_6641(h);
    }

    public void method_17119(T sheepEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(sheepEntity, f, g, h, i, j, k);
        this.head.pitch = this.field_3541;
    }
}

