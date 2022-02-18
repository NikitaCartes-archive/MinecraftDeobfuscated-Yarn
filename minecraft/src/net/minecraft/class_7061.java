package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;

public record class_7061(class_7061.class_7062 boundingBox, Pool<SpawnSettings.SpawnEntry> spawns) {
	public static final Codec<class_7061> field_37198 = RecordCodecBuilder.create(
		instance -> instance.group(
					class_7061.class_7062.field_37202.fieldOf("bounding_box").forGetter(class_7061::boundingBox),
					Pool.createCodec(SpawnSettings.SpawnEntry.CODEC).fieldOf("spawns").forGetter(class_7061::spawns)
				)
				.apply(instance, class_7061::new)
	);

	public static enum class_7062 implements StringIdentifiable {
		PIECE("piece"),
		STRUCTURE("full");

		public static final class_7061.class_7062[] field_37201 = values();
		public static final Codec<class_7061.class_7062> field_37202 = StringIdentifiable.createCodec(() -> field_37201, class_7061.class_7062::method_41151);
		private final String field_37203;

		private class_7062(String string2) {
			this.field_37203 = string2;
		}

		@Override
		public String asString() {
			return this.field_37203;
		}

		@Nullable
		public static class_7061.class_7062 method_41151(@Nullable String string) {
			if (string == null) {
				return null;
			} else {
				for (class_7061.class_7062 lv : field_37201) {
					if (lv.field_37203.equals(string)) {
						return lv;
					}
				}

				return null;
			}
		}
	}
}
