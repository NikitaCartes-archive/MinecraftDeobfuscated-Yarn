package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class AbstractInventoryScreen<T extends ScreenHandler> extends ScreenWithHandler<T> {
	protected boolean offsetGuiForEffects;

	public AbstractInventoryScreen(T screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Override
	protected void init() {
		super.init();
		this.applyStatusEffectOffset();
	}

	protected void applyStatusEffectOffset() {
		if (this.client.player.getStatusEffects().isEmpty()) {
			this.x = (this.width - this.backgroundWidth) / 2;
			this.offsetGuiForEffects = false;
		} else {
			this.x = 160 + (this.width - this.backgroundWidth - 200) / 2;
			this.offsetGuiForEffects = true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		if (this.offsetGuiForEffects) {
			this.drawStatusEffects();
		}
	}

	private void drawStatusEffects() {
		int i = this.x - 124;
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty()) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int j = 33;
			if (collection.size() > 5) {
				j = 132 / (collection.size() - 1);
			}

			Iterable<StatusEffectInstance> iterable = Ordering.natural().sortedCopy(collection);
			this.drawStatusEffectBackgrounds(i, j, iterable);
			this.drawStatusEffectSprites(i, j, iterable);
			this.drawStatusEffectDescriptions(i, j, iterable);
		}
	}

	private void drawStatusEffectBackgrounds(int x, int yIncrement, Iterable<StatusEffectInstance> effects) {
		this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : effects) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.blit(x, i, 0, 166, 140, 32);
			i += yIncrement;
		}
	}

	private void drawStatusEffectSprites(int x, int yIncrement, Iterable<StatusEffectInstance> effects) {
		StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : effects) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
			this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
			blit(x + 6, i + 7, this.getZOffset(), 18, 18, sprite);
			i += yIncrement;
		}
	}

	private void drawStatusEffectDescriptions(int x, int yIncrement, Iterable<StatusEffectInstance> effects) {
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : effects) {
			String string = I18n.translate(statusEffectInstance.getEffectType().getTranslationKey());
			if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
				string = string + ' ' + I18n.translate("enchantment.level." + (statusEffectInstance.getAmplifier() + 1));
			}

			this.textRenderer.drawWithShadow(string, (float)(x + 10 + 18), (float)(i + 6), 16777215);
			String string2 = StatusEffectUtil.durationToString(statusEffectInstance, 1.0F);
			this.textRenderer.drawWithShadow(string2, (float)(x + 10 + 18), (float)(i + 6 + 10), 8355711);
			i += yIncrement;
		}
	}
}
