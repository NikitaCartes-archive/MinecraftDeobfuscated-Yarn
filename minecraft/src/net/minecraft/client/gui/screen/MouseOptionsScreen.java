package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class MouseOptionsScreen extends Screen {
	private final Screen parent;
	private ButtonListWidget buttonList;
	private static final Option[] OPTIONS = new Option[]{
		Option.SENSITIVITY, Option.INVERT_MOUSE, Option.MOUSE_WHEEL_SENSITIVITY, Option.DISCRETE_MOUSE_SCROLL, Option.TOUCHSCREEN
	};

	public MouseOptionsScreen(Screen screen) {
		super(new TranslatableText("options.mouse_settings.title"));
		this.parent = screen;
	}

	@Override
	protected void init() {
		this.buttonList = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		this.buttonList.addAll(OPTIONS);
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
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.buttonList.render(i, j, f);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 5, 16777215);
		super.render(i, j, f);
	}
}
