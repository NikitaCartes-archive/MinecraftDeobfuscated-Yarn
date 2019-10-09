package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer extends BlockEntityRenderer<EnchantingTableBlockEntity> {
	public static final Identifier BOOK_TEX = new Identifier("entity/enchanting_table_book");
	private final BookModel book = new BookModel();

	public EnchantingTableBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3571(
		EnchantingTableBlockEntity enchantingTableBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		int j
	) {
		matrixStack.push();
		matrixStack.translate(0.5, 0.75, 0.5);
		float h = (float)enchantingTableBlockEntity.ticks + g;
		matrixStack.translate(0.0, (double)(0.1F + MathHelper.sin(h * 0.1F) * 0.01F), 0.0);
		float k = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963;

		while (k >= (float) Math.PI) {
			k -= (float) (Math.PI * 2);
		}

		while (k < (float) -Math.PI) {
			k += (float) (Math.PI * 2);
		}

		float l = enchantingTableBlockEntity.field_11963 + k * g;
		matrixStack.multiply(Vector3f.POSITIVE_Y.method_23626(-l));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(80.0F));
		float m = MathHelper.lerp(g, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
		float n = MathHelper.method_22450(m + 0.25F) * 1.6F - 0.3F;
		float o = MathHelper.method_22450(m + 0.75F) * 1.6F - 0.3F;
		float p = MathHelper.lerp(g, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
		this.book.setPageAngles(h, MathHelper.clamp(n, 0.0F, 1.0F), MathHelper.clamp(o, 0.0F, 1.0F), p);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
		this.book.render(matrixStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, this.getSprite(BOOK_TEX));
		matrixStack.pop();
	}
}
