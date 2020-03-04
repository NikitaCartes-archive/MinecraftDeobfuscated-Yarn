package net.minecraft.data.client.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
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

	public Texture copyAndAnd(TextureKey key, Identifier id) {
		Texture texture = new Texture();
		texture.entries.putAll(this.entries);
		texture.inherited.addAll(this.inherited);
		texture.put(key, id);
		return texture;
	}

	public static Texture all(Block block) {
		Identifier identifier = getModelId(block);
		return all(identifier);
	}

	public static Texture texture(Block block) {
		Identifier identifier = getModelId(block);
		return texture(identifier);
	}

	public static Texture texture(Identifier id) {
		return new Texture().put(TextureKey.TEXTURE, id);
	}

	public static Texture all(Identifier id) {
		return new Texture().put(TextureKey.ALL, id);
	}

	public static Texture cross(Block block) {
		return of(TextureKey.CROSS, getModelId(block));
	}

	public static Texture cross(Identifier id) {
		return of(TextureKey.CROSS, id);
	}

	public static Texture plant(Block block) {
		return of(TextureKey.PLANT, getModelId(block));
	}

	public static Texture plant(Identifier id) {
		return of(TextureKey.PLANT, id);
	}

	public static Texture rail(Block block) {
		return of(TextureKey.RAIL, getModelId(block));
	}

	public static Texture rail(Identifier id) {
		return of(TextureKey.RAIL, id);
	}

	public static Texture wool(Block block) {
		return of(TextureKey.WOOL, getModelId(block));
	}

	public static Texture stem(Block block) {
		return of(TextureKey.STEM, getModelId(block));
	}

	public static Texture stemAndUpper(Block stem, Block upper) {
		return new Texture().put(TextureKey.STEM, getModelId(stem)).put(TextureKey.UPPER_STEM, getModelId(upper));
	}

	public static Texture pattern(Block block) {
		return of(TextureKey.PATTERN, getModelId(block));
	}

	public static Texture fan(Block block) {
		return of(TextureKey.FAN, getModelId(block));
	}

	public static Texture crop(Identifier id) {
		return of(TextureKey.CROP, id);
	}

	public static Texture paneAndTopForEdge(Block block, Block top) {
		return new Texture().put(TextureKey.PANE, getModelId(block)).put(TextureKey.EDGE, getSubModelId(top, "_top"));
	}

	public static Texture of(TextureKey key, Identifier id) {
		return new Texture().put(key, id);
	}

	public static Texture sideEnd(Block block) {
		return new Texture().put(TextureKey.SIDE, getSubModelId(block, "_side")).put(TextureKey.END, getSubModelId(block, "_top"));
	}

	public static Texture sideAndTop(Block block) {
		return new Texture().put(TextureKey.SIDE, getSubModelId(block, "_side")).put(TextureKey.TOP, getSubModelId(block, "_top"));
	}

	public static Texture sideAndEndForTop(Block block) {
		return new Texture().put(TextureKey.SIDE, getModelId(block)).put(TextureKey.END, getSubModelId(block, "_top"));
	}

	public static Texture sideEnd(Identifier side, Identifier end) {
		return new Texture().put(TextureKey.SIDE, side).put(TextureKey.END, end);
	}

	public static Texture sideTopBottom(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubModelId(block, "_side"))
			.put(TextureKey.TOP, getSubModelId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubModelId(block, "_bottom"));
	}

	public static Texture wallSideTopBottom(Block block) {
		Identifier identifier = getModelId(block);
		return new Texture()
			.put(TextureKey.WALL, identifier)
			.put(TextureKey.SIDE, identifier)
			.put(TextureKey.TOP, getSubModelId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubModelId(block, "_bottom"));
	}

	public static Texture topBottom(Block block) {
		return new Texture().put(TextureKey.TOP, getSubModelId(block, "_top")).put(TextureKey.BOTTOM, getSubModelId(block, "_bottom"));
	}

	public static Texture particle(Block block) {
		return new Texture().put(TextureKey.PARTICLE, getModelId(block));
	}

	public static Texture particle(Identifier id) {
		return new Texture().put(TextureKey.PARTICLE, id);
	}

	public static Texture fire0(Block block) {
		return new Texture().put(TextureKey.FIRE, getSubModelId(block, "_0"));
	}

	public static Texture fire1(Block block) {
		return new Texture().put(TextureKey.FIRE, getSubModelId(block, "_1"));
	}

	public static Texture lantern(Block block) {
		return new Texture().put(TextureKey.LANTERN, getModelId(block));
	}

	public static Texture torch(Block block) {
		return new Texture().put(TextureKey.TORCH, getModelId(block));
	}

	public static Texture torch(Identifier id) {
		return new Texture().put(TextureKey.TORCH, id);
	}

	public static Texture particle(Item item) {
		return new Texture().put(TextureKey.PARTICLE, getModelId(item));
	}

	public static Texture sideFrontBack(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubModelId(block, "_side"))
			.put(TextureKey.FRONT, getSubModelId(block, "_front"))
			.put(TextureKey.BACK, getSubModelId(block, "_back"));
	}

	public static Texture sideFrontTopBottom(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubModelId(block, "_side"))
			.put(TextureKey.FRONT, getSubModelId(block, "_front"))
			.put(TextureKey.TOP, getSubModelId(block, "_top"))
			.put(TextureKey.BOTTOM, getSubModelId(block, "_bottom"));
	}

	public static Texture sideFrontTop(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubModelId(block, "_side"))
			.put(TextureKey.FRONT, getSubModelId(block, "_front"))
			.put(TextureKey.TOP, getSubModelId(block, "_top"));
	}

	public static Texture sideFrontEnd(Block block) {
		return new Texture()
			.put(TextureKey.SIDE, getSubModelId(block, "_side"))
			.put(TextureKey.FRONT, getSubModelId(block, "_front"))
			.put(TextureKey.END, getSubModelId(block, "_end"));
	}

	public static Texture top(Block top) {
		return new Texture().put(TextureKey.TOP, getSubModelId(top, "_top"));
	}

	public static Texture frontSideWithCustomBottom(Block block, Block bottom) {
		return new Texture()
			.put(TextureKey.PARTICLE, getSubModelId(block, "_front"))
			.put(TextureKey.DOWN, getModelId(bottom))
			.put(TextureKey.UP, getSubModelId(block, "_top"))
			.put(TextureKey.NORTH, getSubModelId(block, "_front"))
			.put(TextureKey.EAST, getSubModelId(block, "_side"))
			.put(TextureKey.SOUTH, getSubModelId(block, "_side"))
			.put(TextureKey.WEST, getSubModelId(block, "_front"));
	}

	public static Texture frontTopSide(Block frontTopSideBlock, Block downBlock) {
		return new Texture()
			.put(TextureKey.PARTICLE, getSubModelId(frontTopSideBlock, "_front"))
			.put(TextureKey.DOWN, getModelId(downBlock))
			.put(TextureKey.UP, getSubModelId(frontTopSideBlock, "_top"))
			.put(TextureKey.NORTH, getSubModelId(frontTopSideBlock, "_front"))
			.put(TextureKey.SOUTH, getSubModelId(frontTopSideBlock, "_front"))
			.put(TextureKey.EAST, getSubModelId(frontTopSideBlock, "_side"))
			.put(TextureKey.WEST, getSubModelId(frontTopSideBlock, "_side"));
	}

	public static Texture layer0(Item item) {
		return new Texture().put(TextureKey.LAYER0, getModelId(item));
	}

	public static Texture layer0(Block block) {
		return new Texture().put(TextureKey.LAYER0, getModelId(block));
	}

	public static Texture layer0(Identifier id) {
		return new Texture().put(TextureKey.LAYER0, id);
	}

	public static Identifier getModelId(Block block) {
		Identifier identifier = Registry.BLOCK.getId(block);
		return new Identifier(identifier.getNamespace(), "block/" + identifier.getPath());
	}

	public static Identifier getSubModelId(Block block, String suffix) {
		Identifier identifier = Registry.BLOCK.getId(block);
		return new Identifier(identifier.getNamespace(), "block/" + identifier.getPath() + suffix);
	}

	public static Identifier getModelId(Item item) {
		Identifier identifier = Registry.ITEM.getId(item);
		return new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
	}

	public static Identifier getSubModelId(Item item, String suffix) {
		Identifier identifier = Registry.ITEM.getId(item);
		return new Identifier(identifier.getNamespace(), "item/" + identifier.getPath() + suffix);
	}
}
