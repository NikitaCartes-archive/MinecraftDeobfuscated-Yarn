package net.minecraft.client.gui.screen.resourcepack;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ResourcePackOptionsScreen extends Screen {
	private final Screen parent;
	private AvailableResourcePackListWidget availablePacks;
	private SelectedResourcePackListWidget selectedPacks;
	private boolean edited;

	public ResourcePackOptionsScreen(Screen screen) {
		super(new TranslatableText("resourcePack.title"));
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
			if (this.edited) {
				List<ClientResourcePackContainer> listx = Lists.<ClientResourcePackContainer>newArrayList();

				for (ResourcePackListWidget.ResourcePackEntry resourcePackEntry : this.selectedPacks.children()) {
					listx.add(resourcePackEntry.getPackContainer());
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
		AvailableResourcePackListWidget availableResourcePackListWidget = this.availablePacks;
		SelectedResourcePackListWidget selectedResourcePackListWidget = this.selectedPacks;
		this.availablePacks = new AvailableResourcePackListWidget(this.minecraft, 200, this.height);
		this.availablePacks.setLeftPos(this.width / 2 - 4 - 200);
		if (availableResourcePackListWidget != null) {
			this.availablePacks.children().addAll(availableResourcePackListWidget.children());
		}

		this.children.add(this.availablePacks);
		this.selectedPacks = new SelectedResourcePackListWidget(this.minecraft, 200, this.height);
		this.selectedPacks.setLeftPos(this.width / 2 + 4);
		if (selectedResourcePackListWidget != null) {
			this.selectedPacks.children().addAll(selectedResourcePackListWidget.children());
		}

		this.children.add(this.selectedPacks);
		if (!this.edited) {
			this.availablePacks.children().clear();
			this.selectedPacks.children().clear();
			ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager = this.minecraft.getResourcePackContainerManager();
			resourcePackContainerManager.callCreators();
			List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList(resourcePackContainerManager.getAlphabeticallyOrderedContainers());
			list.removeAll(resourcePackContainerManager.getEnabledContainers());

			for (ClientResourcePackContainer clientResourcePackContainer : list) {
				this.availablePacks.addEntry(new ResourcePackListWidget.ResourcePackEntry(this.availablePacks, this, clientResourcePackContainer));
			}

			for (ClientResourcePackContainer clientResourcePackContainer : Lists.reverse(Lists.newArrayList(resourcePackContainerManager.getEnabledContainers()))) {
				this.selectedPacks.addEntry(new ResourcePackListWidget.ResourcePackEntry(this.selectedPacks, this, clientResourcePackContainer));
			}
		}
	}

	public void select(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		this.availablePacks.children().remove(resourcePackEntry);
		resourcePackEntry.setList(this.selectedPacks);
		this.setEdited();
	}

	public void remove(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		this.selectedPacks.children().remove(resourcePackEntry);
		this.availablePacks.addEntry(resourcePackEntry);
		this.setEdited();
	}

	public boolean isSelected(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		return this.selectedPacks.children().contains(resourcePackEntry);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.availablePacks.render(i, j, f);
		this.selectedPacks.render(i, j, f);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.render(i, j, f);
	}

	public void setEdited() {
		this.edited = true;
	}
}
