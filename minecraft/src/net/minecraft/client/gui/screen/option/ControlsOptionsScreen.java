package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends GameOptionsScreen {
	public KeyBinding focusedBinding;
	public long time;
	private ControlsListWidget keyBindingListWidget;
	private ButtonWidget resetButton;

	public ControlsOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, new TranslatableText("controls.title"));
	}

	@Override
	protected void init() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				18,
				150,
				20,
				new TranslatableText("options.mouse_settings"),
				button -> this.client.openScreen(new MouseOptionsScreen(this, this.gameOptions))
			)
		);
		this.addButton(Option.AUTO_JUMP.createButton(this.gameOptions, this.width / 2 - 155 + 160, 18, 150));
		this.keyBindingListWidget = new ControlsListWidget(this, this.client);
		this.children.add(this.keyBindingListWidget);
		this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), button -> {
			for (KeyBinding keyBinding : this.gameOptions.keysAll) {
				keyBinding.setBoundKey(keyBinding.getDefaultKey());
			}

			KeyBinding.updateKeysByCode();
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.DONE, button -> this.client.openScreen(this.parent)));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.focusedBinding != null) {
			this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.Type.MOUSE.createFromCode(button));
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
				this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEY);
			} else {
				this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.fromKeyCode(keyCode, scanCode));
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.keyBindingListWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.gameOptions.keysAll) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetButton.active = bl;
		super.render(matrices, mouseX, mouseY, delta);
	}
}
