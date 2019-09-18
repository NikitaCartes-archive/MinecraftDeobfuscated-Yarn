package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4576;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer extends class_4576<EnchantingTableBlockEntity> {
	public static final Identifier BOOK_TEX = new Identifier("entity/enchanting_table_book");
	private final BookModel book = new BookModel();

	protected void method_3571(
		EnchantingTableBlockEntity enchantingTableBlockEntity,
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
		bufferBuilder.method_22629();
		bufferBuilder.method_22626(0.5, 0.75, 0.5);
		float h = (float)enchantingTableBlockEntity.ticks + g;
		bufferBuilder.method_22626(0.0, (double)(0.1F + MathHelper.sin(h * 0.1F) * 0.01F), 0.0);
		float l = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963;

		while (l >= (float) Math.PI) {
			l -= (float) (Math.PI * 2);
		}

		while (l < (float) -Math.PI) {
			l += (float) (Math.PI * 2);
		}

		float m = enchantingTableBlockEntity.field_11963 + l * g;
		bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -m, false));
		bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 80.0F, true));
		float n = MathHelper.lerp(g, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
		float o = MathHelper.method_22450(n + 0.25F) * 1.6F - 0.3F;
		float p = MathHelper.method_22450(n + 0.75F) * 1.6F - 0.3F;
		float q = MathHelper.lerp(g, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
		this.book.setPageAngles(h, MathHelper.clamp(o, 0.0F, 1.0F), MathHelper.clamp(p, 0.0F, 1.0F), q);
		this.book.render(bufferBuilder, 0.0625F, j, k, this.method_22739(BOOK_TEX));
		bufferBuilder.method_22630();
	}
}
