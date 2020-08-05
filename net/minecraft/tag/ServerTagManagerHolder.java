/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.stream.Collectors;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;

/**
 * A class containing the single static instance of {@link TagManager} on the server.
 */
public class ServerTagManagerHolder {
    private static volatile TagManager tagManager = TagManager.create(TagGroup.create(BlockTags.method_31072().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))), TagGroup.create(ItemTags.method_31074().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))), TagGroup.create(FluidTags.all().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))), TagGroup.create(EntityTypeTags.method_31073().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))));

    public static TagManager getTagManager() {
        return tagManager;
    }

    public static void setTagManager(TagManager tagManager) {
        ServerTagManagerHolder.tagManager = tagManager;
    }
}

