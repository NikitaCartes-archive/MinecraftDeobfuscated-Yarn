package net.minecraft.vote;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_8275;
import net.minecraft.class_8291;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

public class BiomeColorRule extends class_8275<RegistryKey<Biome>, NamedColors.NamedColor> {
	private final Map<RegistryKey<Biome>, NamedColors.NamedColor> biomeToColor = new HashMap();
	private final String rule;
	private boolean field_43740;

	public BiomeColorRule(String rule) {
		super(RegistryKey.createCodec(RegistryKeys.BIOME), NamedColors.COLOR_CODEC);
		this.rule = rule;
	}

	public int getColor(RegistryEntry<Biome> biomeEntry, int fallback) {
		Optional<RegistryKey<Biome>> optional = biomeEntry.getKey();
		if (optional.isEmpty()) {
			return fallback;
		} else {
			NamedColors.NamedColor namedColor = (NamedColors.NamedColor)this.biomeToColor.get(optional.get());
			return namedColor != null ? namedColor.rgb() : fallback;
		}
	}

	public Optional<Integer> getColor(RegistryEntry<Biome> biomeEntry) {
		return biomeEntry.getKey().map(this.biomeToColor::get).map(NamedColors.NamedColor::rgb);
	}

	protected Text method_50162(RegistryKey<Biome> registryKey, NamedColors.NamedColor namedColor) {
		return Text.translatable(this.rule, Text.translatable(Util.createTranslationKey("biome", registryKey.getValue())), namedColor.name());
	}

	protected void method_50138(RegistryKey<Biome> registryKey, NamedColors.NamedColor namedColor) {
		boolean bl = !Objects.equals(namedColor, this.biomeToColor.put(registryKey, namedColor));
		this.field_43740 |= bl;
	}

	protected void method_50136(RegistryKey<Biome> registryKey) {
		boolean bl = this.biomeToColor.remove(registryKey) != null;
		this.field_43740 |= bl;
	}

	public boolean method_50285() {
		boolean bl = this.field_43740;
		this.field_43740 = false;
		return bl;
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.biomeToColor.entrySet().stream().map(entry -> new class_8275.class_8276((RegistryKey)entry.getKey(), (NamedColors.NamedColor)entry.getValue()));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Registry<Biome> registry = minecraftServer.getRegistryManager().get(RegistryKeys.BIOME);
		return Stream.generate(() -> Util.getRandomOrEmpty(NamedColors.NAMED_COLORS, random))
			.flatMap(Optional::stream)
			.map(namedColor -> registry.getRandom(random).map(reference -> new class_8275.class_8276(reference.registryKey(), namedColor)))
			.flatMap(Optional::stream)
			.limit((long)i)
			.map(arg -> arg);
	}
}
