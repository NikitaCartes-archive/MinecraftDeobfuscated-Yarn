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

public class ResourcePackContainer
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PackResourceMetadata BROKEN_PACK_META = new PackResourceMetadata(new TranslatableText("resourcePack.broken_assets", new Object[0]).formatted(Formatting.RED, Formatting.ITALIC), SharedConstants.getGameVersion().getPackVersion());
    private final String name;
    private final Supplier<ResourcePack> packCreator;
    private final Text displayName;
    private final Text description;
    private final ResourcePackCompatibility compatibility;
    private final InsertionPosition position;
    private final boolean notSorting;
    private final boolean positionFixed;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static <T extends ResourcePackContainer> T of(String string, boolean bl, Supplier<ResourcePack> supplier, Factory<T> factory, InsertionPosition insertionPosition) {
        try (ResourcePack resourcePack = supplier.get();){
            PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);
            if (bl && packResourceMetadata == null) {
                LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
                packResourceMetadata = BROKEN_PACK_META;
            }
            if (packResourceMetadata != null) {
                T t = factory.create(string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition);
                return t;
            }
            LOGGER.warn("Couldn't find pack meta for pack {}", (Object)string);
            return null;
        } catch (IOException iOException) {
            LOGGER.warn("Couldn't get pack info for: {}", (Object)iOException.toString());
        }
        return null;
    }

    public ResourcePackContainer(String string, boolean bl, Supplier<ResourcePack> supplier, Text text, Text text2, ResourcePackCompatibility resourcePackCompatibility, InsertionPosition insertionPosition, boolean bl2) {
        this.name = string;
        this.packCreator = supplier;
        this.displayName = text;
        this.description = text2;
        this.compatibility = resourcePackCompatibility;
        this.notSorting = bl;
        this.position = insertionPosition;
        this.positionFixed = bl2;
    }

    public ResourcePackContainer(String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePack resourcePack, PackResourceMetadata packResourceMetadata, InsertionPosition insertionPosition) {
        this(string, bl, supplier, new LiteralText(resourcePack.getName()), packResourceMetadata.getDescription(), ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()), insertionPosition, false);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDisplayName() {
        return this.displayName;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDescription() {
        return this.description;
    }

    public Text getInformationText(boolean bl) {
        return Texts.bracketed(new LiteralText(this.name)).styled(style -> style.setColor(bl ? Formatting.GREEN : Formatting.RED).setInsertion(StringArgumentType.escapeIfRequired(this.name)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("").append(this.displayName).append("\n").append(this.description))));
    }

    public ResourcePackCompatibility getCompatibility() {
        return this.compatibility;
    }

    public ResourcePack createResourcePack() {
        return this.packCreator.get();
    }

    public String getName() {
        return this.name;
    }

    public boolean canBeSorted() {
        return this.notSorting;
    }

    public boolean isPositionFixed() {
        return this.positionFixed;
    }

    public InsertionPosition getInitialPosition() {
        return this.position;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ResourcePackContainer)) {
            return false;
        }
        ResourcePackContainer resourcePackContainer = (ResourcePackContainer)object;
        return this.name.equals(resourcePackContainer.name);
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


        public <T, P extends ResourcePackContainer> int insert(List<T> list, T object, Function<T, P> function, boolean bl) {
            ResourcePackContainer resourcePackContainer;
            int i;
            InsertionPosition insertionPosition;
            InsertionPosition insertionPosition2 = insertionPosition = bl ? this.inverse() : this;
            if (insertionPosition == BOTTOM) {
                ResourcePackContainer resourcePackContainer2;
                int i2;
                for (i2 = 0; i2 < list.size() && (resourcePackContainer2 = (ResourcePackContainer)function.apply(list.get(i2))).isPositionFixed() && resourcePackContainer2.getInitialPosition() == this; ++i2) {
                }
                list.add(i2, object);
                return i2;
            }
            for (i = list.size() - 1; i >= 0 && (resourcePackContainer = (ResourcePackContainer)function.apply(list.get(i))).isPositionFixed() && resourcePackContainer.getInitialPosition() == this; --i) {
            }
            list.add(i + 1, object);
            return i + 1;
        }

        public InsertionPosition inverse() {
            return this == TOP ? BOTTOM : TOP;
        }
    }

    @FunctionalInterface
    public static interface Factory<T extends ResourcePackContainer> {
        @Nullable
        public T create(String var1, boolean var2, Supplier<ResourcePack> var3, ResourcePack var4, PackResourceMetadata var5, InsertionPosition var6);
    }
}

