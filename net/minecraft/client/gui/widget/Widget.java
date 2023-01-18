/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;

@Environment(value=EnvType.CLIENT)
public interface Widget {
    public void setX(int var1);

    public void setY(int var1);

    public int getX();

    public int getY();

    public int getWidth();

    public int getHeight();

    default public void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public void forEachChild(Consumer<ClickableWidget> var1);
}

