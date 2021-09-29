package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeybindsScreen extends GameOptionsScreen {
	@Nullable
	public KeyBinding selectedKeyBinding;
	public long field_34800;
	private ControlsListWidget controlsList;
	private ButtonWidget resetAllButton;

	public KeybindsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, new TranslatableText("controls.keybinds.title"));
	}

	@Override
	protected void init() {
		this.controlsList = new ControlsListWidget(this, this.client);
		this.addSelectableChild(this.controlsList);
		this.resetAllButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), buttonWidget -> {
				for (KeyBinding keyBinding : this.gameOptions.keysAll) {
					keyBinding.setBoundKey(keyBinding.getDefaultKey());
				}

				KeyBinding.updateKeysByCode();
			})
		);
		this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.DONE, buttonWidget -> this.client.setScreen(this.parent))
		);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.selectedKeyBinding != null) {
			this.gameOptions.setKeyCode(this.selectedKeyBinding, InputUtil.Type.MOUSE.createFromCode(button));
			this.selectedKeyBinding = null;
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.selectedKeyBinding != null) {
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				this.gameOptions.setKeyCode(this.selectedKeyBinding, InputUtil.UNKNOWN_KEY);
			} else {
				this.gameOptions.setKeyCode(this.selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
			}

			this.selectedKeyBinding = null;
			this.field_34800 = Util.getMeasuringTimeMs();
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.controlsList.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.gameOptions.keysAll) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetAllButton.active = bl;
		super.render(matrices, mouseX, mouseY, delta);
	}
}
