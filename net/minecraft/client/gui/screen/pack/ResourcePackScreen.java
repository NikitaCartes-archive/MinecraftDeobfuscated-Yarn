/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.pack;

import java.io.File;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.AbstractPackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ResourcePackScreen
extends AbstractPackScreen {
    public ResourcePackScreen(Screen parent, ResourcePackManager<ClientResourcePackProfile> resourcePackManager, Consumer<ResourcePackManager<ClientResourcePackProfile>> consumer, File file) {
        super(parent, new TranslatableText("resourcePack.title"), (Runnable runnable) -> new ResourcePackOrganizer<ClientResourcePackProfile>((Runnable)runnable, ClientResourcePackProfile::drawIcon, resourcePackManager, consumer), file);
    }
}

