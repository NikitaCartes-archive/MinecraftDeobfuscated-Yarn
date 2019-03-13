package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BellBlockEntityRenderer extends BlockEntityRenderer<BellBlockEntity> {
	private static final Identifier field_17145 = new Identifier("textures/entity/bell/bell_body.png");
	private final BellModel model = new BellModel();

	public void method_17139(BellBlockEntity bellBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		this.method_3566(field_17145);
		GlStateManager.translatef((float)d, (float)e, (float)f);
		float h = (float)bellBlockEntity.ringTicks + g;
		float j = 0.0F;
		float k = 0.0F;
		if (bellBlockEntity.isRinging) {
			float l = MathHelper.sin(h / (float) Math.PI) / (4.0F + h / 3.0F);
			if (bellBlockEntity.field_17097 == Direction.NORTH) {
				j = -l;
			} else if (bellBlockEntity.field_17097 == Direction.SOUTH) {
				j = l;
			} else if (bellBlockEntity.field_17097 == Direction.EAST) {
				k = -l;
			} else if (bellBlockEntity.field_17097 == Direction.WEST) {
				k = l;
			}
		}

		this.model.method_17070(j, k, 0.0625F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
