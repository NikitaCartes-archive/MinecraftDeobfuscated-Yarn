/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PufferfishEntityRenderer
extends MobEntityRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/fish/pufferfish.png");
    private int modelSize = 3;
    private final EntityModel<PufferfishEntity> smallModel;
    private final EntityModel<PufferfishEntity> mediumModel;
    private final EntityModel<PufferfishEntity> largeModel = this.getModel();

    public PufferfishEntityRenderer(class_5617.class_5618 arg) {
        super(arg, new LargePufferfishEntityModel(arg.method_32167(EntityModelLayers.PUFFERFISH_BIG)), 0.2f);
        this.mediumModel = new MediumPufferfishEntityModel<PufferfishEntity>(arg.method_32167(EntityModelLayers.PUFFERFISH_MEDIUM));
        this.smallModel = new SmallPufferfishEntityModel<PufferfishEntity>(arg.method_32167(EntityModelLayers.PUFFERFISH_SMALL));
    }

    @Override
    public Identifier getTexture(PufferfishEntity pufferfishEntity) {
        return TEXTURE;
    }

    @Override
    public void render(PufferfishEntity pufferfishEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        int j = pufferfishEntity.getPuffState();
        if (j != this.modelSize) {
            this.model = j == 0 ? this.smallModel : (j == 1 ? this.mediumModel : this.largeModel);
        }
        this.modelSize = j;
        this.shadowRadius = 0.1f + 0.1f * (float)j;
        super.render(pufferfishEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    protected void setupTransforms(PufferfishEntity pufferfishEntity, MatrixStack matrixStack, float f, float g, float h) {
        matrixStack.translate(0.0, MathHelper.cos(f * 0.05f) * 0.08f, 0.0);
        super.setupTransforms(pufferfishEntity, matrixStack, f, g, h);
    }
}

