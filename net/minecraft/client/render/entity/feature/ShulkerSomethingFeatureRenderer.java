/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ShulkerSomethingFeatureRenderer
extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
    public ShulkerSomethingFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4115(class_4587 arg, class_4597 arg2, int i, ShulkerEntity shulkerEntity, float f, float g, float h, float j, float k, float l, float m) {
        arg.method_22903();
        switch (shulkerEntity.getAttachedFace()) {
            case DOWN: {
                break;
            }
            case EAST: {
                arg.method_22907(Vector3f.field_20707.method_23214(90.0f, true));
                arg.method_22907(Vector3f.field_20703.method_23214(90.0f, true));
                arg.method_22904(1.0, -1.0, 0.0);
                arg.method_22907(Vector3f.field_20705.method_23214(180.0f, true));
                break;
            }
            case WEST: {
                arg.method_22907(Vector3f.field_20707.method_23214(-90.0f, true));
                arg.method_22907(Vector3f.field_20703.method_23214(90.0f, true));
                arg.method_22904(-1.0, -1.0, 0.0);
                arg.method_22907(Vector3f.field_20705.method_23214(180.0f, true));
                break;
            }
            case NORTH: {
                arg.method_22907(Vector3f.field_20703.method_23214(90.0f, true));
                arg.method_22904(0.0, -1.0, -1.0);
                break;
            }
            case SOUTH: {
                arg.method_22907(Vector3f.field_20707.method_23214(180.0f, true));
                arg.method_22907(Vector3f.field_20703.method_23214(90.0f, true));
                arg.method_22904(0.0, -1.0, 1.0);
                break;
            }
            case UP: {
                arg.method_22907(Vector3f.field_20703.method_23214(180.0f, true));
                arg.method_22904(0.0, -2.0, 0.0);
            }
        }
        ModelPart modelPart = ((ShulkerEntityModel)this.getModel()).method_2830();
        modelPart.yaw = k * ((float)Math.PI / 180);
        modelPart.pitch = l * ((float)Math.PI / 180);
        DyeColor dyeColor = shulkerEntity.getColor();
        Identifier identifier = dyeColor == null ? ShulkerEntityRenderer.SKIN : ShulkerEntityRenderer.SKIN_COLOR[dyeColor.getId()];
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(identifier));
        LivingEntityRenderer.method_23184(shulkerEntity, lv, 0.0f);
        modelPart.method_22698(arg, lv, m, i, null);
        lv.method_22923();
        arg.method_22909();
    }
}

