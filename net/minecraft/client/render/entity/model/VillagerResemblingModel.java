/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity>
extends EntityModel<T>
implements ModelWithHead,
ModelWithHat {
    protected final Cuboid head;
    protected Cuboid headOverlay;
    protected final Cuboid hat;
    protected final Cuboid body;
    protected final Cuboid robe;
    protected final Cuboid arms;
    protected final Cuboid leftLeg;
    protected final Cuboid rightLeg;
    protected final Cuboid nose;

    public VillagerResemblingModel(float f) {
        this(f, 64, 64);
    }

    public VillagerResemblingModel(float f, int i, int j) {
        float g = 0.5f;
        this.head = new Cuboid(this).setTextureSize(i, j);
        this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, f);
        this.headOverlay = new Cuboid(this).setTextureSize(i, j);
        this.headOverlay.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.headOverlay.setTextureOffset(32, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, f + 0.5f);
        this.head.addChild(this.headOverlay);
        this.hat = new Cuboid(this).setTextureSize(i, j);
        this.hat.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.hat.setTextureOffset(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16, 16, 1, f);
        this.hat.pitch = -1.5707964f;
        this.headOverlay.addChild(this.hat);
        this.nose = new Cuboid(this).setTextureSize(i, j);
        this.nose.setRotationPoint(0.0f, -2.0f, 0.0f);
        this.nose.setTextureOffset(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2, 4, 2, f);
        this.head.addChild(this.nose);
        this.body = new Cuboid(this).setTextureSize(i, j);
        this.body.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.body.setTextureOffset(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, f);
        this.robe = new Cuboid(this).setTextureSize(i, j);
        this.robe.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.robe.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, f + 0.5f);
        this.body.addChild(this.robe);
        this.arms = new Cuboid(this).setTextureSize(i, j);
        this.arms.setRotationPoint(0.0f, 2.0f, 0.0f);
        this.arms.setTextureOffset(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4, 8, 4, f);
        this.arms.setTextureOffset(44, 22).addBox(4.0f, -2.0f, -2.0f, 4, 8, 4, f, true);
        this.arms.setTextureOffset(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8, 4, 4, f);
        this.leftLeg = new Cuboid(this, 0, 22).setTextureSize(i, j);
        this.leftLeg.setRotationPoint(-2.0f, 12.0f, 0.0f);
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.rightLeg = new Cuboid(this, 0, 22).setTextureSize(i, j);
        this.rightLeg.mirror = true;
        this.rightLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
        this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.head.render(k);
        this.body.render(k);
        this.leftLeg.render(k);
        this.rightLeg.render(k);
        this.arms.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        boolean bl = false;
        if (entity instanceof AbstractTraderEntity) {
            bl = ((AbstractTraderEntity)entity).getHeadRollingTimeLeft() > 0;
        }
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        if (bl) {
            this.head.roll = 0.3f * MathHelper.sin(0.45f * h);
            this.head.pitch = 0.4f;
        } else {
            this.head.roll = 0.0f;
        }
        this.arms.rotationPointY = 3.0f;
        this.arms.rotationPointZ = -1.0f;
        this.arms.pitch = -0.75f;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g * 0.5f;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g * 0.5f;
        this.leftLeg.yaw = 0.0f;
        this.rightLeg.yaw = 0.0f;
    }

    @Override
    public Cuboid getHead() {
        return this.head;
    }

    @Override
    public void setHatVisible(boolean bl) {
        this.head.visible = bl;
        this.headOverlay.visible = bl;
        this.hat.visible = bl;
    }
}

