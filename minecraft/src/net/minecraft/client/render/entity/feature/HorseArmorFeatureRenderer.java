package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class HorseArmorFeatureRenderer extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
	private final HorseEntityModel<HorseEntity> model = new HorseEntityModel<>(0.1F);

	public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18658(HorseEntity horseEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = horseEntity.getArmorType();
		if (itemStack.getItem() instanceof HorseArmorItem) {
			HorseArmorItem horseArmorItem = (HorseArmorItem)itemStack.getItem();
			this.getModel().copyStateTo(this.model);
			this.model.method_17084(horseEntity, f, g, h);
			this.bindTexture(horseArmorItem.getEntityTexture());
			if (horseArmorItem instanceof DyeableHorseArmorItem) {
				int m = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
				float n = (float)(m >> 16 & 0xFF) / 255.0F;
				float o = (float)(m >> 8 & 0xFF) / 255.0F;
				float p = (float)(m & 0xFF) / 255.0F;
				GlStateManager.color4f(n, o, p, 1.0F);
				this.model.method_17085(horseEntity, f, g, i, j, k, l);
				return;
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.model.method_17085(horseEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
