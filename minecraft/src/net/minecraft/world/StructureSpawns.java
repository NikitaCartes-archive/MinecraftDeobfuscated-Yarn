package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;

public record StructureSpawns(StructureSpawns.BoundingBox boundingBox, Pool<SpawnSettings.SpawnEntry> spawns) {
	public static final Codec<StructureSpawns> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StructureSpawns.BoundingBox.CODEC.fieldOf("bounding_box").forGetter(StructureSpawns::boundingBox),
					Pool.createCodec(SpawnSettings.SpawnEntry.CODEC).fieldOf("spawns").forGetter(StructureSpawns::spawns)
				)
				.apply(instance, StructureSpawns::new)
	);

	public static enum BoundingBox implements StringIdentifiable {
		PIECE("piece"),
		STRUCTURE("full");

		public static final Codec<StructureSpawns.BoundingBox> CODEC = StringIdentifiable.createCodec(StructureSpawns.BoundingBox::values);
		private final String name;

		private BoundingBox(final String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
