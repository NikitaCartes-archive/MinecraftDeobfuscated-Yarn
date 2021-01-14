/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class CowEntityModel<T extends Entity>
extends QuadrupedEntityModel<T> {
    public CowEntityModel() {
        super(12, 0.0f, false, 10.0f, 4.0f, 2.0f, 2.0f, 24);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f, 0.0f);
        this.head.setPivot(0.0f, 4.0f, -8.0f);
        this.head.setTextureOffset(22, 0).addCuboid(-5.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f, 0.0f);
        this.head.setTextureOffset(22, 0).addCuboid(4.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f, 0.0f);
        this.body = new ModelPart(this, 18, 4);
        this.body.addCuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, 0.0f);
        this.body.setPivot(0.0f, 5.0f, 2.0f);
        this.body.setTextureOffset(52, 0).addCuboid(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f);
        this.backRightLeg.pivotX -= 1.0f;
        this.backLeftLeg.pivotX += 1.0f;
        this.backRightLeg.pivotZ += 0.0f;
        this.backLeftLeg.pivotZ += 0.0f;
        this.frontRightLeg.pivotX -= 1.0f;
        this.frontLeftLeg.pivotX += 1.0f;
        this.frontRightLeg.pivotZ -= 1.0f;
        this.frontLeftLeg.pivotZ -= 1.0f;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

