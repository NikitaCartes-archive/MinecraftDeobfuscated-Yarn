package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Widget {
	void setX(int x);

	void setY(int y);

	int getX();

	int getY();

	int getWidth();

	int getHeight();

	default void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	void forEachChild(Consumer<ClickableWidget> consumer);
}
