/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class TextureMap {
    private final Map<TextureKey, Identifier> entries = Maps.newHashMap();
    private final Set<TextureKey> inherited = Sets.newHashSet();

    public TextureMap put(TextureKey key, Identifier id) {
        this.entries.put(key, id);
        return this;
    }

    public TextureMap register(TextureKey key, Identifier id) {
        this.entries.put(key, id);
        this.inherited.add(key);
        return this;
    }

    public Stream<TextureKey> getInherited() {
        return this.inherited.stream();
    }

    public TextureMap copy(TextureKey parent, TextureKey child) {
        this.entries.put(child, this.entries.get(parent));
        return this;
    }

    public TextureMap inherit(TextureKey parent, TextureKey child) {
        this.entries.put(child, this.entries.get(parent));
        this.inherited.add(child);
        return this;
    }

    public Identifier getTexture(TextureKey key) {
        for (TextureKey textureKey = key; textureKey != null; textureKey = textureKey.getParent()) {
            Identifier identifier = this.entries.get(textureKey);
            if (identifier == null) continue;
            return identifier;
        }
        throw new IllegalStateException("Can't find texture for slot " + key);
    }

    public TextureMap copyAndAdd(TextureKey key, Identifier id) {
        TextureMap textureMap = new TextureMap();
        textureMap.entries.putAll(this.entries);
        textureMap.inherited.addAll(this.inherited);
        textureMap.put(key, id);
        return textureMap;
    }

    public static TextureMap all(Block block) {
        Identifier identifier = TextureMap.getId(block);
        return TextureMap.all(identifier);
    }

    public static TextureMap texture(Block block) {
        Identifier identifier = TextureMap.getId(block);
        return TextureMap.texture(identifier);
    }

    public static TextureMap texture(Identifier id) {
        return new TextureMap().put(TextureKey.TEXTURE, id);
    }

    public static TextureMap all(Identifier id) {
        return new TextureMap().put(TextureKey.ALL, id);
    }

    public static TextureMap cross(Block block) {
        return TextureMap.of(TextureKey.CROSS, TextureMap.getId(block));
    }

    public static TextureMap cross(Identifier id) {
        return TextureMap.of(TextureKey.CROSS, id);
    }

    public static TextureMap plant(Block block) {
        return TextureMap.of(TextureKey.PLANT, TextureMap.getId(block));
    }

    public static TextureMap plant(Identifier id) {
        return TextureMap.of(TextureKey.PLANT, id);
    }

    public static TextureMap rail(Block block) {
        return TextureMap.of(TextureKey.RAIL, TextureMap.getId(block));
    }

    public static TextureMap rail(Identifier id) {
        return TextureMap.of(TextureKey.RAIL, id);
    }

    public static TextureMap wool(Block block) {
        return TextureMap.of(TextureKey.WOOL, TextureMap.getId(block));
    }

    public static TextureMap wool(Identifier id) {
        return TextureMap.of(TextureKey.WOOL, id);
    }

    public static TextureMap stem(Block block) {
        return TextureMap.of(TextureKey.STEM, TextureMap.getId(block));
    }

    public static TextureMap stemAndUpper(Block stem, Block upper) {
        return new TextureMap().put(TextureKey.STEM, TextureMap.getId(stem)).put(TextureKey.UPPERSTEM, TextureMap.getId(upper));
    }

    public static TextureMap pattern(Block block) {
        return TextureMap.of(TextureKey.PATTERN, TextureMap.getId(block));
    }

    public static TextureMap fan(Block block) {
        return TextureMap.of(TextureKey.FAN, TextureMap.getId(block));
    }

    public static TextureMap crop(Identifier id) {
        return TextureMap.of(TextureKey.CROP, id);
    }

    public static TextureMap paneAndTopForEdge(Block block, Block top) {
        return new TextureMap().put(TextureKey.PANE, TextureMap.getId(block)).put(TextureKey.EDGE, TextureMap.getSubId(top, "_top"));
    }

    public static TextureMap of(TextureKey key, Identifier id) {
        return new TextureMap().put(key, id);
    }

    public static TextureMap sideEnd(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.END, TextureMap.getSubId(block, "_top"));
    }

    public static TextureMap sideAndTop(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top"));
    }

    public static TextureMap sideAndEndForTop(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getId(block)).put(TextureKey.END, TextureMap.getSubId(block, "_top")).put(TextureKey.PARTICLE, TextureMap.getId(block));
    }

    public static TextureMap sideEnd(Identifier side, Identifier end) {
        return new TextureMap().put(TextureKey.SIDE, side).put(TextureKey.END, end);
    }

    public static TextureMap textureSideTop(Block block) {
        return new TextureMap().put(TextureKey.TEXTURE, TextureMap.getId(block)).put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top"));
    }

    public static TextureMap textureParticle(Block block) {
        return new TextureMap().put(TextureKey.TEXTURE, TextureMap.getId(block)).put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_particle"));
    }

    public static TextureMap sideTopBottom(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));
    }

    public static TextureMap wallSideTopBottom(Block block) {
        Identifier identifier = TextureMap.getId(block);
        return new TextureMap().put(TextureKey.WALL, identifier).put(TextureKey.SIDE, identifier).put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));
    }

    public static TextureMap wallSideEnd(Block block) {
        Identifier identifier = TextureMap.getId(block);
        return new TextureMap().put(TextureKey.TEXTURE, identifier).put(TextureKey.WALL, identifier).put(TextureKey.SIDE, identifier).put(TextureKey.END, TextureMap.getSubId(block, "_top"));
    }

    public static TextureMap topBottom(Identifier top, Identifier bottom) {
        return new TextureMap().put(TextureKey.TOP, top).put(TextureKey.BOTTOM, bottom);
    }

    public static TextureMap topBottom(Block block) {
        return new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));
    }

    public static TextureMap particle(Block block) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getId(block));
    }

    public static TextureMap particle(Identifier id) {
        return new TextureMap().put(TextureKey.PARTICLE, id);
    }

    public static TextureMap fire0(Block block) {
        return new TextureMap().put(TextureKey.FIRE, TextureMap.getSubId(block, "_0"));
    }

    public static TextureMap fire1(Block block) {
        return new TextureMap().put(TextureKey.FIRE, TextureMap.getSubId(block, "_1"));
    }

    public static TextureMap lantern(Block block) {
        return new TextureMap().put(TextureKey.LANTERN, TextureMap.getId(block));
    }

    public static TextureMap torch(Block block) {
        return new TextureMap().put(TextureKey.TORCH, TextureMap.getId(block));
    }

    public static TextureMap torch(Identifier id) {
        return new TextureMap().put(TextureKey.TORCH, id);
    }

    public static TextureMap particle(Item item) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getId(item));
    }

    public static TextureMap sideFrontBack(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).put(TextureKey.BACK, TextureMap.getSubId(block, "_back"));
    }

    public static TextureMap sideFrontTopBottom(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));
    }

    public static TextureMap sideFrontTop(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top"));
    }

    public static TextureMap sideFrontEnd(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).put(TextureKey.END, TextureMap.getSubId(block, "_end"));
    }

    public static TextureMap top(Block top) {
        return new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(top, "_top"));
    }

    public static TextureMap frontSideWithCustomBottom(Block block, Block bottom) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_front")).put(TextureKey.DOWN, TextureMap.getId(bottom)).put(TextureKey.UP, TextureMap.getSubId(block, "_top")).put(TextureKey.NORTH, TextureMap.getSubId(block, "_front")).put(TextureKey.EAST, TextureMap.getSubId(block, "_side")).put(TextureKey.SOUTH, TextureMap.getSubId(block, "_side")).put(TextureKey.WEST, TextureMap.getSubId(block, "_front"));
    }

    public static TextureMap frontTopSide(Block frontTopSideBlock, Block downBlock) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(frontTopSideBlock, "_front")).put(TextureKey.DOWN, TextureMap.getId(downBlock)).put(TextureKey.UP, TextureMap.getSubId(frontTopSideBlock, "_top")).put(TextureKey.NORTH, TextureMap.getSubId(frontTopSideBlock, "_front")).put(TextureKey.SOUTH, TextureMap.getSubId(frontTopSideBlock, "_front")).put(TextureKey.EAST, TextureMap.getSubId(frontTopSideBlock, "_side")).put(TextureKey.WEST, TextureMap.getSubId(frontTopSideBlock, "_side"));
    }

    public static TextureMap campfire(Block block) {
        return new TextureMap().put(TextureKey.LIT_LOG, TextureMap.getSubId(block, "_log_lit")).put(TextureKey.FIRE, TextureMap.getSubId(block, "_fire"));
    }

    public static TextureMap candleCake(Block block, boolean lit) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(Blocks.CAKE, "_side")).put(TextureKey.BOTTOM, TextureMap.getSubId(Blocks.CAKE, "_bottom")).put(TextureKey.TOP, TextureMap.getSubId(Blocks.CAKE, "_top")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.CAKE, "_side")).put(TextureKey.CANDLE, TextureMap.getSubId(block, lit ? "_lit" : ""));
    }

    public static TextureMap cauldron(Identifier content) {
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(Blocks.CAULDRON, "_side")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.CAULDRON, "_side")).put(TextureKey.TOP, TextureMap.getSubId(Blocks.CAULDRON, "_top")).put(TextureKey.BOTTOM, TextureMap.getSubId(Blocks.CAULDRON, "_bottom")).put(TextureKey.INSIDE, TextureMap.getSubId(Blocks.CAULDRON, "_inner")).put(TextureKey.CONTENT, content);
    }

    public static TextureMap sculkShrieker(boolean canSummon) {
        String string = canSummon ? "_can_summon" : "";
        return new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(Blocks.SCULK_SHRIEKER, "_bottom")).put(TextureKey.SIDE, TextureMap.getSubId(Blocks.SCULK_SHRIEKER, "_side")).put(TextureKey.TOP, TextureMap.getSubId(Blocks.SCULK_SHRIEKER, "_top")).put(TextureKey.INNER_TOP, TextureMap.getSubId(Blocks.SCULK_SHRIEKER, string + "_inner_top")).put(TextureKey.BOTTOM, TextureMap.getSubId(Blocks.SCULK_SHRIEKER, "_bottom"));
    }

    public static TextureMap layer0(Item item) {
        return new TextureMap().put(TextureKey.LAYER0, TextureMap.getId(item));
    }

    public static TextureMap layer0(Block block) {
        return new TextureMap().put(TextureKey.LAYER0, TextureMap.getId(block));
    }

    public static TextureMap layer0(Identifier id) {
        return new TextureMap().put(TextureKey.LAYER0, id);
    }

    public static TextureMap layered(Identifier layer0, Identifier layer1) {
        return new TextureMap().put(TextureKey.LAYER0, layer0).put(TextureKey.LAYER1, layer1);
    }

    public static Identifier getId(Block block) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPrefixedPath("block/");
    }

    public static Identifier getSubId(Block block, String suffix) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPath(path -> "block/" + path + suffix);
    }

    public static Identifier getId(Item item) {
        Identifier identifier = Registries.ITEM.getId(item);
        return identifier.withPrefixedPath("item/");
    }

    public static Identifier getSubId(Item item, String suffix) {
        Identifier identifier = Registries.ITEM.getId(item);
        return identifier.withPath(path -> "item/" + path + suffix);
    }
}

