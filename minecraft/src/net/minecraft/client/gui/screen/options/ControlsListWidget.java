package net.minecraft.client.gui.screen.options;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class ControlsListWidget extends ElementListWidget<ControlsListWidget.Entry> {
	private final ControlsOptionsScreen gui;
	private int maxKeyNameLength;

	public ControlsListWidget(ControlsOptionsScreen gui, MinecraftClient client) {
		super(client, gui.width + 45, gui.height, 43, gui.height - 32, 20);
		this.gui = gui;
		KeyBinding[] keyBindings = ArrayUtils.clone(client.options.keysAll);
		Arrays.sort(keyBindings);
		String string = null;

		for (KeyBinding keyBinding : keyBindings) {
			String string2 = keyBinding.getCategory();
			if (!string2.equals(string)) {
				string = string2;
				this.addEntry(new ControlsListWidget.CategoryEntry(new TranslatableText(string2)));
			}

			Text text = new TranslatableText(keyBinding.getId());
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
		private final Text name;
		private final int nameWidth;

		public CategoryEntry(Text text) {
			this.name = text;
			this.nameWidth = ControlsListWidget.this.client.textRenderer.getWidth(this.name);
		}

		@Override
		public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
			ControlsListWidget.this.client
				.textRenderer
				.draw(matrices, this.name, (float)(ControlsListWidget.this.client.currentScreen.width / 2 - this.nameWidth / 2), (float)(y + mouseX - 9 - 1), 16777215);
		}

		@Override
		public boolean changeFocus(boolean lookForwards) {
			return false;
		}

		@Override
		public List<? extends Element> children() {
			return Collections.emptyList();
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

		private KeyBindingEntry(KeyBinding keyBinding, Text text) {
			this.binding = keyBinding;
			this.bindingName = text;
			this.editButton = new ButtonWidget(0, 0, 75, 20, text, buttonWidget -> ControlsListWidget.this.gui.focusedBinding = keyBinding) {
				@Override
				protected MutableText getNarrationMessage() {
					return keyBinding.isNotBound()
						? new TranslatableText("narrator.controls.unbound", text)
						: new TranslatableText("narrator.controls.bound", text, super.getNarrationMessage());
				}
			};
			this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), buttonWidget -> {
				ControlsListWidget.this.client.options.setKeyCode(keyBinding, keyBinding.getDefaultKeyCode());
				KeyBinding.updateKeysByCode();
			}) {
				@Override
				protected MutableText getNarrationMessage() {
					return new TranslatableText("narrator.controls.reset", text);
				}
			};
		}

		@Override
		public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
			boolean bl2 = ControlsListWidget.this.gui.focusedBinding == this.binding;
			ControlsListWidget.this.client
				.textRenderer
				.draw(matrices, this.bindingName, (float)(width + 90 - ControlsListWidget.this.maxKeyNameLength), (float)(y + mouseX / 2 - 9 / 2), 16777215);
			this.resetButton.x = width + 190;
			this.resetButton.y = y;
			this.resetButton.active = !this.binding.isDefault();
			this.resetButton.render(matrices, mouseY, i, tickDelta);
			this.editButton.x = width + 105;
			this.editButton.y = y;
			this.editButton.setMessage(this.binding.getLocalizedName());
			boolean bl3 = false;
			if (!this.binding.isNotBound()) {
				for (KeyBinding keyBinding : ControlsListWidget.this.client.options.keysAll) {
					if (keyBinding != this.binding && this.binding.equals(keyBinding)) {
						bl3 = true;
						break;
					}
				}
			}

			if (bl2) {
				this.editButton
					.setMessage(
						new LiteralText("> ").append(this.editButton.getMessage().shallowCopy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
					);
			} else if (bl3) {
				this.editButton.setMessage(this.editButton.getMessage().shallowCopy().formatted(Formatting.RED));
			}

			this.editButton.render(matrices, mouseY, i, tickDelta);
		}

		@Override
		public List<? extends Element> children() {
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
