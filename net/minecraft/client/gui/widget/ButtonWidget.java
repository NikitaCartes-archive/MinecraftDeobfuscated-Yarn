/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;

@Environment(value=EnvType.CLIENT)
public class ButtonWidget
extends AbstractPressableButtonWidget {
    protected final PressAction onPress;

    public ButtonWidget(int i, int j, int k, int l, String string, PressAction pressAction) {
        super(i, j, k, l, string);
        this.onPress = pressAction;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface PressAction {
        public void onPress(ButtonWidget var1);
    }
}

