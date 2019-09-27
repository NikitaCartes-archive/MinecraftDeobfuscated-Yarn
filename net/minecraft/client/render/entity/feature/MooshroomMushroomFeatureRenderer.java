/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;

@Environment(value=EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity>
extends FeatureRenderer<T, CowEntityModel<T>> {
    public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4195(class_4587 arg, class_4597 arg2, int i, T mooshroomEntity, float f, float g, float h, float j, float k, float l, float m) {
        if (((PassiveEntity)mooshroomEntity).isBaby() || ((Entity)mooshroomEntity).isInvisible()) {
            return;
        }
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        BlockState blockState = ((MooshroomEntity)mooshroomEntity).getMooshroomType().getMushroomState();
        arg.method_22903();
        arg.method_22905(-1.0f, -1.0f, 1.0f);
        arg.method_22904(-0.2f, 0.35f, 0.5);
        arg.method_22907(Vector3f.field_20705.method_23214(-42.0f, true));
        int n = class_4608.method_23212(((MooshroomEntity)mooshroomEntity).hurtTime > 0 || ((MooshroomEntity)mooshroomEntity).deathTime > 0);
        arg.method_22903();
        arg.method_22904(-0.5, -0.5, 0.5);
        blockRenderManager.renderDynamic(blockState, arg, arg2, i, 0, n);
        arg.method_22909();
        arg.method_22903();
        arg.method_22904(-0.1f, 0.0, -0.6f);
        arg.method_22907(Vector3f.field_20705.method_23214(-42.0f, true));
        arg.method_22904(-0.5, -0.5, 0.5);
        blockRenderManager.renderDynamic(blockState, arg, arg2, i, 0, n);
        arg.method_22909();
        arg.method_22909();
        arg.method_22903();
        ((CowEntityModel)this.getModel()).getHead().method_22703(arg, 0.0625f);
        arg.method_22905(-1.0f, -1.0f, 1.0f);
        arg.method_22904(0.0, 0.7f, -0.2f);
        arg.method_22907(Vector3f.field_20705.method_23214(-12.0f, true));
        arg.method_22904(-0.5, -0.5, 0.5);
        blockRenderManager.renderDynamic(blockState, arg, arg2, i, 0, n);
        arg.method_22909();
    }
}

