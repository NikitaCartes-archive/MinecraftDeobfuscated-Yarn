package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.entity.model.EnchantingTableBookEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer extends BlockEntityRenderer<EnchantingTableBlockEntity> {
	private static final Identifier BOOK_TEX = new Identifier("textures/entity/enchanting_table_book.png");
	private final EnchantingTableBookEntityModel modelTable = new EnchantingTableBookEntityModel();

	public void render(EnchantingTableBlockEntity enchantingTableBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d + 0.5F, (float)e + 0.75F, (float)f + 0.5F);
		float h = (float)enchantingTableBlockEntity.ticks + g;
		GlStateManager.translatef(0.0F, 0.1F + MathHelper.sin(h * 0.1F) * 0.01F, 0.0F);
		float j = enchantingTableBlockEntity.field_11964 - enchantingTableBlockEntity.field_11963;

		while (j >= (float) Math.PI) {
			j -= (float) (Math.PI * 2);
		}

		while (j < (float) -Math.PI) {
			j += (float) (Math.PI * 2);
		}

		float k = enchantingTableBlockEntity.field_11963 + j * g;
		GlStateManager.rotatef(-k * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
		this.bindTexture(BOOK_TEX);
		float l = MathHelper.lerp(g, enchantingTableBlockEntity.field_11960, enchantingTableBlockEntity.field_11958) + 0.25F;
		float m = MathHelper.lerp(g, enchantingTableBlockEntity.field_11960, enchantingTableBlockEntity.field_11958) + 0.75F;
		l = (l - (float)MathHelper.fastFloor((double)l)) * 1.6F - 0.3F;
		m = (m - (float)MathHelper.fastFloor((double)m)) * 1.6F - 0.3F;
		if (l < 0.0F) {
			l = 0.0F;
		}

		if (m < 0.0F) {
			m = 0.0F;
		}

		if (l > 1.0F) {
			l = 1.0F;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		float n = MathHelper.lerp(g, enchantingTableBlockEntity.field_11965, enchantingTableBlockEntity.field_11966);
		GlStateManager.enableCull();
		this.modelTable.render(h, l, m, n, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
