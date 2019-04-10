package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;

@Environment(EnvType.CLIENT)
public abstract class ElementListWidget<E extends ElementListWidget.ElementItem<E>> extends ItemListWidget<E> {
	public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	public boolean changeFocus(boolean bl) {
		boolean bl2 = super.changeFocus(bl);
		if (bl2) {
			this.ensureVisible(this.getFocused());
		}

		return bl2;
	}

	@Override
	protected boolean isSelected(int i) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class ElementItem<E extends ElementListWidget.ElementItem<E>> extends ItemListWidget.Item<E> implements ParentElement {
		@Nullable
		private Element focused;
		private boolean dragging;

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean bl) {
			this.dragging = bl;
		}

		@Override
		public void setFocused(@Nullable Element element) {
			this.focused = element;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}
	}
}
