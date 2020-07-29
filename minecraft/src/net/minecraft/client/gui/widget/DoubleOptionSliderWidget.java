package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5499;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.OrderedText;

@Environment(EnvType.CLIENT)
public class DoubleOptionSliderWidget extends OptionSliderWidget implements class_5499 {
	private final DoubleOption option;

	public DoubleOptionSliderWidget(GameOptions gameOptions, int x, int y, int width, int height, DoubleOption option) {
		super(gameOptions, x, y, width, height, (double)((float)option.getRatio(option.get(gameOptions))));
		this.option = option;
		this.updateMessage();
	}

	@Override
	protected void applyValue() {
		this.option.set(this.options, this.option.getValue(this.value));
		this.options.write();
	}

	@Override
	protected void updateMessage() {
		this.setMessage(this.option.getDisplayString(this.options));
	}

	@Override
	public Optional<List<OrderedText>> method_31047() {
		return this.option.getTooltip();
	}
}
