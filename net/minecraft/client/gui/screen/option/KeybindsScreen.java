/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public class KeybindsScreen
extends GameOptionsScreen {
    @Nullable
    public KeyBinding selectedKeyBinding;
    public long lastKeyCodeUpdateTime;
    private ControlsListWidget controlsList;
    private ButtonWidget resetAllButton;

    public KeybindsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, new TranslatableText("controls.keybinds.title"));
    }

    @Override
    protected void init() {
        this.controlsList = new ControlsListWidget(this, this.client);
        this.addSelectableChild(this.controlsList);
        this.resetAllButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), button -> {
            for (KeyBinding keyBinding : this.gameOptions.allKeys) {
                keyBinding.setBoundKey(keyBinding.getDefaultKey());
            }
            KeyBinding.updateKeysByCode();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selectedKeyBinding != null) {
            this.gameOptions.setKeyCode(this.selectedKeyBinding, InputUtil.Type.MOUSE.createFromCode(button));
            this.selectedKeyBinding = null;
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.selectedKeyBinding != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.gameOptions.setKeyCode(this.selectedKeyBinding, InputUtil.UNKNOWN_KEY);
            } else {
                this.gameOptions.setKeyCode(this.selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
            }
            this.selectedKeyBinding = null;
            this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.controlsList.render(matrices, mouseX, mouseY, delta);
        KeybindsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        boolean bl = false;
        for (KeyBinding keyBinding : this.gameOptions.allKeys) {
            if (keyBinding.isDefault()) continue;
            bl = true;
            break;
        }
        this.resetAllButton.active = bl;
        super.render(matrices, mouseX, mouseY, delta);
    }
}

