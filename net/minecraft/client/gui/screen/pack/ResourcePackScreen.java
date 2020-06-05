/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.AbstractPackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ResourcePackScreen
extends AbstractPackScreen {
    public ResourcePackScreen(Screen parent, GameOptions gameOptions, ResourcePackManager<ClientResourcePackProfile> packManager, Runnable runnable) {
        super(parent, new TranslatableText("resourcePack.title"), (Runnable runnable2) -> {
            packManager.scanPacks();
            ArrayList list3 = Lists.newArrayList(packManager.getEnabledProfiles());
            ArrayList list22 = Lists.newArrayList(packManager.getProfiles());
            list22.removeAll(list3);
            return new ResourcePackOrganizer<ClientResourcePackProfile>((Runnable)runnable2, ClientResourcePackProfile::drawIcon, Lists.reverse(list3), list22, (list, list2, bl) -> {
                List<ClientResourcePackProfile> list3 = Lists.reverse(list);
                List list4 = list3.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                packManager.setEnabledProfiles(list4);
                gameOptions.resourcePacks.clear();
                gameOptions.incompatibleResourcePacks.clear();
                for (ClientResourcePackProfile clientResourcePackProfile : list3) {
                    if (clientResourcePackProfile.isPinned()) continue;
                    gameOptions.resourcePacks.add(clientResourcePackProfile.getName());
                    if (clientResourcePackProfile.getCompatibility().isCompatible()) continue;
                    gameOptions.incompatibleResourcePacks.add(clientResourcePackProfile.getName());
                }
                gameOptions.write();
                if (!bl) {
                    runnable.run();
                }
            });
        }, MinecraftClient::getResourcePackDir);
    }
}

