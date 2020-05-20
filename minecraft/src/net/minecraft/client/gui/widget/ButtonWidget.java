package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends AbstractPressableButtonWidget {
	public static final ButtonWidget.class_5316 field_25035 = (buttonWidget, matrixStack, i, j) -> {
	};
	protected final ButtonWidget.PressAction onPress;
	protected final ButtonWidget.class_5316 field_25036;

	public ButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
		this(x, y, width, height, message, onPress, field_25035);
	}

	public ButtonWidget(int i, int j, int k, int l, Text text, ButtonWidget.PressAction pressAction, ButtonWidget.class_5316 arg) {
		super(i, j, k, l, text);
		this.onPress = pressAction;
		this.field_25036 = arg;
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
	public void renderToolTip(MatrixStack matrixStack, int i, int j) {
		this.field_25036.onTooltip(this, matrixStack, i, j);
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(ButtonWidget button);
	}

	@Environment(EnvType.CLIENT)
	public interface class_5316 {
		void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j);
	}
}
