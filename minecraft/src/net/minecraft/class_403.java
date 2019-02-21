package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class class_403 extends Screen {
	private final Runnable field_2345;
	protected final TextComponent field_2350;
	protected final TextComponent field_2346;
	private final List<String> field_2348 = Lists.<String>newArrayList();
	protected final String field_2349;
	private int field_2347;

	public class_403(Runnable runnable, TextComponent textComponent, TextComponent textComponent2) {
		this(runnable, textComponent, textComponent2, "gui.back");
	}

	public class_403(Runnable runnable, TextComponent textComponent, TextComponent textComponent2, String string) {
		this.field_2345 = runnable;
		this.field_2350 = textComponent;
		this.field_2346 = textComponent2;
		this.field_2349 = I18n.translate(string);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 6 + 168, this.field_2349) {
			@Override
			public void onPressed(double d, double e) {
				class_403.this.field_2345.run();
			}
		});
		this.field_2348.clear();
		this.field_2348.addAll(this.fontRenderer.wrapStringToWidthAsList(this.field_2346.getFormattedText(), this.screenWidth - 50));
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.field_2350.getFormattedText(), this.screenWidth / 2, 70, 16777215);
		int k = 90;

		for (String string : this.field_2348) {
			this.drawStringCentered(this.fontRenderer, string, this.screenWidth / 2, k, 16777215);
			k += 9;
		}

		super.draw(i, j, f);
	}

	@Override
	public void update() {
		super.update();
		if (--this.field_2347 == 0) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.enabled = true;
			}
		}
	}
}
