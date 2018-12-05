package net.minecraft.client.gui.widget;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.settings.ControlsSettingsGui;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.text.TextFormat;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class KeyBindingListWidget extends EntryListWidget<KeyBindingListWidget.Entry> {
	private final ControlsSettingsGui gui;
	private final MinecraftClient client;
	private int field_2733;

	public KeyBindingListWidget(ControlsSettingsGui controlsSettingsGui, MinecraftClient minecraftClient) {
		super(minecraftClient, controlsSettingsGui.width + 45, controlsSettingsGui.height, 63, controlsSettingsGui.height - 32, 20);
		this.gui = controlsSettingsGui;
		this.client = minecraftClient;
		KeyBinding[] keyBindings = ArrayUtils.clone(minecraftClient.options.keysAll);
		Arrays.sort(keyBindings);
		String string = null;

		for (KeyBinding keyBinding : keyBindings) {
			String string2 = keyBinding.method_1423();
			if (!string2.equals(string)) {
				string = string2;
				this.method_1901(new KeyBindingListWidget.class_460(string2));
			}

			int i = minecraftClient.fontRenderer.getStringWidth(I18n.translate(keyBinding.method_1431()));
			if (i > this.field_2733) {
				this.field_2733 = i;
			}

			this.method_1901(new KeyBindingListWidget.class_462(keyBinding));
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
	public abstract static class Entry extends EntryListWidget.Entry<KeyBindingListWidget.Entry> {
	}

	@Environment(EnvType.CLIENT)
	public class class_460 extends KeyBindingListWidget.Entry {
		private final String field_2736;
		private final int field_2737;

		public class_460(String string) {
			this.field_2736 = I18n.translate(string);
			this.field_2737 = KeyBindingListWidget.this.client.fontRenderer.getStringWidth(this.field_2736);
		}

		@Override
		public void drawEntry(int i, int j, int k, int l, boolean bl, float f) {
			KeyBindingListWidget.this.client
				.fontRenderer
				.draw(
					this.field_2736,
					(float)(KeyBindingListWidget.this.client.currentGui.width / 2 - this.field_2737 / 2),
					(float)(this.method_1906() + j - KeyBindingListWidget.this.client.fontRenderer.FONT_HEIGHT - 1),
					16777215
				);
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_462 extends KeyBindingListWidget.Entry {
		private final KeyBinding field_2740;
		private final String field_2741;
		private final ButtonWidget field_2739;
		private final ButtonWidget field_2743;

		private class_462(KeyBinding keyBinding) {
			this.field_2740 = keyBinding;
			this.field_2741 = I18n.translate(keyBinding.method_1431());
			this.field_2739 = new ButtonWidget(0, 0, 0, 75, 20, I18n.translate(keyBinding.method_1431())) {
				@Override
				public void onPressed(double d, double e) {
					KeyBindingListWidget.this.gui.field_2727 = keyBinding;
				}
			};
			this.field_2743 = new ButtonWidget(0, 0, 0, 50, 20, I18n.translate("controls.reset")) {
				@Override
				public void onPressed(double d, double e) {
					KeyBindingListWidget.this.client.options.method_1641(keyBinding, keyBinding.getDefaultKeyCode());
					KeyBinding.method_1426();
				}
			};
		}

		@Override
		public void drawEntry(int i, int j, int k, int l, boolean bl, float f) {
			int m = this.method_1906();
			int n = this.method_1907();
			boolean bl2 = KeyBindingListWidget.this.gui.field_2727 == this.field_2740;
			KeyBindingListWidget.this.client
				.fontRenderer
				.draw(
					this.field_2741,
					(float)(n + 90 - KeyBindingListWidget.this.field_2733),
					(float)(m + j / 2 - KeyBindingListWidget.this.client.fontRenderer.FONT_HEIGHT / 2),
					16777215
				);
			this.field_2743.x = n + 190;
			this.field_2743.y = m;
			this.field_2743.enabled = !this.field_2740.isDefault();
			this.field_2743.draw(k, l, f);
			this.field_2739.x = n + 105;
			this.field_2739.y = m;
			this.field_2739.text = this.field_2740.method_16007();
			boolean bl3 = false;
			if (!this.field_2740.isUnbound()) {
				for (KeyBinding keyBinding : KeyBindingListWidget.this.client.options.keysAll) {
					if (keyBinding != this.field_2740 && this.field_2740.equals(keyBinding)) {
						bl3 = true;
						break;
					}
				}
			}

			if (bl2) {
				this.field_2739.text = TextFormat.WHITE + "> " + TextFormat.YELLOW + this.field_2739.text + TextFormat.WHITE + " <";
			} else if (bl3) {
				this.field_2739.text = TextFormat.RED + this.field_2739.text;
			}

			this.field_2739.draw(k, l, f);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			return this.field_2739.mouseClicked(d, e, i) ? true : this.field_2743.mouseClicked(d, e, i);
		}

		@Override
		public boolean mouseReleased(double d, double e, int i) {
			return this.field_2739.mouseReleased(d, e, i) || this.field_2743.mouseReleased(d, e, i);
		}
	}
}
