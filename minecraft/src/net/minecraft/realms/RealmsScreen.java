package net.minecraft.realms;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.NarratorManager;

@Environment(EnvType.CLIENT)
public abstract class RealmsScreen extends Screen {
	public RealmsScreen() {
		super(NarratorManager.EMPTY);
	}

	/**
	 * Moved from RealmsConstants in 20w10a
	 */
	protected static int row(int index) {
		return 40 + index * 13;
	}

	@Override
	public void tick() {
		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			if (abstractButtonWidget instanceof TickableElement) {
				((TickableElement)abstractButtonWidget).tick();
			}
		}
	}

	public void narrateLabels() {
		List<String> list = (List<String>)this.children
			.stream()
			.filter(RealmsLabel.class::isInstance)
			.map(RealmsLabel.class::cast)
			.map(RealmsLabel::getText)
			.collect(Collectors.toList());
		Realms.narrateNow(list);
	}
}
