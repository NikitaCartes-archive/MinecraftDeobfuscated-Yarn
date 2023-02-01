/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.tab;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface Tab {
    public Text getTitle();

    public void forEachChild(Consumer<ClickableWidget> var1);

    public void refreshGrid(FocusedRect var1);

    default public void tick() {
    }
}

