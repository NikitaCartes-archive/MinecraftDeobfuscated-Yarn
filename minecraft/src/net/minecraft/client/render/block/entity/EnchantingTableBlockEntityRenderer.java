package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
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

	public void method_3571(
		EnchantingTableBlockEntity enchantingTableBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
	) {
		matrixStack.push();
		matrixStack.translate(0.5, 0.75, 0.5);
		float g = (float)enchantingTableBlockEntity.ticks + f;
		matrixStack.translate(0.0, (double)(0.1F + MathHelper.sin(g * 0.1F) * 0.01F), 0.0);
		float h = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963;

		while (h >= (float) Math.PI) {
			h -= (float) (Math.PI * 2);
		}

		while (h < (float) -Math.PI) {
			h += (float) (Math.PI * 2);
		}

		float k = enchantingTableBlockEntity.field_11963 + h * f;
		matrixStack.multiply(Vector3f.POSITIVE_Y.method_23626(-k));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(80.0F));
		float l = MathHelper.lerp(f, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
		float m = MathHelper.method_22450(l + 0.25F) * 1.6F - 0.3F;
		float n = MathHelper.method_22450(l + 0.75F) * 1.6F - 0.3F;
		float o = MathHelper.lerp(f, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
		this.book.setPageAngles(g, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
		this.book.render(matrixStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, this.getSprite(BOOK_TEX));
		matrixStack.pop();
	}
}
