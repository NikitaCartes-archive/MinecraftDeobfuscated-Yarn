package net.minecraft.client.gui.tab;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface Tab {
	Text getTitle();

	void forEachChild(Consumer<ClickableWidget> consumer);

	void refreshGrid(ScreenRect tabArea);
}
