/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;

@Environment(value=EnvType.CLIENT)
public interface LayoutWidget
extends Widget {
    public void forEachElement(Consumer<Widget> var1);

    @Override
    default public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.forEachElement(element -> element.forEachChild(consumer));
    }

    default public void refreshPositions() {
        this.forEachElement(element -> {
            if (element instanceof LayoutWidget) {
                LayoutWidget layoutWidget = (LayoutWidget)element;
                layoutWidget.refreshPositions();
            }
        });
    }
}

