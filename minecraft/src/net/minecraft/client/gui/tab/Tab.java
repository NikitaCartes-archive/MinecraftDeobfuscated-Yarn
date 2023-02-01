package net.minecraft.client.gui.tab;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface Tab {
	Text getTitle();

	void forEachChild(Consumer<ClickableWidget> consumer);

	void refreshGrid(FocusedRect tabArea);

	default void tick() {
	}
}
