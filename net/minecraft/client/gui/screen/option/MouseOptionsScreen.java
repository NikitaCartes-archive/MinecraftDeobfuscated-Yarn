/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class MouseOptionsScreen
extends GameOptionsScreen {
    private ButtonListWidget buttonList;

    private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
        return new SimpleOption[]{gameOptions.getMouseSensitivity(), gameOptions.getInvertYMouse(), gameOptions.getMouseWheelSensitivity(), gameOptions.getDiscreteMouseScroll(), gameOptions.getTouchscreen()};
    }

    public MouseOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, Text.translatable("options.mouse_settings.title"));
    }

    @Override
    protected void init() {
        this.buttonList = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        if (InputUtil.isRawMouseMotionSupported()) {
            this.buttonList.addAll((SimpleOption[])Stream.concat(Arrays.stream(MouseOptionsScreen.getOptions(this.gameOptions)), Stream.of(this.gameOptions.getRawMouseInput())).toArray(SimpleOption[]::new));
        } else {
            this.buttonList.addAll(MouseOptionsScreen.getOptions(this.gameOptions));
        }
        this.addSelectableChild(this.buttonList);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            this.gameOptions.write();
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.buttonList.render(matrices, mouseX, mouseY, delta);
        MouseOptionsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

