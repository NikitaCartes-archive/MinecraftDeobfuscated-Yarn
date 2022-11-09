package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Tooltip;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends PressableWidget {
	public static final int DEFAULT_WIDTH_SMALL = 120;
	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;
	protected static final ButtonWidget.NarrationSupplier DEFAULT_NARRATION_SUPPLIER = textSupplier -> (MutableText)textSupplier.get();
	protected final ButtonWidget.PressAction onPress;
	protected final ButtonWidget.NarrationSupplier narrationSupplier;

	public static ButtonWidget.Builder createBuilder(Text message, ButtonWidget.PressAction onPress) {
		return new ButtonWidget.Builder(message, onPress);
	}

	protected ButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.NarrationSupplier narrationSupplier) {
		super(x, y, width, height, message);
		this.onPress = onPress;
		this.narrationSupplier = narrationSupplier;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	@Override
	protected MutableText getNarrationMessage() {
		return this.narrationSupplier.createNarrationMessage(() -> super.getNarrationMessage());
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		this.appendDefaultNarrations(builder);
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final Text message;
		private final ButtonWidget.PressAction onPress;
		@Nullable
		private Tooltip tooltip;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private ButtonWidget.NarrationSupplier narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;

		public Builder(Text message, ButtonWidget.PressAction onPress) {
			this.message = message;
			this.onPress = onPress;
		}

		public ButtonWidget.Builder setPosition(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public ButtonWidget.Builder setWidth(int width) {
			this.width = width;
			return this;
		}

		public ButtonWidget.Builder setSize(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public ButtonWidget.Builder setPositionAndSize(int x, int y, int width, int height) {
			return this.setPosition(x, y).setSize(width, height);
		}

		public ButtonWidget.Builder setTooltip(@Nullable Tooltip tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public ButtonWidget.Builder setNarrationSupplier(ButtonWidget.NarrationSupplier narrationSupplier) {
			this.narrationSupplier = narrationSupplier;
			return this;
		}

		public ButtonWidget build() {
			ButtonWidget buttonWidget = new ButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
			buttonWidget.setTooltip(this.tooltip);
			return buttonWidget;
		}
	}

	@Environment(EnvType.CLIENT)
	public interface NarrationSupplier {
		MutableText createNarrationMessage(Supplier<MutableText> textSupplier);
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(ButtonWidget button);
	}
}
