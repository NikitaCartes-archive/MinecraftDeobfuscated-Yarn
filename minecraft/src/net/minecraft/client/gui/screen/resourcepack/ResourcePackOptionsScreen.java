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
	private AvailableResourcePackListWidget field_3157;
	private SelectedResourcePackListWidget selectedList;
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

				for (ResourcePackListWidget.ResourcePackEntry resourcePackEntry : this.selectedList.children()) {
					listx.add(resourcePackEntry.method_20150());
				}

				Collections.reverse(listx);
				this.minecraft.getResourcePackContainerManager().setEnabled(listx);
				this.minecraft.field_1690.resourcePacks.clear();
				this.minecraft.field_1690.incompatibleResourcePacks.clear();

				for (ClientResourcePackContainer clientResourcePackContainerx : listx) {
					if (!clientResourcePackContainerx.isPositionFixed()) {
						this.minecraft.field_1690.resourcePacks.add(clientResourcePackContainerx.getName());
						if (!clientResourcePackContainerx.getCompatibility().isCompatible()) {
							this.minecraft.field_1690.incompatibleResourcePacks.add(clientResourcePackContainerx.getName());
						}
					}
				}

				this.minecraft.field_1690.write();
				this.minecraft.method_1507(this.parent);
				this.minecraft.reloadResources();
			} else {
				this.minecraft.method_1507(this.parent);
			}
		}));
		AvailableResourcePackListWidget availableResourcePackListWidget = this.field_3157;
		SelectedResourcePackListWidget selectedResourcePackListWidget = this.selectedList;
		this.field_3157 = new AvailableResourcePackListWidget(this.minecraft, 200, this.height);
		this.field_3157.setLeftPos(this.width / 2 - 4 - 200);
		if (availableResourcePackListWidget != null) {
			this.field_3157.children().addAll(availableResourcePackListWidget.children());
		}

		this.children.add(this.field_3157);
		this.selectedList = new SelectedResourcePackListWidget(this.minecraft, 200, this.height);
		this.selectedList.setLeftPos(this.width / 2 + 4);
		if (selectedResourcePackListWidget != null) {
			this.selectedList.children().addAll(selectedResourcePackListWidget.children());
		}

		this.children.add(this.selectedList);
		if (!this.edited) {
			this.field_3157.children().clear();
			this.selectedList.children().clear();
			ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager = this.minecraft.getResourcePackContainerManager();
			resourcePackContainerManager.callCreators();
			List<ClientResourcePackContainer> list = Lists.<ClientResourcePackContainer>newArrayList(resourcePackContainerManager.getAlphabeticallyOrderedContainers());
			list.removeAll(resourcePackContainerManager.getEnabledContainers());

			for (ClientResourcePackContainer clientResourcePackContainer : list) {
				this.field_3157.addEntry(new ResourcePackListWidget.ResourcePackEntry(this.field_3157, this, clientResourcePackContainer));
			}

			for (ClientResourcePackContainer clientResourcePackContainer : Lists.reverse(Lists.newArrayList(resourcePackContainerManager.getEnabledContainers()))) {
				this.selectedList.addEntry(new ResourcePackListWidget.ResourcePackEntry(this.selectedList, this, clientResourcePackContainer));
			}
		}
	}

	public void select(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		this.field_3157.children().remove(resourcePackEntry);
		resourcePackEntry.method_20145(this.selectedList);
		this.setEdited();
	}

	public void remove(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		this.selectedList.children().remove(resourcePackEntry);
		this.field_3157.addEntry(resourcePackEntry);
		this.setEdited();
	}

	public boolean method_2669(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
		return this.selectedList.children().contains(resourcePackEntry);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.field_3157.render(i, j, f);
		this.selectedList.render(i, j, f);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.render(i, j, f);
	}

	public void setEdited() {
		this.edited = true;
	}
}
