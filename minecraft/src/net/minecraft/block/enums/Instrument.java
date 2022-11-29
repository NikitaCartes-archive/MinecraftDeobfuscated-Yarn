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

public enum Instrument implements StringIdentifiable {
	HARP("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP, Instrument.Type.BASE_BLOCK),
	BASEDRUM("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, Instrument.Type.BASE_BLOCK),
	SNARE("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE, Instrument.Type.BASE_BLOCK),
	HAT("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT, Instrument.Type.BASE_BLOCK),
	BASS("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS, Instrument.Type.BASE_BLOCK),
	FLUTE("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, Instrument.Type.BASE_BLOCK),
	BELL("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL, Instrument.Type.BASE_BLOCK),
	GUITAR("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, Instrument.Type.BASE_BLOCK),
	CHIME("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME, Instrument.Type.BASE_BLOCK),
	XYLOPHONE("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, Instrument.Type.BASE_BLOCK),
	IRON_XYLOPHONE("iron_xylophone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, Instrument.Type.BASE_BLOCK),
	COW_BELL("cow_bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, Instrument.Type.BASE_BLOCK),
	DIDGERIDOO("didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, Instrument.Type.BASE_BLOCK),
	BIT("bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT, Instrument.Type.BASE_BLOCK),
	BANJO("banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO, Instrument.Type.BASE_BLOCK),
	PLING("pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING, Instrument.Type.BASE_BLOCK),
	ZOMBIE("zombie", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ZOMBIE, Instrument.Type.MOB_HEAD),
	SKELETON("skeleton", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_SKELETON, Instrument.Type.MOB_HEAD),
	CREEPER("creeper", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_CREEPER, Instrument.Type.MOB_HEAD),
	DRAGON("dragon", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, Instrument.Type.MOB_HEAD),
	WITHER_SKELETON("wither_skeleton", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_WITHER_SKELETON, Instrument.Type.MOB_HEAD),
	PIGLIN("piglin", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_PIGLIN, Instrument.Type.MOB_HEAD),
	CUSTOM_HEAD("custom_head", SoundEvents.UI_BUTTON_CLICK, Instrument.Type.CUSTOM);

	private final String name;
	private final RegistryEntry<SoundEvent> sound;
	private final Instrument.Type type;

	private Instrument(String name, RegistryEntry<SoundEvent> sound, Instrument.Type type) {
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
		return this.type == Instrument.Type.BASE_BLOCK;
	}

	/**
	 * {@return whether note blocks playing this instrument should determine the sound from skulls above them}
	 */
	public boolean hasCustomSound() {
		return this.type == Instrument.Type.CUSTOM;
	}

	/**
	 * {@return whether this instrument should prevent note blocks from playing when they do not have air above them}
	 */
	public boolean shouldRequireAirAbove() {
		return this.type == Instrument.Type.BASE_BLOCK;
	}

	public static Optional<Instrument> fromAboveState(BlockState state) {
		if (state.isOf(Blocks.ZOMBIE_HEAD)) {
			return Optional.of(ZOMBIE);
		} else if (state.isOf(Blocks.SKELETON_SKULL)) {
			return Optional.of(SKELETON);
		} else if (state.isOf(Blocks.CREEPER_HEAD)) {
			return Optional.of(CREEPER);
		} else if (state.isOf(Blocks.DRAGON_HEAD)) {
			return Optional.of(DRAGON);
		} else if (state.isOf(Blocks.WITHER_SKELETON_SKULL)) {
			return Optional.of(WITHER_SKELETON);
		} else if (state.isOf(Blocks.PIGLIN_HEAD)) {
			return Optional.of(PIGLIN);
		} else {
			return state.isOf(Blocks.PLAYER_HEAD) ? Optional.of(CUSTOM_HEAD) : Optional.empty();
		}
	}

	public static Instrument fromBelowState(BlockState state) {
		if (state.isOf(Blocks.CLAY)) {
			return FLUTE;
		} else if (state.isOf(Blocks.GOLD_BLOCK)) {
			return BELL;
		} else if (state.isIn(BlockTags.WOOL)) {
			return GUITAR;
		} else if (state.isOf(Blocks.PACKED_ICE)) {
			return CHIME;
		} else if (state.isOf(Blocks.BONE_BLOCK)) {
			return XYLOPHONE;
		} else if (state.isOf(Blocks.IRON_BLOCK)) {
			return IRON_XYLOPHONE;
		} else if (state.isOf(Blocks.SOUL_SAND)) {
			return COW_BELL;
		} else if (state.isOf(Blocks.PUMPKIN)) {
			return DIDGERIDOO;
		} else if (state.isOf(Blocks.EMERALD_BLOCK)) {
			return BIT;
		} else if (state.isOf(Blocks.HAY_BLOCK)) {
			return BANJO;
		} else if (state.isOf(Blocks.GLOWSTONE)) {
			return PLING;
		} else {
			Material material = state.getMaterial();
			if (material == Material.STONE) {
				return BASEDRUM;
			} else if (material == Material.AGGREGATE) {
				return SNARE;
			} else if (material == Material.GLASS) {
				return HAT;
			} else {
				return material != Material.WOOD && material != Material.NETHER_WOOD ? HARP : BASS;
			}
		}
	}

	static enum Type {
		BASE_BLOCK,
		MOB_HEAD,
		CUSTOM;
	}
}
