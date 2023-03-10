/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractInventoryScreen<T extends ScreenHandler>
extends HandledScreen<T> {
    public AbstractInventoryScreen(T screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawStatusEffects(matrices, mouseX, mouseY);
    }

    public boolean hideStatusEffectHud() {
        int i = this.x + this.backgroundWidth + 2;
        int j = this.width - i;
        return j >= 32;
    }

    private void drawStatusEffects(MatrixStack matrices, int mouseX, int mouseY) {
        int i = this.x + this.backgroundWidth + 2;
        int j = this.width - i;
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (collection.isEmpty() || j < 32) {
            return;
        }
        boolean bl = j >= 120;
        int k = 33;
        if (collection.size() > 5) {
            k = 132 / (collection.size() - 1);
        }
        List<StatusEffectInstance> iterable = Ordering.natural().sortedCopy(collection);
        this.drawStatusEffectBackgrounds(matrices, i, k, iterable, bl);
        this.drawStatusEffectSprites(matrices, i, k, iterable, bl);
        if (bl) {
            this.drawStatusEffectDescriptions(matrices, i, k, iterable);
        } else if (mouseX >= i && mouseX <= i + 33) {
            int l = this.y;
            StatusEffectInstance statusEffectInstance = null;
            for (StatusEffectInstance statusEffectInstance2 : iterable) {
                if (mouseY >= l && mouseY <= l + k) {
                    statusEffectInstance = statusEffectInstance2;
                }
                l += k;
            }
            if (statusEffectInstance != null) {
                List<Text> list = List.of(this.getStatusEffectDescription(statusEffectInstance), StatusEffectUtil.durationToString(statusEffectInstance, 1.0f));
                this.renderTooltip(matrices, list, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    private void drawStatusEffectBackgrounds(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects, boolean wide) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = this.y;
        for (StatusEffectInstance statusEffectInstance : statusEffects) {
            if (wide) {
                AbstractInventoryScreen.drawTexture(matrices, x, i, 0, 166, 120, 32);
            } else {
                AbstractInventoryScreen.drawTexture(matrices, x, i, 0, 198, 32, 32);
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
            RenderSystem.setShaderTexture(0, sprite.getAtlasId());
            AbstractInventoryScreen.drawSprite(matrices, x + (wide ? 6 : 7), i + 7, 0, 18, 18, sprite);
            i += height;
        }
    }

    private void drawStatusEffectDescriptions(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects) {
        int i = this.y;
        for (StatusEffectInstance statusEffectInstance : statusEffects) {
            Text text = this.getStatusEffectDescription(statusEffectInstance);
            this.textRenderer.drawWithShadow(matrices, text, (float)(x + 10 + 18), (float)(i + 6), 0xFFFFFF);
            Text text2 = StatusEffectUtil.durationToString(statusEffectInstance, 1.0f);
            this.textRenderer.drawWithShadow(matrices, text2, (float)(x + 10 + 18), (float)(i + 6 + 10), 0x7F7F7F);
            i += height;
        }
    }

    private Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
        MutableText mutableText = statusEffect.getEffectType().getName().copy();
        if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
            mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + (statusEffect.getAmplifier() + 1)));
        }
        return mutableText;
    }
}

