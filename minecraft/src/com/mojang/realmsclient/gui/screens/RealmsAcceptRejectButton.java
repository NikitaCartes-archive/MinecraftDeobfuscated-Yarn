package com.mojang.realmsclient.gui.screens;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.realms.RealmsObjectSelectionList;

@Environment(EnvType.CLIENT)
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

	protected abstract void render(int x, int y, boolean hovered);

	public int getRight() {
		return this.x + this.width;
	}

	public int getBottom() {
		return this.y + this.height;
	}

	public abstract void handleClick(int index);

	public static void render(List<RealmsAcceptRejectButton> buttons, RealmsObjectSelectionList<?> selectionList, int offsetX, int offsetY, int mouseX, int mouseY) {
		for (RealmsAcceptRejectButton realmsAcceptRejectButton : buttons) {
			if (selectionList.getRowWidth() > realmsAcceptRejectButton.getRight()) {
				realmsAcceptRejectButton.render(offsetX, offsetY, mouseX, mouseY);
			}
		}
	}

	public static void handleClick(
		RealmsObjectSelectionList<?> selectionList,
		AlwaysSelectedEntryListWidget.Entry<?> entry,
		List<RealmsAcceptRejectButton> buttons,
		int button,
		double mouseX,
		double mouseY
	) {
		if (button == 0) {
			int i = selectionList.children().indexOf(entry);
			if (i > -1) {
				selectionList.setSelected(i);
				int j = selectionList.getRowLeft();
				int k = selectionList.getRowTop(i);
				int l = (int)(mouseX - (double)j);
				int m = (int)(mouseY - (double)k);

				for (RealmsAcceptRejectButton realmsAcceptRejectButton : buttons) {
					if (l >= realmsAcceptRejectButton.x
						&& l <= realmsAcceptRejectButton.getRight()
						&& m >= realmsAcceptRejectButton.y
						&& m <= realmsAcceptRejectButton.getBottom()) {
						realmsAcceptRejectButton.handleClick(i);
					}
				}
			}
		}
	}
}
