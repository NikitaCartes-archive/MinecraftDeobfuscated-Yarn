package net.minecraft;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class class_4158 {
	public static final Predicate<class_4158> field_18500 = arg -> ((Set)Registry.VILLAGER_PROFESSION
				.stream()
				.map(VillagerProfession::method_19198)
				.collect(Collectors.toSet()))
			.contains(arg);
	public static final Predicate<class_4158> field_18501 = arg -> true;
	public static final class_4158 field_18502 = method_19160("unemployed", BlockTags.field_18830, 1, null, field_18500);
	public static final class_4158 field_18503 = method_19159("armorer", BlockTags.field_18831, 1, SoundEvents.field_18826);
	public static final class_4158 field_18504 = method_19159("butcher", BlockTags.field_18832, 1, SoundEvents.field_18827);
	public static final class_4158 field_18505 = method_19159("cartographer", BlockTags.field_18833, 1, SoundEvents.field_18828);
	public static final class_4158 field_18506 = method_19159("cleric", BlockTags.field_18834, 1, SoundEvents.field_18829);
	public static final class_4158 field_18507 = method_19159("farmer", BlockTags.field_18835, 1, SoundEvents.field_18817);
	public static final class_4158 field_18508 = method_19159("fisherman", BlockTags.field_18836, 1, SoundEvents.field_18818);
	public static final class_4158 field_18509 = method_19159("fletcher", BlockTags.field_18837, 1, SoundEvents.field_18819);
	public static final class_4158 field_18510 = method_19159("leatherworker", BlockTags.field_18838, 1, SoundEvents.field_18820);
	public static final class_4158 field_18511 = method_19159("librarian", BlockTags.field_18839, 1, SoundEvents.field_18821);
	public static final class_4158 field_18512 = method_19159("mason", BlockTags.field_18840, 1, SoundEvents.field_18822);
	public static final class_4158 field_18513 = method_19159("nitwit", BlockTags.field_18841, 1, null);
	public static final class_4158 field_18514 = method_19159("shepherd", BlockTags.field_18842, 1, SoundEvents.field_18823);
	public static final class_4158 field_18515 = method_19159("toolsmith", BlockTags.field_18843, 1, SoundEvents.field_18824);
	public static final class_4158 field_18516 = method_19159("weaponsmith", BlockTags.field_18844, 1, SoundEvents.field_18825);
	public static final class_4158 field_18517 = method_19159("home", BlockTags.field_16443, 1, null);
	public static final class_4158 field_18518 = method_19159("meeting", BlockTags.field_18846, 32, null);
	private final String field_18519;
	private final Tag<Block> field_18520;
	private final int field_18521;
	@Nullable
	private final SoundEvent field_18522;
	private final Predicate<class_4158> field_18523;

	private class_4158(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent, Predicate<class_4158> predicate) {
		this.field_18519 = string;
		this.field_18520 = tag;
		this.field_18521 = i;
		this.field_18522 = soundEvent;
		this.field_18523 = predicate;
	}

	private class_4158(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent) {
		this.field_18519 = string;
		this.field_18520 = tag;
		this.field_18521 = i;
		this.field_18522 = soundEvent;
		this.field_18523 = arg -> arg == this;
	}

	@Environment(EnvType.CLIENT)
	public String method_19155() {
		return this.field_18519;
	}

	public int method_19161() {
		return this.field_18521;
	}

	public Predicate<class_4158> method_19164() {
		return this.field_18523;
	}

	public boolean method_19157(Block block) {
		return this.field_18520.contains(block);
	}

	public String toString() {
		return this.field_18519;
	}

	@Nullable
	public SoundEvent method_19166() {
		return this.field_18522;
	}

	private static class_4158 method_19159(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent) {
		return Registry.field_18792.method_10272(new Identifier(string), new class_4158(string, tag, i, soundEvent));
	}

	private static class_4158 method_19160(String string, Tag<Block> tag, int i, @Nullable SoundEvent soundEvent, Predicate<class_4158> predicate) {
		return Registry.field_18792.method_10272(new Identifier(string), new class_4158(string, tag, i, soundEvent, predicate));
	}

	public static class_4158 method_19163(Block block) {
		List<class_4158> list = (List<class_4158>)Registry.field_18792.stream().filter(arg -> arg.method_19157(block)).collect(Collectors.toList());
		if (list.size() > 1) {
			throw new IllegalStateException(String.format("%s is defined in too many tags", block));
		} else {
			return (class_4158)list.get(0);
		}
	}
}
