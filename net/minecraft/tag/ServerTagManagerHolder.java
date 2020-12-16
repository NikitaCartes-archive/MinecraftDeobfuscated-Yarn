/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.TagManager;

/**
 * A class containing the single static instance of {@link TagManager} on the server.
 */
public class ServerTagManagerHolder {
    private static volatile TagManager tagManager = RequiredTagListRegistry.method_33152();

    public static TagManager getTagManager() {
        return tagManager;
    }

    public static void setTagManager(TagManager tagManager) {
        ServerTagManagerHolder.tagManager = tagManager;
    }
}

