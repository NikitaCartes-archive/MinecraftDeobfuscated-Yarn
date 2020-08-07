package net.minecraft.block.enums;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.StringIdentifiable;

public enum Instrument implements StringIdentifiable {
	field_12648("harp", SoundEvents.field_15114),
	field_12653("basedrum", SoundEvents.field_15047),
	field_12643("snare", SoundEvents.field_14708),
	field_12645("hat", SoundEvents.field_15204),
	field_12651("bass", SoundEvents.field_14624),
	field_12650("flute", SoundEvents.field_14989),
	field_12644("bell", SoundEvents.field_14793),
	field_12654("guitar", SoundEvents.field_14903),
	field_12647("chime", SoundEvents.field_14725),
	field_12655("xylophone", SoundEvents.field_14776),
	field_18284("iron_xylophone", SoundEvents.field_18308),
	field_18285("cow_bell", SoundEvents.field_18309),
	field_18286("didgeridoo", SoundEvents.field_18310),
	field_18287("bit", SoundEvents.field_18311),
	field_18288("banjo", SoundEvents.field_18312),
	field_18289("pling", SoundEvents.field_14622);

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
		if (state.isOf(Blocks.field_10460)) {
			return field_12650;
		} else if (state.isOf(Blocks.field_10205)) {
			return field_12644;
		} else if (state.isIn(BlockTags.field_15481)) {
			return field_12654;
		} else if (state.isOf(Blocks.field_10225)) {
			return field_12647;
		} else if (state.isOf(Blocks.field_10166)) {
			return field_12655;
		} else if (state.isOf(Blocks.field_10085)) {
			return field_18284;
		} else if (state.isOf(Blocks.field_10114)) {
			return field_18285;
		} else if (state.isOf(Blocks.field_10261)) {
			return field_18286;
		} else if (state.isOf(Blocks.field_10234)) {
			return field_18287;
		} else if (state.isOf(Blocks.field_10359)) {
			return field_18288;
		} else if (state.isOf(Blocks.field_10171)) {
			return field_18289;
		} else {
			Material material = state.getMaterial();
			if (material == Material.STONE) {
				return field_12653;
			} else if (material == Material.AGGREGATE) {
				return field_12643;
			} else if (material == Material.GLASS) {
				return field_12645;
			} else {
				return material != Material.WOOD && material != Material.NETHER_WOOD ? field_12648 : field_12651;
			}
		}
	}
}
