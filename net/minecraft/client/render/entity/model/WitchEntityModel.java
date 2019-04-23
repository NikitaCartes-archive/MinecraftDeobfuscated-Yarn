/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitchEntityModel<T extends Entity>
extends VillagerResemblingModel<T> {
    private boolean field_3614;
    private final Cuboid mole = new Cuboid(this).setTextureSize(64, 128);

    public WitchEntityModel(float f) {
        super(f, 64, 128);
        this.mole.setRotationPoint(0.0f, -2.0f, 0.0f);
        this.mole.setTextureOffset(0, 0).addBox(0.0f, 3.0f, -6.75f, 1, 1, 1, -0.25f);
        this.nose.addChild(this.mole);
        this.head.removeChild(this.headOverlay);
        this.headOverlay = new Cuboid(this).setTextureSize(64, 128);
        this.headOverlay.setRotationPoint(-5.0f, -10.03125f, -5.0f);
        this.headOverlay.setTextureOffset(0, 64).addBox(0.0f, 0.0f, 0.0f, 10, 2, 10);
        this.head.addChild(this.headOverlay);
        Cuboid cuboid = new Cuboid(this).setTextureSize(64, 128);
        cuboid.setRotationPoint(1.75f, -4.0f, 2.0f);
        cuboid.setTextureOffset(0, 76).addBox(0.0f, 0.0f, 0.0f, 7, 4, 7);
        cuboid.pitch = -0.05235988f;
        cuboid.roll = 0.02617994f;
        this.headOverlay.addChild(cuboid);
        Cuboid cuboid2 = new Cuboid(this).setTextureSize(64, 128);
        cuboid2.setRotationPoint(1.75f, -4.0f, 2.0f);
        cuboid2.setTextureOffset(0, 87).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4);
        cuboid2.pitch = -0.10471976f;
        cuboid2.roll = 0.05235988f;
        cuboid.addChild(cuboid2);
        Cuboid cuboid3 = new Cuboid(this).setTextureSize(64, 128);
        cuboid3.setRotationPoint(1.75f, -2.0f, 2.0f);
        cuboid3.setTextureOffset(0, 95).addBox(0.0f, 0.0f, 0.0f, 1, 2, 1, 0.25f);
        cuboid3.pitch = -0.20943952f;
        cuboid3.roll = 0.10471976f;
        cuboid2.addChild(cuboid3);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(entity, f, g, h, i, j, k);
        this.nose.x = 0.0f;
        this.nose.y = 0.0f;
        this.nose.z = 0.0f;
        float l = 0.01f * (float)(((Entity)entity).getEntityId() % 10);
        this.nose.pitch = MathHelper.sin((float)((Entity)entity).age * l) * 4.5f * ((float)Math.PI / 180);
        this.nose.yaw = 0.0f;
        this.nose.roll = MathHelper.cos((float)((Entity)entity).age * l) * 2.5f * ((float)Math.PI / 180);
        if (this.field_3614) {
            this.nose.pitch = -0.9f;
            this.nose.z = -0.09375f;
            this.nose.y = 0.1875f;
        }
    }

    public Cuboid method_2839() {
        return this.nose;
    }

    public void method_2840(boolean bl) {
        this.field_3614 = bl;
    }
}

