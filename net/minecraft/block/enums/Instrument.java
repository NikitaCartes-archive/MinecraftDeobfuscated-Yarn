/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;

public enum Instrument implements StringIdentifiable
{
    HARP("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP, Type.BASE_BLOCK),
    BASEDRUM("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, Type.BASE_BLOCK),
    SNARE("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE, Type.BASE_BLOCK),
    HAT("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT, Type.BASE_BLOCK),
    BASS("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS, Type.BASE_BLOCK),
    FLUTE("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, Type.BASE_BLOCK),
    BELL("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL, Type.BASE_BLOCK),
    GUITAR("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, Type.BASE_BLOCK),
    CHIME("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME, Type.BASE_BLOCK),
    XYLOPHONE("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, Type.BASE_BLOCK),
    IRON_XYLOPHONE("iron_xylophone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, Type.BASE_BLOCK),
    COW_BELL("cow_bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, Type.BASE_BLOCK),
    DIDGERIDOO("didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, Type.BASE_BLOCK),
    BIT("bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT, Type.BASE_BLOCK),
    BANJO("banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO, Type.BASE_BLOCK),
    PLING("pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING, Type.BASE_BLOCK),
    ZOMBIE("zombie", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ZOMBIE, Type.MOB_HEAD),
    SKELETON("skeleton", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_SKELETON, Type.MOB_HEAD),
    CREEPER("creeper", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_CREEPER, Type.MOB_HEAD),
    DRAGON("dragon", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, Type.MOB_HEAD),
    WITHER_SKELETON("wither_skeleton", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_WITHER_SKELETON, Type.MOB_HEAD),
    PIGLIN("piglin", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_PIGLIN, Type.MOB_HEAD),
    CUSTOM_HEAD("custom_head", SoundEvents.UI_BUTTON_CLICK, Type.CUSTOM);

    private final String name;
    private final RegistryEntry<SoundEvent> sound;
    private final Type type;

    private Instrument(String name, RegistryEntry<SoundEvent> sound, Type type) {
        this.name = name;
        this.sound = sound;
        this.type = type;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    /**
     * {@return whether note blocks playing this instrument should produce note particles}
     */
    public boolean shouldSpawnNoteParticles() {
        return this.type == Type.BASE_BLOCK;
    }

    /**
     * {@return whether note blocks playing this instrument should determine the sound from skulls above them}
     */
    public boolean hasCustomSound() {
        return this.type == Type.CUSTOM;
    }

    /**
     * {@return whether this instrument should prevent note blocks from playing when they do not have air above them}
     */
    public boolean shouldRequireAirAbove() {
        return this.type == Type.BASE_BLOCK;
    }

    public static Optional<Instrument> fromAboveState(BlockState state) {
        if (state.isOf(Blocks.ZOMBIE_HEAD)) {
            return Optional.of(ZOMBIE);
        }
        if (state.isOf(Blocks.SKELETON_SKULL)) {
            return Optional.of(SKELETON);
        }
        if (state.isOf(Blocks.CREEPER_HEAD)) {
            return Optional.of(CREEPER);
        }
        if (state.isOf(Blocks.DRAGON_HEAD)) {
            return Optional.of(DRAGON);
        }
        if (state.isOf(Blocks.WITHER_SKELETON_SKULL)) {
            return Optional.of(WITHER_SKELETON);
        }
        if (state.isOf(Blocks.PIGLIN_HEAD)) {
            return Optional.of(PIGLIN);
        }
        if (state.isOf(Blocks.PLAYER_HEAD)) {
            return Optional.of(CUSTOM_HEAD);
        }
        return Optional.empty();
    }

    public static Instrument fromBelowState(BlockState state) {
        if (state.isOf(Blocks.CLAY)) {
            return FLUTE;
        }
        if (state.isOf(Blocks.GOLD_BLOCK)) {
            return BELL;
        }
        if (state.isIn(BlockTags.WOOL)) {
            return GUITAR;
        }
        if (state.isOf(Blocks.PACKED_ICE)) {
            return CHIME;
        }
        if (state.isOf(Blocks.BONE_BLOCK)) {
            return XYLOPHONE;
        }
        if (state.isOf(Blocks.IRON_BLOCK)) {
            return IRON_XYLOPHONE;
        }
        if (state.isOf(Blocks.SOUL_SAND)) {
            return COW_BELL;
        }
        if (state.isOf(Blocks.PUMPKIN)) {
            return DIDGERIDOO;
        }
        if (state.isOf(Blocks.EMERALD_BLOCK)) {
            return BIT;
        }
        if (state.isOf(Blocks.HAY_BLOCK)) {
            return BANJO;
        }
        if (state.isOf(Blocks.GLOWSTONE)) {
            return PLING;
        }
        Material material = state.getMaterial();
        if (material == Material.STONE) {
            return BASEDRUM;
        }
        if (material == Material.AGGREGATE) {
            return SNARE;
        }
        if (material == Material.GLASS) {
            return HAT;
        }
        if (material == Material.WOOD || material == Material.NETHER_WOOD) {
            return BASS;
        }
        return HARP;
    }

    static enum Type {
        BASE_BLOCK,
        MOB_HEAD,
        CUSTOM;

    }
}

