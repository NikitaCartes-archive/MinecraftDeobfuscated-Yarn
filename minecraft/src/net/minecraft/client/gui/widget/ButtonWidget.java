package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends AbstractPressableButtonWidget {
	protected final ButtonWidget.PressAction onPress;

	public ButtonWidget(int i, int j, int k, int l, String string, ButtonWidget.PressAction pressAction) {
		super(i, j, k, l, string);
		this.onPress = pressAction;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	@Environment(EnvType.CLIENT)
	public interface PressAction {
		void onPress(ButtonWidget buttonWidget);
	}
}
