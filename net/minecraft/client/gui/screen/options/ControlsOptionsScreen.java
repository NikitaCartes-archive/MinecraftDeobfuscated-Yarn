/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.screen.options.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class ControlsOptionsScreen
extends GameOptionsScreen {
    public KeyBinding focusedBinding;
    public long time;
    private ControlsListWidget keyBindingListWidget;
    private ButtonWidget resetButton;

    public ControlsOptionsScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, new TranslatableText("controls.title", new Object[0]));
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, 18, 150, 20, I18n.translate("options.mouse_settings", new Object[0]), buttonWidget -> this.minecraft.openScreen(new MouseOptionsScreen(this, this.gameOptions))));
        this.addButton(Option.AUTO_JUMP.createButton(this.gameOptions, this.width / 2 - 155 + 160, 18, 150));
        this.keyBindingListWidget = new ControlsListWidget(this, this.minecraft);
        this.children.add(this.keyBindingListWidget);
        this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll", new Object[0]), buttonWidget -> {
            for (KeyBinding keyBinding : this.gameOptions.keysAll) {
                keyBinding.setKeyCode(keyBinding.getDefaultKeyCode());
            }
            KeyBinding.updateKeysByCode();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.focusedBinding != null) {
            this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.Type.MOUSE.createFromCode(i));
            this.focusedBinding = null;
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.focusedBinding != null) {
            if (i == 256) {
                this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
            } else {
                this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(i, j));
            }
            this.focusedBinding = null;
            this.time = Util.getMeasuringTimeMs();
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.keyBindingListWidget.render(i, j, f);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 0xFFFFFF);
        boolean bl = false;
        for (KeyBinding keyBinding : this.gameOptions.keysAll) {
            if (keyBinding.isDefault()) continue;
            bl = true;
            break;
        }
        this.resetButton.active = bl;
        super.render(i, j, f);
    }
}

