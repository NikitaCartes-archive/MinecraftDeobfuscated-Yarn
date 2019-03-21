package net.minecraft.client.gui.widget;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.settings.ControlsSettingsScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextFormat;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class KeyBindingListWidget extends EntryListWidget<KeyBindingListWidget.Entry> {
	private final ControlsSettingsScreen gui;
	private final MinecraftClient client;
	private int field_2733;

	public KeyBindingListWidget(ControlsSettingsScreen controlsSettingsScreen, MinecraftClient minecraftClient) {
		super(minecraftClient, controlsSettingsScreen.screenWidth + 45, controlsSettingsScreen.screenHeight, 63, controlsSettingsScreen.screenHeight - 32, 20);
		this.gui = controlsSettingsScreen;
		this.client = minecraftClient;
		KeyBinding[] keyBindings = ArrayUtils.clone(minecraftClient.options.keysAll);
		Arrays.sort(keyBindings);
		String string = null;

		for (KeyBinding keyBinding : keyBindings) {
			String string2 = keyBinding.getCategory();
			if (!string2.equals(string)) {
				string = string2;
				this.addEntry(new KeyBindingListWidget.CategoryEntry(string2));
			}

			int i = minecraftClient.textRenderer.getStringWidth(I18n.translate(keyBinding.getId()));
			if (i > this.field_2733) {
				this.field_2733 = i;
			}

			this.addEntry(new KeyBindingListWidget.KeyBindingEntry(keyBinding));
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 15;
	}

	@Override
	public int getEntryWidth() {
		return super.getEntryWidth() + 32;
	}

	@Environment(EnvType.CLIENT)
	public class CategoryEntry extends KeyBindingListWidget.Entry {
		private final String name;
		private final int nameWidth;

		public CategoryEntry(String string) {
			this.name = I18n.translate(string);
			this.nameWidth = KeyBindingListWidget.this.client.textRenderer.getStringWidth(this.name);
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			KeyBindingListWidget.this.client
				.textRenderer
				.draw(this.name, (float)(KeyBindingListWidget.this.client.currentScreen.screenWidth / 2 - this.nameWidth / 2), (float)(this.getY() + j - 9 - 1), 16777215);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends EntryListWidget.Entry<KeyBindingListWidget.Entry> {
	}

	@Environment(EnvType.CLIENT)
	public class KeyBindingEntry extends KeyBindingListWidget.Entry {
		private final KeyBinding binding;
		private final String bindingName;
		private final ButtonWidget editButton;
		private final ButtonWidget resetButton;

		private KeyBindingEntry(KeyBinding keyBinding) {
			this.binding = keyBinding;
			this.bindingName = I18n.translate(keyBinding.getId());
			this.editButton = new ButtonWidget(
				0, 0, 75, 20, I18n.translate(keyBinding.getId()), buttonWidget -> KeyBindingListWidget.this.gui.focusedBinding = keyBinding
			);
			this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> {
				KeyBindingListWidget.this.client.options.setKeyCode(keyBinding, keyBinding.getDefaultKeyCode());
				KeyBinding.updateKeysByCode();
			});
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			int m = this.getY();
			int n = this.getX();
			boolean bl2 = KeyBindingListWidget.this.gui.focusedBinding == this.binding;
			KeyBindingListWidget.this.client
				.textRenderer
				.draw(this.bindingName, (float)(n + 90 - KeyBindingListWidget.this.field_2733), (float)(m + j / 2 - 9 / 2), 16777215);
			this.resetButton.x = n + 190;
			this.resetButton.y = m;
			this.resetButton.active = !this.binding.isDefault();
			this.resetButton.render(k, l, f);
			this.editButton.x = n + 105;
			this.editButton.y = m;
			this.editButton.setMessage(this.binding.getLocalizedName());
			boolean bl3 = false;
			if (!this.binding.isNotBound()) {
				for (KeyBinding keyBinding : KeyBindingListWidget.this.client.options.keysAll) {
					if (keyBinding != this.binding && this.binding.equals(keyBinding)) {
						bl3 = true;
						break;
					}
				}
			}

			if (bl2) {
				this.editButton.setMessage(TextFormat.field_1068 + "> " + TextFormat.field_1054 + this.editButton.getMessage() + TextFormat.field_1068 + " <");
			} else if (bl3) {
				this.editButton.setMessage(TextFormat.field_1061 + this.editButton.getMessage());
			}

			this.editButton.render(k, l, f);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			return this.editButton.mouseClicked(d, e, i) ? true : this.resetButton.mouseClicked(d, e, i);
		}

		@Override
		public boolean mouseReleased(double d, double e, int i) {
			return this.editButton.mouseReleased(d, e, i) || this.resetButton.mouseReleased(d, e, i);
		}
	}
}
