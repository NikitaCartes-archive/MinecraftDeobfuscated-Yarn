package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StatusEffectsDisplay {
	private static final Identifier EFFECT_BACKGROUND_LARGE_TEXTURE = Identifier.ofVanilla("container/inventory/effect_background_large");
	private static final Identifier EFFECT_BACKGROUND_SMALL_TEXTURE = Identifier.ofVanilla("container/inventory/effect_background_small");
	private final HandledScreen<?> parent;
	private final MinecraftClient client;

	public StatusEffectsDisplay(HandledScreen<?> parent) {
		this.parent = parent;
		this.client = MinecraftClient.getInstance();
	}

	public void drawStatusEffects(DrawContext context, int mouseX, int mouseY, float tickDelta) {
		this.drawStatusEffects(context, mouseX, mouseY);
	}

	public boolean shouldHideStatusEffectHud() {
		int i = this.parent.x + this.parent.backgroundWidth + 2;
		int j = this.parent.width - i;
		return j >= 32;
	}

	private void drawStatusEffects(DrawContext context, int mouseX, int mouseY) {
		int i = this.parent.x + this.parent.backgroundWidth + 2;
		int j = this.parent.width - i;
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty() && j >= 32) {
			boolean bl = j >= 120;
			int k = 33;
			if (collection.size() > 5) {
				k = 132 / (collection.size() - 1);
			}

			Iterable<StatusEffectInstance> iterable = Ordering.natural().<StatusEffectInstance>sortedCopy(collection);
			this.drawStatusEffectBackgrounds(context, i, k, iterable, bl);
			this.drawStatusEffectSprites(context, i, k, iterable, bl);
			if (bl) {
				this.drawStatusEffectDescriptions(context, i, k, iterable);
			} else if (mouseX >= i && mouseX <= i + 33) {
				int l = this.parent.y;
				StatusEffectInstance statusEffectInstance = null;

				for (StatusEffectInstance statusEffectInstance2 : iterable) {
					if (mouseY >= l && mouseY <= l + k) {
						statusEffectInstance = statusEffectInstance2;
					}

					l += k;
				}

				if (statusEffectInstance != null) {
					List<Text> list = List.of(
						this.getStatusEffectDescription(statusEffectInstance),
						StatusEffectUtil.getDurationText(statusEffectInstance, 1.0F, this.client.world.getTickManager().getTickRate())
					);
					context.drawTooltip(this.parent.getTextRenderer(), list, Optional.empty(), mouseX, mouseY);
				}
			}
		}
	}

	private void drawStatusEffectBackgrounds(DrawContext context, int x, int height, Iterable<StatusEffectInstance> statusEffects, boolean wide) {
		int i = this.parent.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			if (wide) {
				context.drawGuiTexture(RenderLayer::getGuiTextured, EFFECT_BACKGROUND_LARGE_TEXTURE, x, i, 120, 32);
			} else {
				context.drawGuiTexture(RenderLayer::getGuiTextured, EFFECT_BACKGROUND_SMALL_TEXTURE, x, i, 32, 32);
			}

			i += height;
		}
	}

	private void drawStatusEffectSprites(DrawContext context, int x, int height, Iterable<StatusEffectInstance> statusEffects, boolean wide) {
		StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
		int i = this.parent.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
			Sprite sprite = statusEffectSpriteManager.getSprite(registryEntry);
			context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, x + (wide ? 6 : 7), i + 7, 18, 18);
			i += height;
		}
	}

	private void drawStatusEffectDescriptions(DrawContext context, int x, int height, Iterable<StatusEffectInstance> statusEffects) {
		int i = this.parent.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			Text text = this.getStatusEffectDescription(statusEffectInstance);
			context.drawTextWithShadow(this.parent.getTextRenderer(), text, x + 10 + 18, i + 6, 16777215);
			Text text2 = StatusEffectUtil.getDurationText(statusEffectInstance, 1.0F, this.client.world.getTickManager().getTickRate());
			context.drawTextWithShadow(this.parent.getTextRenderer(), text2, x + 10 + 18, i + 6 + 10, 8355711);
			i += height;
		}
	}

	private Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
		MutableText mutableText = statusEffect.getEffectType().value().getName().copy();
		if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
			mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + (statusEffect.getAmplifier() + 1)));
		}

		return mutableText;
	}
}
