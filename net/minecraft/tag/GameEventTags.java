/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
    protected static final RequiredTagList<GameEvent> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.GAME_EVENT_KEY, "tags/game_events");
    public static final Tag.Identified<GameEvent> VIBRATIONS = GameEventTags.register("vibrations");
    public static final Tag.Identified<GameEvent> IGNORE_VIBRATIONS_SNEAKING = GameEventTags.register("ignore_vibrations_sneaking");

    private static Tag.Identified<GameEvent> register(String id) {
        return REQUIRED_TAGS.add(id);
    }

    public static TagGroup<GameEvent> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }
}

