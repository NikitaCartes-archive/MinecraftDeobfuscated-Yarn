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
import net.minecraft.client.resource.language.I18n;
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
				this.addEntry(new ControlsListWidget.CategoryEntry(string2));
			}

			int i = client.textRenderer.getStringWidth(I18n.translate(keyBinding.getId()));
			if (i > this.maxKeyNameLength) {
				this.maxKeyNameLength = i;
			}

			this.addEntry(new ControlsListWidget.KeyBindingEntry(keyBinding));
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
		private final String name;
		private final int nameWidth;

		public CategoryEntry(String translationKey) {
			this.name = I18n.translate(translationKey);
			this.nameWidth = ControlsListWidget.this.client.textRenderer.getStringWidth(this.name);
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			ControlsListWidget.this.client
				.textRenderer
				.draw(this.name, (float)(ControlsListWidget.this.client.currentScreen.width / 2 - this.nameWidth / 2), (float)(y + height - 9 - 1), 16777215);
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
		private final String bindingName;
		private final ButtonWidget editButton;
		private final ButtonWidget resetButton;

		private KeyBindingEntry(KeyBinding binding) {
			this.binding = binding;
			this.bindingName = I18n.translate(binding.getId());
			this.editButton = new ButtonWidget(0, 0, 75, 20, this.bindingName, buttonWidget -> ControlsListWidget.this.gui.focusedBinding = binding) {
				@Override
				protected String getNarrationMessage() {
					return binding.isNotBound()
						? I18n.translate("narrator.controls.unbound", KeyBindingEntry.this.bindingName)
						: I18n.translate("narrator.controls.bound", KeyBindingEntry.this.bindingName, super.getNarrationMessage());
				}
			};
			this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> {
				ControlsListWidget.this.client.options.setKeyCode(binding, binding.getDefaultKeyCode());
				KeyBinding.updateKeysByCode();
			}) {
				@Override
				protected String getNarrationMessage() {
					return I18n.translate("narrator.controls.reset", KeyBindingEntry.this.bindingName);
				}
			};
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			boolean bl = ControlsListWidget.this.gui.focusedBinding == this.binding;
			ControlsListWidget.this.client
				.textRenderer
				.draw(this.bindingName, (float)(x + 90 - ControlsListWidget.this.maxKeyNameLength), (float)(y + height / 2 - 9 / 2), 16777215);
			this.resetButton.x = x + 190;
			this.resetButton.y = y;
			this.resetButton.active = !this.binding.isDefault();
			this.resetButton.render(mouseX, mouseY, delta);
			this.editButton.x = x + 105;
			this.editButton.y = y;
			this.editButton.setMessage(this.binding.getLocalizedName());
			boolean bl2 = false;
			if (!this.binding.isNotBound()) {
				for (KeyBinding keyBinding : ControlsListWidget.this.client.options.keysAll) {
					if (keyBinding != this.binding && this.binding.equals(keyBinding)) {
						bl2 = true;
						break;
					}
				}
			}

			if (bl) {
				this.editButton.setMessage(Formatting.WHITE + "> " + Formatting.YELLOW + this.editButton.getMessage() + Formatting.WHITE + " <");
			} else if (bl2) {
				this.editButton.setMessage(Formatting.RED + this.editButton.getMessage());
			}

			this.editButton.render(mouseX, mouseY, delta);
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
