package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record Defines(Map<String, String> values, Set<String> flags) {
	public static final Defines EMPTY = new Defines(Map.of(), Set.of());
	public static final Codec<Defines> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("values", Map.of()).forGetter(Defines::values),
					Codec.STRING.listOf().<Set>xmap(Set::copyOf, List::copyOf).optionalFieldOf("flags", Set.of()).forGetter(Defines::flags)
				)
				.apply(instance, Defines::new)
	);

	public static Defines.Builder builder() {
		return new Defines.Builder();
	}

	public Defines withMerged(Defines other) {
		if (this.isEmpty()) {
			return other;
		} else if (other.isEmpty()) {
			return this;
		} else {
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builderWithExpectedSize(this.values.size() + other.values.size());
			builder.putAll(this.values);
			builder.putAll(other.values);
			ImmutableSet.Builder<String> builder2 = ImmutableSet.builderWithExpectedSize(this.flags.size() + other.flags.size());
			builder2.addAll(this.flags);
			builder2.addAll(other.flags);
			return new Defines(builder.buildKeepingLast(), builder2.build());
		}
	}

	public String toSource() {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<String, String> entry : this.values.entrySet()) {
			String string = (String)entry.getKey();
			String string2 = (String)entry.getValue();
			stringBuilder.append("#define ").append(string).append(" ").append(string2).append('\n');
		}

		for (String string3 : this.flags) {
			stringBuilder.append("#define ").append(string3).append('\n');
		}

		return stringBuilder.toString();
	}

	public boolean isEmpty() {
		return this.values.isEmpty() && this.flags.isEmpty();
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final ImmutableMap.Builder<String, String> values = ImmutableMap.builder();
		private final ImmutableSet.Builder<String> flags = ImmutableSet.builder();

		Builder() {
		}

		public Defines.Builder define(String key, String value) {
			if (value.isBlank()) {
				throw new IllegalArgumentException("Cannot define empty string");
			} else {
				this.values.put(key, escapeLinebreak(value));
				return this;
			}
		}

		private static String escapeLinebreak(String string) {
			return string.replaceAll("\n", "\\\\\n");
		}

		public Defines.Builder define(String key, float value) {
			this.values.put(key, String.valueOf(value));
			return this;
		}

		public Defines.Builder flag(String flag) {
			this.flags.add(flag);
			return this;
		}

		public Defines build() {
			return new Defines(this.values.build(), this.flags.build());
		}
	}
}
