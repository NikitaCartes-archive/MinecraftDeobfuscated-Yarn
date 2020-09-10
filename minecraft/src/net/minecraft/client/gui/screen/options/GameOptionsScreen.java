package net.minecraft.client.gui.screen.options;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GameOptionsScreen extends Screen {
	protected final Screen parent;
	protected final GameOptions gameOptions;

	public GameOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
		super(title);
		this.parent = parent;
		this.gameOptions = gameOptions;
	}

	@Override
	public void removed() {
		this.client.options.write();
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.parent);
	}

	@Nullable
	public static List<OrderedText> getHoveredButtonTooltip(ButtonListWidget buttonListWidget, int mouseX, int mouseY) {
		Optional<AbstractButtonWidget> optional = buttonListWidget.getHoveredButton((double)mouseX, (double)mouseY);
		if (optional.isPresent() && optional.get() instanceof OrderableTooltip) {
			Optional<List<OrderedText>> optional2 = ((OrderableTooltip)optional.get()).getOrderedTooltip();
			return (List<OrderedText>)optional2.orElse(null);
		} else {
			return null;
		}
	}
}
