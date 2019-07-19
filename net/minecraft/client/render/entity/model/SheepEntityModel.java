/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Environment(value=EnvType.CLIENT)
public class SheepEntityModel<T extends SheepEntity>
extends QuadrupedEntityModel<T> {
    private float field_3552;

    public SheepEntityModel() {
        super(12, 0.0f);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-3.0f, -4.0f, -6.0f, 6, 6, 8, 0.0f);
        this.head.setPivot(0.0f, 6.0f, -8.0f);
        this.torso = new ModelPart(this, 28, 8);
        this.torso.addCuboid(-4.0f, -10.0f, -7.0f, 8, 16, 6, 0.0f);
        this.torso.setPivot(0.0f, 5.0f, 2.0f);
    }

    @Override
    public void animateModel(T sheepEntity, float f, float g, float h) {
        super.animateModel(sheepEntity, f, g, h);
        this.head.pivotY = 6.0f + ((SheepEntity)sheepEntity).method_6628(h) * 9.0f;
        this.field_3552 = ((SheepEntity)sheepEntity).method_6641(h);
    }

    @Override
    public void setAngles(T sheepEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(sheepEntity, f, g, h, i, j, k);
        this.head.pitch = this.field_3552;
    }
}

