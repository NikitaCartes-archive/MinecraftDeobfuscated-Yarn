/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity>
extends HorseEntityModel<T> {
    private final Cuboid field_3349 = new Cuboid(this, 26, 21);
    private final Cuboid field_3348;

    public DonkeyEntityModel(float f) {
        super(f);
        this.field_3349.addBox(-4.0f, 0.0f, -2.0f, 8, 8, 3);
        this.field_3348 = new Cuboid(this, 26, 21);
        this.field_3348.addBox(-4.0f, 0.0f, -2.0f, 8, 8, 3);
        this.field_3349.yaw = -1.5707964f;
        this.field_3348.yaw = 1.5707964f;
        this.field_3349.setRotationPoint(6.0f, -8.0f, 0.0f);
        this.field_3348.setRotationPoint(-6.0f, -8.0f, 0.0f);
        this.field_3305.addChild(this.field_3349);
        this.field_3305.addChild(this.field_3348);
    }

    @Override
    protected void method_2789(Cuboid cuboid) {
        Cuboid cuboid2 = new Cuboid(this, 0, 12);
        cuboid2.addBox(-1.0f, -7.0f, 0.0f, 2, 7, 1);
        cuboid2.setRotationPoint(1.25f, -10.0f, 4.0f);
        Cuboid cuboid3 = new Cuboid(this, 0, 12);
        cuboid3.addBox(-1.0f, -7.0f, 0.0f, 2, 7, 1);
        cuboid3.setRotationPoint(-1.25f, -10.0f, 4.0f);
        cuboid2.pitch = 0.2617994f;
        cuboid2.roll = 0.2617994f;
        cuboid3.pitch = 0.2617994f;
        cuboid3.roll = -0.2617994f;
        cuboid.addChild(cuboid2);
        cuboid.addChild(cuboid3);
    }

    public void method_17076(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
        if (((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest()) {
            this.field_3349.visible = true;
            this.field_3348.visible = true;
        } else {
            this.field_3349.visible = false;
            this.field_3348.visible = false;
        }
        super.method_17085(abstractDonkeyEntity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17076((AbstractDonkeyEntity)entity, f, g, h, i, j, k);
    }
}

