package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.KeyBindingListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ControlsSettingsGui extends Gui {
	private static final GameOptions.Option[] SETTINGS = new GameOptions.Option[]{
		GameOptions.Option.INVERT_MOUSE, GameOptions.Option.SENSITIVITY, GameOptions.Option.TOUCHSCREEN, GameOptions.Option.AUTO_JUMP
	};
	private final Gui parent;
	protected String title = "Controls";
	private final GameOptions settings;
	public KeyBinding field_2727;
	public long field_2723;
	private KeyBindingListWidget field_2728;
	private ButtonWidget resetButton;

	public ControlsSettingsGui(Gui gui, GameOptions gameOptions) {
		this.parent = gui;
		this.settings = gameOptions;
	}

	@Override
	protected void onInitialized() {
		this.field_2728 = new KeyBindingListWidget(this, this.client);
		this.listeners.add(this.field_2728);
		this.setFocused(this.field_2728);
		this.addButton(new ButtonWidget(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				ControlsSettingsGui.this.client.openGui(ControlsSettingsGui.this.parent);
			}
		});
		this.resetButton = this.addButton(new ButtonWidget(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll")) {
			@Override
			public void onPressed(double d, double e) {
				for (KeyBinding keyBinding : ControlsSettingsGui.this.client.field_1690.keysAll) {
					keyBinding.setKeyCode(keyBinding.getDefaultKeyCode());
				}

				KeyBinding.method_1426();
			}
		});
		this.title = I18n.translate("controls.title");
		int i = 0;

		for (GameOptions.Option option : SETTINGS) {
			if (option.isSlider()) {
				this.addButton(new OptionSliderWidget(option.getId(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), option));
			} else {
				this.addButton(
					new OptionButtonWidget(option.getId(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), option, this.settings.getTranslatedName(option)) {
						@Override
						public void onPressed(double d, double e) {
							ControlsSettingsGui.this.settings.updateOption(this.getOption(), 1);
							this.text = ControlsSettingsGui.this.settings.getTranslatedName(GameOptions.Option.byId(this.id));
						}
					}
				);
			}

			i++;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2727 != null) {
			this.settings.method_1641(this.field_2727, InputUtil.Type.field_1672.createFromCode(i));
			this.field_2727 = null;
			KeyBinding.method_1426();
			return true;
		} else if (i == 0 && this.field_2728.mouseClicked(d, e, i)) {
			this.setActive(true);
			this.setFocused(this.field_2728);
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0 && this.field_2728.mouseReleased(d, e, i)) {
			this.setActive(false);
			return true;
		} else {
			return super.mouseReleased(d, e, i);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_2727 != null) {
			if (i == 256) {
				this.settings.method_1641(this.field_2727, InputUtil.UNKNOWN_KEYCODE);
			} else {
				this.settings.method_1641(this.field_2727, InputUtil.getKeyCode(i, j));
			}

			this.field_2727 = null;
			this.field_2723 = SystemUtil.getMeasuringTimeMs();
			KeyBinding.method_1426();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.field_2728.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
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
