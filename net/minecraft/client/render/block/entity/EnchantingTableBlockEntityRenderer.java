/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer
extends BlockEntityRenderer<EnchantingTableBlockEntity> {
    public static final Identifier BOOK_TEX = new Identifier("entity/enchanting_table_book");
    private final BookModel book = new BookModel();

    public EnchantingTableBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_3571(EnchantingTableBlockEntity enchantingTableBlockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i) {
        float j;
        matrixStack.push();
        matrixStack.translate(0.5, 0.75, 0.5);
        float h = (float)enchantingTableBlockEntity.ticks + g;
        matrixStack.translate(0.0, 0.1f + MathHelper.sin(h * 0.1f) * 0.01f, 0.0);
        for (j = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963; j >= (float)Math.PI; j -= (float)Math.PI * 2) {
        }
        while (j < (float)(-Math.PI)) {
            j += (float)Math.PI * 2;
        }
        float k = enchantingTableBlockEntity.field_11963 + j * g;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-k, false));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(80.0f, true));
        float l = MathHelper.lerp(g, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
        float m = MathHelper.method_22450(l + 0.25f) * 1.6f - 0.3f;
        float n = MathHelper.method_22450(l + 0.75f) * 1.6f - 0.3f;
        float o = MathHelper.lerp(g, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
        this.book.setPageAngles(h, MathHelper.clamp(m, 0.0f, 1.0f), MathHelper.clamp(n, 0.0f, 1.0f), o);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.SOLID);
        this.book.render(matrixStack, vertexConsumer, 0.0625f, i, this.getSprite(BOOK_TEX));
        matrixStack.pop();
    }
}

