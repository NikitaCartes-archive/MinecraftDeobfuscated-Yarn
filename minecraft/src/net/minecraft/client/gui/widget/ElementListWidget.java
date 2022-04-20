package net.minecraft.client.gui.widget;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends EntryListWidget<E> {
	private boolean widgetFocused;

	public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		this.widgetFocused = super.changeFocus(lookForwards);
		if (this.widgetFocused) {
			this.ensureVisible(this.getFocused());
		}

		return this.widgetFocused;
	}

	@Override
	public Selectable.SelectionType getType() {
		return this.widgetFocused ? Selectable.SelectionType.FOCUSED : super.getType();
	}

	@Override
	protected boolean isSelectedEntry(int index) {
		return false;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		E entry = this.getHoveredEntry();
		if (entry != null) {
			entry.appendNarrations(builder.nextMessage());
			this.appendNarrations(builder, entry);
		} else {
			E entry2 = this.getFocused();
			if (entry2 != null) {
				entry2.appendNarrations(builder.nextMessage());
				this.appendNarrations(builder, entry2);
			}
		}

		builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends ElementListWidget.Entry<E>> extends EntryListWidget.Entry<E> implements ParentElement {
		@Nullable
		private Element focused;
		@Nullable
		private Selectable focusedSelectable;
		private boolean dragging;

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Override
		public void setFocused(@Nullable Element focused) {
			this.focused = focused;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}

		public abstract List<? extends Selectable> selectableChildren();

		void appendNarrations(NarrationMessageBuilder builder) {
			List<? extends Selectable> list = this.selectableChildren();
			Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, this.focusedSelectable);
			if (selectedElementNarrationData != null) {
				if (selectedElementNarrationData.selectType.isFocused()) {
					this.focusedSelectable = selectedElementNarrationData.selectable;
				}

				if (list.size() > 1) {
					builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.object_list", selectedElementNarrationData.index + 1, list.size()));
					if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
						builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
					}
				}

				selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
			}
		}
	}
}
