package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ItemListWidget;

@Environment(EnvType.CLIENT)
public abstract class AlwaysSelectedItemListWidget<E extends ItemListWidget.Item<E>> extends ItemListWidget<E> {
	private boolean inFocus;

	public AlwaysSelectedItemListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	public boolean changeFocus(boolean bl) {
		this.inFocus = !this.inFocus;
		if (this.inFocus && this.getSelectedItem() == null && this.getItemCount() > 0) {
			this.moveSelection(1);
		}

		return this.inFocus;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_4281<E extends AlwaysSelectedItemListWidget.class_4281<E>> extends ItemListWidget.Item<E> {
		@Override
		public boolean changeFocus(boolean bl) {
			return false;
		}
	}
}
