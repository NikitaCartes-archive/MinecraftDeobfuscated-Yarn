package net.minecraft.client.gui.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;

@Environment(EnvType.CLIENT)
public abstract class AbstractGuiInventory extends ContainerGui {
	protected boolean offsetGuiForEffects;

	public AbstractGuiInventory(Container container) {
		super(container);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.method_2476();
	}

	protected void method_2476() {
		if (this.client.player.getPotionEffects().isEmpty()) {
			this.left = (this.width - this.containerWidth) / 2;
			this.offsetGuiForEffects = false;
		} else {
			this.left = 160 + (this.width - this.containerWidth - 200) / 2;
			this.offsetGuiForEffects = true;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		super.draw(i, j, f);
		if (this.offsetGuiForEffects) {
			this.drawPotionEffects();
		}
	}

	private void drawPotionEffects() {
		int i = this.left - 124;
		int j = this.top;
		int k = 166;
		Collection<StatusEffectInstance> collection = this.client.player.getPotionEffects();
		if (!collection.isEmpty()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			int l = 33;
			if (collection.size() > 5) {
				l = 132 / (collection.size() - 1);
			}

			for (StatusEffectInstance statusEffectInstance : Ordering.natural().sortedCopy(collection)) {
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
				this.drawTexturedRect(i, j, 0, 166, 140, 32);
				if (statusEffect.hasIcon()) {
					int m = statusEffect.getIconIndex();
					this.drawTexturedRect(i + 6, j + 7, m % 12 * 18, 198 + m / 12 * 18, 18, 18);
				}

				String string = I18n.translate(statusEffect.getTranslationKey());
				if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
					string = string + ' ' + I18n.translate("enchantment.level." + (statusEffectInstance.getAmplifier() + 1));
				}

				this.fontRenderer.drawWithShadow(string, (float)(i + 10 + 18), (float)(j + 6), 16777215);
				String string2 = StatusEffectUtil.durationToString(statusEffectInstance, 1.0F);
				this.fontRenderer.drawWithShadow(string2, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
				j += l;
			}
		}
	}
}
