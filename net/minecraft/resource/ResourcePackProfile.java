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
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ResourcePackProfile
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String name;
    private final Supplier<ResourcePack> packGetter;
    private final Text displayName;
    private final Text description;
    private final ResourcePackCompatibility compatibility;
    private final InsertionPosition position;
    private final boolean alwaysEnabled;
    private final boolean pinned;
    private final ResourcePackSource source;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static ResourcePackProfile of(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Factory containerFactory, InsertionPosition insertionPosition, ResourcePackSource resourcePackSource) {
        try (ResourcePack resourcePack = packFactory.get();){
            PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);
            if (packResourceMetadata != null) {
                ResourcePackProfile resourcePackProfile = containerFactory.create(name, new LiteralText(resourcePack.getName()), alwaysEnabled, packFactory, packResourceMetadata, insertionPosition, resourcePackSource);
                return resourcePackProfile;
            }
            LOGGER.warn("Couldn't find pack meta for pack {}", (Object)name);
            return null;
        } catch (IOException iOException) {
            LOGGER.warn("Couldn't get pack info for: {}", (Object)iOException.toString());
        }
        return null;
    }

    public ResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Text displayName, Text description, ResourcePackCompatibility compatibility, InsertionPosition direction, boolean pinned, ResourcePackSource source) {
        this.name = name;
        this.packGetter = packFactory;
        this.displayName = displayName;
        this.description = description;
        this.compatibility = compatibility;
        this.alwaysEnabled = alwaysEnabled;
        this.position = direction;
        this.pinned = pinned;
        this.source = source;
    }

    public ResourcePackProfile(String name, Text displayName, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, PackResourceMetadata metadata, ResourceType type, InsertionPosition direction, ResourcePackSource source) {
        this(name, alwaysEnabled, packFactory, displayName, metadata.getDescription(), ResourcePackCompatibility.from(metadata, type), direction, false, source);
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
        return Texts.bracketed(this.source.decorate(new LiteralText(this.name))).styled(style -> style.withColor(enabled ? Formatting.GREEN : Formatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.name)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("").append(this.displayName).append("\n").append(this.description))));
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

    @Environment(value=EnvType.CLIENT)
    public ResourcePackSource getSource() {
        return this.source;
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


        public <T> int insert(List<T> items, T item, Function<T, ResourcePackProfile> profileGetter, boolean listInverted) {
            ResourcePackProfile resourcePackProfile;
            int i;
            InsertionPosition insertionPosition;
            InsertionPosition insertionPosition2 = insertionPosition = listInverted ? this.inverse() : this;
            if (insertionPosition == BOTTOM) {
                ResourcePackProfile resourcePackProfile2;
                int i2;
                for (i2 = 0; i2 < items.size() && (resourcePackProfile2 = profileGetter.apply(items.get(i2))).isPinned() && resourcePackProfile2.getInitialPosition() == this; ++i2) {
                }
                items.add(i2, item);
                return i2;
            }
            for (i = items.size() - 1; i >= 0 && (resourcePackProfile = profileGetter.apply(items.get(i))).isPinned() && resourcePackProfile.getInitialPosition() == this; --i) {
            }
            items.add(i + 1, item);
            return i + 1;
        }

        public InsertionPosition inverse() {
            return this == TOP ? BOTTOM : TOP;
        }
    }

    @FunctionalInterface
    public static interface Factory {
        @Nullable
        public ResourcePackProfile create(String var1, Text var2, boolean var3, Supplier<ResourcePack> var4, PackResourceMetadata var5, InsertionPosition var6, ResourcePackSource var7);
    }
}

