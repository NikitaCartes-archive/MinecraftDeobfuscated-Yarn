package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TextureMap {
	private final Map<TextureKey, Identifier> entries = Maps.<TextureKey, Identifier>newHashMap();
	private final Set<TextureKey> inherited = Sets.<TextureKey>newHashSet();

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
		this.entries.put(child, (Identifier)this.entries.get(parent));
		return this;
	}

	public TextureMap inherit(TextureKey parent, TextureKey child) {
		this.entries.put(child, (Identifier)this.entries.get(parent));
		this.inherited.add(child);
		return this;
	}

	public Identifier getTexture(TextureKey key) {
		for (TextureKey textureKey = key; textureKey != null; textureKey = textureKey.getParent()) {
			Identifier identifier = (Identifier)this.entries.get(textureKey);
			if (identifier != null) {
				return identifier;
			}
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
		Identifier identifier = getId(block);
		return all(identifier);
	}

	public static TextureMap texture(Block block) {
		Identifier identifier = getId(block);
		return texture(identifier);
	}

	public static TextureMap texture(Identifier id) {
		return new TextureMap().put(TextureKey.TEXTURE, id);
	}

	public static TextureMap all(Identifier id) {
		return new TextureMap().put(TextureKey.ALL, id);
	}

	public static TextureMap cross(Block block) {
		return of(TextureKey.CROSS, getId(block));
	}

	public static TextureMap cross(Identifier id) {
		return of(TextureKey.CROSS, id);
	}

	public static TextureMap plant(Block block) {
		return of(TextureKey.PLANT, getId(block));
	}

	public static TextureMap plant(Identifier id) {
		return of(TextureKey.PLANT, id);
	}

	public static TextureMap rail(Block block) {
		return of(TextureKey.RAIL, getId(block));
	}

	public static TextureMap rail(Identifier id) {
		return of(TextureKey.RAIL, id);
	}

	public static TextureMap wool(Block block) {
		return of(TextureKey.WOOL, getId(block));
	}

	public static TextureMap wool(Identifier id) {
		return of(TextureKey.WOOL, id);
	}

	public static TextureMap stem(Block block) {
		return of(TextureKey.STEM, getId(block));
	}

	public static TextureMap stemAndUpper(Block stem, Block upper) {
		return new TextureMap().put(TextureKey.STEM, getId(stem)).put(TextureKey.UPPERSTEM, getId(upper));
	}

	public static TextureMap pattern(Block block) {
		return of(TextureKey.PATTERN, getId(block));
	}

	public static TextureMap fan(Block block) {
		return of(TextureKey.FAN, getId(block));
	}

	public static TextureMap crop(Identifier id) {
		return of(TextureKey.CROP, id);
	}

	public static TextureMap paneAndTopForEdge(Block block, Block top) {
		return new TextureMap().put(TextureKey.PANE, getId(block)).put(TextureKey.EDGE, getSubId(top, "_top"));
	}

	public static TextureMap of(TextureKey key, Identifier id) {
		return new TextureMap().put(key, id);
	}

	public static TextureMap sideEnd(Block block) {
		return new TextureMap().put(TextureKey.SIDE, getSubId(block, "_side")).put(TextureKey.END, getSubId(block, "_top"));
	}

	public static TextureMap sideAndTop(Block block) {
		return new TextureMap().put(TextureKey.SIDE, getSubId(block, "_side")).put(TextureKey.TOP, getSubId(block, "_top"));
	}

	public static TextureMap sideAndEndForTop(Block block) {
		return new TextureMap().put(TextureKey.SIDE, getId(block)).put(TextureKey.END, getSubId(block, "_top"));
	}

	public static TextureMap sideEnd(Identifier side, Identifier end) {
		return new TextureMap().put(TextureKey.SIDE, side).put(TextureKey.END, end);
	}

	public static TextureMap sideTopBottom(Block block) {
		return new TextureMap()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.TOP, getSubId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static TextureMap wallSideTopBottom(Block block) {
		Identifier identifier = getId(block);
		return new TextureMap()
			.put(TextureKey.WALL, identifier)
			.put(TextureKey.SIDE, identifier)
			.put(TextureKey.TOP, getSubId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static TextureMap wallSideEnd(Block block) {
		Identifier identifier = getId(block);
		return new TextureMap().put(TextureKey.WALL, identifier).put(TextureKey.SIDE, identifier).put(TextureKey.END, getSubId(block, "_top"));
	}

	public static TextureMap topBottom(Identifier top, Identifier bottom) {
		return new TextureMap().put(TextureKey.TOP, top).put(TextureKey.BOTTOM, bottom);
	}

	public static TextureMap topBottom(Block block) {
		return new TextureMap().put(TextureKey.TOP, getSubId(block, "_top")).put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static TextureMap particle(Block block) {
		return new TextureMap().put(TextureKey.PARTICLE, getId(block));
	}

	public static TextureMap particle(Identifier id) {
		return new TextureMap().put(TextureKey.PARTICLE, id);
	}

	public static TextureMap fire0(Block block) {
		return new TextureMap().put(TextureKey.FIRE, getSubId(block, "_0"));
	}

	public static TextureMap fire1(Block block) {
		return new TextureMap().put(TextureKey.FIRE, getSubId(block, "_1"));
	}

	public static TextureMap lantern(Block block) {
		return new TextureMap().put(TextureKey.LANTERN, getId(block));
	}

	public static TextureMap torch(Block block) {
		return new TextureMap().put(TextureKey.TORCH, getId(block));
	}

	public static TextureMap torch(Identifier id) {
		return new TextureMap().put(TextureKey.TORCH, id);
	}

	public static TextureMap particle(Item item) {
		return new TextureMap().put(TextureKey.PARTICLE, getId(item));
	}

	public static TextureMap sideFrontBack(Block block) {
		return new TextureMap()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.BACK, getSubId(block, "_back"));
	}

	public static TextureMap sideFrontTopBottom(Block block) {
		return new TextureMap()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.TOP, getSubId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static TextureMap sideFrontTop(Block block) {
		return new TextureMap()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.TOP, getSubId(block, "_top"));
	}

	public static TextureMap sideFrontEnd(Block block) {
		return new TextureMap()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.END, getSubId(block, "_end"));
	}

	public static TextureMap top(Block top) {
		return new TextureMap().put(TextureKey.TOP, getSubId(top, "_top"));
	}

	public static TextureMap frontSideWithCustomBottom(Block block, Block bottom) {
		return new TextureMap()
			.put(TextureKey.PARTICLE, getSubId(block, "_front"))
			.put(TextureKey.DOWN, getId(bottom))
			.put(TextureKey.UP, getSubId(block, "_top"))
			.put(TextureKey.NORTH, getSubId(block, "_front"))
			.put(TextureKey.EAST, getSubId(block, "_side"))
			.put(TextureKey.SOUTH, getSubId(block, "_side"))
			.put(TextureKey.WEST, getSubId(block, "_front"));
	}

	public static TextureMap frontTopSide(Block frontTopSideBlock, Block downBlock) {
		return new TextureMap()
			.put(TextureKey.PARTICLE, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.DOWN, getId(downBlock))
			.put(TextureKey.UP, getSubId(frontTopSideBlock, "_top"))
			.put(TextureKey.NORTH, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.SOUTH, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.EAST, getSubId(frontTopSideBlock, "_side"))
			.put(TextureKey.WEST, getSubId(frontTopSideBlock, "_side"));
	}

	public static TextureMap campfire(Block block) {
		return new TextureMap().put(TextureKey.LIT_LOG, getSubId(block, "_log_lit")).put(TextureKey.FIRE, getSubId(block, "_fire"));
	}

	public static TextureMap candleCake(Block block, boolean lit) {
		return new TextureMap()
			.put(TextureKey.PARTICLE, getSubId(Blocks.CAKE, "_side"))
			.put(TextureKey.BOTTOM, getSubId(Blocks.CAKE, "_bottom"))
			.put(TextureKey.TOP, getSubId(Blocks.CAKE, "_top"))
			.put(TextureKey.SIDE, getSubId(Blocks.CAKE, "_side"))
			.put(TextureKey.CANDLE, getSubId(block, lit ? "_lit" : ""));
	}

	public static TextureMap cauldron(Identifier content) {
		return new TextureMap()
			.put(TextureKey.PARTICLE, getSubId(Blocks.CAULDRON, "_side"))
			.put(TextureKey.SIDE, getSubId(Blocks.CAULDRON, "_side"))
			.put(TextureKey.TOP, getSubId(Blocks.CAULDRON, "_top"))
			.put(TextureKey.BOTTOM, getSubId(Blocks.CAULDRON, "_bottom"))
			.put(TextureKey.INSIDE, getSubId(Blocks.CAULDRON, "_inner"))
			.put(TextureKey.CONTENT, content);
	}

	public static TextureMap method_42753(boolean bl) {
		String string = bl ? "_can_summon" : "";
		return new TextureMap()
			.put(TextureKey.PARTICLE, getSubId(Blocks.SCULK_SHRIEKER, "_bottom"))
			.put(TextureKey.SIDE, getSubId(Blocks.SCULK_SHRIEKER, "_side"))
			.put(TextureKey.TOP, getSubId(Blocks.SCULK_SHRIEKER, "_top"))
			.put(TextureKey.INNER_TOP, getSubId(Blocks.SCULK_SHRIEKER, string + "_inner_top"))
			.put(TextureKey.BOTTOM, getSubId(Blocks.SCULK_SHRIEKER, "_bottom"));
	}

	public static TextureMap layer0(Item item) {
		return new TextureMap().put(TextureKey.LAYER0, getId(item));
	}

	public static TextureMap layer0(Block block) {
		return new TextureMap().put(TextureKey.LAYER0, getId(block));
	}

	public static TextureMap layer0(Identifier id) {
		return new TextureMap().put(TextureKey.LAYER0, id);
	}

	public static Identifier getId(Block block) {
		Identifier identifier = Registry.BLOCK.getId(block);
		return new Identifier(identifier.getNamespace(), "block/" + identifier.getPath());
	}

	public static Identifier getSubId(Block block, String suffix) {
		Identifier identifier = Registry.BLOCK.getId(block);
		return new Identifier(identifier.getNamespace(), "block/" + identifier.getPath() + suffix);
	}

	public static Identifier getId(Item item) {
		Identifier identifier = Registry.ITEM.getId(item);
		return new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
	}

	public static Identifier getSubId(Item item, String suffix) {
		Identifier identifier = Registry.ITEM.getId(item);
		return new Identifier(identifier.getNamespace(), "item/" + identifier.getPath() + suffix);
	}
}
