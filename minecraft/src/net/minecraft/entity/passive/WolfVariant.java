package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public record WolfVariant(Identifier texture, Identifier tameTexture, Identifier angryTexture, RegistryEntryList<Biome> biomes) {
	public static final Codec<WolfVariant> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("texture").forGetter(WolfVariant::texture),
					Identifier.CODEC.fieldOf("tame_texture").forGetter(WolfVariant::tameTexture),
					Identifier.CODEC.fieldOf("angry_texture").forGetter(WolfVariant::angryTexture),
					RegistryCodecs.entryList(RegistryKeys.BIOME).fieldOf("biomes").forGetter(WolfVariant::biomes)
				)
				.apply(instance, WolfVariant::new)
	);
}
