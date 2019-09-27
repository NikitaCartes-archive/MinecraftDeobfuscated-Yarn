/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.class_4582;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class class_4607<T extends Entity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public class_4607(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(class_4587 arg, class_4597 arg2, int i, T entity, float f, float g, float h, float j, float k, float l, float m) {
        if (!((class_4582)entity).isAtHalfHealth()) {
            return;
        }
        float n = (float)((Entity)entity).age + h;
        EntityModel<T> entityModel = this.method_23203();
        entityModel.animateModel(entity, f, g, h);
        ((EntityModel)this.getModel()).copyStateTo(entityModel);
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23018(this.method_23201(), this.method_23202(n), n * 0.01f));
        class_4608.method_23211(lv);
        entityModel.setAngles(entity, f, g, j, k, l, m);
        entityModel.method_17116(arg, lv, i, 0.5f, 0.5f, 0.5f);
        lv.method_22923();
    }

    protected abstract float method_23202(float var1);

    protected abstract Identifier method_23201();

    protected abstract EntityModel<T> method_23203();
}

