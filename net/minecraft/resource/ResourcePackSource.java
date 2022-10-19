/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.function.UnaryOperator;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface ResourcePackSource {
    public static final UnaryOperator<Text> NONE_SOURCE_TEXT_SUPPLIER = UnaryOperator.identity();
    public static final ResourcePackSource NONE = ResourcePackSource.create(NONE_SOURCE_TEXT_SUPPLIER, true);
    public static final ResourcePackSource BUILTIN = ResourcePackSource.create(ResourcePackSource.getSourceTextSupplier("pack.source.builtin"), true);
    public static final ResourcePackSource FEATURE = ResourcePackSource.create(ResourcePackSource.getSourceTextSupplier("pack.source.feature"), false);
    public static final ResourcePackSource WORLD = ResourcePackSource.create(ResourcePackSource.getSourceTextSupplier("pack.source.world"), true);
    public static final ResourcePackSource SERVER = ResourcePackSource.create(ResourcePackSource.getSourceTextSupplier("pack.source.server"), true);

    public Text decorate(Text var1);

    public boolean canBeEnabledLater();

    public static ResourcePackSource create(final UnaryOperator<Text> sourceTextSupplier, final boolean canBeEnabledLater) {
        return new ResourcePackSource(){

            @Override
            public Text decorate(Text packName) {
                return (Text)sourceTextSupplier.apply(packName);
            }

            @Override
            public boolean canBeEnabledLater() {
                return canBeEnabledLater;
            }
        };
    }

    private static UnaryOperator<Text> getSourceTextSupplier(String translationKey) {
        MutableText text = Text.translatable(translationKey);
        return name -> Text.translatable("pack.nameAndSource", name, text).formatted(Formatting.GRAY);
    }
}

