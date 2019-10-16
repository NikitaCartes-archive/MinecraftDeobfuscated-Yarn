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
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends GameOptionsScreen {
	public KeyBinding focusedBinding;
	public long time;
	private ControlsListWidget keyBindingListWidget;
	private ButtonWidget resetButton;

	public ControlsOptionsScreen(Screen screen, GameOptions gameOptions) {
		super(screen, gameOptions, new TranslatableText("controls.title"));
	}

	@Override
	protected void init() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				18,
				150,
				20,
				I18n.translate("options.mouse_settings"),
				buttonWidget -> this.minecraft.openScreen(new MouseOptionsScreen(this, this.field_21336))
			)
		);
		this.addButton(Option.AUTO_JUMP.createButton(this.field_21336, this.width / 2 - 155 + 160, 18, 150));
		this.keyBindingListWidget = new ControlsListWidget(this, this.minecraft);
		this.children.add(this.keyBindingListWidget);
		this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll"), buttonWidget -> {
			for (KeyBinding keyBinding : this.field_21336.keysAll) {
				keyBinding.setKeyCode(keyBinding.getDefaultKeyCode());
			}

			KeyBinding.updateKeysByCode();
		}));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.field_21335)
			)
		);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.focusedBinding != null) {
			this.field_21336.setKeyCode(this.focusedBinding, InputUtil.Type.MOUSE.createFromCode(i));
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
				this.field_21336.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
			} else {
				this.field_21336.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(i, j));
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
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.field_21336.keysAll) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetButton.active = bl;
		super.render(i, j, f);
	}
}
