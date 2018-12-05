package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_380;
import net.minecraft.class_382;
import net.minecraft.class_383;
import net.minecraft.class_384;
import net.minecraft.class_390;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final class_384 field_2250 = new class_384();
	private static final Glyph field_2251 = () -> 4.0F;
	private static final Random RANDOM = new Random();
	private final TextureManager field_2248;
	private final Identifier field_2246;
	private class_382 field_2256;
	private final List<class_390> entries = Lists.<class_390>newArrayList();
	private final Char2ObjectMap<class_382> field_2253 = new Char2ObjectOpenHashMap<>();
	private final Char2ObjectMap<Glyph> glyphCache = new Char2ObjectOpenHashMap<>();
	private final Int2ObjectMap<CharList> field_2249 = new Int2ObjectOpenHashMap<>();
	private final List<class_380> field_2254 = Lists.<class_380>newArrayList();

	public FontStorage(TextureManager textureManager, Identifier identifier) {
		this.field_2248 = textureManager;
		this.field_2246 = identifier;
	}

	public void method_2004(List<class_390> list) {
		for (class_390 lv : this.entries) {
			lv.close();
		}

		this.entries.clear();
		this.method_2010();
		this.field_2254.clear();
		this.field_2253.clear();
		this.glyphCache.clear();
		this.field_2249.clear();
		this.field_2256 = this.method_2012(BlankGlyph.INSTANCE);
		Set<class_390> set = Sets.<class_390>newHashSet();

		for (char c = 0; c < '\uffff'; c++) {
			for (class_390 lv2 : list) {
				Glyph glyph = (Glyph)(c == ' ' ? field_2251 : lv2.method_2040(c));
				if (glyph != null) {
					set.add(lv2);
					if (glyph != BlankGlyph.INSTANCE) {
						this.field_2249.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), i -> new CharArrayList()).add(c);
					}
					break;
				}
			}
		}

		list.stream().filter(set::contains).forEach(this.entries::add);
	}

	public void close() {
		this.method_2010();
	}

	public void method_2010() {
		for (class_380 lv : this.field_2254) {
			lv.close();
		}
	}

	public Glyph getGlyph(char c) {
		return this.glyphCache.computeIfAbsent(c, i -> (Glyph)(i == 32 ? field_2251 : this.method_2008((char)i)));
	}

	private class_383 method_2008(char c) {
		for (class_390 lv : this.entries) {
			class_383 lv2 = lv.method_2040(c);
			if (lv2 != null) {
				return lv2;
			}
		}

		return BlankGlyph.INSTANCE;
	}

	public class_382 method_2014(char c) {
		return this.field_2253.computeIfAbsent(c, i -> (class_382)(i == 32 ? field_2250 : this.method_2012(this.method_2008((char)i))));
	}

	private class_382 method_2012(class_383 arg) {
		for (class_380 lv : this.field_2254) {
			class_382 lv2 = lv.method_2022(arg);
			if (lv2 != null) {
				return lv2;
			}
		}

		class_380 lv3 = new class_380(new Identifier(this.field_2246.getNamespace(), this.field_2246.getPath() + "/" + this.field_2254.size()), arg.method_2033());
		this.field_2254.add(lv3);
		this.field_2248.registerTexture(lv3.method_2023(), lv3);
		class_382 lv4 = lv3.method_2022(arg);
		return lv4 == null ? this.field_2256 : lv4;
	}

	public class_382 method_2013(Glyph glyph) {
		CharList charList = this.field_2249.get(MathHelper.ceil(glyph.getAdvance(false)));
		return charList != null && !charList.isEmpty() ? this.method_2014(charList.get(RANDOM.nextInt(charList.size()))) : this.field_2256;
	}
}
