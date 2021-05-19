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
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends EntryListWidget<E> {
	private boolean field_33781;

	public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		this.field_33781 = super.changeFocus(lookForwards);
		if (this.field_33781) {
			this.ensureVisible(this.getFocused());
		}

		return this.field_33781;
	}

	@Override
	public Selectable.SelectionType getType() {
		return this.field_33781 ? Selectable.SelectionType.FOCUSED : super.getType();
	}

	@Override
	protected boolean isSelectedEntry(int index) {
		return false;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		E entry = this.method_37019();
		if (entry != null) {
			entry.method_37024(builder.nextMessage());
			this.method_37017(builder, entry);
		} else {
			E entry2 = this.getFocused();
			if (entry2 != null) {
				entry2.method_37024(builder.nextMessage());
				this.method_37017(builder, entry2);
			}
		}

		builder.put(NarrationPart.USAGE, new TranslatableText("narration.component_list.usage"));
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends ElementListWidget.Entry<E>> extends EntryListWidget.Entry<E> implements ParentElement {
		@Nullable
		private Element focused;
		@Nullable
		private Selectable field_33782;
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

		public abstract List<? extends Selectable> method_37025();

		void method_37024(NarrationMessageBuilder narrationMessageBuilder) {
			List<? extends Selectable> list = this.method_37025();
			Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, this.field_33782);
			if (selectedElementNarrationData != null) {
				if (selectedElementNarrationData.selectType.isFocused()) {
					this.field_33782 = selectedElementNarrationData.selectable;
				}

				if (list.size() > 1) {
					narrationMessageBuilder.put(
						NarrationPart.POSITION, new TranslatableText("narrator.position.object_list", selectedElementNarrationData.index + 1, list.size())
					);
					if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
						narrationMessageBuilder.put(NarrationPart.USAGE, new TranslatableText("narration.component_list.usage"));
					}
				}

				selectedElementNarrationData.selectable.appendNarrations(narrationMessageBuilder.nextMessage());
			}
		}
	}
}
