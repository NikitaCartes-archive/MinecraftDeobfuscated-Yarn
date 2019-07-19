package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends Screen {
	private static final Option[] OPTIONS = new Option[]{Option.INVERT_MOUSE, Option.SENSITIVITY, Option.TOUCHSCREEN, Option.AUTO_JUMP};
	private final Screen parent;
	private final GameOptions options;
	public KeyBinding focusedBinding;
	public long time;
	private ControlsListWidget keyBindingListWidget;
	private ButtonWidget resetButton;

	public ControlsOptionsScreen(Screen parent, GameOptions options) {
		super(new TranslatableText("controls.title"));
		this.parent = parent;
		this.options = options;
	}

	@Override
	protected void init() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155, 18, 150, 20, I18n.translate("options.mouse_settings"), buttonWidget -> this.minecraft.openScreen(new MouseOptionsScreen(this))
			)
		);
		this.addButton(Option.AUTO_JUMP.createButton(this.minecraft.options, this.width / 2 - 155 + 160, 18, 150));
		this.keyBindingListWidget = new ControlsListWidget(this, this.minecraft);
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.focusedBinding != null) {
			this.options.setKeyCode(this.focusedBinding, InputUtil.Type.MOUSE.createFromCode(button));
			this.focusedBinding = null;
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.focusedBinding != null) {
			if (keyCode == 256) {
				this.options.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
			} else {
				this.options.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(keyCode, scanCode));
			}

			this.focusedBinding = null;
			this.time = Util.getMeasuringTimeMs();
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.keyBindingListWidget.render(mouseX, mouseY, delta);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.options.keysAll) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetButton.active = bl;
		super.render(mouseX, mouseY, delta);
	}
}
