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
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOptionsScreen
extends GameOptionsScreen {
    private AvailableResourcePackListWidget availablePacks;
    private SelectedResourcePackListWidget enabledPacks;
    private boolean dirty;

    public ResourcePackOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, new TranslatableText("resourcePack.title"));
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, new TranslatableText("resourcePack.openFolder"), buttonWidget -> Util.getOperatingSystem().open(this.client.getResourcePackDir())));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, buttonWidget -> {
            if (this.dirty) {
                ArrayList<ClientResourcePackProfile> list = Lists.newArrayList();
                for (ResourcePackListWidget.ResourcePackEntry resourcePackEntry : this.enabledPacks.children()) {
                    list.add(resourcePackEntry.getPack());
                }
                Collections.reverse(list);
                this.client.getResourcePackManager().setEnabledProfiles(list);
                this.gameOptions.resourcePacks.clear();
                this.gameOptions.incompatibleResourcePacks.clear();
                for (ClientResourcePackProfile clientResourcePackProfile : list) {
                    if (clientResourcePackProfile.isPinned()) continue;
                    this.gameOptions.resourcePacks.add(clientResourcePackProfile.getName());
                    if (clientResourcePackProfile.getCompatibility().isCompatible()) continue;
                    this.gameOptions.incompatibleResourcePacks.add(clientResourcePackProfile.getName());
                }
                this.gameOptions.write();
                this.client.openScreen(this.parent);
                this.client.reloadResources();
            } else {
                this.client.openScreen(this.parent);
            }
        }));
        AvailableResourcePackListWidget availableResourcePackListWidget = this.availablePacks;
        SelectedResourcePackListWidget selectedResourcePackListWidget = this.enabledPacks;
        this.availablePacks = new AvailableResourcePackListWidget(this.client, 200, this.height);
        this.availablePacks.setLeftPos(this.width / 2 - 4 - 200);
        if (availableResourcePackListWidget != null) {
            this.availablePacks.children().addAll(availableResourcePackListWidget.children());
        }
        this.children.add(this.availablePacks);
        this.enabledPacks = new SelectedResourcePackListWidget(this.client, 200, this.height);
        this.enabledPacks.setLeftPos(this.width / 2 + 4);
        if (selectedResourcePackListWidget != null) {
            selectedResourcePackListWidget.children().forEach(resourcePackEntry -> {
                this.enabledPacks.children().add((ResourcePackListWidget.ResourcePackEntry)resourcePackEntry);
                resourcePackEntry.method_24232(this.enabledPacks);
            });
        }
        this.children.add(this.enabledPacks);
        if (!this.dirty) {
            this.availablePacks.children().clear();
            this.enabledPacks.children().clear();
            ResourcePackManager<ClientResourcePackProfile> resourcePackManager = this.client.getResourcePackManager();
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

    public void enable(ResourcePackListWidget.ResourcePackEntry resourcePack) {
        this.availablePacks.children().remove(resourcePack);
        resourcePack.enable(this.enabledPacks);
        this.markDirty();
    }

    public void disable(ResourcePackListWidget.ResourcePackEntry resourcePack) {
        this.enabledPacks.children().remove(resourcePack);
        this.availablePacks.add(resourcePack);
        this.markDirty();
    }

    public boolean isEnabled(ResourcePackListWidget.ResourcePackEntry resourcePack) {
        return this.enabledPacks.children().contains(resourcePack);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        this.availablePacks.render(matrices, mouseX, mouseY, delta);
        this.enabledPacks.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 0x808080);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void markDirty() {
        this.dirty = true;
    }
}

