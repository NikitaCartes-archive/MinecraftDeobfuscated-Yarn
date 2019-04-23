/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
    private static final Joiner JOINER = Joiner.on(",");
    private final List<String[]> aisles = Lists.newArrayList();
    private final Map<Character, Predicate<CachedBlockPosition>> charMap = Maps.newHashMap();
    private int height;
    private int width;

    private BlockPatternBuilder() {
        this.charMap.put(Character.valueOf(' '), Predicates.alwaysTrue());
    }

    public BlockPatternBuilder aisle(String ... strings) {
        if (ArrayUtils.isEmpty(strings) || StringUtils.isEmpty(strings[0])) {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
        if (this.aisles.isEmpty()) {
            this.height = strings.length;
            this.width = strings[0].length();
        }
        if (strings.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + strings.length + ")");
        }
        for (String string : strings) {
            if (string.length() != this.width) {
                throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + string.length() + ")");
            }
            for (char c : string.toCharArray()) {
                if (this.charMap.containsKey(Character.valueOf(c))) continue;
                this.charMap.put(Character.valueOf(c), null);
            }
        }
        this.aisles.add(strings);
        return this;
    }

    public static BlockPatternBuilder start() {
        return new BlockPatternBuilder();
    }

    public BlockPatternBuilder where(char c, Predicate<CachedBlockPosition> predicate) {
        this.charMap.put(Character.valueOf(c), predicate);
        return this;
    }

    public BlockPattern build() {
        return new BlockPattern(this.bakePredicates());
    }

    private Predicate<CachedBlockPosition>[][][] bakePredicates() {
        this.validate();
        Predicate[][][] predicates = (Predicate[][][])Array.newInstance(Predicate.class, this.aisles.size(), this.height, this.width);
        for (int i = 0; i < this.aisles.size(); ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.width; ++k) {
                    predicates[i][j][k] = this.charMap.get(Character.valueOf(this.aisles.get(i)[j].charAt(k)));
                }
            }
        }
        return predicates;
    }

    private void validate() {
        ArrayList<Character> list = Lists.newArrayList();
        for (Map.Entry<Character, Predicate<CachedBlockPosition>> entry : this.charMap.entrySet()) {
            if (entry.getValue() != null) continue;
            list.add(entry.getKey());
        }
        if (!list.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + JOINER.join(list) + " are missing");
        }
    }
}

