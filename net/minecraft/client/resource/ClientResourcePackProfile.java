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
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientResourcePackProfile
extends ResourcePackProfile {
    @Nullable
    private NativeImage icon;
    @Nullable
    private Identifier iconId;

    public ClientResourcePackProfile(String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePack resourcePack, PackResourceMetadata packResourceMetadata, ResourcePackProfile.InsertionPosition insertionPosition, ResourcePackSource resourcePackSource) {
        super(string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition, resourcePackSource);
        this.icon = ClientResourcePackProfile.method_29713(resourcePack);
    }

    public ClientResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Text displayName, Text description, ResourcePackCompatibility compatibility, ResourcePackProfile.InsertionPosition insertionPosition, boolean pinned, ResourcePackSource resourcePackSource, @Nullable NativeImage nativeImage) {
        super(name, alwaysEnabled, packFactory, displayName, description, compatibility, insertionPosition, pinned, resourcePackSource);
        this.icon = nativeImage;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static NativeImage method_29713(ResourcePack resourcePack) {
        try (InputStream inputStream = resourcePack.openRoot("pack.png");){
            NativeImage nativeImage = NativeImage.read(inputStream);
            return nativeImage;
        } catch (IOException | IllegalArgumentException exception) {
            return null;
        }
    }

    public void drawIcon(TextureManager manager) {
        if (this.iconId == null) {
            this.iconId = this.icon == null ? new Identifier("textures/misc/unknown_pack.png") : manager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.icon));
        }
        manager.bindTexture(this.iconId);
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

