/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity>
extends HorseEntityModel<T> {
    private final ModelPart field_3349 = new ModelPart(this, 26, 21);
    private final ModelPart field_3348;

    public DonkeyEntityModel(float f) {
        super(f);
        this.field_3349.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.field_3348 = new ModelPart(this, 26, 21);
        this.field_3348.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.field_3349.yaw = -1.5707964f;
        this.field_3348.yaw = 1.5707964f;
        this.field_3349.setPivot(6.0f, -8.0f, 0.0f);
        this.field_3348.setPivot(-6.0f, -8.0f, 0.0f);
        this.field_3305.addChild(this.field_3349);
        this.field_3305.addChild(this.field_3348);
    }

    @Override
    protected void method_2789(ModelPart modelPart) {
        ModelPart modelPart2 = new ModelPart(this, 0, 12);
        modelPart2.addCuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPart2.setPivot(1.25f, -10.0f, 4.0f);
        ModelPart modelPart3 = new ModelPart(this, 0, 12);
        modelPart3.addCuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPart3.setPivot(-1.25f, -10.0f, 4.0f);
        modelPart2.pitch = 0.2617994f;
        modelPart2.roll = 0.2617994f;
        modelPart3.pitch = 0.2617994f;
        modelPart3.roll = -0.2617994f;
        modelPart.addChild(modelPart2);
        modelPart.addChild(modelPart3);
    }

    public void method_17076(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
        super.method_17085(abstractDonkeyEntity, f, g, h, i, j);
        if (((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest()) {
            this.field_3349.visible = true;
            this.field_3348.visible = true;
        } else {
            this.field_3349.visible = false;
            this.field_3348.visible = false;
        }
    }
}

