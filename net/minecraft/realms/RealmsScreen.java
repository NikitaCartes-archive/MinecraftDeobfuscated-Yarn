/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsScreen
extends Screen {
    public RealmsScreen() {
        super(NarratorManager.EMPTY);
    }

    /**
     * Moved from RealmsConstants in 20w10a
     */
    protected static int row(int index) {
        return 40 + index * 13;
    }

    @Override
    public void tick() {
        for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
            if (!(abstractButtonWidget instanceof TickableElement)) continue;
            ((TickableElement)((Object)abstractButtonWidget)).tick();
        }
    }

    public void narrateLabels() {
        List<String> list = this.children.stream().filter(RealmsLabel.class::isInstance).map(RealmsLabel.class::cast).map(RealmsLabel::getText).collect(Collectors.toList());
        Realms.narrateNow(list);
    }
}

