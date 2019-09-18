/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.class_4576;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer
extends class_4576<EnchantingTableBlockEntity> {
    public static final Identifier BOOK_TEX = new Identifier("entity/enchanting_table_book");
    private final BookModel book = new BookModel();

    protected void method_3571(EnchantingTableBlockEntity enchantingTableBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k) {
        float l;
        bufferBuilder.method_22629();
        bufferBuilder.method_22626(0.5, 0.75, 0.5);
        float h = (float)enchantingTableBlockEntity.ticks + g;
        bufferBuilder.method_22626(0.0, 0.1f + MathHelper.sin(h * 0.1f) * 0.01f, 0.0);
        for (l = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963; l >= (float)Math.PI; l -= (float)Math.PI * 2) {
        }
        while (l < (float)(-Math.PI)) {
            l += (float)Math.PI * 2;
        }
        float m = enchantingTableBlockEntity.field_11963 + l * g;
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -m, false));
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 80.0f, true));
        float n = MathHelper.lerp(g, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
        float o = MathHelper.method_22450(n + 0.25f) * 1.6f - 0.3f;
        float p = MathHelper.method_22450(n + 0.75f) * 1.6f - 0.3f;
        float q = MathHelper.lerp(g, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
        this.book.setPageAngles(h, MathHelper.clamp(o, 0.0f, 1.0f), MathHelper.clamp(p, 0.0f, 1.0f), q);
        this.book.render(bufferBuilder, 0.0625f, j, k, this.method_22739(BOOK_TEX));
        bufferBuilder.method_22630();
    }
}

