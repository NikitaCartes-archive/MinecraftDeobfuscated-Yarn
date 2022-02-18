/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTags {
    public static final TagKey<GameEvent> VIBRATIONS = GameEventTags.register("vibrations");
    public static final TagKey<GameEvent> IGNORE_VIBRATIONS_SNEAKING = GameEventTags.register("ignore_vibrations_sneaking");

    private static TagKey<GameEvent> register(String id) {
        return TagKey.of(Registry.GAME_EVENT_KEY, new Identifier(id));
    }
}

