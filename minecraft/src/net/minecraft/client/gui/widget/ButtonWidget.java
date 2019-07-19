package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends AbstractPressableButtonWidget {
	protected final ButtonWidget.PressAction onPress;

	public ButtonWidget(int x, int y, int width, int height, String message, ButtonWidget.PressAction onPress) {
		super(x, y, width, height, message);
		this.onPress = onPress;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(ButtonWidget button);
	}
}
