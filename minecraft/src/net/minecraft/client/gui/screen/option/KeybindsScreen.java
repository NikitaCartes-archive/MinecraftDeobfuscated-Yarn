package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeybindsScreen extends GameOptionsScreen {
	@Nullable
	public KeyBinding selectedKeyBinding;
	public long lastKeyCodeUpdateTime;
	private ControlsListWidget controlsList;
	private ButtonWidget resetAllButton;

	public KeybindsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, Text.translatable("controls.keybinds.title"));
	}

	@Override
	protected void init() {
		this.controlsList = new ControlsListWidget(this, this.client);
		this.addSelectableChild(this.controlsList);
		this.resetAllButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("controls.resetAll"), button -> {
			for (KeyBinding keyBinding : this.gameOptions.allKeys) {
				keyBinding.setBoundKey(keyBinding.getDefaultKey());
			}

			this.controlsList.update();
		}).dimensions(this.width / 2 - 155, this.height - 29, 150, 20).build());
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent))
				.dimensions(this.width / 2 - 155 + 160, this.height - 29, 150, 20)
				.build()
		);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.selectedKeyBinding != null) {
			this.gameOptions.setKeyCode(this.selectedKeyBinding, InputUtil.Type.MOUSE.createFromCode(button));
			this.selectedKeyBinding = null;
			this.controlsList.update();
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
			this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
			this.controlsList.update();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.controlsList.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.gameOptions.allKeys) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetAllButton.active = bl;
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
	}
}
