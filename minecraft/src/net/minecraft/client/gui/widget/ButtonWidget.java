package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends AbstractPressableButtonWidget {
	public static final ButtonWidget.TooltipSupplier EMPTY = (button, matrices, mouseX, mouseY) -> {
	};
	protected final ButtonWidget.PressAction onPress;
	protected final ButtonWidget.TooltipSupplier tooltipSupplier;

	public ButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
		this(x, y, width, height, message, onPress, EMPTY);
	}

	public ButtonWidget(int i, int j, int k, int l, Text text, ButtonWidget.PressAction pressAction, ButtonWidget.TooltipSupplier tooltipSupplier) {
		super(i, j, k, l, text);
		this.onPress = pressAction;
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
			this.renderToolTip(matrices, mouseX, mouseY);
		}
	}

	@Override
	public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
		this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(ButtonWidget button);
	}

	@Environment(EnvType.CLIENT)
	public interface TooltipSupplier {
		void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY);
	}
}
