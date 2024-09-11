package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeybindsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("controls.keybinds.title");
	@Nullable
	public KeyBinding selectedKeyBinding;
	public long lastKeyCodeUpdateTime;
	private ControlsListWidget controlsList;
	private ButtonWidget resetAllButton;

	public KeybindsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, TITLE_TEXT);
	}

	@Override
	protected void initBody() {
		this.controlsList = this.layout.addBody(new ControlsListWidget(this, this.client));
	}

	@Override
	protected void addOptions() {
	}

	@Override
	protected void initFooter() {
		this.resetAllButton = ButtonWidget.builder(Text.translatable("controls.resetAll"), button -> {
			for (KeyBinding keyBinding : this.gameOptions.allKeys) {
				keyBinding.setBoundKey(keyBinding.getDefaultKey());
			}

			this.controlsList.update();
		}).build();
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget.add(this.resetAllButton);
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
		this.controlsList.position(this.width, this.layout);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.selectedKeyBinding != null) {
			this.selectedKeyBinding.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
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
				this.selectedKeyBinding.setBoundKey(InputUtil.UNKNOWN_KEY);
			} else {
				this.selectedKeyBinding.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode));
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
		boolean bl = false;

		for (KeyBinding keyBinding : this.gameOptions.allKeys) {
			if (!keyBinding.isDefault()) {
				bl = true;
				break;
			}
		}

		this.resetAllButton.active = bl;
	}
}
