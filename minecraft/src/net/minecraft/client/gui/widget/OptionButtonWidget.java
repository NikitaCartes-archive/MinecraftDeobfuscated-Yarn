package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class OptionButtonWidget extends ButtonWidget implements OrderableTooltip {
	private final Option option;

	public OptionButtonWidget(int x, int y, int width, int height, Option option, Text text, ButtonWidget.PressAction pressAction) {
		super(x, y, width, height, text, pressAction);
		this.option = option;
	}

	public Option getOption() {
		return this.option;
	}

	@Override
	public Optional<List<OrderedText>> getOrderedTooltip() {
		return this.option.getTooltip();
	}
}
