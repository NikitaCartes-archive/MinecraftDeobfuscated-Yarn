/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.options;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5499;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameOptionsScreen
extends Screen {
    protected final Screen parent;
    protected final GameOptions gameOptions;

    public GameOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(title);
        this.parent = parent;
        this.gameOptions = gameOptions;
    }

    @Override
    public void removed() {
        this.client.options.write();
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Nullable
    public static List<OrderedText> method_31048(ButtonListWidget buttonListWidget, int i, int j) {
        Optional<AbstractButtonWidget> optional = buttonListWidget.getHoveredButton(i, j);
        if (optional.isPresent() && optional.get() instanceof class_5499) {
            Optional<List<OrderedText>> optional2 = ((class_5499)((Object)optional.get())).method_31047();
            return optional2.orElse(null);
        }
        return null;
    }
}

