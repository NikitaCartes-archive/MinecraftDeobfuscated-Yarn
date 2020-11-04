/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MagmaCubeEntityRenderer
extends MobEntityRenderer<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/slime/magmacube.png");

    public MagmaCubeEntityRenderer(class_5617.class_5618 arg) {
        super(arg, new MagmaCubeEntityModel(arg.method_32167(EntityModelLayers.MAGMA_CUBE)), 0.25f);
    }

    @Override
    protected int getBlockLight(MagmaCubeEntity magmaCubeEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(MagmaCubeEntity magmaCubeEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(MagmaCubeEntity magmaCubeEntity, MatrixStack matrixStack, float f) {
        int i = magmaCubeEntity.getSize();
        float g = MathHelper.lerp(f, magmaCubeEntity.lastStretch, magmaCubeEntity.stretch) / ((float)i * 0.5f + 1.0f);
        float h = 1.0f / (g + 1.0f);
        matrixStack.scale(h * (float)i, 1.0f / h * (float)i, h * (float)i);
    }
}

