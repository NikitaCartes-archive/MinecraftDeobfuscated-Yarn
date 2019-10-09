/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity>
extends class_4595<T>
implements ModelWithHead,
ModelWithHat {
    protected ModelPart head;
    protected ModelPart headOverlay;
    protected final ModelPart hat;
    protected final ModelPart body;
    protected final ModelPart robe;
    protected final ModelPart arms;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;
    protected final ModelPart nose;

    public VillagerResemblingModel(float f) {
        this(f, 64, 64);
    }

    public VillagerResemblingModel(float f, int i, int j) {
        super(RenderLayer::getEntityCutoutNoCull);
        float g = 0.5f;
        this.head = new ModelPart(this).setTextureSize(i, j);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f);
        this.headOverlay = new ModelPart(this).setTextureSize(i, j);
        this.headOverlay.setPivot(0.0f, 0.0f, 0.0f);
        this.headOverlay.setTextureOffset(32, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f + 0.5f);
        this.head.addChild(this.headOverlay);
        this.hat = new ModelPart(this).setTextureSize(i, j);
        this.hat.setPivot(0.0f, 0.0f, 0.0f);
        this.hat.setTextureOffset(30, 47).addCuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f, f);
        this.hat.pitch = -1.5707964f;
        this.headOverlay.addChild(this.hat);
        this.nose = new ModelPart(this).setTextureSize(i, j);
        this.nose.setPivot(0.0f, -2.0f, 0.0f);
        this.nose.setTextureOffset(24, 0).addCuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, f);
        this.head.addChild(this.nose);
        this.body = new ModelPart(this).setTextureSize(i, j);
        this.body.setPivot(0.0f, 0.0f, 0.0f);
        this.body.setTextureOffset(16, 20).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, f);
        this.robe = new ModelPart(this).setTextureSize(i, j);
        this.robe.setPivot(0.0f, 0.0f, 0.0f);
        this.robe.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, f + 0.5f);
        this.body.addChild(this.robe);
        this.arms = new ModelPart(this).setTextureSize(i, j);
        this.arms.setPivot(0.0f, 2.0f, 0.0f);
        this.arms.setTextureOffset(44, 22).addCuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, f);
        this.arms.setTextureOffset(44, 22).addCuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, f, true);
        this.arms.setTextureOffset(40, 38).addCuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f, f);
        this.leftLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.leftLeg.setPivot(-2.0f, 12.0f, 0.0f);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.rightLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.rightLeg.mirror = true;
        this.rightLeg.setPivot(2.0f, 12.0f, 0.0f);
        this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.body, this.leftLeg, this.rightLeg, this.arms);
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
        this.arms.pivotY = 3.0f;
        this.arms.pivotZ = -1.0f;
        this.arms.pitch = -0.75f;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g * 0.5f;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g * 0.5f;
        this.leftLeg.yaw = 0.0f;
        this.rightLeg.yaw = 0.0f;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setHatVisible(boolean bl) {
        this.head.visible = bl;
        this.headOverlay.visible = bl;
        this.hat.visible = bl;
    }
}

