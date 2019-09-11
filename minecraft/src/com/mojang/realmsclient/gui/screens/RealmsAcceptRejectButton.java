package com.mojang.realmsclient.gui.screens;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;

@Environment(EnvType.CLIENT)
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

	protected abstract void render(int i, int j, boolean bl);

	public int getRight() {
		return this.x + this.width;
	}

	public int getBottom() {
		return this.y + this.height;
	}

	public abstract void handleClick(int i);

	public static void render(List<RealmsAcceptRejectButton> list, RealmsObjectSelectionList realmsObjectSelectionList, int i, int j, int k, int l) {
		for (RealmsAcceptRejectButton realmsAcceptRejectButton : list) {
			if (realmsObjectSelectionList.getRowWidth() > realmsAcceptRejectButton.getRight()) {
				realmsAcceptRejectButton.render(i, j, k, l);
			}
		}
	}

	public static void handleClick(
		RealmsObjectSelectionList realmsObjectSelectionList, RealmListEntry realmListEntry, List<RealmsAcceptRejectButton> list, int i, double d, double e
	) {
		if (i == 0) {
			int j = realmsObjectSelectionList.children().indexOf(realmListEntry);
			if (j > -1) {
				realmsObjectSelectionList.selectItem(j);
				int k = realmsObjectSelectionList.getRowLeft();
				int l = realmsObjectSelectionList.getRowTop(j);
				int m = (int)(d - (double)k);
				int n = (int)(e - (double)l);

				for (RealmsAcceptRejectButton realmsAcceptRejectButton : list) {
					if (m >= realmsAcceptRejectButton.x
						&& m <= realmsAcceptRejectButton.getRight()
						&& n >= realmsAcceptRejectButton.y
						&& n <= realmsAcceptRejectButton.getBottom()) {
						realmsAcceptRejectButton.handleClick(j);
					}
				}
			}
		}
	}
}
