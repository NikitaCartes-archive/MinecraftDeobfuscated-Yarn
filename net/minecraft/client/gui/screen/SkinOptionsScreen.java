/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SkinOptionsScreen
extends Screen {
    private final Screen parent;

    public SkinOptionsScreen(Screen screen) {
        super(new TranslatableText("options.skinCustomisation.title", new Object[0]));
        this.parent = screen;
    }

    @Override
    protected void init() {
        int i = 0;
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            this.addButton(new ButtonWidget(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.getPlayerModelPartDisplayString(playerModelPart), buttonWidget -> {
                this.minecraft.options.togglePlayerModelPart(playerModelPart);
                buttonWidget.setMessage(this.getPlayerModelPartDisplayString(playerModelPart));
            }));
            ++i;
        }
        this.addButton(new OptionButtonWidget(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Option.MAIN_HAND, Option.MAIN_HAND.getMessage(this.minecraft.options), buttonWidget -> {
            Option.MAIN_HAND.cycle(this.minecraft.options, 1);
            this.minecraft.options.write();
            buttonWidget.setMessage(Option.MAIN_HAND.getMessage(this.minecraft.options));
            this.minecraft.options.onPlayerModelPartChange();
        }));
        if (++i % 2 == 1) {
            ++i;
        }
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }

    @Override
    public void removed() {
        this.minecraft.options.write();
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        super.render(i, j, f);
    }

    private String getPlayerModelPartDisplayString(PlayerModelPart playerModelPart) {
        String string = this.minecraft.options.getEnabledPlayerModelParts().contains((Object)playerModelPart) ? I18n.translate("options.on", new Object[0]) : I18n.translate("options.off", new Object[0]);
        return playerModelPart.getOptionName().asFormattedString() + ": " + string;
    }
}

