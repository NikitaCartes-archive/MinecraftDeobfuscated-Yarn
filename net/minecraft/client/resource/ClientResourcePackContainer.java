/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientResourcePackContainer
extends ResourcePackContainer {
    @Nullable
    private NativeImage icon;
    @Nullable
    private Identifier iconId;

    public ClientResourcePackContainer(String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePack resourcePack, PackResourceMetadata packResourceMetadata, ResourcePackContainer.InsertionPosition insertionPosition) {
        super(string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition);
        NativeImage nativeImage = null;
        try (InputStream inputStream = resourcePack.openRoot("pack.png");){
            nativeImage = NativeImage.fromInputStream(inputStream);
        } catch (IOException | IllegalArgumentException exception) {
            // empty catch block
        }
        this.icon = nativeImage;
    }

    public ClientResourcePackContainer(String string, boolean bl, Supplier<ResourcePack> supplier, Text text, Text text2, ResourcePackCompatibility resourcePackCompatibility, ResourcePackContainer.InsertionPosition insertionPosition, boolean bl2, @Nullable NativeImage nativeImage) {
        super(string, bl, supplier, text, text2, resourcePackCompatibility, insertionPosition, bl2);
        this.icon = nativeImage;
    }

    public void drawIcon(TextureManager textureManager) {
        if (this.iconId == null) {
            this.iconId = this.icon == null ? new Identifier("textures/misc/unknown_pack.png") : textureManager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.icon));
        }
        textureManager.bindTexture(this.iconId);
    }

    @Override
    public void close() {
        super.close();
        if (this.icon != null) {
            this.icon.close();
            this.icon = null;
        }
    }
}

