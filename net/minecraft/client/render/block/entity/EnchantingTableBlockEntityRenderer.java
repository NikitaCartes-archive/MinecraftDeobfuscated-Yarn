/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer
extends BlockEntityRenderer<EnchantingTableBlockEntity> {
    private static final Identifier BOOK_TEX = new Identifier("textures/entity/enchanting_table_book.png");
    private final BookModel book = new BookModel();

    public void method_3571(EnchantingTableBlockEntity enchantingTableBlockEntity, double d, double e, double f, float g, int i) {
        float j;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)d + 0.5f, (float)e + 0.75f, (float)f + 0.5f);
        float h = (float)enchantingTableBlockEntity.ticks + g;
        RenderSystem.translatef(0.0f, 0.1f + MathHelper.sin(h * 0.1f) * 0.01f, 0.0f);
        for (j = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963; j >= (float)Math.PI; j -= (float)Math.PI * 2) {
        }
        while (j < (float)(-Math.PI)) {
            j += (float)Math.PI * 2;
        }
        float k = enchantingTableBlockEntity.field_11963 + j * g;
        RenderSystem.rotatef(-k * 57.295776f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(80.0f, 0.0f, 0.0f, 1.0f);
        this.bindTexture(BOOK_TEX);
        float l = MathHelper.lerp(g, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle) + 0.25f;
        float m = MathHelper.lerp(g, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle) + 0.75f;
        l = (l - (float)MathHelper.fastFloor(l)) * 1.6f - 0.3f;
        m = (m - (float)MathHelper.fastFloor(m)) * 1.6f - 0.3f;
        if (l < 0.0f) {
            l = 0.0f;
        }
        if (m < 0.0f) {
            m = 0.0f;
        }
        if (l > 1.0f) {
            l = 1.0f;
        }
        if (m > 1.0f) {
            m = 1.0f;
        }
        float n = MathHelper.lerp(g, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
        RenderSystem.enableCull();
        this.book.render(h, l, m, n, 0.0f, 0.0625f);
        RenderSystem.popMatrix();
    }
}

