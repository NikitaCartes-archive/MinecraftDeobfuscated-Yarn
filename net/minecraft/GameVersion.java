/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Date;
import net.minecraft.SaveVersion;
import net.minecraft.resource.ResourceType;

/**
 * The game version interface used by Minecraft, replacing the javabridge
 * one's occurrences in Minecraft code.
 */
public interface GameVersion {
    /**
     * {@return the save version information for this game version}
     */
    public SaveVersion getSaveVersion();

    public String getId();

    public String getName();

    public int getProtocolVersion();

    public int getResourceVersion(ResourceType var1);

    public Date getBuildTime();

    public boolean isStable();
}

