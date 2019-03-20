package net.minecraft.client.gui.menu.settings;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AvailableResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ResourcePackListEntry;
import net.minecraft.client.gui.widget.SelectedResourcePackListWidget;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ResourcePackSettingsScreen extends Screen {
	private final Screen parent;
	private AvailableResourcePackListWidget availableList;
	private SelectedResourcePackListWidget selectedList;
	private boolean field_3155;

	public ResourcePackSettingsScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 154, this.screenHeight - 48, 150, 20, I18n.translate("resourcePack.openFolder")) {
			@Override
			public void onPressed() {
				SystemUtil.getOperatingSystem().open(ResourcePackSettingsScreen.this.client.getResourcePackDir());
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 4, this.screenHeight - 48, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				if (ResourcePackSettingsScreen.this.field_3155) {
					List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList();

					for (ResourcePackListEntry resourcePackListEntry : ResourcePackSettingsScreen.this.selectedList.getInputListeners()) {
						list.add(resourcePackListEntry.getResourcePack());
					}

					Collections.reverse(list);
					ResourcePackSettingsScreen.this.client.method_1520().setEnabled(list);
					ResourcePackSettingsScreen.this.client.options.resourcePacks.clear();
					ResourcePackSettingsScreen.this.client.options.incompatibleResourcePacks.clear();

					for (ClientResourcePackContainer clientResourcePackContainer : list) {
						if (!clientResourcePackContainer.sortsTillEnd()) {
							ResourcePackSettingsScreen.this.client.options.resourcePacks.add(clientResourcePackContainer.getName());
							if (!clientResourcePackContainer.getCompatibility().isCompatible()) {
								ResourcePackSettingsScreen.this.client.options.incompatibleResourcePacks.add(clientResourcePackContainer.getName());
							}
						}
					}

					ResourcePackSettingsScreen.this.client.options.write();
					ResourcePackSettingsScreen.this.client.openScreen(ResourcePackSettingsScreen.this.parent);
					ResourcePackSettingsScreen.this.client.reloadResources();
				} else {
					ResourcePackSettingsScreen.this.client.openScreen(ResourcePackSettingsScreen.this.parent);
				}
			}
		});
		AvailableResourcePackListWidget availableResourcePackListWidget = this.availableList;
		SelectedResourcePackListWidget selectedResourcePackListWidget = this.selectedList;
		this.availableList = new AvailableResourcePackListWidget(this.client, 200, this.screenHeight);
		this.availableList.setX(this.screenWidth / 2 - 4 - 200);
		if (availableResourcePackListWidget != null) {
			this.availableList.getInputListeners().addAll(availableResourcePackListWidget.getInputListeners());
		}

		this.listeners.add(this.availableList);
		this.selectedList = new SelectedResourcePackListWidget(this.client, 200, this.screenHeight);
		this.selectedList.setX(this.screenWidth / 2 + 4);
		if (selectedResourcePackListWidget != null) {
			this.selectedList.getInputListeners().addAll(selectedResourcePackListWidget.getInputListeners());
		}

		this.listeners.add(this.selectedList);
		if (!this.field_3155) {
			this.availableList.getInputListeners().clear();
			this.selectedList.getInputListeners().clear();
			ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager = this.client.method_1520();
			resourcePackContainerManager.callCreators();
			List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList(resourcePackContainerManager.getAlphabeticallyOrderedContainers());
			list.removeAll(resourcePackContainerManager.getEnabledContainers());

			for (ClientResourcePackContainer clientResourcePackContainer : list) {
				this.availableList.addEntry(new ResourcePackListEntry(this, clientResourcePackContainer));
			}

			for (ClientResourcePackContainer clientResourcePackContainer : Lists.reverse(Lists.newArrayList(resourcePackContainerManager.getEnabledContainers()))) {
				this.selectedList.addEntry(new ResourcePackListEntry(this, clientResourcePackContainer));
			}
		}
	}

	public void select(ResourcePackListEntry resourcePackListEntry) {
		this.availableList.getInputListeners().remove(resourcePackListEntry);
		resourcePackListEntry.addTo(this.selectedList);
		this.method_2660();
	}

	public void remove(ResourcePackListEntry resourcePackListEntry) {
		this.selectedList.getInputListeners().remove(resourcePackListEntry);
		this.availableList.addEntry(resourcePackListEntry);
		this.method_2660();
	}

	public boolean method_2669(ResourcePackListEntry resourcePackListEntry) {
		return this.selectedList.getInputListeners().contains(resourcePackListEntry);
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.availableList.render(i, j, f);
		this.selectedList.render(i, j, f);
		this.drawStringCentered(this.fontRenderer, I18n.translate("resourcePack.title"), this.screenWidth / 2, 16, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("resourcePack.folderInfo"), this.screenWidth / 2 - 77, this.screenHeight - 26, 8421504);
		super.render(i, j, f);
	}

	public void method_2660() {
		this.field_3155 = true;
	}
}
