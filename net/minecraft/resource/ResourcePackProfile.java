/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ResourcePackProfile
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PackResourceMetadata BROKEN_PACK_META = new PackResourceMetadata(new TranslatableText("resourcePack.broken_assets", new Object[0]).formatted(Formatting.RED, Formatting.ITALIC), SharedConstants.getGameVersion().getPackVersion());
    private final String name;
    private final Supplier<ResourcePack> packGetter;
    private final Text displayName;
    private final Text description;
    private final ResourcePackCompatibility compatibility;
    private final InsertionPosition position;
    private final boolean alwaysEnabled;
    private final boolean pinned;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static <T extends ResourcePackProfile> T of(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Factory<T> containerFactory, InsertionPosition insertionPosition) {
        try (ResourcePack resourcePack = packFactory.get();){
            PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);
            if (alwaysEnabled && packResourceMetadata == null) {
                LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
                packResourceMetadata = BROKEN_PACK_META;
            }
            if (packResourceMetadata != null) {
                T t = containerFactory.create(name, alwaysEnabled, packFactory, resourcePack, packResourceMetadata, insertionPosition);
                return t;
            }
            LOGGER.warn("Couldn't find pack meta for pack {}", (Object)name);
            return null;
        } catch (IOException iOException) {
            LOGGER.warn("Couldn't get pack info for: {}", (Object)iOException.toString());
        }
        return null;
    }

    public ResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Text displayName, Text description, ResourcePackCompatibility compatibility, InsertionPosition direction, boolean pinned) {
        this.name = name;
        this.packGetter = packFactory;
        this.displayName = displayName;
        this.description = description;
        this.compatibility = compatibility;
        this.alwaysEnabled = alwaysEnabled;
        this.position = direction;
        this.pinned = pinned;
    }

    public ResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, ResourcePack pack, PackResourceMetadata metadata, InsertionPosition direction) {
        this(name, alwaysEnabled, packFactory, new LiteralText(pack.getName()), metadata.getDescription(), ResourcePackCompatibility.from(metadata.getPackFormat()), direction, false);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDisplayName() {
        return this.displayName;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDescription() {
        return this.description;
    }

    public Text getInformationText(boolean enabled) {
        return Texts.bracketed(new LiteralText(this.name)).styled(style -> style.setColor(enabled ? Formatting.GREEN : Formatting.RED).setInsertion(StringArgumentType.escapeIfRequired(this.name)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("").append(this.displayName).append("\n").append(this.description))));
    }

    public ResourcePackCompatibility getCompatibility() {
        return this.compatibility;
    }

    public ResourcePack createResourcePack() {
        return this.packGetter.get();
    }

    public String getName() {
        return this.name;
    }

    public boolean isAlwaysEnabled() {
        return this.alwaysEnabled;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public InsertionPosition getInitialPosition() {
        return this.position;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourcePackProfile)) {
            return false;
        }
        ResourcePackProfile resourcePackProfile = (ResourcePackProfile)o;
        return this.name.equals(resourcePackProfile.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public void close() {
    }

    public static enum InsertionPosition {
        TOP,
        BOTTOM;


        public <T, P extends ResourcePackProfile> int insert(List<T> items, T item, Function<T, P> profileGetter, boolean listInversed) {
            ResourcePackProfile resourcePackProfile;
            int i;
            InsertionPosition insertionPosition;
            InsertionPosition insertionPosition2 = insertionPosition = listInversed ? this.inverse() : this;
            if (insertionPosition == BOTTOM) {
                ResourcePackProfile resourcePackProfile2;
                int i2;
                for (i2 = 0; i2 < items.size() && (resourcePackProfile2 = (ResourcePackProfile)profileGetter.apply(items.get(i2))).isPinned() && resourcePackProfile2.getInitialPosition() == this; ++i2) {
                }
                items.add(i2, item);
                return i2;
            }
            for (i = items.size() - 1; i >= 0 && (resourcePackProfile = (ResourcePackProfile)profileGetter.apply(items.get(i))).isPinned() && resourcePackProfile.getInitialPosition() == this; --i) {
            }
            items.add(i + 1, item);
            return i + 1;
        }

        public InsertionPosition inverse() {
            return this == TOP ? BOTTOM : TOP;
        }
    }

    @FunctionalInterface
    public static interface Factory<T extends ResourcePackProfile> {
        @Nullable
        public T create(String var1, boolean var2, Supplier<ResourcePack> var3, ResourcePack var4, PackResourceMetadata var5, InsertionPosition var6);
    }
}

