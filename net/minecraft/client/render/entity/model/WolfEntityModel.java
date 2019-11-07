/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.TintableAnimalModel;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity>
extends TintableAnimalModel<T> {
    private final ModelPart head;
    private final ModelPart field_20788;
    private final ModelPart field_3623;
    private final ModelPart field_3622;
    private final ModelPart field_3620;
    private final ModelPart field_3618;
    private final ModelPart field_3624;
    private final ModelPart field_3617;
    private final ModelPart field_20789;
    private final ModelPart field_3619;

    public WolfEntityModel() {
        float f = 0.0f;
        float g = 13.5f;
        this.head = new ModelPart(this, 0, 0);
        this.head.setPivot(-1.0f, 13.5f, -7.0f);
        this.field_20788 = new ModelPart(this, 0, 0);
        this.field_20788.addCuboid(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f, 0.0f);
        this.head.addChild(this.field_20788);
        this.field_3623 = new ModelPart(this, 18, 14);
        this.field_3623.addCuboid(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f, 0.0f);
        this.field_3623.setPivot(0.0f, 14.0f, 2.0f);
        this.field_3619 = new ModelPart(this, 21, 0);
        this.field_3619.addCuboid(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f, 0.0f);
        this.field_3619.setPivot(-1.0f, 14.0f, 2.0f);
        this.field_3622 = new ModelPart(this, 0, 18);
        this.field_3622.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.field_3622.setPivot(-2.5f, 16.0f, 7.0f);
        this.field_3620 = new ModelPart(this, 0, 18);
        this.field_3620.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.field_3620.setPivot(0.5f, 16.0f, 7.0f);
        this.field_3618 = new ModelPart(this, 0, 18);
        this.field_3618.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.field_3618.setPivot(-2.5f, 16.0f, -4.0f);
        this.field_3624 = new ModelPart(this, 0, 18);
        this.field_3624.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.field_3624.setPivot(0.5f, 16.0f, -4.0f);
        this.field_3617 = new ModelPart(this, 9, 18);
        this.field_3617.setPivot(-1.0f, 12.0f, 8.0f);
        this.field_20789 = new ModelPart(this, 9, 18);
        this.field_20789.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, 0.0f);
        this.field_3617.addChild(this.field_20789);
        this.field_20788.setTextureOffset(16, 14).addCuboid(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0.0f);
        this.field_20788.setTextureOffset(16, 14).addCuboid(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0.0f);
        this.field_20788.setTextureOffset(0, 10).addCuboid(-0.5f, 0.0f, -5.0f, 3.0f, 3.0f, 4.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.field_3623, this.field_3622, this.field_3620, this.field_3618, this.field_3624, this.field_3617, this.field_3619);
    }

    public void method_17131(T wolfEntity, float f, float g, float h) {
        this.field_3617.yaw = ((WolfEntity)wolfEntity).isAngry() ? 0.0f : MathHelper.cos(f * 0.6662f) * 1.4f * g;
        if (((TameableEntity)wolfEntity).isSitting()) {
            this.field_3619.setPivot(-1.0f, 16.0f, -3.0f);
            this.field_3619.pitch = 1.2566371f;
            this.field_3619.yaw = 0.0f;
            this.field_3623.setPivot(0.0f, 18.0f, 0.0f);
            this.field_3623.pitch = 0.7853982f;
            this.field_3617.setPivot(-1.0f, 21.0f, 6.0f);
            this.field_3622.setPivot(-2.5f, 22.7f, 2.0f);
            this.field_3622.pitch = 4.712389f;
            this.field_3620.setPivot(0.5f, 22.7f, 2.0f);
            this.field_3620.pitch = 4.712389f;
            this.field_3618.pitch = 5.811947f;
            this.field_3618.setPivot(-2.49f, 17.0f, -4.0f);
            this.field_3624.pitch = 5.811947f;
            this.field_3624.setPivot(0.51f, 17.0f, -4.0f);
        } else {
            this.field_3623.setPivot(0.0f, 14.0f, 2.0f);
            this.field_3623.pitch = 1.5707964f;
            this.field_3619.setPivot(-1.0f, 14.0f, -3.0f);
            this.field_3619.pitch = this.field_3623.pitch;
            this.field_3617.setPivot(-1.0f, 12.0f, 8.0f);
            this.field_3622.setPivot(-2.5f, 16.0f, 7.0f);
            this.field_3620.setPivot(0.5f, 16.0f, 7.0f);
            this.field_3618.setPivot(-2.5f, 16.0f, -4.0f);
            this.field_3624.setPivot(0.5f, 16.0f, -4.0f);
            this.field_3622.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
            this.field_3620.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_3618.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_3624.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        }
        this.field_20788.roll = ((WolfEntity)wolfEntity).getBegAnimationProgress(h) + ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, 0.0f);
        this.field_3619.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.08f);
        this.field_3623.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.16f);
        this.field_20789.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.2f);
    }

    public void method_17133(T wolfEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.field_3617.pitch = h;
    }
}

