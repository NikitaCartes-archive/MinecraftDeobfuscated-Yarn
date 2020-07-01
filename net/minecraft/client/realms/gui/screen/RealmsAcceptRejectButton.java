/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.util.math.MatrixStack;

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

    public void render(MatrixStack matrices, int i, int j, int k, int l) {
        int m = i + this.x;
        int n = j + this.y;
        boolean bl = false;
        if (k >= m && k <= m + this.width && l >= n && l <= n + this.height) {
            bl = true;
        }
        this.render(matrices, m, n, bl);
    }

    protected abstract void render(MatrixStack var1, int var2, int var3, boolean var4);

    public int getRight() {
        return this.x + this.width;
    }

    public int getBottom() {
        return this.y + this.height;
    }

    public abstract void handleClick(int var1);

    public static void render(MatrixStack matrices, List<RealmsAcceptRejectButton> list, RealmsObjectSelectionList<?> realmsObjectSelectionList, int i, int j, int k, int l) {
        for (RealmsAcceptRejectButton realmsAcceptRejectButton : list) {
            if (realmsObjectSelectionList.getRowWidth() <= realmsAcceptRejectButton.getRight()) continue;
            realmsAcceptRejectButton.render(matrices, i, j, k, l);
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

