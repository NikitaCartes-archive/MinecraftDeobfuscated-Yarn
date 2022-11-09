/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModelIds {
    @Deprecated
    public static Identifier getMinecraftNamespacedBlock(String name) {
        return new Identifier("minecraft", "block/" + name);
    }

    public static Identifier getMinecraftNamespacedItem(String name) {
        return new Identifier("minecraft", "item/" + name);
    }

    public static Identifier getBlockSubModelId(Block block, String suffix) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPath(path -> "block/" + path + suffix);
    }

    public static Identifier getBlockModelId(Block block) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPrefixedPath("block/");
    }

    public static Identifier getItemModelId(Item item) {
        Identifier identifier = Registries.ITEM.getId(item);
        return identifier.withPrefixedPath("item/");
    }

    public static Identifier getItemSubModelId(Item item, String suffix) {
        Identifier identifier = Registries.ITEM.getId(item);
        return identifier.withPath(path -> "item/" + path + suffix);
    }
}

