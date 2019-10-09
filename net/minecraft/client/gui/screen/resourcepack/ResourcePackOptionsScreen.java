/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.resourcepack;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4667;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOptionsScreen
extends class_4667 {
    private AvailableResourcePackListWidget availablePacks;
    private SelectedResourcePackListWidget enabledPacks;
    private boolean dirty;

    public ResourcePackOptionsScreen(Screen screen, GameOptions gameOptions) {
        super(screen, gameOptions, new TranslatableText("resourcePack.title", new Object[0]));
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, I18n.translate("resourcePack.openFolder", new Object[0]), buttonWidget -> SystemUtil.getOperatingSystem().open(this.minecraft.getResourcePackDir())));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            if (this.dirty) {
                ArrayList<ClientResourcePackProfile> list = Lists.newArrayList();
                for (ResourcePackListWidget.ResourcePackEntry resourcePackEntry : this.enabledPacks.children()) {
                    list.add(resourcePackEntry.getPack());
                }
                Collections.reverse(list);
                this.minecraft.getResourcePackManager().setEnabledProfiles(list);
                this.field_21336.resourcePacks.clear();
                this.field_21336.incompatibleResourcePacks.clear();
                for (ClientResourcePackProfile clientResourcePackProfile : list) {
                    if (clientResourcePackProfile.isPinned()) continue;
                    this.field_21336.resourcePacks.add(clientResourcePackProfile.getName());
                    if (clientResourcePackProfile.getCompatibility().isCompatible()) continue;
                    this.field_21336.incompatibleResourcePacks.add(clientResourcePackProfile.getName());
                }
                this.field_21336.write();
                this.minecraft.openScreen(this.field_21335);
                this.minecraft.reloadResources();
            } else {
                this.minecraft.openScreen(this.field_21335);
            }
        }));
        AvailableResourcePackListWidget availableResourcePackListWidget = this.availablePacks;
        SelectedResourcePackListWidget selectedResourcePackListWidget = this.enabledPacks;
        this.availablePacks = new AvailableResourcePackListWidget(this.minecraft, 200, this.height);
        this.availablePacks.setLeftPos(this.width / 2 - 4 - 200);
        if (availableResourcePackListWidget != null) {
            this.availablePacks.children().addAll(availableResourcePackListWidget.children());
        }
        this.children.add(this.availablePacks);
        this.enabledPacks = new SelectedResourcePackListWidget(this.minecraft, 200, this.height);
        this.enabledPacks.setLeftPos(this.width / 2 + 4);
        if (selectedResourcePackListWidget != null) {
            this.enabledPacks.children().addAll(selectedResourcePackListWidget.children());
        }
        this.children.add(this.enabledPacks);
        if (!this.dirty) {
            this.availablePacks.children().clear();
            this.enabledPacks.children().clear();
            ResourcePackManager<ClientResourcePackProfile> resourcePackManager = this.minecraft.getResourcePackManager();
            resourcePackManager.scanPacks();
            ArrayList<ClientResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getProfiles());
            list.removeAll(resourcePackManager.getEnabledProfiles());
            for (ClientResourcePackProfile clientResourcePackProfile : list) {
                this.availablePacks.add(new ResourcePackListWidget.ResourcePackEntry(this.availablePacks, this, clientResourcePackProfile));
            }
            for (ClientResourcePackProfile clientResourcePackProfile : Lists.reverse(Lists.newArrayList(resourcePackManager.getEnabledProfiles()))) {
                this.enabledPacks.add(new ResourcePackListWidget.ResourcePackEntry(this.enabledPacks, this, clientResourcePackProfile));
            }
        }
    }

    public void enable(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
        this.availablePacks.children().remove(resourcePackEntry);
        resourcePackEntry.enable(this.enabledPacks);
        this.markDirty();
    }

    public void disable(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
        this.enabledPacks.children().remove(resourcePackEntry);
        this.availablePacks.add(resourcePackEntry);
        this.markDirty();
    }

    public boolean isEnabled(ResourcePackListWidget.ResourcePackEntry resourcePackEntry) {
        return this.enabledPacks.children().contains(resourcePackEntry);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderDirtBackground(0);
        this.availablePacks.render(i, j, f);
        this.enabledPacks.render(i, j, f);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 0x808080);
        super.render(i, j, f);
    }

    public void markDirty() {
        this.dirty = true;
    }
}

