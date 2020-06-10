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
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DataPackScreen
extends AbstractPackScreen {
    private static final Identifier UNKNOWN_PACK_TEXTURE = new Identifier("textures/misc/unknown_pack.png");

    public DataPackScreen(Screen parent, ResourcePackManager<ResourcePackProfile> resourcePackManager, Consumer<ResourcePackManager<ResourcePackProfile>> consumer, File file) {
        super(parent, new TranslatableText("dataPack.title"), (Runnable runnable) -> new ResourcePackOrganizer<ResourcePackProfile>((Runnable)runnable, (resourcePackProfile, textureManager) -> textureManager.bindTexture(UNKNOWN_PACK_TEXTURE), resourcePackManager, consumer), file);
    }
}

