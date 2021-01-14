package net.minecraft.client.option;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CyclingOption extends Option {
	private final BiConsumer<GameOptions, Integer> setter;
	private final BiFunction<GameOptions, CyclingOption, Text> messageProvider;

	public CyclingOption(String key, BiConsumer<GameOptions, Integer> setter, BiFunction<GameOptions, CyclingOption, Text> messageProvider) {
		super(key);
		this.setter = setter;
		this.messageProvider = messageProvider;
	}

	public void cycle(GameOptions options, int amount) {
		this.setter.accept(options, amount);
		options.write();
	}

	@Override
	public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
		return new OptionButtonWidget(x, y, width, 20, this, this.getMessage(options), button -> {
			this.cycle(options, 1);
			button.setMessage(this.getMessage(options));
		});
	}

	public Text getMessage(GameOptions options) {
		return (Text)this.messageProvider.apply(options, this);
	}
}
