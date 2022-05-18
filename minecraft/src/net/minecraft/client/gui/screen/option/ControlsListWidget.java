package net.minecraft.client.gui.screen.option;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class ControlsListWidget extends ElementListWidget<ControlsListWidget.Entry> {
	final KeybindsScreen parent;
	int maxKeyNameLength;

	public ControlsListWidget(KeybindsScreen parent, MinecraftClient client) {
		super(client, parent.width + 45, parent.height, 20, parent.height - 32, 20);
		this.parent = parent;
		KeyBinding[] keyBindings = ArrayUtils.clone((KeyBinding[])client.options.allKeys);
		Arrays.sort(keyBindings);
		String string = null;

		for (KeyBinding keyBinding : keyBindings) {
			String string2 = keyBinding.getCategory();
			if (!string2.equals(string)) {
				string = string2;
				this.addEntry(new ControlsListWidget.CategoryEntry(Text.translatable(string2)));
			}

			Text text = Text.translatable(keyBinding.getTranslationKey());
			int i = client.textRenderer.getWidth(text);
			if (i > this.maxKeyNameLength) {
				this.maxKeyNameLength = i;
			}

			this.addEntry(new ControlsListWidget.KeyBindingEntry(keyBinding, text));
		}
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 15;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 32;
	}

	@Environment(EnvType.CLIENT)
	public class CategoryEntry extends ControlsListWidget.Entry {
		final Text text;
		private final int textWidth;

		public CategoryEntry(Text text) {
			this.text = text;
			this.textWidth = ControlsListWidget.this.client.textRenderer.getWidth(this.text);
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			ControlsListWidget.this.client
				.textRenderer
				.draw(matrices, this.text, (float)(ControlsListWidget.this.client.currentScreen.width / 2 - this.textWidth / 2), (float)(y + entryHeight - 9 - 1), 16777215);
		}

		@Override
		public boolean changeFocus(boolean lookForwards) {
			return false;
		}

		@Override
		public List<? extends Element> children() {
			return Collections.emptyList();
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(new Selectable() {
				@Override
				public Selectable.SelectionType getType() {
					return Selectable.SelectionType.HOVERED;
				}

				@Override
				public void appendNarrations(NarrationMessageBuilder builder) {
					builder.put(NarrationPart.TITLE, CategoryEntry.this.text);
				}
			});
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends ElementListWidget.Entry<ControlsListWidget.Entry> {
	}

	@Environment(EnvType.CLIENT)
	public class KeyBindingEntry extends ControlsListWidget.Entry {
		private final KeyBinding binding;
		private final Text bindingName;
		private final ButtonWidget editButton;
		private final ButtonWidget resetButton;

		KeyBindingEntry(KeyBinding binding, Text bindingName) {
			this.binding = binding;
			this.bindingName = bindingName;
			this.editButton = new ButtonWidget(0, 0, 75, 20, bindingName, button -> ControlsListWidget.this.parent.selectedKeyBinding = binding) {
				@Override
				protected MutableText getNarrationMessage() {
					return binding.isUnbound()
						? Text.translatable("narrator.controls.unbound", bindingName)
						: Text.translatable("narrator.controls.bound", bindingName, super.getNarrationMessage());
				}
			};
			this.resetButton = new ButtonWidget(0, 0, 50, 20, Text.translatable("controls.reset"), button -> {
				ControlsListWidget.this.client.options.setKeyCode(binding, binding.getDefaultKey());
				KeyBinding.updateKeysByCode();
			}) {
				@Override
				protected MutableText getNarrationMessage() {
					return Text.translatable("narrator.controls.reset", bindingName);
				}
			};
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			boolean bl = ControlsListWidget.this.parent.selectedKeyBinding == this.binding;
			float var10003 = (float)(x + 90 - ControlsListWidget.this.maxKeyNameLength);
			ControlsListWidget.this.client.textRenderer.draw(matrices, this.bindingName, var10003, (float)(y + entryHeight / 2 - 9 / 2), 16777215);
			this.resetButton.x = x + 190;
			this.resetButton.y = y;
			this.resetButton.active = !this.binding.isDefault();
			this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
			this.editButton.x = x + 105;
			this.editButton.y = y;
			this.editButton.setMessage(this.binding.getBoundKeyLocalizedText());
			boolean bl2 = false;
			if (!this.binding.isUnbound()) {
				for (KeyBinding keyBinding : ControlsListWidget.this.client.options.allKeys) {
					if (keyBinding != this.binding && this.binding.equals(keyBinding)) {
						bl2 = true;
						break;
					}
				}
			}

			if (bl) {
				this.editButton
					.setMessage(Text.literal("> ").append(this.editButton.getMessage().copy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW));
			} else if (bl2) {
				this.editButton.setMessage(this.editButton.getMessage().copy().formatted(Formatting.RED));
			}

			this.editButton.render(matrices, mouseX, mouseY, tickDelta);
		}

		@Override
		public List<? extends Element> children() {
			return ImmutableList.of(this.editButton, this.resetButton);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(this.editButton, this.resetButton);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return this.editButton.mouseClicked(mouseX, mouseY, button) ? true : this.resetButton.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
		}
	}
}
