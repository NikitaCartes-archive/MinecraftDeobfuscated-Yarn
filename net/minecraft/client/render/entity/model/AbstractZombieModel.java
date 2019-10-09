/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractZombieModel<T extends HostileEntity>
extends BipedEntityModel<T> {
    protected AbstractZombieModel(Function<Identifier, RenderLayer> function, float f, float g, int i, int j) {
        super(function, f, g, i, j);
    }

    public void method_17791(T hostileEntity, float f, float g, float h, float i, float j, float k) {
        float n;
        super.method_17087(hostileEntity, f, g, h, i, j, k);
        boolean bl = this.isAttacking(hostileEntity);
        float l = MathHelper.sin(this.handSwingProgress * (float)Math.PI);
        float m = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * (float)Math.PI);
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightArm.yaw = -(0.1f - l * 0.6f);
        this.leftArm.yaw = 0.1f - l * 0.6f;
        this.rightArm.pitch = n = (float)(-Math.PI) / (bl ? 1.5f : 2.25f);
        this.leftArm.pitch = n;
        this.rightArm.pitch += l * 1.2f - m * 0.4f;
        this.leftArm.pitch += l * 1.2f - m * 0.4f;
        this.rightArm.roll += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
        this.leftArm.roll -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
        this.rightArm.pitch += MathHelper.sin(h * 0.067f) * 0.05f;
        this.leftArm.pitch -= MathHelper.sin(h * 0.067f) * 0.05f;
    }

    public abstract boolean isAttacking(T var1);
}

