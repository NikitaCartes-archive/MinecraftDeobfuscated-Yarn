package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public abstract class AbstractInventoryScreen<T extends ScreenHandler> extends HandledScreen<T> {
	public AbstractInventoryScreen(T screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.drawStatusEffects(matrices, mouseX, mouseY);
	}

	public boolean method_38934() {
		int i = this.x + this.backgroundWidth + 2;
		int j = this.width - i;
		return j >= 32;
	}

	private void drawStatusEffects(MatrixStack matrices, int i, int j) {
		int k = this.x + this.backgroundWidth + 2;
		int l = this.width - k;
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty() && l >= 32) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			boolean bl = l >= 120;
			int m = 33;
			if (collection.size() > 5) {
				m = 132 / (collection.size() - 1);
			}

			Iterable<StatusEffectInstance> iterable = Ordering.natural().sortedCopy(collection);
			this.drawStatusEffectBackgrounds(matrices, k, m, iterable, bl);
			this.drawStatusEffectSprites(matrices, k, m, iterable, bl);
			if (bl) {
				this.drawStatusEffectDescriptions(matrices, k, m, iterable);
			} else if (i >= k && i <= k + 33) {
				int n = this.y;
				StatusEffectInstance statusEffectInstance = null;

				for (StatusEffectInstance statusEffectInstance2 : iterable) {
					if (j >= n && j <= n + m) {
						statusEffectInstance = statusEffectInstance2;
					}

					n += m;
				}

				if (statusEffectInstance != null) {
					List<Text> list = List.of(
						this.getStatusEffectDescription(statusEffectInstance), new LiteralText(StatusEffectUtil.durationToString(statusEffectInstance, 1.0F))
					);
					this.renderTooltip(matrices, list, Optional.empty(), i, j);
				}
			}
		}
	}

	private void drawStatusEffectBackgrounds(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects, boolean wide) {
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			if (wide) {
				this.drawTexture(matrices, x, i, 0, 166, 120, 32);
			} else {
				this.drawTexture(matrices, x, i, 0, 198, 32, 32);
			}

			i += height;
		}
	}

	private void drawStatusEffectSprites(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects, boolean wide) {
		StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
			RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
			drawSprite(matrices, x + (wide ? 6 : 7), i + 7, this.getZOffset(), 18, 18, sprite);
			i += height;
		}
	}

	private void drawStatusEffectDescriptions(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects) {
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			Text text = this.getStatusEffectDescription(statusEffectInstance);
			this.textRenderer.drawWithShadow(matrices, text, (float)(x + 10 + 18), (float)(i + 6), 16777215);
			String string = StatusEffectUtil.durationToString(statusEffectInstance, 1.0F);
			this.textRenderer.drawWithShadow(matrices, string, (float)(x + 10 + 18), (float)(i + 6 + 10), 8355711);
			i += height;
		}
	}

	private Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
		MutableText mutableText = statusEffect.getEffectType().getName().shallowCopy();
		if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
			mutableText.append(" ").append(new TranslatableText("enchantment.level." + (statusEffect.getAmplifier() + 1)));
		}

		return mutableText;
	}
}
