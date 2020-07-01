/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagManager;

/**
 * A class containing the single static instance of {@link TagManager} on the server.
 */
public class ServerTagManagerHolder {
    private static volatile TagManager tagManager = TagManager.create(BlockTags.getTagGroup(), ItemTags.getTagGroup(), FluidTags.getTagGroup(), EntityTypeTags.getTagGroup());

    public static TagManager getTagManager() {
        return tagManager;
    }

    public static void setTagManager(TagManager tagManager) {
        ServerTagManagerHolder.tagManager = tagManager;
    }
}

