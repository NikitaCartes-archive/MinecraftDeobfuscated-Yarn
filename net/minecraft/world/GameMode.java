/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.IntFunction;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum GameMode implements StringIdentifiable
{
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure"),
    SPECTATOR(3, "spectator");

    public static final GameMode DEFAULT;
    public static final StringIdentifiable.Codec<GameMode> CODEC;
    private static final IntFunction<GameMode> BY_ID;
    private static final int UNKNOWN = -1;
    private final int id;
    private final String name;
    private final Text simpleTranslatableName;
    private final Text translatableName;

    private GameMode(int id, String name) {
        this.id = id;
        this.name = name;
        this.simpleTranslatableName = Text.translatable("selectWorld.gameMode." + name);
        this.translatableName = Text.translatable("gameMode." + name);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Text getTranslatableName() {
        return this.translatableName;
    }

    public Text getSimpleTranslatableName() {
        return this.simpleTranslatableName;
    }

    public void setAbilities(PlayerAbilities abilities) {
        if (this == CREATIVE) {
            abilities.allowFlying = true;
            abilities.creativeMode = true;
            abilities.invulnerable = true;
        } else if (this == SPECTATOR) {
            abilities.allowFlying = true;
            abilities.creativeMode = false;
            abilities.invulnerable = true;
            abilities.flying = true;
        } else {
            abilities.allowFlying = false;
            abilities.creativeMode = false;
            abilities.invulnerable = false;
            abilities.flying = false;
        }
        abilities.allowModifyWorld = !this.isBlockBreakingRestricted();
    }

    public boolean isBlockBreakingRestricted() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    public boolean isSurvivalLike() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static GameMode byId(int id) {
        return BY_ID.apply(id);
    }

    public static GameMode byName(String name) {
        return GameMode.byName(name, SURVIVAL);
    }

    @Nullable
    @Contract(value="_,!null->!null;_,null->_")
    public static GameMode byName(String name, @Nullable GameMode defaultMode) {
        GameMode gameMode = CODEC.byId(name);
        return gameMode != null ? gameMode : defaultMode;
    }

    public static int getId(@Nullable GameMode gameMode) {
        return gameMode != null ? gameMode.id : -1;
    }

    @Nullable
    public static GameMode getOrNull(int id) {
        if (id == -1) {
            return null;
        }
        return GameMode.byId(id);
    }

    static {
        DEFAULT = SURVIVAL;
        CODEC = StringIdentifiable.createCodec(GameMode::values);
        BY_ID = ValueLists.createIdToValueFunction(GameMode::getId, GameMode.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}

