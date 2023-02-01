package net.minecraft.client.gui.tab;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.widget.ClickableWidget;

@Environment(EnvType.CLIENT)
public class TabManager {
	private final Consumer<ClickableWidget> tabLoadConsumer;
	private final Consumer<ClickableWidget> tabUnloadConsumer;
	@Nullable
	private Tab currentTab;
	@Nullable
	private FocusedRect tabArea;

	public TabManager(Consumer<ClickableWidget> tabLoadConsumer, Consumer<ClickableWidget> tabUnloadConsumer) {
		this.tabLoadConsumer = tabLoadConsumer;
		this.tabUnloadConsumer = tabUnloadConsumer;
	}

	public void setTabArea(FocusedRect tabArea) {
		this.tabArea = tabArea;
		Tab tab = this.getCurrentTab();
		if (tab != null) {
			tab.refreshGrid(tabArea);
		}
	}

	public void setCurrentTab(Tab tab) {
		if (!Objects.equals(this.currentTab, tab)) {
			if (this.currentTab != null) {
				this.currentTab.forEachChild(this.tabUnloadConsumer);
			}

			this.currentTab = tab;
			tab.forEachChild(this.tabLoadConsumer);
			if (this.tabArea != null) {
				tab.refreshGrid(this.tabArea);
			}
		}
	}

	@Nullable
	public Tab getCurrentTab() {
		return this.currentTab;
	}

	public void tick() {
		Tab tab = this.getCurrentTab();
		if (tab != null) {
			tab.tick();
		}
	}
}
