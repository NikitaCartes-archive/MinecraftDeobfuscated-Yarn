/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item.trim;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class ArmorTrim {
    public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ArmorTrimMaterial.ENTRY_CODEC.fieldOf("material")).forGetter(ArmorTrim::getMaterial), ((MapCodec)ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern")).forGetter(ArmorTrim::getPattern)).apply((Applicative<ArmorTrim, ?>)instance, ArmorTrim::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String NBT_KEY = "Trim";
    private static final Text UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("item", new Identifier("smithing_template.upgrade"))).formatted(Formatting.GRAY);
    private final RegistryEntry<ArmorTrimMaterial> material;
    private final RegistryEntry<ArmorTrimPattern> pattern;
    private final Supplier<Identifier> leggingsModelIdGetter;
    private final Supplier<Identifier> genericModelIdGetter;

    public ArmorTrim(RegistryEntry<ArmorTrimMaterial> material, RegistryEntry<ArmorTrimPattern> pattern) {
        this.material = material;
        this.pattern = pattern;
        this.leggingsModelIdGetter = Suppliers.memoize(() -> {
            Identifier identifier = ((ArmorTrimPattern)pattern.value()).assetId();
            String string = ((ArmorTrimMaterial)material.value()).assetName();
            return identifier.withPath(path -> "trims/models/armor/" + path + "_leggings_" + string);
        });
        this.genericModelIdGetter = Suppliers.memoize(() -> {
            Identifier identifier = ((ArmorTrimPattern)pattern.value()).assetId();
            String string = ((ArmorTrimMaterial)material.value()).assetName();
            return identifier.withPath(path -> "trims/models/armor/" + path + "_" + string);
        });
    }

    public boolean equals(RegistryEntry<ArmorTrimPattern> pattern, RegistryEntry<ArmorTrimMaterial> material) {
        return pattern == this.pattern && material == this.material;
    }

    public RegistryEntry<ArmorTrimPattern> getPattern() {
        return this.pattern;
    }

    public RegistryEntry<ArmorTrimMaterial> getMaterial() {
        return this.material;
    }

    public Identifier getLeggingsModelId() {
        return this.leggingsModelIdGetter.get();
    }

    public Identifier getGenericModelId() {
        return this.genericModelIdGetter.get();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ArmorTrim)) {
            return false;
        }
        ArmorTrim armorTrim = (ArmorTrim)o;
        return armorTrim.pattern == this.pattern && armorTrim.material == this.material;
    }

    public static boolean apply(DynamicRegistryManager registryManager, ItemStack stack, ArmorTrim trim) {
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
            stack.getOrCreateNbt().put(NBT_KEY, CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
            return true;
        }
        return false;
    }

    public static Optional<ArmorTrim> getTrim(DynamicRegistryManager registryManager, ItemStack stack) {
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) && stack.getNbt() != null && stack.getNbt().contains(NBT_KEY)) {
            NbtCompound nbtCompound = stack.getSubNbt(NBT_KEY);
            ArmorTrim armorTrim = CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtCompound).resultOrPartial(LOGGER::error).orElse(null);
            return Optional.ofNullable(armorTrim);
        }
        return Optional.empty();
    }

    public static void appendTooltip(ItemStack stack, DynamicRegistryManager registryManager, List<Text> tooltip) {
        Optional<ArmorTrim> optional = ArmorTrim.getTrim(registryManager, stack);
        if (optional.isPresent()) {
            ArmorTrim armorTrim = optional.get();
            tooltip.add(UPGRADE_TEXT);
            tooltip.add(ScreenTexts.space().append(armorTrim.getPattern().value().getDescription(armorTrim.getMaterial())));
            tooltip.add(ScreenTexts.space().append(armorTrim.getMaterial().value().description()));
        }
    }
}

