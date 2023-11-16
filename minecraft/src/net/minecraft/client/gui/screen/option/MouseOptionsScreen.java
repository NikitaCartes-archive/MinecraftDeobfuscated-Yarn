package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MouseOptionsScreen extends GameOptionsScreen {
	private OptionListWidget buttonList;

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{
			gameOptions.getMouseSensitivity(),
			gameOptions.getInvertYMouse(),
			gameOptions.getMouseWheelSensitivity(),
			gameOptions.getDiscreteMouseScroll(),
			gameOptions.getTouchscreen()
		};
	}

	public MouseOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, Text.translatable("options.mouse_settings.title"));
	}

	@Override
	protected void init() {
		this.buttonList = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height - 64, 32, 25));
		if (InputUtil.isRawMouseMotionSupported()) {
			this.buttonList
				.addAll(
					(SimpleOption<?>[])Stream.concat(Arrays.stream(getOptions(this.gameOptions)), Stream.of(this.gameOptions.getRawMouseInput())).toArray(SimpleOption[]::new)
				);
		} else {
			this.buttonList.addAll(getOptions(this.gameOptions));
		}

		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			this.gameOptions.write();
			this.client.setScreen(this.parent);
		}).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 16777215);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
	}
}
