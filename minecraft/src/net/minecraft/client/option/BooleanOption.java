package net.minecraft.client.option;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BooleanOption extends Option {
	private final Predicate<GameOptions> getter;
	private final BiConsumer<GameOptions, Boolean> setter;
	@Nullable
	private final Text field_26923;

	public BooleanOption(String key, Predicate<GameOptions> getter, BiConsumer<GameOptions, Boolean> setter) {
		this(key, null, getter, setter);
	}

	public BooleanOption(String key, @Nullable Text tooltip, Predicate<GameOptions> getter, BiConsumer<GameOptions, Boolean> setter) {
		super(key);
		this.getter = getter;
		this.setter = setter;
		this.field_26923 = tooltip;
	}

	public void set(GameOptions options, String value) {
		this.set(options, "true".equals(value));
	}

	public void toggle(GameOptions options) {
		this.set(options, !this.get(options));
		options.write();
	}

	private void set(GameOptions options, boolean value) {
		this.setter.accept(options, value);
	}

	public boolean get(GameOptions options) {
		return this.getter.test(options);
	}

	@Override
	public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
		if (this.field_26923 != null) {
			this.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(this.field_26923, 200));
		}

		return new OptionButtonWidget(x, y, width, 20, this, this.getDisplayString(options), button -> {
			this.toggle(options);
			button.setMessage(this.getDisplayString(options));
		});
	}

	public Text getDisplayString(GameOptions options) {
		return ScreenTexts.composeToggleText(this.getDisplayPrefix(), this.get(options));
	}
}
