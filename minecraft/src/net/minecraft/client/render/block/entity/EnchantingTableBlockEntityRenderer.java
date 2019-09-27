package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer extends BlockEntityRenderer<EnchantingTableBlockEntity> {
	public static final Identifier BOOK_TEX = new Identifier("entity/enchanting_table_book");
	private final BookModel book = new BookModel();

	public EnchantingTableBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3571(EnchantingTableBlockEntity enchantingTableBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		arg.method_22903();
		arg.method_22904(0.5, 0.75, 0.5);
		float h = (float)enchantingTableBlockEntity.ticks + g;
		arg.method_22904(0.0, (double)(0.1F + MathHelper.sin(h * 0.1F) * 0.01F), 0.0);
		float j = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963;

		while (j >= (float) Math.PI) {
			j -= (float) (Math.PI * 2);
		}

		while (j < (float) -Math.PI) {
			j += (float) (Math.PI * 2);
		}

		float k = enchantingTableBlockEntity.field_11963 + j * g;
		arg.method_22907(Vector3f.field_20705.method_23214(-k, false));
		arg.method_22907(Vector3f.field_20707.method_23214(80.0F, true));
		float l = MathHelper.lerp(g, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
		float m = MathHelper.method_22450(l + 0.25F) * 1.6F - 0.3F;
		float n = MathHelper.method_22450(l + 0.75F) * 1.6F - 0.3F;
		float o = MathHelper.lerp(g, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
		this.book.setPageAngles(h, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
		this.book.render(arg, lv, 0.0625F, i, this.method_23082(BOOK_TEX));
		arg.method_22909();
	}
}
