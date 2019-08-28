/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.resourcepack;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOptionsScreen
extends Screen {
    private final Screen parent;
    private AvailableResourcePackListWidget availablePacks;
    private SelectedResourcePackListWidget selectedPacks;
    private boolean edited;

    public ResourcePackOptionsScreen(Screen screen) {
        super(new TranslatableText("resourcePack.title", new Object[0]));
        this.parent = screen;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, I18n.translate("resourcePack.openFolder", new Object[0]), buttonWidget -> SystemUtil.getOperatingSystem().open(this.minecraft.getResourcePackDir())));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            if (this.edited) {
                ArrayList<ClientResourcePackContainer> list = Lists.newArrayList();
                for (ResourcePackListWidget.ResourcePackEntry resourcePackEntry : this.selectedPacks.children()) {
                    list.add(resourcePackEntry.getPackContainer());
                }
                Collections.reverse(list);
                this.minecraft.getResourcePackContainerManager().setEnabled(list);
                this.minecraft.options.resourcePacks.clear();
                this.minecraft.options.incompatibleResourcePacks.clear();
                for (ClientResourcePackContainer clientResourcePackContainer : list) {
                    if (clientResourcePackContainer.isPositionFixed()) continue;
                    this.minecraft.options.resourcePacks.add(clientResourcePackContainer.getName());
                    if (clientResourcePackContainer.getCompatibility().isCompatible()) continue;
                    this.minecraft.options.incompatibleResourcePacks.add(clientResourcePackContainer.getName());
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
            ArrayList<ClientResourcePackContainer> list = Lists.newArrayList(resourcePackContainerManager.getAlphabeticallyOrderedContainers());
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
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 0x808080);
        super.render(i, j, f);
    }

    public void setEdited() {
        this.edited = true;
    }
}

