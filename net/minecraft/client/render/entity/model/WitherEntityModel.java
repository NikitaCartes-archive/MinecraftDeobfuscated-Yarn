/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity>
extends EntityModel<T> {
    private final Cuboid[] field_3613;
    private final Cuboid[] field_3612;

    public WitherEntityModel(float f) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3613 = new Cuboid[3];
        this.field_3613[0] = new Cuboid(this, 0, 16);
        this.field_3613[0].addBox(-10.0f, 3.9f, -0.5f, 20, 3, 3, f);
        this.field_3613[1] = new Cuboid(this).setTextureSize(this.textureWidth, this.textureHeight);
        this.field_3613[1].setRotationPoint(-2.0f, 6.9f, -0.5f);
        this.field_3613[1].setTextureOffset(0, 22).addBox(0.0f, 0.0f, 0.0f, 3, 10, 3, f);
        this.field_3613[1].setTextureOffset(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11, 2, 2, f);
        this.field_3613[1].setTextureOffset(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11, 2, 2, f);
        this.field_3613[1].setTextureOffset(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11, 2, 2, f);
        this.field_3613[2] = new Cuboid(this, 12, 22);
        this.field_3613[2].addBox(0.0f, 0.0f, 0.0f, 3, 6, 3, f);
        this.field_3612 = new Cuboid[3];
        this.field_3612[0] = new Cuboid(this, 0, 0);
        this.field_3612[0].addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8, f);
        this.field_3612[1] = new Cuboid(this, 32, 0);
        this.field_3612[1].addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, f);
        this.field_3612[1].rotationPointX = -8.0f;
        this.field_3612[1].rotationPointY = 4.0f;
        this.field_3612[2] = new Cuboid(this, 32, 0);
        this.field_3612[2].addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, f);
        this.field_3612[2].rotationPointX = 10.0f;
        this.field_3612[2].rotationPointY = 4.0f;
    }

    public void method_17129(T witherEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17130(witherEntity, f, g, h, i, j, k);
        for (Cuboid cuboid : this.field_3612) {
            cuboid.render(k);
        }
        for (Cuboid cuboid : this.field_3613) {
            cuboid.render(k);
        }
    }

    public void method_17130(T witherEntity, float f, float g, float h, float i, float j, float k) {
        float l = MathHelper.cos(h * 0.1f);
        this.field_3613[1].pitch = (0.065f + 0.05f * l) * (float)Math.PI;
        this.field_3613[2].setRotationPoint(-2.0f, 6.9f + MathHelper.cos(this.field_3613[1].pitch) * 10.0f, -0.5f + MathHelper.sin(this.field_3613[1].pitch) * 10.0f);
        this.field_3613[2].pitch = (0.265f + 0.1f * l) * (float)Math.PI;
        this.field_3612[0].yaw = i * ((float)Math.PI / 180);
        this.field_3612[0].pitch = j * ((float)Math.PI / 180);
    }

    public void method_17128(T witherEntity, float f, float g, float h) {
        for (int i = 1; i < 3; ++i) {
            this.field_3612[i].yaw = (((WitherEntity)witherEntity).getHeadYaw(i - 1) - ((WitherEntity)witherEntity).field_6283) * ((float)Math.PI / 180);
            this.field_3612[i].pitch = ((WitherEntity)witherEntity).getHeadPitch(i - 1) * ((float)Math.PI / 180);
        }
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17130((WitherEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17129((WitherEntity)entity, f, g, h, i, j, k);
    }
}

