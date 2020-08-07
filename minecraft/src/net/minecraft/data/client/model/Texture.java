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
		return new Texture().put(TextureKey.field_23011, id);
	}

	public static Texture all(Identifier id) {
		return new Texture().put(TextureKey.field_23010, id);
	}

	public static Texture cross(Block block) {
		return of(TextureKey.field_23025, getId(block));
	}

	public static Texture cross(Identifier id) {
		return of(TextureKey.field_23025, id);
	}

	public static Texture plant(Block block) {
		return of(TextureKey.field_23026, getId(block));
	}

	public static Texture plant(Identifier id) {
		return of(TextureKey.field_23026, id);
	}

	public static Texture rail(Block block) {
		return of(TextureKey.field_23028, getId(block));
	}

	public static Texture rail(Identifier id) {
		return of(TextureKey.field_23028, id);
	}

	public static Texture wool(Block block) {
		return of(TextureKey.field_23029, getId(block));
	}

	public static Texture stem(Block block) {
		return of(TextureKey.field_23034, getId(block));
	}

	public static Texture stemAndUpper(Block stem, Block upper) {
		return new Texture().put(TextureKey.field_23034, getId(stem)).put(TextureKey.field_23035, getId(upper));
	}

	public static Texture pattern(Block block) {
		return of(TextureKey.field_23030, getId(block));
	}

	public static Texture fan(Block block) {
		return of(TextureKey.field_23033, getId(block));
	}

	public static Texture crop(Identifier id) {
		return of(TextureKey.field_22999, id);
	}

	public static Texture paneAndTopForEdge(Block block, Block top) {
		return new Texture().put(TextureKey.field_23031, getId(block)).put(TextureKey.field_23032, getSubId(top, "_top"));
	}

	public static Texture of(TextureKey key, Identifier id) {
		return new Texture().put(key, id);
	}

	public static Texture sideEnd(Block block) {
		return new Texture().put(TextureKey.field_23018, getSubId(block, "_side")).put(TextureKey.field_23013, getSubId(block, "_top"));
	}

	public static Texture sideAndTop(Block block) {
		return new Texture().put(TextureKey.field_23018, getSubId(block, "_side")).put(TextureKey.field_23015, getSubId(block, "_top"));
	}

	public static Texture sideAndEndForTop(Block block) {
		return new Texture().put(TextureKey.field_23018, getId(block)).put(TextureKey.field_23013, getSubId(block, "_top"));
	}

	public static Texture sideEnd(Identifier side, Identifier end) {
		return new Texture().put(TextureKey.field_23018, side).put(TextureKey.field_23013, end);
	}

	public static Texture sideTopBottom(Block block) {
		return new Texture()
			.put(TextureKey.field_23018, getSubId(block, "_side"))
			.put(TextureKey.field_23015, getSubId(block, "_top"))
			.put(TextureKey.field_23014, getSubId(block, "_bottom"));
	}

	public static Texture wallSideTopBottom(Block block) {
		Identifier identifier = getId(block);
		return new Texture()
			.put(TextureKey.field_23027, identifier)
			.put(TextureKey.field_23018, identifier)
			.put(TextureKey.field_23015, getSubId(block, "_top"))
			.put(TextureKey.field_23014, getSubId(block, "_bottom"));
	}

	public static Texture method_27168(Block block) {
		Identifier identifier = getId(block);
		return new Texture().put(TextureKey.field_23027, identifier).put(TextureKey.field_23018, identifier).put(TextureKey.field_23013, getSubId(block, "_top"));
	}

	public static Texture topBottom(Block block) {
		return new Texture().put(TextureKey.field_23015, getSubId(block, "_top")).put(TextureKey.field_23014, getSubId(block, "_bottom"));
	}

	public static Texture particle(Block block) {
		return new Texture().put(TextureKey.field_23012, getId(block));
	}

	public static Texture particle(Identifier id) {
		return new Texture().put(TextureKey.field_23012, id);
	}

	public static Texture fire0(Block block) {
		return new Texture().put(TextureKey.field_23001, getSubId(block, "_0"));
	}

	public static Texture fire1(Block block) {
		return new Texture().put(TextureKey.field_23001, getSubId(block, "_1"));
	}

	public static Texture lantern(Block block) {
		return new Texture().put(TextureKey.field_23002, getId(block));
	}

	public static Texture torch(Block block) {
		return new Texture().put(TextureKey.field_23005, getId(block));
	}

	public static Texture torch(Identifier id) {
		return new Texture().put(TextureKey.field_23005, id);
	}

	public static Texture particle(Item item) {
		return new Texture().put(TextureKey.field_23012, getId(item));
	}

	public static Texture sideFrontBack(Block block) {
		return new Texture()
			.put(TextureKey.field_23018, getSubId(block, "_side"))
			.put(TextureKey.field_23016, getSubId(block, "_front"))
			.put(TextureKey.field_23017, getSubId(block, "_back"));
	}

	public static Texture sideFrontTopBottom(Block block) {
		return new Texture()
			.put(TextureKey.field_23018, getSubId(block, "_side"))
			.put(TextureKey.field_23016, getSubId(block, "_front"))
			.put(TextureKey.field_23015, getSubId(block, "_top"))
			.put(TextureKey.field_23014, getSubId(block, "_bottom"));
	}

	public static Texture sideFrontTop(Block block) {
		return new Texture()
			.put(TextureKey.field_23018, getSubId(block, "_side"))
			.put(TextureKey.field_23016, getSubId(block, "_front"))
			.put(TextureKey.field_23015, getSubId(block, "_top"));
	}

	public static Texture sideFrontEnd(Block block) {
		return new Texture()
			.put(TextureKey.field_23018, getSubId(block, "_side"))
			.put(TextureKey.field_23016, getSubId(block, "_front"))
			.put(TextureKey.field_23013, getSubId(block, "_end"));
	}

	public static Texture top(Block top) {
		return new Texture().put(TextureKey.field_23015, getSubId(top, "_top"));
	}

	public static Texture frontSideWithCustomBottom(Block block, Block bottom) {
		return new Texture()
			.put(TextureKey.field_23012, getSubId(block, "_front"))
			.put(TextureKey.field_23024, getId(bottom))
			.put(TextureKey.field_23023, getSubId(block, "_top"))
			.put(TextureKey.field_23019, getSubId(block, "_front"))
			.put(TextureKey.field_23021, getSubId(block, "_side"))
			.put(TextureKey.field_23020, getSubId(block, "_side"))
			.put(TextureKey.field_23022, getSubId(block, "_front"));
	}

	public static Texture frontTopSide(Block frontTopSideBlock, Block downBlock) {
		return new Texture()
			.put(TextureKey.field_23012, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.field_23024, getId(downBlock))
			.put(TextureKey.field_23023, getSubId(frontTopSideBlock, "_top"))
			.put(TextureKey.field_23019, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.field_23020, getSubId(frontTopSideBlock, "_front"))
			.put(TextureKey.field_23021, getSubId(frontTopSideBlock, "_side"))
			.put(TextureKey.field_23022, getSubId(frontTopSideBlock, "_side"));
	}

	public static Texture method_27167(Block block) {
		return new Texture().put(TextureKey.field_23958, getSubId(block, "_log_lit")).put(TextureKey.field_23001, getSubId(block, "_fire"));
	}

	public static Texture layer0(Item item) {
		return new Texture().put(TextureKey.field_23006, getId(item));
	}

	public static Texture layer0(Block block) {
		return new Texture().put(TextureKey.field_23006, getId(block));
	}

	public static Texture layer0(Identifier id) {
		return new Texture().put(TextureKey.field_23006, id);
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
