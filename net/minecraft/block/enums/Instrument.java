/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.enums;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.StringIdentifiable;

public enum Instrument implements StringIdentifiable
{
    HARP("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP),
    BASEDRUM("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM),
    SNARE("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE),
    HAT("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT),
    BASS("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS),
    FLUTE("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE),
    BELL("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL),
    GUITAR("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR),
    CHIME("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME),
    XYLOPHONE("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE),
    IRON_XYLOPHONE("iron_xylophone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE),
    COW_BELL("cow_bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL),
    DIDGERIDOO("didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO),
    BIT("bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT),
    BANJO("banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO),
    PLING("pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING);

    private final String name;
    private final SoundEvent sound;

    private Instrument(String name, SoundEvent sound) {
        this.name = name;
        this.sound = sound;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public static Instrument fromBlockState(BlockState state) {
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
}

