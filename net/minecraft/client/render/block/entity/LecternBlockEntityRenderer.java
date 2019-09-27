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
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;

@Environment(value=EnvType.CLIENT)
public class LecternBlockEntityRenderer
extends BlockEntityRenderer<LecternBlockEntity> {
    private final BookModel book = new BookModel();

    public LecternBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_17582(LecternBlockEntity lecternBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
        BlockState blockState = lecternBlockEntity.getCachedState();
        if (!blockState.get(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        arg.method_22903();
        arg.method_22904(0.5, 1.0625, 0.5);
        float h = blockState.get(LecternBlock.FACING).rotateYClockwise().asRotation();
        arg.method_22907(Vector3f.field_20705.method_23214(-h, true));
        arg.method_22907(Vector3f.field_20707.method_23214(67.5f, true));
        arg.method_22904(0.0, -0.125, 0.0);
        this.book.setPageAngles(0.0f, 0.1f, 0.9f, 1.2f);
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
        this.book.render(arg, lv, 0.0625f, i, this.method_23082(EnchantingTableBlockEntityRenderer.BOOK_TEX));
        arg.method_22909();
    }
}

