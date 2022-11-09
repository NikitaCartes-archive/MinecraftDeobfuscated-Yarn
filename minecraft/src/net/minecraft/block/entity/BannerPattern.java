package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class BannerPattern {
	final String id;

	public BannerPattern(String id) {
		this.id = id;
	}

	public static Identifier getSpriteId(RegistryKey<BannerPattern> pattern, boolean banner) {
		String string = banner ? "banner" : "shield";
		return pattern.getValue().withPrefixedPath("entity/" + string + "/");
	}

	public String getId() {
		return this.id;
	}

	@Nullable
	public static RegistryEntry<BannerPattern> byId(String id) {
		return (RegistryEntry<BannerPattern>)Registries.BANNER_PATTERN
			.streamEntries()
			.filter(pattern -> ((BannerPattern)pattern.value()).id.equals(id))
			.findAny()
			.orElse(null);
	}

	public static class Patterns {
		private final List<Pair<RegistryEntry<BannerPattern>, DyeColor>> entries = Lists.<Pair<RegistryEntry<BannerPattern>, DyeColor>>newArrayList();

		public BannerPattern.Patterns add(RegistryKey<BannerPattern> pattern, DyeColor color) {
			return this.add(Registries.BANNER_PATTERN.entryOf(pattern), color);
		}

		public BannerPattern.Patterns add(RegistryEntry<BannerPattern> pattern, DyeColor color) {
			return this.add(Pair.of(pattern, color));
		}

		public BannerPattern.Patterns add(Pair<RegistryEntry<BannerPattern>, DyeColor> pattern) {
			this.entries.add(pattern);
			return this;
		}

		public NbtList toNbt() {
			NbtList nbtList = new NbtList();

			for (Pair<RegistryEntry<BannerPattern>, DyeColor> pair : this.entries) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putString("Pattern", pair.getFirst().value().id);
				nbtCompound.putInt("Color", pair.getSecond().getId());
				nbtList.add(nbtCompound);
			}

			return nbtList;
		}
	}
}
