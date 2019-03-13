package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class LecternBlockEntityRenderer extends BlockEntityRenderer<LecternBlockEntity> {
	private static final Identifier field_17427 = new Identifier("textures/entity/enchanting_table_book.png");
	private final BookModel book = new BookModel();

	public void method_17582(LecternBlockEntity lecternBlockEntity, double d, double e, double f, float g, int i) {
		BlockState blockState = lecternBlockEntity.method_11010();
		if ((Boolean)blockState.method_11654(LecternBlock.field_17366)) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 1.0F + 0.0625F, (float)f + 0.5F);
			float h = ((Direction)blockState.method_11654(LecternBlock.field_16404)).rotateYClockwise().asRotation();
			GlStateManager.rotatef(-h, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(67.5F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(0.0F, -0.125F, 0.0F);
			this.method_3566(field_17427);
			GlStateManager.enableCull();
			this.book.render(0.0F, 0.1F, 0.9F, 1.2F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
		}
	}
}
