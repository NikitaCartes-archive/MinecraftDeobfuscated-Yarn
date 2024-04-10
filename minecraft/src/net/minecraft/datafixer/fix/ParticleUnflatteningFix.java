package net.minecraft.datafixer.fix;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

public class ParticleUnflatteningFix extends DataFix {
	private static final Logger LOGGER = LogUtils.getLogger();

	public ParticleUnflatteningFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.PARTICLE);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.PARTICLE);
		return this.writeFixAndRead("ParticleUnflatteningFix", type, type2, this::fixParticle);
	}

	private <T> Dynamic<T> fixParticle(Dynamic<T> dynamic) {
		Optional<String> optional = dynamic.asString().result();
		if (optional.isEmpty()) {
			return dynamic;
		} else {
			String string = (String)optional.get();
			String[] strings = string.split(" ", 2);
			String string2 = IdentifierNormalizingSchema.normalize(strings[0]);
			Dynamic<T> dynamic2 = dynamic.createMap(Map.of(dynamic.createString("type"), dynamic.createString(string2)));

			return switch (string2) {
				case "minecraft:item" -> strings.length > 1 ? this.fixItemParticle(dynamic2, strings[1]) : dynamic2;
				case "minecraft:block", "minecraft:block_marker", "minecraft:falling_dust", "minecraft:dust_pillar" -> strings.length > 1
				? this.fixBlockParticle(dynamic2, strings[1])
				: dynamic2;
				case "minecraft:dust" -> strings.length > 1 ? this.fixDustParticle(dynamic2, strings[1]) : dynamic2;
				case "minecraft:dust_color_transition" -> strings.length > 1 ? this.fixDustColorTransitionParticle(dynamic2, strings[1]) : dynamic2;
				case "minecraft:sculk_charge" -> strings.length > 1 ? this.fixSculkChargeParticle(dynamic2, strings[1]) : dynamic2;
				case "minecraft:vibration" -> strings.length > 1 ? this.fixVibrationParticle(dynamic2, strings[1]) : dynamic2;
				case "minecraft:shriek" -> strings.length > 1 ? this.fixShriekParticle(dynamic2, strings[1]) : dynamic2;
				default -> dynamic2;
			};
		}
	}

	private <T> Dynamic<T> fixItemParticle(Dynamic<T> dynamic, String string) {
		int i = string.indexOf("{");
		Dynamic<T> dynamic2 = dynamic.createMap(Map.of(dynamic.createString("Count"), dynamic.createInt(1)));
		if (i == -1) {
			dynamic2 = dynamic2.set("id", dynamic.createString(string));
		} else {
			dynamic2 = dynamic2.set("id", dynamic.createString(string.substring(0, i)));
			NbtCompound nbtCompound = tryParse(string.substring(i));
			if (nbtCompound != null) {
				dynamic2 = dynamic2.set("tag", new Dynamic<>(NbtOps.INSTANCE, nbtCompound).convert(dynamic.getOps()));
			}
		}

		return dynamic.set("item", dynamic2);
	}

	@Nullable
	private static NbtCompound tryParse(String snbt) {
		try {
			return StringNbtReader.parse(snbt);
		} catch (Exception var2) {
			LOGGER.warn("Failed to parse tag: {}", snbt, var2);
			return null;
		}
	}

	private <T> Dynamic<T> fixBlockParticle(Dynamic<T> dynamic, String string) {
		int i = string.indexOf("[");
		Dynamic<T> dynamic2 = dynamic.emptyMap();
		if (i == -1) {
			dynamic2 = dynamic2.set("Name", dynamic.createString(IdentifierNormalizingSchema.normalize(string)));
		} else {
			dynamic2 = dynamic2.set("Name", dynamic.createString(IdentifierNormalizingSchema.normalize(string.substring(0, i))));
			Map<Dynamic<T>, Dynamic<T>> map = method_59632(dynamic, string.substring(i));
			if (!map.isEmpty()) {
				dynamic2 = dynamic2.set("Properties", dynamic.createMap(map));
			}
		}

		return dynamic.set("block_state", dynamic2);
	}

	private static <T> Map<Dynamic<T>, Dynamic<T>> method_59632(Dynamic<T> dynamic, String string) {
		try {
			Map<Dynamic<T>, Dynamic<T>> map = new HashMap();
			StringReader stringReader = new StringReader(string);
			stringReader.expect('[');
			stringReader.skipWhitespace();

			while (stringReader.canRead() && stringReader.peek() != ']') {
				stringReader.skipWhitespace();
				String string2 = stringReader.readString();
				stringReader.skipWhitespace();
				stringReader.expect('=');
				stringReader.skipWhitespace();
				String string3 = stringReader.readString();
				stringReader.skipWhitespace();
				map.put(dynamic.createString(string2), dynamic.createString(string3));
				if (stringReader.canRead()) {
					if (stringReader.peek() != ',') {
						break;
					}

					stringReader.skip();
				}
			}

			stringReader.expect(']');
			return map;
		} catch (Exception var6) {
			LOGGER.warn("Failed to parse block properties: {}", string, var6);
			return Map.of();
		}
	}

	private static <T> Dynamic<T> method_59628(Dynamic<T> dynamic, StringReader stringReader) throws CommandSyntaxException {
		float f = stringReader.readFloat();
		stringReader.expect(' ');
		float g = stringReader.readFloat();
		stringReader.expect(' ');
		float h = stringReader.readFloat();
		return dynamic.createList(Stream.of(f, g, h).map(dynamic::createFloat));
	}

	private <T> Dynamic<T> fixDustParticle(Dynamic<T> dynamic, String string) {
		try {
			StringReader stringReader = new StringReader(string);
			Dynamic<T> dynamic2 = method_59628(dynamic, stringReader);
			stringReader.expect(' ');
			float f = stringReader.readFloat();
			return dynamic.set("color", dynamic2).set("scale", dynamic.createFloat(f));
		} catch (Exception var6) {
			LOGGER.warn("Failed to parse particle options: {}", string, var6);
			return dynamic;
		}
	}

	private <T> Dynamic<T> fixDustColorTransitionParticle(Dynamic<T> dynamic, String string) {
		try {
			StringReader stringReader = new StringReader(string);
			Dynamic<T> dynamic2 = method_59628(dynamic, stringReader);
			stringReader.expect(' ');
			float f = stringReader.readFloat();
			stringReader.expect(' ');
			Dynamic<T> dynamic3 = method_59628(dynamic, stringReader);
			return dynamic.set("from_color", dynamic2).set("to_color", dynamic3).set("scale", dynamic.createFloat(f));
		} catch (Exception var7) {
			LOGGER.warn("Failed to parse particle options: {}", string, var7);
			return dynamic;
		}
	}

	private <T> Dynamic<T> fixSculkChargeParticle(Dynamic<T> dynamic, String string) {
		try {
			StringReader stringReader = new StringReader(string);
			float f = stringReader.readFloat();
			return dynamic.set("roll", dynamic.createFloat(f));
		} catch (Exception var5) {
			LOGGER.warn("Failed to parse particle options: {}", string, var5);
			return dynamic;
		}
	}

	private <T> Dynamic<T> fixVibrationParticle(Dynamic<T> dynamic, String string) {
		try {
			StringReader stringReader = new StringReader(string);
			float f = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float)stringReader.readDouble();
			stringReader.expect(' ');
			int i = stringReader.readInt();
			Dynamic<T> dynamic2 = (Dynamic<T>)dynamic.createIntList(IntStream.of(new int[]{MathHelper.floor(f), MathHelper.floor(g), MathHelper.floor(h)}));
			Dynamic<T> dynamic3 = dynamic.createMap(Map.of(dynamic.createString("type"), dynamic.createString("minecraft:block"), dynamic.createString("pos"), dynamic2));
			return dynamic.set("destination", dynamic3).set("arrival_in_ticks", dynamic.createInt(i));
		} catch (Exception var10) {
			LOGGER.warn("Failed to parse particle options: {}", string, var10);
			return dynamic;
		}
	}

	private <T> Dynamic<T> fixShriekParticle(Dynamic<T> dynamic, String string) {
		try {
			StringReader stringReader = new StringReader(string);
			int i = stringReader.readInt();
			return dynamic.set("delay", dynamic.createInt(i));
		} catch (Exception var5) {
			LOGGER.warn("Failed to parse particle options: {}", string, var5);
			return dynamic;
		}
	}
}
