/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class LecternBlockEntityRenderer
implements BlockEntityRenderer<LecternBlockEntity> {
    private final BookModel book;

    public LecternBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(LecternBlockEntity lecternBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = lecternBlockEntity.getCachedState();
        if (!blockState.get(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.5, 1.0625, 0.5);
        float g = blockState.get(LecternBlock.FACING).rotateYClockwise().asRotation();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-g));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(67.5f));
        matrixStack.translate(0.0, -0.125, 0.0);
        this.book.setPageAngles(0.0f, 0.1f, 0.9f, 1.2f);
        VertexConsumer vertexConsumer = EnchantingTableBlockEntityRenderer.BOOK_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.book.renderBook(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}

