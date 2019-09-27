package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class LecternBlockEntityRenderer extends BlockEntityRenderer<LecternBlockEntity> {
	private final BookModel book = new BookModel();

	public LecternBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_17582(LecternBlockEntity lecternBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		BlockState blockState = lecternBlockEntity.getCachedState();
		if ((Boolean)blockState.get(LecternBlock.HAS_BOOK)) {
			arg.method_22903();
			arg.method_22904(0.5, 1.0625, 0.5);
			float h = ((Direction)blockState.get(LecternBlock.FACING)).rotateYClockwise().asRotation();
			arg.method_22907(Vector3f.field_20705.method_23214(-h, true));
			arg.method_22907(Vector3f.field_20707.method_23214(67.5F, true));
			arg.method_22904(0.0, -0.125, 0.0);
			this.book.setPageAngles(0.0F, 0.1F, 0.9F, 1.2F);
			class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
			this.book.render(arg, lv, 0.0625F, i, this.method_23082(EnchantingTableBlockEntityRenderer.BOOK_TEX));
			arg.method_22909();
		}
	}
}
