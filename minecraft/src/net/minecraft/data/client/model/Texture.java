package net.minecraft.data.client.model;

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

public class Texture {
	private final Map<TextureKey, Identifier> entries = Maps.<TextureKey, Identifier>newHashMap();
	private final Set<TextureKey> inherited = Sets.<TextureKey>newHashSet();

	public Texture put(TextureKey key, Identifier id) {
		this.entries.put(key, id);
		return this;
	}

	public Stream<TextureKey> getInherited() {
		return this.inherited.stream();
	}

	public Texture inherit(TextureKey parent, TextureKey child) {
		this.entries.put(child, this.entries.get(parent));
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

	public Texture copyAndAdd(TextureKey key, Identifier id) {
		Texture texture = new Texture();
		texture.entries.putAll(this.entries);
		texture.inherited.addAll(this.inherited);
		texture.put(key, id);
		return texture;
	}

	public static Texture all(Block block) {
		Identifier identifier = getId(block);
		return all(identifier);
	}

	public static Texture texture(Block block) {
		Identifier identifier = getId(block);
		return texture(identifier);
	}

	public static Texture texture(Identifier id) {
		return new Texture().put(TextureKey.TEXTURE, id);
	}

	public static Texture all(Identifier id) {
		return new Texture().put(TextureKey.ALL, id);
	}

	public static Texture cross(Block block) {
		return of(TextureKey.CROSS, getId(block));
	}

	public static Texture cross(Identifier id) {
		return of(TextureKey.CROSS, id);
	}

	public static Texture plant(Block block) {
		return of(TextureKey.PLANT, getId(block));
	}

	public static Texture plant(Identifier id) {
		return of(TextureKey.PLANT, id);
	}

	public static Texture rail(Block block) {
		return of(TextureKey.RAIL, getId(block));
	}

	public static Texture rail(Identifier id) {
		return of(TextureKey.RAIL, id);
	}

	public static Texture wool(Block block) {
		return of(TextureKey.WOOL, getId(block));
	}

	public static Texture stem(Block block) {
		return of(TextureKey.STEM, getId(block));
	}

	public static Texture stemAndUpper(Block stem, Block upper) {
		return new Texture().put(TextureKey.STEM, getId(stem)).put(TextureKey.UPPERSTEM, getId(upper));
	}

	public static Texture pattern(Block block) {
		return of(TextureKey.PATTERN, getId(block));
	}

	public static Texture fan(Block block) {
		return of(TextureKey.FAN, getId(block));
	}

	public static Texture crop(Identifier id) {
		return of(TextureKey.CROP, id);
	}

	public static Texture paneAndTopForEdge(Block block, Block top) {
		return new Texture().put(TextureKey.PANE, getId(block)).put(TextureKey.EDGE, getSubId(top, "_top"));
	}

	public static Texture of(TextureKey key, Identifier id) {
		return new Texture().put(key, id);
	}

	public static Texture sideEnd(Block block) {
		return new Texture().put(TextureKey.SIDE, getSubId(block, "_side")).put(TextureKey.END, getSubId(block, "_top"));
	}

	public static Texture sideAndTop(Block block) {
		return new Texture().put(TextureKey.SIDE, getSubId(block, "_side")).put(TextureKey.TOP, getSubId(block, "_top"));
	}

	public static Texture sideAndEndForTop(Block block) {
		return new Texture().put(TextureKey.SIDE, getId(block)).put(TextureKey.END, getSubId(block, "_top"));
	}

	public static Texture sideEnd(Identifier side, Identifier end) {
		return new Texture().put(TextureKey.SIDE, side).put(TextureKey.END, end);
	}

	public static Texture sideTopBottom(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.TOP, getSubId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static Texture wallSideTopBottom(Block block) {
		Identifier identifier = getId(block);
		return new Texture()
			.put(TextureKey.WALL, identifier)
			.put(TextureKey.SIDE, identifier)
			.put(TextureKey.TOP, getSubId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static Texture method_27168(Block block) {
		Identifier identifier = getId(block);
		return new Texture().put(TextureKey.WALL, identifier).put(TextureKey.SIDE, identifier).put(TextureKey.END, getSubId(block, "_top"));
	}

	public static Texture topBottom(Block block) {
		return new Texture().put(TextureKey.TOP, getSubId(block, "_top")).put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static Texture particle(Block block) {
		return new Texture().put(TextureKey.PARTICLE, getId(block));
	}

	public static Texture particle(Identifier id) {
		return new Texture().put(TextureKey.PARTICLE, id);
	}

	public static Texture fire0(Block block) {
		return new Texture().put(TextureKey.FIRE, getSubId(block, "_0"));
	}

	public static Texture fire1(Block block) {
		return new Texture().put(TextureKey.FIRE, getSubId(block, "_1"));
	}

	public static Texture lantern(Block block) {
		return new Texture().put(TextureKey.LANTERN, getId(block));
	}

	public static Texture torch(Block block) {
		return new Texture().put(TextureKey.TORCH, getId(block));
	}

	public static Texture torch(Identifier id) {
		return new Texture().put(TextureKey.TORCH, id);
	}

	public static Texture particle(Item item) {
		return new Texture().put(TextureKey.PARTICLE, getId(item));
	}

	public static Texture sideFrontBack(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.BACK, getSubId(block, "_back"));
	}

	public static Texture sideFrontTopBottom(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.TOP, getSubId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
	}

	public static Texture sideFrontTop(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.TOP, getSubId(block, "_top"));
	}

	public static Texture sideFrontEnd(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubId(block, "_side"))
			.put(TextureKey.FRONT, getSubId(block, "_front"))
			.put(TextureKey.END, getSubId(block, "_end"));
	}

	public static Texture top(Block top) {
		return new Texture().put(TextureKey.TOP, getSubId(top, "_top"));
	}

	public static Texture frontSideWithCustomBottom(Block block, Block bottom) {
		return new Texture()
			.put(TextureKey.PARTICLE, getSubId(block, "_front"))
			.put(TextureKey.DOWN, getId(bottom))
			.put(TextureKey.UP, getSubId(block, "_top"))
			.put(TextureKey.NORTH, getSubId(block, "_front"))
			.put(TextureKey.EAST, getSubId(block, "_side"))
			.put(TextureKey.SOUTH, getSubId(block, "_side"))
			.put(TextureKey.WEST, getSubId(block, "_front"));
	}

	public static Texture frontTopSide(Block frontTopSideBlock, Block downBlock) {
		return new Texture()
			.put(TextureKey.PARTICLE, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.DOWN, getId(downBlock))
			.put(TextureKey.UP, getSubId(frontTopSideBlock, "_top"))
			.put(TextureKey.NORTH, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.SOUTH, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.EAST, getSubId(frontTopSideBlock, "_side"))
			.put(TextureKey.WEST, getSubId(frontTopSideBlock, "_side"));
	}

	public static Texture method_27167(Block block) {
		return new Texture().put(TextureKey.LIT_LOG, getSubId(block, "_log_lit")).put(TextureKey.FIRE, getSubId(block, "_fire"));
	}

	public static Texture method_32231(Block block) {
		return new Texture()
			.put(TextureKey.PARTICLE, getSubId(Blocks.CAKE, "_side"))
			.put(TextureKey.BOTTOM, getSubId(Blocks.CAKE, "_bottom"))
			.put(TextureKey.TOP, getSubId(Blocks.CAKE, "_top"))
			.put(TextureKey.SIDE, getSubId(Blocks.CAKE, "_side"))
			.put(TextureKey.CANDLE, getId(block));
	}

	public static Texture method_32232(Identifier identifier) {
		return new Texture()
			.put(TextureKey.PARTICLE, getSubId(Blocks.CAULDRON, "_side"))
			.put(TextureKey.SIDE, getSubId(Blocks.CAULDRON, "_side"))
			.put(TextureKey.TOP, getSubId(Blocks.CAULDRON, "_top"))
			.put(TextureKey.BOTTOM, getSubId(Blocks.CAULDRON, "_bottom"))
			.put(TextureKey.INSIDE, getSubId(Blocks.CAULDRON, "_inner"))
			.put(TextureKey.CONTENT, identifier);
	}

	public static Texture layer0(Item item) {
		return new Texture().put(TextureKey.LAYER0, getId(item));
	}

	public static Texture layer0(Block block) {
		return new Texture().put(TextureKey.LAYER0, getId(block));
	}

	public static Texture layer0(Identifier id) {
		return new Texture().put(TextureKey.LAYER0, id);
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
