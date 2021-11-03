package net.minecraft.block.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
	private static final Joiner JOINER = Joiner.on(",");
	private final List<String[]> aisles = Lists.<String[]>newArrayList();
	private final Map<Character, Predicate<CachedBlockPosition>> charMap = Maps.<Character, Predicate<CachedBlockPosition>>newHashMap();
	private int height;
	private int width;

	private BlockPatternBuilder() {
		this.charMap.put(' ', (Predicate)pos -> true);
	}

	public BlockPatternBuilder aisle(String... pattern) {
		if (!ArrayUtils.isEmpty((Object[])pattern) && !StringUtils.isEmpty(pattern[0])) {
			if (this.aisles.isEmpty()) {
				this.height = pattern.length;
				this.width = pattern[0].length();
			}

			if (pattern.length != this.height) {
				throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + pattern.length + ")");
			} else {
				for (String string : pattern) {
					if (string.length() != this.width) {
						throw new IllegalArgumentException(
							"Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + string.length() + ")"
						);
					}

					for (char c : string.toCharArray()) {
						if (!this.charMap.containsKey(c)) {
							this.charMap.put(c, null);
						}
					}
				}

				this.aisles.add(pattern);
				return this;
			}
		} else {
			throw new IllegalArgumentException("Empty pattern for aisle");
		}
	}

	public static BlockPatternBuilder start() {
		return new BlockPatternBuilder();
	}

	public BlockPatternBuilder where(char key, Predicate<CachedBlockPosition> predicate) {
		this.charMap.put(key, predicate);
		return this;
	}

	public BlockPattern build() {
		return new BlockPattern(this.bakePredicates());
	}

	private Predicate<CachedBlockPosition>[][][] bakePredicates() {
		this.validate();
		Predicate<CachedBlockPosition>[][][] predicates = (Predicate<CachedBlockPosition>[][][])Array.newInstance(
			Predicate.class, new int[]{this.aisles.size(), this.height, this.width}
		);

		for (int i = 0; i < this.aisles.size(); i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.width; k++) {
					predicates[i][j][k] = (Predicate<CachedBlockPosition>)this.charMap.get(((String[])this.aisles.get(i))[j].charAt(k));
				}
			}
		}

		return predicates;
	}

	private void validate() {
		List<Character> list = Lists.<Character>newArrayList();

		for (Entry<Character, Predicate<CachedBlockPosition>> entry : this.charMap.entrySet()) {
			if (entry.getValue() == null) {
				list.add((Character)entry.getKey());
			}
		}

		if (!list.isEmpty()) {
			throw new IllegalStateException("Predicates for character(s) " + JOINER.join(list) + " are missing");
		}
	}
}
