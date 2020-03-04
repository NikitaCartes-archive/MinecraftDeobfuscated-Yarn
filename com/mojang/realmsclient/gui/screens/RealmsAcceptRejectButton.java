/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.realms.RealmsObjectSelectionList;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsAcceptRejectButton {
    public final int width;
    public final int height;
    public final int x;
    public final int y;

    public RealmsAcceptRejectButton(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public void render(int offsetX, int offsetY, int mouseX, int mouseY) {
        int i = offsetX + this.x;
        int j = offsetY + this.y;
        boolean bl = false;
        if (mouseX >= i && mouseX <= i + this.width && mouseY >= j && mouseY <= j + this.height) {
            bl = true;
        }
        this.render(i, j, bl);
    }

    protected abstract void render(int var1, int var2, boolean var3);

    public int getRight() {
        return this.x + this.width;
    }

    public int getBottom() {
        return this.y + this.height;
    }

    public abstract void handleClick(int var1);

    public static void render(List<RealmsAcceptRejectButton> buttons, RealmsObjectSelectionList<?> selectionList, int offsetX, int offsetY, int mouseX, int mouseY) {
        for (RealmsAcceptRejectButton realmsAcceptRejectButton : buttons) {
            if (selectionList.getRowWidth() <= realmsAcceptRejectButton.getRight()) continue;
            realmsAcceptRejectButton.render(offsetX, offsetY, mouseX, mouseY);
        }
    }

    public static void handleClick(RealmsObjectSelectionList<?> selectionList, AlwaysSelectedEntryListWidget.Entry<?> entry, List<RealmsAcceptRejectButton> buttons, int button, double mouseX, double mouseY) {
        int i;
        if (button == 0 && (i = selectionList.children().indexOf(entry)) > -1) {
            selectionList.setSelected(i);
            int j = selectionList.getRowLeft();
            int k = selectionList.getRowTop(i);
            int l = (int)(mouseX - (double)j);
            int m = (int)(mouseY - (double)k);
            for (RealmsAcceptRejectButton realmsAcceptRejectButton : buttons) {
                if (l < realmsAcceptRejectButton.x || l > realmsAcceptRejectButton.getRight() || m < realmsAcceptRejectButton.y || m > realmsAcceptRejectButton.getBottom()) continue;
                realmsAcceptRejectButton.handleClick(i);
            }
        }
    }
}

