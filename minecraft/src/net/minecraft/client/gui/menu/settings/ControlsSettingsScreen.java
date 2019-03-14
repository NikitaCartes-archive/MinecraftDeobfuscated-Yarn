package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.KeyBindingListWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ControlsSettingsScreen extends Screen {
	private static final GameOption[] SETTINGS = new GameOption[]{GameOption.INVERT_MOUSE, GameOption.SENSITIVITY, GameOption.TOUCHSCREEN, GameOption.AUTO_JUMP};
	private final Screen parent;
	protected String title = "Controls";
	private final GameOptions settings;
	public KeyBinding focusedBinding;
	public long field_2723;
	private KeyBindingListWidget keyBindingListWidget;
	private ButtonWidget resetButton;

	public ControlsSettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.keyBindingListWidget = new KeyBindingListWidget(this, this.client);
		this.listeners.add(this.keyBindingListWidget);
		this.focusOn(this.keyBindingListWidget);
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155 + 160, this.screenHeight - 29, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				ControlsSettingsScreen.this.client.openScreen(ControlsSettingsScreen.this.parent);
			}
		});
		this.resetButton = this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight - 29, 150, 20, I18n.translate("controls.resetAll")) {
			@Override
			public void onPressed() {
				for (KeyBinding keyBinding : ControlsSettingsScreen.this.client.options.keysAll) {
					keyBinding.setKeyCode(keyBinding.getDefaultKeyCode());
				}

				KeyBinding.updateKeysByCode();
			}
		});
		this.title = I18n.translate("controls.title");
		int i = 0;

		for (GameOption gameOption : SETTINGS) {
			int j = this.screenWidth / 2 - 155 + i % 2 * 160;
			int k = 18 + 24 * (i >> 1);
			this.addButton(gameOption.method_18520(this.client.options, j, k, 150));
			i++;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.focusedBinding != null) {
			this.settings.setKeyCode(this.focusedBinding, InputUtil.Type.field_1672.createFromCode(i));
			this.focusedBinding = null;
			KeyBinding.updateKeysByCode();
			return true;
		} else if (i == 0 && this.keyBindingListWidget.mouseClicked(d, e, i)) {
			this.setActive(true);
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0 && this.keyBindingListWidget.mouseReleased(d, e, i)) {
			this.setActive(false);
			return true;
		} else {
			return super.mouseReleased(d, e, i);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.focusedBinding != null) {
			if (i == 256) {
				this.settings.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
			} else {
				this.settings.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(i, j));
			}

			this.focusedBinding = null;
			this.field_2723 = SystemUtil.getMeasuringTimeMs();
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.keyBindingListWidget.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.settings.keysAll) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetButton.enabled = bl;
		super.draw(i, j, f);
	}
}
