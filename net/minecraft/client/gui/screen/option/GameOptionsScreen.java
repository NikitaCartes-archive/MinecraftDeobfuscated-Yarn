/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

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

    public static List<OrderedText> getHoveredButtonTooltip(ButtonListWidget buttonList, int mouseX, int mouseY) {
        Optional<ClickableWidget> optional = buttonList.getHoveredButton(mouseX, mouseY);
        if (optional.isPresent() && optional.get() instanceof OrderableTooltip) {
            return ((OrderableTooltip)((Object)optional.get())).getOrderedTooltip();
        }
        return ImmutableList.of();
    }
}

