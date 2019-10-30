/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
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
    private static final Identifier SKIN = new Identifier("textures/entity/fish/pufferfish.png");
    private int modelSize = 3;
    private final SmallPufferfishEntityModel<PufferfishEntity> smallModel = new SmallPufferfishEntityModel();
    private final MediumPufferfishEntityModel<PufferfishEntity> mediumModel = new MediumPufferfishEntityModel();
    private final LargePufferfishEntityModel<PufferfishEntity> largeModel = new LargePufferfishEntityModel();

    public PufferfishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new LargePufferfishEntityModel(), 0.2f);
    }

    public Identifier method_4096(PufferfishEntity pufferfishEntity) {
        return SKIN;
    }

    public void method_4094(PufferfishEntity pufferfishEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        int i = pufferfishEntity.getPuffState();
        if (i != this.modelSize) {
            this.model = i == 0 ? this.smallModel : (i == 1 ? this.mediumModel : this.largeModel);
        }
        this.modelSize = i;
        this.field_4673 = 0.1f + 0.1f * (float)i;
        super.method_4072(pufferfishEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
    }

    protected void method_4095(PufferfishEntity pufferfishEntity, MatrixStack matrixStack, float f, float g, float h) {
        matrixStack.translate(0.0, MathHelper.cos(f * 0.05f) * 0.08f, 0.0);
        super.setupTransforms(pufferfishEntity, matrixStack, f, g, h);
    }
}

