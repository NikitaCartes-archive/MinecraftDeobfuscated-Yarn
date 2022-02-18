package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class class_7064 extends class_7065 {
	private static final Text field_37212 = new TranslatableText("title.32bit.deprecation.realms.header").formatted(Formatting.BOLD);
	private static final Text field_37213 = new TranslatableText("title.32bit.deprecation.realms");
	private static final Text field_37214 = new TranslatableText("title.32bit.deprecation.realms.check");
	private static final Text field_37215 = field_37212.shallowCopy().append("\n").append(field_37213);

	public class_7064(Screen screen) {
		super(field_37212, field_37213, field_37214, field_37215, screen);
	}

	@Override
	protected void method_41160(int i) {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 75, 100 + i, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			if (this.field_37217.isChecked()) {
				this.client.options.field_37208 = true;
				this.client.options.write();
			}

			this.client.setScreen(this.field_37216);
		}));
	}
}
