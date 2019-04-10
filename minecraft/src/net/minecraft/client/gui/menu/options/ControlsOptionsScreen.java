package net.minecraft.client.gui.menu.options;

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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends Screen {
	private static final GameOption[] OPTIONS = new GameOption[]{GameOption.INVERT_MOUSE, GameOption.SENSITIVITY, GameOption.TOUCHSCREEN, GameOption.AUTO_JUMP};
	private final Screen parent;
	private final GameOptions options;
	public KeyBinding focusedBinding;
	public long time;
	private KeyBindingListWidget keyBindingListWidget;
	private ButtonWidget resetButton;

	public ControlsOptionsScreen(Screen screen, GameOptions gameOptions) {
		super(new TranslatableTextComponent("controls.title"));
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	protected void init() {
		int i = 0;

		for (GameOption gameOption : OPTIONS) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = 18 + 24 * (i >> 1);
			this.addButton(gameOption.createOptionButton(this.minecraft.options, j, k, 150));
			i++;
		}

		this.keyBindingListWidget = new KeyBindingListWidget(this, this.minecraft);
		this.children.add(this.keyBindingListWidget);
		this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll"), buttonWidget -> {
			for (KeyBinding keyBinding : this.minecraft.options.keysAll) {
				keyBinding.setKeyCode(keyBinding.getDefaultKeyCode());
			}

			KeyBinding.updateKeysByCode();
		}));
		this.addButton(
			new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.focusedBinding != null) {
			this.options.setKeyCode(this.focusedBinding, InputUtil.Type.field_1672.createFromCode(i));
			this.focusedBinding = null;
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.focusedBinding != null) {
			if (i == 256) {
				this.options.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
			} else {
				this.options.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(i, j));
			}

			this.focusedBinding = null;
			this.time = SystemUtil.getMeasuringTimeMs();
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.keyBindingListWidget.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.options.keysAll) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetButton.active = bl;
		super.render(i, j, f);
	}
}
