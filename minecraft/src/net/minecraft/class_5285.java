package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DynamicLike;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.CavesChunkGenerator;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5285 {
	private static final Dynamic<?> field_24522 = new Dynamic<>(NbtOps.INSTANCE, new CompoundTag());
	private static final ChunkGenerator field_24523 = new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig());
	private static final int field_24524 = "North Carolina".hashCode();
	public static final class_5285 field_24520 = new class_5285(
		(long)field_24524,
		true,
		true,
		class_5285.class_5287.field_24542,
		field_24522,
		new OverworldChunkGenerator(new VanillaLayeredBiomeSource((long)field_24524, false, 4), (long)field_24524, new OverworldChunkGeneratorConfig())
	);
	public static final class_5285 field_24521 = new class_5285(0L, false, false, class_5285.class_5287.field_24543, field_24522, field_24523);
	private static final Logger field_24525 = LogManager.getLogger();
	private final long field_24526;
	private final boolean field_24527;
	private final boolean field_24528;
	private final class_5285.class_5287 field_24529;
	private final Dynamic<?> field_24530;
	private final ChunkGenerator field_24531;
	@Nullable
	private final String field_24532;
	private final boolean field_24533;
	private static final Map<class_5285.class_5287, class_5285.class_5288> field_24534 = Maps.<class_5285.class_5287, class_5285.class_5288>newHashMap();

	public class_5285(long l, boolean bl, boolean bl2, class_5285.class_5287 arg, Dynamic<?> dynamic, ChunkGenerator chunkGenerator) {
		this(l, bl, bl2, arg, dynamic, chunkGenerator, null, false);
	}

	private class_5285(
		long l, boolean bl, boolean bl2, class_5285.class_5287 arg, Dynamic<?> dynamic, ChunkGenerator chunkGenerator, @Nullable String string, boolean bl3
	) {
		this.field_24526 = l;
		this.field_24527 = bl;
		this.field_24528 = bl2;
		this.field_24532 = string;
		this.field_24533 = bl3;
		this.field_24529 = arg;
		this.field_24530 = dynamic;
		this.field_24531 = chunkGenerator;
	}

	public static class_5285 method_28023(CompoundTag compoundTag, DataFixer dataFixer, int i) {
		long l = compoundTag.getLong("RandomSeed");
		String string = null;
		class_5285.class_5287 lv;
		Dynamic<?> dynamic3;
		ChunkGenerator chunkGenerator;
		if (compoundTag.contains("generatorName", 8)) {
			String string2 = compoundTag.getString("generatorName");
			lv = class_5285.class_5287.method_28048(string2);
			if (lv == null) {
				lv = class_5285.class_5287.field_24542;
			} else if (lv == class_5285.class_5287.field_24548) {
				string = compoundTag.getString("generatorOptions");
			} else if (lv == class_5285.class_5287.field_24542) {
				int j = 0;
				if (compoundTag.contains("generatorVersion", 99)) {
					j = compoundTag.getInt("generatorVersion");
				}

				if (j == 0) {
					lv = class_5285.class_5287.field_24549;
				}
			}

			CompoundTag compoundTag2 = compoundTag.getCompound("generatorOptions");
			Dynamic<?> dynamic = new Dynamic<>(NbtOps.INSTANCE, compoundTag2);
			int k = Math.max(i, 2501);
			Dynamic<?> dynamic2 = dynamic.merge(dynamic.createString("levelType"), dynamic.createString(lv.field_24551));
			dynamic3 = dataFixer.update(TypeReferences.CHUNK_GENERATOR_SETTINGS, dynamic2, k, SharedConstants.getGameVersion().getWorldVersion()).remove("levelType");
			chunkGenerator = method_28013(lv, dynamic3, l);
		} else {
			dynamic3 = field_24522;
			chunkGenerator = new OverworldChunkGenerator(new VanillaLayeredBiomeSource(l, false, 4), l, new OverworldChunkGeneratorConfig());
			lv = class_5285.class_5287.field_24542;
		}

		if (compoundTag.contains("legacy_custom_options", 8)) {
			string = compoundTag.getString("legacy_custom_options");
		}

		boolean bl;
		if (compoundTag.contains("MapFeatures", 99)) {
			bl = compoundTag.getBoolean("MapFeatures");
		} else {
			bl = true;
		}

		boolean bl2 = compoundTag.getBoolean("BonusChest");
		boolean bl3 = lv == class_5285.class_5287.field_24548 && i < 1466;
		return new class_5285(l, bl, bl2, lv, dynamic3, chunkGenerator, string, bl3);
	}

	private static ChunkGenerator method_28011(long l) {
		TheEndBiomeSource theEndBiomeSource = new TheEndBiomeSource(l);
		class_5284 lv = new class_5284(new ChunkGeneratorConfig());
		lv.setDefaultBlock(Blocks.END_STONE.getDefaultState());
		lv.setDefaultFluid(Blocks.AIR.getDefaultState());
		return new FloatingIslandsChunkGenerator(theEndBiomeSource, l, lv);
	}

	private static ChunkGenerator method_28026(long l) {
		ImmutableList<Biome> immutableList = ImmutableList.of(
			Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS
		);
		MultiNoiseBiomeSource multiNoiseBiomeSource = MultiNoiseBiomeSource.fromBiomes(l, immutableList);
		CavesChunkGeneratorConfig cavesChunkGeneratorConfig = new CavesChunkGeneratorConfig(new ChunkGeneratorConfig());
		cavesChunkGeneratorConfig.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
		cavesChunkGeneratorConfig.setDefaultFluid(Blocks.LAVA.getDefaultState());
		return new CavesChunkGenerator(multiNoiseBiomeSource, l, cavesChunkGeneratorConfig);
	}

	@Environment(EnvType.CLIENT)
	public static class_5285 method_28009() {
		long l = new Random().nextLong();
		return new class_5285(
			l,
			true,
			false,
			class_5285.class_5287.field_24542,
			field_24522,
			new OverworldChunkGenerator(new VanillaLayeredBiomeSource(l, false, 4), l, new OverworldChunkGeneratorConfig())
		);
	}

	public CompoundTag method_28025() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putLong("RandomSeed", this.method_28028());
		class_5285.class_5287 lv = this.field_24529 == class_5285.class_5287.field_24548 ? class_5285.class_5287.field_24542 : this.field_24529;
		compoundTag.putString("generatorName", lv.field_24551);
		compoundTag.putInt("generatorVersion", this.field_24529 == class_5285.class_5287.field_24542 ? 1 : 0);
		CompoundTag compoundTag2 = (CompoundTag)this.field_24530.convert(NbtOps.INSTANCE).getValue();
		if (!compoundTag2.isEmpty()) {
			compoundTag.put("generatorOptions", compoundTag2);
		}

		if (this.field_24532 != null) {
			compoundTag.putString("legacy_custom_options", this.field_24532);
		}

		compoundTag.putBoolean("MapFeatures", this.method_28029());
		compoundTag.putBoolean("BonusChest", this.method_28030());
		return compoundTag;
	}

	public long method_28028() {
		return this.field_24526;
	}

	public boolean method_28029() {
		return this.field_24527;
	}

	public boolean method_28030() {
		return this.field_24528;
	}

	public Map<DimensionType, ChunkGenerator> method_28031() {
		return ImmutableMap.of(
			DimensionType.OVERWORLD, this.field_24531, DimensionType.THE_NETHER, method_28026(this.field_24526), DimensionType.THE_END, method_28011(this.field_24526)
		);
	}

	public ChunkGenerator method_28032() {
		return this.field_24531;
	}

	public boolean method_28033() {
		return this.field_24529 == class_5285.class_5287.field_24547;
	}

	public boolean method_28034() {
		return this.field_24529 == class_5285.class_5287.field_24543;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_28035() {
		return this.field_24533;
	}

	public class_5285 method_28036() {
		return new class_5285(this.field_24526, this.field_24527, true, this.field_24529, this.field_24530, this.field_24531, this.field_24532, this.field_24533);
	}

	@Environment(EnvType.CLIENT)
	public class_5285 method_28037() {
		return new class_5285(this.field_24526, !this.field_24527, this.field_24528, this.field_24529, this.field_24530, this.field_24531);
	}

	@Environment(EnvType.CLIENT)
	public class_5285 method_28038() {
		return new class_5285(this.field_24526, this.field_24527, !this.field_24528, this.field_24529, this.field_24530, this.field_24531);
	}

	public static class_5285 method_28021(Properties properties) {
		String string = MoreObjects.firstNonNull((String)properties.get("generator-settings"), "");
		properties.put("generator-settings", string);
		String string2 = MoreObjects.firstNonNull((String)properties.get("level-seed"), "");
		properties.put("level-seed", string2);
		String string3 = (String)properties.get("generate-structures");
		boolean bl = string3 == null || Boolean.parseBoolean(string3);
		properties.put("generate-structures", Objects.toString(bl));
		String string4 = (String)properties.get("level-type");
		class_5285.class_5287 lv;
		if (string4 != null) {
			lv = MoreObjects.firstNonNull(class_5285.class_5287.method_28048(string4), class_5285.class_5287.field_24542);
		} else {
			lv = class_5285.class_5287.field_24542;
		}

		properties.put("level-type", lv.field_24551);
		JsonObject jsonObject = !string.isEmpty() ? JsonHelper.deserialize(string) : new JsonObject();
		long l = new Random().nextLong();
		if (!string2.isEmpty()) {
			try {
				long m = Long.parseLong(string2);
				if (m != 0L) {
					l = m;
				}
			} catch (NumberFormatException var12) {
				l = (long)string2.hashCode();
			}
		}

		Dynamic<?> dynamic = new Dynamic<>(JsonOps.INSTANCE, jsonObject);
		return new class_5285(l, bl, false, lv, dynamic, method_28013(lv, dynamic, l));
	}

	@Environment(EnvType.CLIENT)
	public class_5285 method_28015(class_5285.class_5288 arg) {
		return this.method_28014(arg.field_24557, field_24522, method_28013(arg.field_24557, field_24522, this.field_24526));
	}

	@Environment(EnvType.CLIENT)
	private class_5285 method_28014(class_5285.class_5287 arg, Dynamic<?> dynamic, ChunkGenerator chunkGenerator) {
		return new class_5285(this.field_24526, this.field_24527, this.field_24528, arg, dynamic, chunkGenerator);
	}

	@Environment(EnvType.CLIENT)
	public class_5285 method_28019(FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
		return this.method_28014(
			class_5285.class_5287.field_24543, flatChunkGeneratorConfig.toDynamic(NbtOps.INSTANCE), new FlatChunkGenerator(flatChunkGeneratorConfig)
		);
	}

	@Environment(EnvType.CLIENT)
	public class_5285 method_28012(class_5285.class_5286 arg, Set<Biome> set) {
		Dynamic<?> dynamic = method_28027(arg, set);
		return this.method_28014(class_5285.class_5287.field_24546, dynamic, method_28013(class_5285.class_5287.field_24546, dynamic, this.field_24526));
	}

	@Environment(EnvType.CLIENT)
	public class_5285.class_5288 method_28039() {
		return this.field_24529 == class_5285.class_5287.field_24548
			? class_5285.class_5288.field_24552
			: (class_5285.class_5288)field_24534.getOrDefault(this.field_24529, class_5285.class_5288.field_24552);
	}

	@Environment(EnvType.CLIENT)
	public class_5285 method_28024(boolean bl, OptionalLong optionalLong) {
		long l = optionalLong.orElse(this.field_24526);
		ChunkGenerator chunkGenerator = optionalLong.isPresent() ? this.field_24531.create(optionalLong.getAsLong()) : this.field_24531;
		class_5285 lv;
		if (this.method_28033()) {
			lv = new class_5285(l, false, false, this.field_24529, this.field_24530, chunkGenerator);
		} else {
			lv = new class_5285(l, this.method_28029(), this.method_28030() && !bl, this.field_24529, this.field_24530, chunkGenerator);
		}

		return lv;
	}

	private static ChunkGenerator method_28013(class_5285.class_5287 arg, Dynamic<?> dynamic, long l) {
		if (arg == class_5285.class_5287.field_24546) {
			BiomeSource biomeSource = method_28017(dynamic.get("biome_source"), l);
			DynamicLike<?> dynamicLike = dynamic.get("chunk_generator");
			class_5285.class_5286 lv = DataFixUtils.orElse(
				dynamicLike.get("type").asString().flatMap(string -> Optional.ofNullable(class_5285.class_5286.method_28045(string))), class_5285.class_5286.field_24536
			);
			DynamicLike<?> dynamicLike2 = dynamicLike.get("options");
			BlockState blockState = method_28018(dynamicLike2.get("default_block"), Registry.BLOCK, Blocks.STONE).getDefaultState();
			BlockState blockState2 = method_28018(dynamicLike2.get("default_fluid"), Registry.BLOCK, Blocks.WATER).getDefaultState();
			switch (lv) {
				case field_24537:
					CavesChunkGeneratorConfig cavesChunkGeneratorConfig = new CavesChunkGeneratorConfig(new ChunkGeneratorConfig());
					cavesChunkGeneratorConfig.setDefaultBlock(blockState);
					cavesChunkGeneratorConfig.setDefaultFluid(blockState2);
					return new CavesChunkGenerator(biomeSource, l, cavesChunkGeneratorConfig);
				case field_24538:
					class_5284 lv2 = new class_5284(new ChunkGeneratorConfig());
					lv2.setDefaultBlock(blockState);
					lv2.setDefaultFluid(blockState2);
					return new FloatingIslandsChunkGenerator(biomeSource, l, lv2);
				case field_24536:
				default:
					OverworldChunkGeneratorConfig overworldChunkGeneratorConfig = new OverworldChunkGeneratorConfig();
					overworldChunkGeneratorConfig.setDefaultBlock(blockState);
					overworldChunkGeneratorConfig.setDefaultFluid(blockState2);
					return new OverworldChunkGenerator(biomeSource, l, overworldChunkGeneratorConfig);
			}
		} else if (arg == class_5285.class_5287.field_24543) {
			FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.fromDynamic(dynamic);
			return new FlatChunkGenerator(flatChunkGeneratorConfig);
		} else if (arg == class_5285.class_5287.field_24547) {
			return DebugChunkGenerator.generator;
		} else {
			boolean bl = arg == class_5285.class_5287.field_24549;
			int i = arg == class_5285.class_5287.field_24544 ? 6 : 4;
			boolean bl2 = arg == class_5285.class_5287.field_24545;
			OverworldChunkGeneratorConfig overworldChunkGeneratorConfig2 = new OverworldChunkGeneratorConfig(new ChunkGeneratorConfig(), bl2);
			return new OverworldChunkGenerator(new VanillaLayeredBiomeSource(l, bl, i), l, overworldChunkGeneratorConfig2);
		}
	}

	private static <T> T method_28018(DynamicLike<?> dynamicLike, Registry<T> registry, T object) {
		return (T)dynamicLike.asString().map(Identifier::new).flatMap(registry::getOrEmpty).orElse(object);
	}

	private static BiomeSource method_28017(DynamicLike<?> dynamicLike, long l) {
		BiomeSourceType biomeSourceType = method_28018(dynamicLike.get("type"), Registry.BIOME_SOURCE_TYPE, BiomeSourceType.FIXED);
		DynamicLike<?> dynamicLike2 = dynamicLike.get("options");
		Stream<Biome> stream = (Stream<Biome>)dynamicLike2.get("biomes")
			.asStreamOpt()
			.map(streamx -> streamx.map(dynamic -> method_28018(dynamic, Registry.BIOME, Biomes.OCEAN)))
			.orElseGet(Stream::empty);
		if (BiomeSourceType.CHECKERBOARD == biomeSourceType) {
			int i = dynamicLike2.get("size").asInt(2);
			Biome[] biomes = (Biome[])stream.toArray(Biome[]::new);
			Biome[] biomes2 = biomes.length > 0 ? biomes : new Biome[]{Biomes.OCEAN};
			return new CheckerboardBiomeSource(biomes2, i);
		} else if (BiomeSourceType.VANILLA_LAYERED == biomeSourceType) {
			return new VanillaLayeredBiomeSource(l, false, 4);
		} else {
			Biome biome = (Biome)stream.findFirst().orElse(Biomes.OCEAN);
			return new FixedBiomeSource(biome);
		}
	}

	@Environment(EnvType.CLIENT)
	private static Dynamic<?> method_28027(class_5285.class_5286 arg, Set<Biome> set) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
		CompoundTag compoundTag3 = new CompoundTag();
		ListTag listTag = new ListTag();

		for (Biome biome : set) {
			listTag.add(StringTag.of(Registry.BIOME.getId(biome).toString()));
		}

		compoundTag3.put("biomes", listTag);
		compoundTag2.put("options", compoundTag3);
		CompoundTag compoundTag4 = new CompoundTag();
		CompoundTag compoundTag5 = new CompoundTag();
		compoundTag4.putString("type", arg.method_28046());
		compoundTag5.putString("default_block", "minecraft:stone");
		compoundTag5.putString("default_fluid", "minecraft:water");
		compoundTag4.put("options", compoundTag5);
		compoundTag.put("biome_source", compoundTag2);
		compoundTag.put("chunk_generator", compoundTag4);
		return new Dynamic<>(NbtOps.INSTANCE, compoundTag);
	}

	@Environment(EnvType.CLIENT)
	public FlatChunkGeneratorConfig method_28040() {
		return this.field_24529 == class_5285.class_5287.field_24543
			? FlatChunkGeneratorConfig.fromDynamic(this.field_24530)
			: FlatChunkGeneratorConfig.getDefaultConfig();
	}

	@Environment(EnvType.CLIENT)
	public Pair<class_5285.class_5286, Set<Biome>> method_28041() {
		if (this.field_24529 != class_5285.class_5287.field_24546) {
			return Pair.of(class_5285.class_5286.field_24536, ImmutableSet.of());
		} else {
			class_5285.class_5286 lv = class_5285.class_5286.field_24536;
			Set<Biome> set = Sets.<Biome>newHashSet();
			CompoundTag compoundTag = (CompoundTag)this.field_24530.convert(NbtOps.INSTANCE).getValue();
			if (compoundTag.contains("chunk_generator", 10) && compoundTag.getCompound("chunk_generator").contains("type", 8)) {
				String string = compoundTag.getCompound("chunk_generator").getString("type");
				lv = class_5285.class_5286.method_28045(string);
			}

			if (compoundTag.contains("biome_source", 10) && compoundTag.getCompound("biome_source").contains("biomes", 9)) {
				ListTag listTag = compoundTag.getCompound("biome_source").getList("biomes", 8);

				for (int i = 0; i < listTag.size(); i++) {
					Identifier identifier = new Identifier(listTag.getString(i));
					Biome biome = Registry.BIOME.get(identifier);
					set.add(biome);
				}
			}

			return Pair.of(lv, set);
		}
	}

	public static enum class_5286 {
		field_24536("minecraft:surface"),
		field_24537("minecraft:caves"),
		field_24538("minecraft:floating_islands");

		private static final Map<String, class_5285.class_5286> field_24539 = (Map<String, class_5285.class_5286>)Arrays.stream(values())
			.collect(Collectors.toMap(class_5285.class_5286::method_28046, Function.identity()));
		private final String field_24540;

		private class_5286(String string2) {
			this.field_24540 = string2;
		}

		@Environment(EnvType.CLIENT)
		public Text method_28043() {
			return new TranslatableText("createWorld.customize.buffet.generatortype")
				.append(" ")
				.append(new TranslatableText(Util.createTranslationKey("generator", new Identifier(this.field_24540))));
		}

		private String method_28046() {
			return this.field_24540;
		}

		@Nullable
		public static class_5285.class_5286 method_28045(String string) {
			return (class_5285.class_5286)field_24539.get(string);
		}
	}

	static class class_5287 {
		private static final Set<class_5285.class_5287> field_24550 = Sets.<class_5285.class_5287>newHashSet();
		public static final class_5285.class_5287 field_24542 = new class_5285.class_5287("default");
		public static final class_5285.class_5287 field_24543 = new class_5285.class_5287("flat");
		public static final class_5285.class_5287 field_24544 = new class_5285.class_5287("largeBiomes");
		public static final class_5285.class_5287 field_24545 = new class_5285.class_5287("amplified");
		public static final class_5285.class_5287 field_24546 = new class_5285.class_5287("buffet");
		public static final class_5285.class_5287 field_24547 = new class_5285.class_5287("debug_all_block_states");
		public static final class_5285.class_5287 field_24548 = new class_5285.class_5287("customized");
		public static final class_5285.class_5287 field_24549 = new class_5285.class_5287("default_1_1");
		private final String field_24551;

		private class_5287(String string) {
			this.field_24551 = string;
			field_24550.add(this);
		}

		@Nullable
		public static class_5285.class_5287 method_28048(String string) {
			for (class_5285.class_5287 lv : field_24550) {
				if (lv.field_24551.equalsIgnoreCase(string)) {
					return lv;
				}
			}

			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class class_5288 {
		public static final class_5285.class_5288 field_24552 = new class_5285.class_5288(class_5285.class_5287.field_24542);
		public static final class_5285.class_5288 field_24553 = new class_5285.class_5288(class_5285.class_5287.field_24543);
		public static final class_5285.class_5288 field_24554 = new class_5285.class_5288(class_5285.class_5287.field_24545);
		public static final class_5285.class_5288 field_24555 = new class_5285.class_5288(class_5285.class_5287.field_24546);
		public static final List<class_5285.class_5288> field_24556 = Lists.<class_5285.class_5288>newArrayList(
			field_24552,
			field_24553,
			new class_5285.class_5288(class_5285.class_5287.field_24544),
			field_24554,
			field_24555,
			new class_5285.class_5288(class_5285.class_5287.field_24547)
		);
		private final class_5285.class_5287 field_24557;
		private final Text field_24558;

		private class_5288(class_5285.class_5287 arg) {
			this.field_24557 = arg;
			class_5285.field_24534.put(arg, this);
			this.field_24558 = new TranslatableText("generator." + arg.field_24551);
		}

		public Text method_28049() {
			return this.field_24558;
		}
	}
}
