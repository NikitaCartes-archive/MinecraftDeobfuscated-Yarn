package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class Format4ResourcePack implements ResourcePack {
	private static final Map<String, Pair<ChestType, Identifier>> NEW_TO_OLD_CHEST_TEXTURES = Util.make(
		Maps.<String, Pair<ChestType, Identifier>>newHashMap(), map -> {
			map.put("textures/entity/chest/normal_left.png", new Pair<>(ChestType.LEFT, new Identifier("textures/entity/chest/normal_double.png")));
			map.put("textures/entity/chest/normal_right.png", new Pair<>(ChestType.RIGHT, new Identifier("textures/entity/chest/normal_double.png")));
			map.put("textures/entity/chest/normal.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/normal.png")));
			map.put("textures/entity/chest/trapped_left.png", new Pair<>(ChestType.LEFT, new Identifier("textures/entity/chest/trapped_double.png")));
			map.put("textures/entity/chest/trapped_right.png", new Pair<>(ChestType.RIGHT, new Identifier("textures/entity/chest/trapped_double.png")));
			map.put("textures/entity/chest/trapped.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/trapped.png")));
			map.put("textures/entity/chest/christmas_left.png", new Pair<>(ChestType.LEFT, new Identifier("textures/entity/chest/christmas_double.png")));
			map.put("textures/entity/chest/christmas_right.png", new Pair<>(ChestType.RIGHT, new Identifier("textures/entity/chest/christmas_double.png")));
			map.put("textures/entity/chest/christmas.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/christmas.png")));
			map.put("textures/entity/chest/ender.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/ender.png")));
		}
	);
	private static final List<String> BANNER_PATTERN_TYPES = Lists.<String>newArrayList(
		"base",
		"border",
		"bricks",
		"circle",
		"creeper",
		"cross",
		"curly_border",
		"diagonal_left",
		"diagonal_right",
		"diagonal_up_left",
		"diagonal_up_right",
		"flower",
		"globe",
		"gradient",
		"gradient_up",
		"half_horizontal",
		"half_horizontal_bottom",
		"half_vertical",
		"half_vertical_right",
		"mojang",
		"rhombus",
		"skull",
		"small_stripes",
		"square_bottom_left",
		"square_bottom_right",
		"square_top_left",
		"square_top_right",
		"straight_cross",
		"stripe_bottom",
		"stripe_center",
		"stripe_downleft",
		"stripe_downright",
		"stripe_left",
		"stripe_middle",
		"stripe_right",
		"stripe_top",
		"triangle_bottom",
		"triangle_top",
		"triangles_bottom",
		"triangles_top"
	);
	private static final Set<String> SHIELD_PATTERN_TEXTURES = (Set<String>)BANNER_PATTERN_TYPES.stream()
		.map(patternName -> "textures/entity/shield/" + patternName + ".png")
		.collect(Collectors.toSet());
	private static final Set<String> BANNER_PATTERN_TEXTURES = (Set<String>)BANNER_PATTERN_TYPES.stream()
		.map(patternName -> "textures/entity/banner/" + patternName + ".png")
		.collect(Collectors.toSet());
	public static final Identifier OLD_SHIELD_BASE_TEXTURE = new Identifier("textures/entity/shield_base.png");
	public static final Identifier OLD_BANNER_BASE_TEXTURE = new Identifier("textures/entity/banner_base.png");
	public static final int field_32966 = 64;
	public static final int field_32967 = 64;
	public static final int field_32968 = 64;
	public static final Identifier IRON_GOLEM_TEXTURE = new Identifier("textures/entity/iron_golem.png");
	public static final String IRON_GOLEM_TEXTURE_PATH = "textures/entity/iron_golem/iron_golem.png";
	private final ResourcePack parent;

	public Format4ResourcePack(ResourcePack parent) {
		this.parent = parent;
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		return this.parent.openRoot(fileName);
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		if (!"minecraft".equals(id.getNamespace())) {
			return this.parent.contains(type, id);
		} else {
			String string = id.getPath();
			if ("textures/misc/enchanted_item_glint.png".equals(string)) {
				return false;
			} else if ("textures/entity/iron_golem/iron_golem.png".equals(string)) {
				return this.parent.contains(type, IRON_GOLEM_TEXTURE);
			} else if ("textures/entity/conduit/wind.png".equals(string) || "textures/entity/conduit/wind_vertical.png".equals(string)) {
				return false;
			} else if (SHIELD_PATTERN_TEXTURES.contains(string)) {
				return this.parent.contains(type, OLD_SHIELD_BASE_TEXTURE) && this.parent.contains(type, id);
			} else if (!BANNER_PATTERN_TEXTURES.contains(string)) {
				Pair<ChestType, Identifier> pair = (Pair<ChestType, Identifier>)NEW_TO_OLD_CHEST_TEXTURES.get(string);
				return pair != null && this.parent.contains(type, pair.getSecond()) ? true : this.parent.contains(type, id);
			} else {
				return this.parent.contains(type, OLD_BANNER_BASE_TEXTURE) && this.parent.contains(type, id);
			}
		}
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		if (!"minecraft".equals(id.getNamespace())) {
			return this.parent.open(type, id);
		} else {
			String string = id.getPath();
			if ("textures/entity/iron_golem/iron_golem.png".equals(string)) {
				return this.parent.open(type, IRON_GOLEM_TEXTURE);
			} else {
				if (SHIELD_PATTERN_TEXTURES.contains(string)) {
					InputStream inputStream = openCroppedStream(this.parent.open(type, OLD_SHIELD_BASE_TEXTURE), this.parent.open(type, id), 64, 2, 2, 12, 22);
					if (inputStream != null) {
						return inputStream;
					}
				} else if (BANNER_PATTERN_TEXTURES.contains(string)) {
					InputStream inputStream = openCroppedStream(this.parent.open(type, OLD_BANNER_BASE_TEXTURE), this.parent.open(type, id), 64, 0, 0, 42, 41);
					if (inputStream != null) {
						return inputStream;
					}
				} else {
					if ("textures/entity/enderdragon/dragon.png".equals(string) || "textures/entity/enderdragon/dragon_exploding.png".equals(string)) {
						ByteArrayInputStream var15;
						try (NativeImage nativeImage = NativeImage.read(this.parent.open(type, id))) {
							int i = nativeImage.getWidth() / 256;

							for (int j = 88 * i; j < 200 * i; j++) {
								for (int k = 56 * i; k < 112 * i; k++) {
									nativeImage.setColor(k, j, 0);
								}
							}

							var15 = new ByteArrayInputStream(nativeImage.getBytes());
						}

						return var15;
					}

					if ("textures/entity/conduit/closed_eye.png".equals(string) || "textures/entity/conduit/open_eye.png".equals(string)) {
						return cropConduitTexture(this.parent.open(type, id));
					}

					Pair<ChestType, Identifier> pair = (Pair<ChestType, Identifier>)NEW_TO_OLD_CHEST_TEXTURES.get(string);
					if (pair != null) {
						ChestType chestType = pair.getFirst();
						InputStream inputStream2 = this.parent.open(type, pair.getSecond());
						if (chestType == ChestType.SINGLE) {
							return cropSingleChestTexture(inputStream2);
						}

						if (chestType == ChestType.LEFT) {
							return cropLeftChestTexture(inputStream2);
						}

						if (chestType == ChestType.RIGHT) {
							return cropRightChestTexture(inputStream2);
						}
					}
				}

				return this.parent.open(type, id);
			}
		}
	}

	@Nullable
	public static InputStream openCroppedStream(InputStream base, InputStream overlay, int width, int left, int top, int right, int bottom) throws IOException {
		try (
			NativeImage nativeImage = NativeImage.read(base);
			NativeImage nativeImage2 = NativeImage.read(overlay);
		) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();
			if (i == nativeImage2.getWidth() && j == nativeImage2.getHeight()) {
				try (NativeImage nativeImage3 = new NativeImage(i, j, true)) {
					int k = i / width;

					for (int l = top * k; l < bottom * k; l++) {
						for (int m = left * k; m < right * k; m++) {
							int n = NativeImage.getRed(nativeImage2.getColor(m, l));
							int o = nativeImage.getColor(m, l);
							nativeImage3.setColor(m, l, NativeImage.packColor(n, NativeImage.getBlue(o), NativeImage.getGreen(o), NativeImage.getRed(o)));
						}
					}

					return new ByteArrayInputStream(nativeImage3.getBytes());
				}
			}
		}

		return null;
	}

	public static InputStream cropConduitTexture(InputStream stream) throws IOException {
		ByteArrayInputStream var5;
		try (NativeImage nativeImage = NativeImage.read(stream)) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();

			try (NativeImage nativeImage2 = new NativeImage(2 * i, 2 * j, true)) {
				loadBytes(nativeImage, nativeImage2, 0, 0, 0, 0, i, j, 1, false, false);
				var5 = new ByteArrayInputStream(nativeImage2.getBytes());
			}
		}

		return var5;
	}

	public static InputStream cropLeftChestTexture(InputStream stream) throws IOException {
		ByteArrayInputStream var6;
		try (NativeImage nativeImage = NativeImage.read(stream)) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();

			try (NativeImage nativeImage2 = new NativeImage(i / 2, j, true)) {
				int k = j / 64;
				loadBytes(nativeImage, nativeImage2, 29, 0, 29, 0, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 59, 0, 14, 0, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 29, 14, 43, 14, 15, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 44, 14, 29, 14, 14, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 58, 14, 14, 14, 15, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 29, 19, 29, 19, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 59, 19, 14, 19, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 29, 33, 43, 33, 15, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 44, 33, 29, 33, 14, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 58, 33, 14, 33, 15, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 2, 0, 2, 0, 1, 1, k, false, true);
				loadBytes(nativeImage, nativeImage2, 4, 0, 1, 0, 1, 1, k, false, true);
				loadBytes(nativeImage, nativeImage2, 2, 1, 3, 1, 1, 4, k, true, true);
				loadBytes(nativeImage, nativeImage2, 3, 1, 2, 1, 1, 4, k, true, true);
				loadBytes(nativeImage, nativeImage2, 4, 1, 1, 1, 1, 4, k, true, true);
				var6 = new ByteArrayInputStream(nativeImage2.getBytes());
			}
		}

		return var6;
	}

	public static InputStream cropRightChestTexture(InputStream stream) throws IOException {
		ByteArrayInputStream var6;
		try (NativeImage nativeImage = NativeImage.read(stream)) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();

			try (NativeImage nativeImage2 = new NativeImage(i / 2, j, true)) {
				int k = j / 64;
				loadBytes(nativeImage, nativeImage2, 14, 0, 29, 0, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 44, 0, 14, 0, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 0, 14, 0, 14, 14, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 14, 14, 43, 14, 15, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 73, 14, 14, 14, 15, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 14, 19, 29, 19, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 44, 19, 14, 19, 15, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 0, 33, 0, 33, 14, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 14, 33, 43, 33, 15, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 73, 33, 14, 33, 15, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 1, 0, 2, 0, 1, 1, k, false, true);
				loadBytes(nativeImage, nativeImage2, 3, 0, 1, 0, 1, 1, k, false, true);
				loadBytes(nativeImage, nativeImage2, 0, 1, 0, 1, 1, 4, k, true, true);
				loadBytes(nativeImage, nativeImage2, 1, 1, 3, 1, 1, 4, k, true, true);
				loadBytes(nativeImage, nativeImage2, 5, 1, 1, 1, 1, 4, k, true, true);
				var6 = new ByteArrayInputStream(nativeImage2.getBytes());
			}
		}

		return var6;
	}

	public static InputStream cropSingleChestTexture(InputStream stream) throws IOException {
		ByteArrayInputStream var6;
		try (NativeImage nativeImage = NativeImage.read(stream)) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();

			try (NativeImage nativeImage2 = new NativeImage(i, j, true)) {
				int k = j / 64;
				loadBytes(nativeImage, nativeImage2, 14, 0, 28, 0, 14, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 28, 0, 14, 0, 14, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 0, 14, 0, 14, 14, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 14, 14, 42, 14, 14, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 28, 14, 28, 14, 14, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 42, 14, 14, 14, 14, 5, k, true, true);
				loadBytes(nativeImage, nativeImage2, 14, 19, 28, 19, 14, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 28, 19, 14, 19, 14, 14, k, false, true);
				loadBytes(nativeImage, nativeImage2, 0, 33, 0, 33, 14, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 14, 33, 42, 33, 14, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 28, 33, 28, 33, 14, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 42, 33, 14, 33, 14, 10, k, true, true);
				loadBytes(nativeImage, nativeImage2, 1, 0, 3, 0, 2, 1, k, false, true);
				loadBytes(nativeImage, nativeImage2, 3, 0, 1, 0, 2, 1, k, false, true);
				loadBytes(nativeImage, nativeImage2, 0, 1, 0, 1, 1, 4, k, true, true);
				loadBytes(nativeImage, nativeImage2, 1, 1, 4, 1, 2, 4, k, true, true);
				loadBytes(nativeImage, nativeImage2, 3, 1, 3, 1, 1, 4, k, true, true);
				loadBytes(nativeImage, nativeImage2, 4, 1, 1, 1, 2, 4, k, true, true);
				var6 = new ByteArrayInputStream(nativeImage2.getBytes());
			}
		}

		return var6;
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, Predicate<Identifier> allowedPathPredicate) {
		return this.parent.findResources(type, namespace, prefix, allowedPathPredicate);
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return this.parent.getNamespaces(type);
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return this.parent.parseMetadata(metaReader);
	}

	@Override
	public String getName() {
		return this.parent.getName();
	}

	@Override
	public void close() {
		this.parent.close();
	}

	private static void loadBytes(
		NativeImage source,
		NativeImage target,
		int sourceLeft,
		int sourceTop,
		int left,
		int top,
		int right,
		int bottom,
		int multiplier,
		boolean mirrorX,
		boolean mirrorY
	) {
		bottom *= multiplier;
		right *= multiplier;
		left *= multiplier;
		top *= multiplier;
		sourceLeft *= multiplier;
		sourceTop *= multiplier;

		for (int i = 0; i < bottom; i++) {
			for (int j = 0; j < right; j++) {
				target.setColor(left + j, top + i, source.getColor(sourceLeft + (mirrorX ? right - 1 - j : j), sourceTop + (mirrorY ? bottom - 1 - i : i)));
			}
		}
	}
}
