/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsAcceptRejectButton {
    public final int width;
    public final int height;
    public final int x;
    public final int y;

    public RealmsAcceptRejectButton(int i, int j, int k, int l) {
        this.width = i;
        this.height = j;
        this.x = k;
        this.y = l;
    }

    public void render(int i, int j, int k, int l) {
        int m = i + this.x;
        int n = j + this.y;
        boolean bl = false;
        if (k >= m && k <= m + this.width && l >= n && l <= n + this.height) {
            bl = true;
        }
        this.render(m, n, bl);
    }

    protected abstract void render(int var1, int var2, boolean var3);

    public int getRight() {
        return this.x + this.width;
    }

    public int getBottom() {
        return this.y + this.height;
    }

    public abstract void handleClick(int var1);

    public static void render(List<RealmsAcceptRejectButton> list, RealmsObjectSelectionList realmsObjectSelectionList, int i, int j, int k, int l) {
        for (RealmsAcceptRejectButton realmsAcceptRejectButton : list) {
            if (realmsObjectSelectionList.getRowWidth() <= realmsAcceptRejectButton.getRight()) continue;
            realmsAcceptRejectButton.render(i, j, k, l);
        }
    }

    public static void handleClick(RealmsObjectSelectionList realmsObjectSelectionList, RealmListEntry realmListEntry, List<RealmsAcceptRejectButton> list, int i, double d, double e) {
        int j;
        if (i == 0 && (j = realmsObjectSelectionList.children().indexOf(realmListEntry)) > -1) {
            realmsObjectSelectionList.selectItem(j);
            int k = realmsObjectSelectionList.getRowLeft();
            int l = realmsObjectSelectionList.getRowTop(j);
            int m = (int)(d - (double)k);
            int n = (int)(e - (double)l);
            for (RealmsAcceptRejectButton realmsAcceptRejectButton : list) {
                if (m < realmsAcceptRejectButton.x || m > realmsAcceptRejectButton.getRight() || n < realmsAcceptRejectButton.y || n > realmsAcceptRejectButton.getBottom()) continue;
                realmsAcceptRejectButton.handleClick(j);
            }
        }
    }
}

