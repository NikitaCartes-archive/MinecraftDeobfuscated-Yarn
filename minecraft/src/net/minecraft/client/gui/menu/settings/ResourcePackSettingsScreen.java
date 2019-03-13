package net.minecraft.client.gui.menu.settings;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AvailableResourcePackListWidget;
import net.minecraft.client.gui.widget.ResourcePackListEntry;
import net.minecraft.client.gui.widget.SelectedResourcePackListWidget;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ResourcePackSettingsScreen extends Screen {
	private final Screen parent;
	private AvailableResourcePackListWidget field_3157;
	private SelectedResourcePackListWidget field_3154;
	private boolean field_3155;

	public ResourcePackSettingsScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new class_4185(this.screenWidth / 2 - 154, this.screenHeight - 48, 150, 20, I18n.translate("resourcePack.openFolder")) {
			@Override
			public void method_1826() {
				SystemUtil.getOperatingSystem().open(ResourcePackSettingsScreen.this.client.getResourcePackDir());
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 4, this.screenHeight - 48, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				if (ResourcePackSettingsScreen.this.field_3155) {
					List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList();

					for (ResourcePackListEntry resourcePackListEntry : ResourcePackSettingsScreen.this.field_3154.getInputListeners()) {
						list.add(resourcePackListEntry.method_2681());
					}

					Collections.reverse(list);
					ResourcePackSettingsScreen.this.client.method_1520().setEnabled(list);
					ResourcePackSettingsScreen.this.client.field_1690.resourcePacks.clear();
					ResourcePackSettingsScreen.this.client.field_1690.incompatibleResourcePacks.clear();

					for (ClientResourcePackContainer clientResourcePackContainer : list) {
						if (!clientResourcePackContainer.sortsTillEnd()) {
							ResourcePackSettingsScreen.this.client.field_1690.resourcePacks.add(clientResourcePackContainer.getName());
							if (!clientResourcePackContainer.getCompatibility().isCompatible()) {
								ResourcePackSettingsScreen.this.client.field_1690.incompatibleResourcePacks.add(clientResourcePackContainer.getName());
							}
						}
					}

					ResourcePackSettingsScreen.this.client.field_1690.write();
					ResourcePackSettingsScreen.this.client.method_1507(ResourcePackSettingsScreen.this.parent);
					ResourcePackSettingsScreen.this.client.reloadResources();
				} else {
					ResourcePackSettingsScreen.this.client.method_1507(ResourcePackSettingsScreen.this.parent);
				}
			}
		});
		AvailableResourcePackListWidget availableResourcePackListWidget = this.field_3157;
		SelectedResourcePackListWidget selectedResourcePackListWidget = this.field_3154;
		this.field_3157 = new AvailableResourcePackListWidget(this.client, 200, this.screenHeight);
		this.field_3157.setX(this.screenWidth / 2 - 4 - 200);
		if (availableResourcePackListWidget != null) {
			this.field_3157.getInputListeners().addAll(availableResourcePackListWidget.getInputListeners());
		}

		this.listeners.add(this.field_3157);
		this.field_3154 = new SelectedResourcePackListWidget(this.client, 200, this.screenHeight);
		this.field_3154.setX(this.screenWidth / 2 + 4);
		if (selectedResourcePackListWidget != null) {
			this.field_3154.getInputListeners().addAll(selectedResourcePackListWidget.getInputListeners());
		}

		this.listeners.add(this.field_3154);
		if (!this.field_3155) {
			this.field_3157.getInputListeners().clear();
			this.field_3154.getInputListeners().clear();
			ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager = this.client.method_1520();
			resourcePackContainerManager.callCreators();
			List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList(resourcePackContainerManager.getAlphabeticallyOrderedContainers());
			list.removeAll(resourcePackContainerManager.getEnabledContainers());

			for (ClientResourcePackContainer clientResourcePackContainer : list) {
				this.field_3157.addEntry(new ResourcePackListEntry(this, clientResourcePackContainer));
			}

			for (ClientResourcePackContainer clientResourcePackContainer : Lists.reverse(Lists.newArrayList(resourcePackContainerManager.getEnabledContainers()))) {
				this.field_3154.addEntry(new ResourcePackListEntry(this, clientResourcePackContainer));
			}
		}
	}

	public void method_2674(ResourcePackListEntry resourcePackListEntry) {
		this.field_3157.getInputListeners().remove(resourcePackListEntry);
		resourcePackListEntry.method_2686(this.field_3154);
		this.method_2660();
	}

	public void method_2663(ResourcePackListEntry resourcePackListEntry) {
		this.field_3154.getInputListeners().remove(resourcePackListEntry);
		this.field_3157.addEntry(resourcePackListEntry);
		this.method_2660();
	}

	public boolean method_2669(ResourcePackListEntry resourcePackListEntry) {
		return this.field_3154.getInputListeners().contains(resourcePackListEntry);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.field_3157.draw(i, j, f);
		this.field_3154.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, I18n.translate("resourcePack.title"), this.screenWidth / 2, 16, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("resourcePack.folderInfo"), this.screenWidth / 2 - 77, this.screenHeight - 26, 8421504);
		super.draw(i, j, f);
	}

	public void method_2660() {
		this.field_3155 = true;
	}
}
