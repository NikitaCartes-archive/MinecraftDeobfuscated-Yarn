/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.class_4576;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class LecternBlockEntityRenderer
extends class_4576<LecternBlockEntity> {
    private final BookModel book = new BookModel();

    protected void method_17582(LecternBlockEntity lecternBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k) {
        BlockState blockState = lecternBlockEntity.getCachedState();
        if (!blockState.get(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        bufferBuilder.method_22629();
        bufferBuilder.method_22626(0.5, 1.0625, 0.5);
        float h = blockState.get(LecternBlock.FACING).rotateYClockwise().asRotation();
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -h, true));
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 67.5f, true));
        bufferBuilder.method_22626(0.0, -0.125, 0.0);
        this.book.setPageAngles(0.0f, 0.1f, 0.9f, 1.2f);
        this.book.render(bufferBuilder, 0.0625f, j, k, this.method_22739(EnchantingTableBlockEntityRenderer.BOOK_TEX));
        bufferBuilder.method_22630();
    }
}

