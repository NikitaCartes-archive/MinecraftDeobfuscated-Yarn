/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class OptionButtonWidget
extends ButtonWidget {
    private final Option option;

    public OptionButtonWidget(int x, int y, int width, int height, Option option, Text text, ButtonWidget.PressAction pressAction) {
        super(x, y, width, height, text, pressAction);
        this.option = option;
    }
}

