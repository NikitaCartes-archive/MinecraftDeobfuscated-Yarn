package net.minecraft;

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
public class class_4729 implements ResourcePack {
	private static final Map<String, Pair<ChestType, Identifier>> field_21762 = Util.create(Maps.<String, Pair<ChestType, Identifier>>newHashMap(), hashMap -> {
		hashMap.put("textures/entity/chest/normal_left.png", new Pair<>(ChestType.LEFT, new Identifier("textures/entity/chest/normal_double.png")));
		hashMap.put("textures/entity/chest/normal_right.png", new Pair<>(ChestType.RIGHT, new Identifier("textures/entity/chest/normal_double.png")));
		hashMap.put("textures/entity/chest/normal.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/normal.png")));
		hashMap.put("textures/entity/chest/trapped_left.png", new Pair<>(ChestType.LEFT, new Identifier("textures/entity/chest/trapped_double.png")));
		hashMap.put("textures/entity/chest/trapped_right.png", new Pair<>(ChestType.RIGHT, new Identifier("textures/entity/chest/trapped_double.png")));
		hashMap.put("textures/entity/chest/trapped.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/trapped.png")));
		hashMap.put("textures/entity/chest/christmas_left.png", new Pair<>(ChestType.LEFT, new Identifier("textures/entity/chest/christmas_double.png")));
		hashMap.put("textures/entity/chest/christmas_right.png", new Pair<>(ChestType.RIGHT, new Identifier("textures/entity/chest/christmas_double.png")));
		hashMap.put("textures/entity/chest/christmas.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/christmas.png")));
		hashMap.put("textures/entity/chest/ender.png", new Pair<>(ChestType.SINGLE, new Identifier("textures/entity/chest/ender.png")));
	});
	private static final List<String> field_21763 = Lists.<String>newArrayList(
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
	private static final Set<String> field_21764 = (Set<String>)field_21763.stream()
		.map(string -> "textures/entity/shield/" + string + ".png")
		.collect(Collectors.toSet());
	private static final Set<String> field_21765 = (Set<String>)field_21763.stream()
		.map(string -> "textures/entity/banner/" + string + ".png")
		.collect(Collectors.toSet());
	public static final Identifier field_21759 = new Identifier("textures/entity/shield_base.png");
	public static final Identifier field_21760 = new Identifier("textures/entity/banner_base.png");
	public static final Identifier field_21761 = new Identifier("textures/entity/iron_golem.png");
	private final ResourcePack field_21766;

	public class_4729(ResourcePack resourcePack) {
		this.field_21766 = resourcePack;
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		return this.field_21766.openRoot(fileName);
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		if (!"minecraft".equals(id.getNamespace())) {
			return this.field_21766.contains(type, id);
		} else {
			String string = id.getPath();
			if ("textures/misc/enchanted_item_glint.png".equals(string)) {
				return false;
			} else if ("textures/entity/iron_golem/iron_golem.png".equals(string)) {
				return this.field_21766.contains(type, field_21761);
			} else if ("textures/entity/conduit/wind.png".equals(string) || "textures/entity/conduit/wind_vertical.png".equals(string)) {
				return false;
			} else if (field_21764.contains(string)) {
				return this.field_21766.contains(type, field_21759) && this.field_21766.contains(type, id);
			} else if (!field_21765.contains(string)) {
				Pair<ChestType, Identifier> pair = (Pair<ChestType, Identifier>)field_21762.get(string);
				return pair != null && this.field_21766.contains(type, pair.getSecond()) ? true : this.field_21766.contains(type, id);
			} else {
				return this.field_21766.contains(type, field_21760) && this.field_21766.contains(type, id);
			}
		}
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		if (!"minecraft".equals(id.getNamespace())) {
			return this.field_21766.open(type, id);
		} else {
			String string = id.getPath();
			if ("textures/entity/iron_golem/iron_golem.png".equals(string)) {
				return this.field_21766.open(type, field_21761);
			} else {
				if (field_21764.contains(string)) {
					InputStream inputStream = method_24134(this.field_21766.open(type, field_21759), this.field_21766.open(type, id), 64, 2, 2, 12, 22);
					if (inputStream != null) {
						return inputStream;
					}
				} else if (field_21765.contains(string)) {
					InputStream inputStream = method_24134(this.field_21766.open(type, field_21760), this.field_21766.open(type, id), 64, 0, 0, 42, 41);
					if (inputStream != null) {
						return inputStream;
					}
				} else {
					if ("textures/entity/enderdragon/dragon.png".equals(string) || "textures/entity/enderdragon/dragon_exploding.png".equals(string)) {
						ByteArrayInputStream var23;
						try (NativeImage nativeImage = NativeImage.read(this.field_21766.open(type, id))) {
							int i = nativeImage.getWidth() / 256;

							for (int j = 88 * i; j < 200 * i; j++) {
								for (int k = 56 * i; k < 112 * i; k++) {
									nativeImage.setPixelRgba(k, j, 0);
								}
							}

							var23 = new ByteArrayInputStream(nativeImage.method_24036());
						}

						return var23;
					}

					Pair<ChestType, Identifier> pair = (Pair<ChestType, Identifier>)field_21762.get(string);
					if (pair != null) {
						ChestType chestType = pair.getFirst();
						InputStream inputStream2 = this.field_21766.open(type, pair.getSecond());
						if (chestType == ChestType.SINGLE) {
							return method_24138(inputStream2);
						}

						if (chestType == ChestType.LEFT) {
							return method_24133(inputStream2);
						}

						if (chestType == ChestType.RIGHT) {
							return method_24137(inputStream2);
						}
					}
				}

				return this.field_21766.open(type, id);
			}
		}
	}

	@Nullable
	public static InputStream method_24134(InputStream inputStream, InputStream inputStream2, int i, int j, int k, int l, int m) throws IOException {
		ByteArrayInputStream var71;
		try (
			NativeImage nativeImage = NativeImage.read(inputStream);
			NativeImage nativeImage2 = NativeImage.read(inputStream2);
		) {
			int n = nativeImage.getWidth();
			int o = nativeImage.getHeight();
			if (n != nativeImage2.getWidth() || o != nativeImage2.getHeight()) {
				return null;
			}

			try (NativeImage nativeImage3 = new NativeImage(n, o, true)) {
				int p = n / i;

				for (int q = k * p; q < m * p; q++) {
					for (int r = j * p; r < l * p; r++) {
						int s = NativeImage.method_24033(nativeImage2.getPixelRgba(r, q));
						int t = nativeImage.getPixelRgba(r, q);
						nativeImage3.setPixelRgba(r, q, NativeImage.method_24031(s, NativeImage.method_24035(t), NativeImage.method_24034(t), NativeImage.method_24033(t)));
					}
				}

				var71 = new ByteArrayInputStream(nativeImage3.method_24036());
			}
		}

		return var71;
	}

	public static InputStream method_24133(InputStream inputStream) throws IOException {
		ByteArrayInputStream var8;
		try (NativeImage nativeImage = NativeImage.read(inputStream)) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();

			try (NativeImage nativeImage2 = new NativeImage(i / 2, j, true)) {
				int k = j / 64;
				method_24132(nativeImage, nativeImage2, 29, 0, 29, 0, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 59, 0, 14, 0, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 29, 14, 43, 14, 15, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 44, 14, 29, 14, 14, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 58, 14, 14, 14, 15, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 29, 19, 29, 19, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 59, 19, 14, 19, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 29, 33, 43, 33, 15, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 44, 33, 29, 33, 14, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 58, 33, 14, 33, 15, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 2, 0, 2, 0, 1, 1, k, false, true);
				method_24132(nativeImage, nativeImage2, 4, 0, 1, 0, 1, 1, k, false, true);
				method_24132(nativeImage, nativeImage2, 2, 1, 3, 1, 1, 4, k, true, true);
				method_24132(nativeImage, nativeImage2, 3, 1, 2, 1, 1, 4, k, true, true);
				method_24132(nativeImage, nativeImage2, 4, 1, 1, 1, 1, 4, k, true, true);
				var8 = new ByteArrayInputStream(nativeImage2.method_24036());
			}
		}

		return var8;
	}

	public static InputStream method_24137(InputStream inputStream) throws IOException {
		ByteArrayInputStream var8;
		try (NativeImage nativeImage = NativeImage.read(inputStream)) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();

			try (NativeImage nativeImage2 = new NativeImage(i / 2, j, true)) {
				int k = j / 64;
				method_24132(nativeImage, nativeImage2, 14, 0, 29, 0, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 44, 0, 14, 0, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 0, 14, 0, 14, 14, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 14, 14, 43, 14, 15, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 73, 14, 14, 14, 15, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 14, 19, 29, 19, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 44, 19, 14, 19, 15, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 0, 33, 0, 33, 14, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 14, 33, 43, 33, 15, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 73, 33, 14, 33, 15, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 1, 0, 2, 0, 1, 1, k, false, true);
				method_24132(nativeImage, nativeImage2, 3, 0, 1, 0, 1, 1, k, false, true);
				method_24132(nativeImage, nativeImage2, 0, 1, 0, 1, 1, 4, k, true, true);
				method_24132(nativeImage, nativeImage2, 1, 1, 3, 1, 1, 4, k, true, true);
				method_24132(nativeImage, nativeImage2, 5, 1, 1, 1, 1, 4, k, true, true);
				var8 = new ByteArrayInputStream(nativeImage2.method_24036());
			}
		}

		return var8;
	}

	public static InputStream method_24138(InputStream inputStream) throws IOException {
		ByteArrayInputStream var8;
		try (NativeImage nativeImage = NativeImage.read(inputStream)) {
			int i = nativeImage.getWidth();
			int j = nativeImage.getHeight();

			try (NativeImage nativeImage2 = new NativeImage(i, j, true)) {
				int k = j / 64;
				method_24132(nativeImage, nativeImage2, 14, 0, 28, 0, 14, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 28, 0, 14, 0, 14, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 0, 14, 0, 14, 14, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 14, 14, 42, 14, 14, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 28, 14, 28, 14, 14, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 42, 14, 14, 14, 14, 5, k, true, true);
				method_24132(nativeImage, nativeImage2, 14, 19, 28, 19, 14, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 28, 19, 14, 19, 14, 14, k, false, true);
				method_24132(nativeImage, nativeImage2, 0, 33, 0, 33, 14, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 14, 33, 42, 33, 14, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 28, 33, 28, 33, 14, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 42, 33, 14, 33, 14, 10, k, true, true);
				method_24132(nativeImage, nativeImage2, 1, 0, 3, 0, 2, 1, k, false, true);
				method_24132(nativeImage, nativeImage2, 3, 0, 1, 0, 2, 1, k, false, true);
				method_24132(nativeImage, nativeImage2, 0, 1, 0, 1, 1, 4, k, true, true);
				method_24132(nativeImage, nativeImage2, 1, 1, 4, 1, 2, 4, k, true, true);
				method_24132(nativeImage, nativeImage2, 3, 1, 3, 1, 1, 4, k, true, true);
				method_24132(nativeImage, nativeImage2, 4, 1, 1, 1, 2, 4, k, true, true);
				var8 = new ByteArrayInputStream(nativeImage2.method_24036());
			}
		}

		return var8;
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String string, int i, Predicate<String> predicate) {
		return this.field_21766.findResources(type, namespace, string, i, predicate);
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return this.field_21766.getNamespaces(type);
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return this.field_21766.parseMetadata(metaReader);
	}

	@Override
	public String getName() {
		return this.field_21766.getName();
	}

	public void close() throws IOException {
		this.field_21766.close();
	}

	private static void method_24132(NativeImage nativeImage, NativeImage nativeImage2, int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2) {
		n *= o;
		m *= o;
		k *= o;
		l *= o;
		i *= o;
		j *= o;

		for (int p = 0; p < n; p++) {
			for (int q = 0; q < m; q++) {
				nativeImage2.setPixelRgba(k + q, l + p, nativeImage.getPixelRgba(i + (bl ? m - 1 - q : q), j + (bl2 ? n - 1 - p : p)));
			}
		}
	}
}
