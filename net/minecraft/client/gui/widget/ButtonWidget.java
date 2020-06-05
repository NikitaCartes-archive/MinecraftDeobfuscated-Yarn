/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ButtonWidget
extends AbstractPressableButtonWidget {
    public static final TooltipSupplier EMPTY = (button, matrices, mouseX, mouseY) -> {};
    protected final PressAction onPress;
    protected final TooltipSupplier tooltipSupplier;

    public ButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        this(x, y, width, height, message, onPress, EMPTY);
    }

    public ButtonWidget(int i, int j, int k, int l, Text text, PressAction pressAction, TooltipSupplier tooltipSupplier) {
        super(i, j, k, l, text);
        this.onPress = pressAction;
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if (this.isHovered()) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface TooltipSupplier {
        public void onTooltip(ButtonWidget var1, MatrixStack var2, int var3, int var4);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface PressAction {
        public void onPress(ButtonWidget var1);
    }
}

