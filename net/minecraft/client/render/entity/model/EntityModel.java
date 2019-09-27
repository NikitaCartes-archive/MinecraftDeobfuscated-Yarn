/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public abstract class EntityModel<T extends Entity>
extends Model {
    public float handSwingProgress;
    public boolean isRiding;
    public boolean isChild = true;

    public void method_22957(class_4587 arg, class_4588 arg2, int i) {
        this.method_17116(arg, arg2, i, 1.0f, 1.0f, 1.0f);
    }

    public abstract void method_17116(class_4587 var1, class_4588 var2, int var3, float var4, float var5, float var6);

    public abstract void setAngles(T var1, float var2, float var3, float var4, float var5, float var6, float var7);

    public void animateModel(T entity, float f, float g, float h) {
    }

    public void copyStateTo(EntityModel<T> entityModel) {
        entityModel.handSwingProgress = this.handSwingProgress;
        entityModel.isRiding = this.isRiding;
        entityModel.isChild = this.isChild;
    }
}

