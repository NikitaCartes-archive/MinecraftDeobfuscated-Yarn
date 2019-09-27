/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
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
public abstract class class_4606<T extends Entity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public class_4606(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(class_4587 arg, class_4597 arg2, int i, T entity, float f, float g, float h, float j, float k, float l, float m) {
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23026(this.method_23193()));
        class_4608.method_23211(lv);
        ((EntityModel)this.getModel()).method_22957(arg, lv, 0xF00000);
        lv.method_22923();
    }

    public abstract Identifier method_23193();
}

