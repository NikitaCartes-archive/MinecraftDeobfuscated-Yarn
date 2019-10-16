/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class EntityModel<T extends Entity>
extends Model {
    public float handSwingProgress;
    public boolean isRiding;
    public boolean isChild = true;

    protected EntityModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    protected EntityModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    public abstract void setAngles(T var1, float var2, float var3, float var4, float var5, float var6, float var7);

    public void animateModel(T entity, float f, float g, float h) {
    }

    public void copyStateTo(EntityModel<T> entityModel) {
        entityModel.handSwingProgress = this.handSwingProgress;
        entityModel.isRiding = this.isRiding;
        entityModel.isChild = this.isChild;
    }
}

