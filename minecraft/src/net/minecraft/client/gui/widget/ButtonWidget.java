package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends PressableWidget {
	public static final ButtonWidget.TooltipSupplier EMPTY = (button, matrices, mouseX, mouseY) -> {
	};
	protected final ButtonWidget.PressAction onPress;
	protected final ButtonWidget.TooltipSupplier tooltipSupplier;

	public ButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
		this(x, y, width, height, message, onPress, EMPTY);
	}

	public ButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.TooltipSupplier tooltipSupplier) {
		super(x, y, width, height, message);
		this.onPress = onPress;
		this.tooltipSupplier = tooltipSupplier;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.renderButton(matrices, mouseX, mouseY, delta);
		if (this.isHovered()) {
			this.renderTooltip(matrices, mouseX, mouseY);
		}
	}

	@Override
	public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
		this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		this.method_37021(builder);
		this.tooltipSupplier.method_37023(text -> builder.put(NarrationPart.HINT, text));
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(ButtonWidget button);
	}

	@Environment(EnvType.CLIENT)
	public interface TooltipSupplier {
		void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY);

		default void method_37023(Consumer<Text> consumer) {
		}
	}
}
