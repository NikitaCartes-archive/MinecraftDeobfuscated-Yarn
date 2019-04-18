package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.menu.options.ControlsOptionsScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextFormat;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class KeyBindingListWidget extends ElementListWidget<KeyBindingListWidget.Entry> {
	private final ControlsOptionsScreen gui;
	private int field_2733;

	public KeyBindingListWidget(ControlsOptionsScreen controlsOptionsScreen, MinecraftClient minecraftClient) {
		super(minecraftClient, controlsOptionsScreen.width + 45, controlsOptionsScreen.height, 43, controlsOptionsScreen.height - 32, 20);
		this.gui = controlsOptionsScreen;
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
	public int getRowWidth() {
		return super.getRowWidth() + 32;
	}

	@Environment(EnvType.CLIENT)
	public class CategoryEntry extends KeyBindingListWidget.Entry {
		private final String name;
		private final int nameWidth;

		public CategoryEntry(String string) {
			this.name = I18n.translate(string);
			this.nameWidth = KeyBindingListWidget.this.minecraft.textRenderer.getStringWidth(this.name);
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			KeyBindingListWidget.this.minecraft
				.textRenderer
				.draw(this.name, (float)(KeyBindingListWidget.this.minecraft.currentScreen.width / 2 - this.nameWidth / 2), (float)(j + m - 9 - 1), 16777215);
		}

		@Override
		public boolean changeFocus(boolean bl) {
			return false;
		}

		@Override
		public List<? extends Element> children() {
			return Collections.emptyList();
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends ElementListWidget.Entry<KeyBindingListWidget.Entry> {
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
			this.editButton = new ButtonWidget(0, 0, 75, 20, this.bindingName, buttonWidget -> KeyBindingListWidget.this.gui.focusedBinding = keyBinding) {
				@Override
				protected String getNarrationMessage() {
					return keyBinding.isNotBound()
						? I18n.translate("narrator.controls.unbound", KeyBindingEntry.this.bindingName)
						: I18n.translate("narrator.controls.bound", KeyBindingEntry.this.bindingName, super.getNarrationMessage());
				}
			};
			this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> {
				KeyBindingListWidget.this.minecraft.options.setKeyCode(keyBinding, keyBinding.getDefaultKeyCode());
				KeyBinding.updateKeysByCode();
			}) {
				@Override
				protected String getNarrationMessage() {
					return I18n.translate("narrator.controls.reset", KeyBindingEntry.this.bindingName);
				}
			};
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			boolean bl2 = KeyBindingListWidget.this.gui.focusedBinding == this.binding;
			KeyBindingListWidget.this.minecraft
				.textRenderer
				.draw(this.bindingName, (float)(k + 90 - KeyBindingListWidget.this.field_2733), (float)(j + m / 2 - 9 / 2), 16777215);
			this.resetButton.x = k + 190;
			this.resetButton.y = j;
			this.resetButton.active = !this.binding.isDefault();
			this.resetButton.render(n, o, f);
			this.editButton.x = k + 105;
			this.editButton.y = j;
			this.editButton.setMessage(this.binding.getLocalizedName());
			boolean bl3 = false;
			if (!this.binding.isNotBound()) {
				for (KeyBinding keyBinding : KeyBindingListWidget.this.minecraft.options.keysAll) {
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

			this.editButton.render(n, o, f);
		}

		@Override
		public List<? extends Element> children() {
			return ImmutableList.of(this.editButton, this.resetButton);
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
