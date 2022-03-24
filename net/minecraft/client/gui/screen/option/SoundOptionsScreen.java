/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundOptionsScreen
extends GameOptionsScreen {
    @Nullable
    private ClickableWidget directionalAudioButton;

    public SoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, new TranslatableText("options.sounds.title"));
    }

    @Override
    protected void init() {
        int i = this.height / 6 - 12;
        int j = 22;
        int k = 0;
        this.addDrawableChild(new SoundSliderWidget(this.client, this.width / 2 - 155 + k % 2 * 160, i + 22 * (k >> 1), SoundCategory.MASTER, 310));
        k += 2;
        for (SoundCategory soundCategory : SoundCategory.values()) {
            if (soundCategory == SoundCategory.MASTER) continue;
            this.addDrawableChild(new SoundSliderWidget(this.client, this.width / 2 - 155 + k % 2 * 160, i + 22 * (k >> 1), soundCategory, 150));
            ++k;
        }
        if (k % 2 == 1) {
            ++k;
        }
        this.addDrawableChild(this.gameOptions.getSoundDevice().createButton(this.gameOptions, this.width / 2 - 155, i + 22 * (k >> 1), 310));
        this.addDrawableChild(this.gameOptions.getShowSubtitles().createButton(this.gameOptions, this.width / 2 - 155, i + 22 * ((k += 2) >> 1), 150));
        this.directionalAudioButton = this.gameOptions.getDirectionalAudio().createButton(this.gameOptions, this.width / 2 + 5, i + 22 * (k >> 1), 150);
        this.addDrawableChild(this.directionalAudioButton);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, i + 22 * ((k += 2) >> 1), 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        SoundOptionsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.directionalAudioButton != null && this.directionalAudioButton.isMouseOver(mouseX, mouseY)) {
            List<OrderedText> list = ((OrderableTooltip)((Object)this.directionalAudioButton)).getOrderedTooltip();
            this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
        }
    }
}

