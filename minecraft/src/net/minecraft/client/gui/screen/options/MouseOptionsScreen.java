package net.minecraft.client.gui.screen.options;

import java.util.Arrays;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class MouseOptionsScreen extends Screen {
	private final Screen parent;
	private ButtonListWidget buttonList;
	private static final Option[] OPTIONS = new Option[]{
		Option.SENSITIVITY, Option.INVERT_MOUSE, Option.MOUSE_WHEEL_SENSITIVITY, Option.DISCRETE_MOUSE_SCROLL, Option.TOUCHSCREEN
	};

	public MouseOptionsScreen(Screen parent) {
		super(new TranslatableText("options.mouse_settings.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.buttonList = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		if (InputUtil.method_21735()) {
			this.buttonList.addAll((Option[])Stream.concat(Arrays.stream(OPTIONS), Stream.of(Option.RAW_MOUSE_INPUT)).toArray(Option[]::new));
		} else {
			this.buttonList.addAll(OPTIONS);
		}

		this.children.add(this.buttonList);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.minecraft.options.write();
			this.minecraft.openScreen(this.parent);
		}));
	}

	@Override
	public void removed() {
		this.minecraft.options.write();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.buttonList.render(mouseX, mouseY, delta);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 5, 16777215);
		super.render(mouseX, mouseY, delta);
	}
}
