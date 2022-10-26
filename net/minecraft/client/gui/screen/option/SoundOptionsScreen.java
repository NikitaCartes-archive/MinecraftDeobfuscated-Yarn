/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SoundOptionsScreen
extends GameOptionsScreen {
    private ButtonListWidget optionButtons;

    private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
        return new SimpleOption[]{gameOptions.getShowSubtitles(), gameOptions.getDirectionalAudio()};
    }

    public SoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, Text.translatable("options.sounds.title"));
    }

    @Override
    protected void init() {
        this.optionButtons = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.optionButtons.addSingleOptionEntry(this.gameOptions.getSoundVolumeOption(SoundCategory.MASTER));
        this.optionButtons.addAll(this.getVolumeOptions());
        this.optionButtons.addSingleOptionEntry(this.gameOptions.getSoundDevice());
        this.optionButtons.addAll(SoundOptionsScreen.getOptions(this.gameOptions));
        this.addSelectableChild(this.optionButtons);
        this.addDrawableChild(ButtonWidget.createBuilder(ScreenTexts.DONE, button -> {
            this.client.options.write();
            this.client.setScreen(this.parent);
        }).setPositionAndSize(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    private SimpleOption<?>[] getVolumeOptions() {
        return (SimpleOption[])Arrays.stream(SoundCategory.values()).filter(category -> category != SoundCategory.MASTER).map(category -> this.gameOptions.getSoundVolumeOption((SoundCategory)((Object)category))).toArray(SimpleOption[]::new);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.render(matrices, this.optionButtons, mouseX, mouseY, delta);
    }
}

