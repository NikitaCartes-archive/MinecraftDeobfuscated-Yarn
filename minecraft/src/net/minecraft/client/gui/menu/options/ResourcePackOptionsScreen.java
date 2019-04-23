package net.minecraft.client.gui.menu.options;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AvailableResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ResourcePackListWidget;
import net.minecraft.client.gui.widget.SelectedResourcePackListWidget;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ResourcePackOptionsScreen extends Screen {
	private final Screen parent;
	private AvailableResourcePackListWidget availableList;
	private SelectedResourcePackListWidget selectedList;
	private boolean field_3155;

	public ResourcePackOptionsScreen(Screen screen) {
		super(new TranslatableComponent("resourcePack.title"));
		this.parent = screen;
	}

	@Override
	protected void init() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 48,
				150,
				20,
				I18n.translate("resourcePack.openFolder"),
				buttonWidget -> SystemUtil.getOperatingSystem().open(this.minecraft.getResourcePackDir())
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
			if (this.field_3155) {
				List<ClientResourcePackContainer> listx = Lists.<ClientResourcePackContainer>newArrayList();

				for (ResourcePackListWidget.ResourcePackItem resourcePackItem : this.selectedList.children()) {
					listx.add(resourcePackItem.getPackContainer());
				}

				Collections.reverse(listx);
				this.minecraft.getResourcePackContainerManager().setEnabled(listx);
				this.minecraft.options.resourcePacks.clear();
				this.minecraft.options.incompatibleResourcePacks.clear();

				for (ClientResourcePackContainer clientResourcePackContainerx : listx) {
					if (!clientResourcePackContainerx.isPositionFixed()) {
						this.minecraft.options.resourcePacks.add(clientResourcePackContainerx.getName());
						if (!clientResourcePackContainerx.getCompatibility().isCompatible()) {
							this.minecraft.options.incompatibleResourcePacks.add(clientResourcePackContainerx.getName());
						}
					}
				}

				this.minecraft.options.write();
				this.minecraft.openScreen(this.parent);
				this.minecraft.reloadResources();
			} else {
				this.minecraft.openScreen(this.parent);
			}
		}));
		AvailableResourcePackListWidget availableResourcePackListWidget = this.availableList;
		SelectedResourcePackListWidget selectedResourcePackListWidget = this.selectedList;
		this.availableList = new AvailableResourcePackListWidget(this.minecraft, 200, this.height);
		this.availableList.setLeftPos(this.width / 2 - 4 - 200);
		if (availableResourcePackListWidget != null) {
			this.availableList.children().addAll(availableResourcePackListWidget.children());
		}

		this.children.add(this.availableList);
		this.selectedList = new SelectedResourcePackListWidget(this.minecraft, 200, this.height);
		this.selectedList.setLeftPos(this.width / 2 + 4);
		if (selectedResourcePackListWidget != null) {
			this.selectedList.children().addAll(selectedResourcePackListWidget.children());
		}

		this.children.add(this.selectedList);
		if (!this.field_3155) {
			this.availableList.children().clear();
			this.selectedList.children().clear();
			ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager = this.minecraft.getResourcePackContainerManager();
			resourcePackContainerManager.callCreators();
			List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList(resourcePackContainerManager.getAlphabeticallyOrderedContainers());
			list.removeAll(resourcePackContainerManager.getEnabledContainers());

			for (ClientResourcePackContainer clientResourcePackContainer : list) {
				this.availableList.addEntry(new ResourcePackListWidget.ResourcePackItem(this.availableList, this, clientResourcePackContainer));
			}

			for (ClientResourcePackContainer clientResourcePackContainer : Lists.reverse(Lists.newArrayList(resourcePackContainerManager.getEnabledContainers()))) {
				this.selectedList.addEntry(new ResourcePackListWidget.ResourcePackItem(this.selectedList, this, clientResourcePackContainer));
			}
		}
	}

	public void select(ResourcePackListWidget.ResourcePackItem resourcePackItem) {
		this.availableList.children().remove(resourcePackItem);
		resourcePackItem.method_20145(this.selectedList);
		this.method_2660();
	}

	public void remove(ResourcePackListWidget.ResourcePackItem resourcePackItem) {
		this.selectedList.children().remove(resourcePackItem);
		this.availableList.addEntry(resourcePackItem);
		this.method_2660();
	}

	public boolean method_2669(ResourcePackListWidget.ResourcePackItem resourcePackItem) {
		return this.selectedList.children().contains(resourcePackItem);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.availableList.render(i, j, f);
		this.selectedList.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.render(i, j, f);
	}

	public void method_2660() {
		this.field_3155 = true;
	}
}
