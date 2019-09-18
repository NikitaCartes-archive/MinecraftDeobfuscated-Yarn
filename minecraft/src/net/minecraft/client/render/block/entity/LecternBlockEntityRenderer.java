package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4576;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class LecternBlockEntityRenderer extends class_4576<LecternBlockEntity> {
	private final BookModel book = new BookModel();

	protected void method_17582(
		LecternBlockEntity lecternBlockEntity,
		double d,
		double e,
		double f,
		float g,
		int i,
		BlockRenderLayer blockRenderLayer,
		BufferBuilder bufferBuilder,
		int j,
		int k
	) {
		BlockState blockState = lecternBlockEntity.getCachedState();
		if ((Boolean)blockState.get(LecternBlock.HAS_BOOK)) {
			bufferBuilder.method_22629();
			bufferBuilder.method_22626(0.5, 1.0625, 0.5);
			float h = ((Direction)blockState.get(LecternBlock.FACING)).rotateYClockwise().asRotation();
			bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -h, true));
			bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 67.5F, true));
			bufferBuilder.method_22626(0.0, -0.125, 0.0);
			this.book.setPageAngles(0.0F, 0.1F, 0.9F, 1.2F);
			this.book.render(bufferBuilder, 0.0625F, j, k, this.method_22739(EnchantingTableBlockEntityRenderer.BOOK_TEX));
			bufferBuilder.method_22630();
		}
	}
}
