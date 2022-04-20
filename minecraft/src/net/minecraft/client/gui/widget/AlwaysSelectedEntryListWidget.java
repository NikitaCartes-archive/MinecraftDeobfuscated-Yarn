package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class AlwaysSelectedEntryListWidget<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends EntryListWidget<E> {
	private static final Text SELECTION_USAGE_TEXT = Text.translatable("narration.selection.usage");
	private boolean inFocus;

	public AlwaysSelectedEntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		if (!this.inFocus && this.getEntryCount() == 0) {
			return false;
		} else {
			this.inFocus = !this.inFocus;
			if (this.inFocus && this.getSelectedOrNull() == null && this.getEntryCount() > 0) {
				this.moveSelection(EntryListWidget.MoveDirection.DOWN);
			} else if (this.inFocus && this.getSelectedOrNull() != null) {
				this.ensureSelectedEntryVisible();
			}

			return this.inFocus;
		}
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		E entry = this.getHoveredEntry();
		if (entry != null) {
			this.appendNarrations(builder.nextMessage(), entry);
			entry.appendNarrations(builder);
		} else {
			E entry2 = this.getSelectedOrNull();
			if (entry2 != null) {
				this.appendNarrations(builder.nextMessage(), entry2);
				entry2.appendNarrations(builder);
			}
		}

		if (this.isFocused()) {
			builder.put(NarrationPart.USAGE, SELECTION_USAGE_TEXT);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends EntryListWidget.Entry<E> implements Narratable {
		@Override
		public boolean changeFocus(boolean lookForwards) {
			return false;
		}

		public abstract Text getNarration();

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {
			builder.put(NarrationPart.TITLE, this.getNarration());
		}
	}
}
