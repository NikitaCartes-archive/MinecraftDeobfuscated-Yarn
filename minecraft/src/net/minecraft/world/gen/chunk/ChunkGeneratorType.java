package net.minecraft.world.gen.chunk;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5017;
import net.minecraft.class_5019;
import net.minecraft.class_5021;
import net.minecraft.class_5027;
import net.minecraft.class_5029;
import net.minecraft.class_5031;
import net.minecraft.class_5034;
import net.minecraft.class_5040;
import net.minecraft.class_5042;
import net.minecraft.class_5045;
import net.minecraft.class_5047;
import net.minecraft.class_5049;
import net.minecraft.class_5051;
import net.minecraft.class_5053;
import net.minecraft.class_5055;
import net.minecraft.class_5057;
import net.minecraft.class_5059;
import net.minecraft.class_5061;
import net.minecraft.class_5063;
import net.minecraft.class_5065;
import net.minecraft.class_5067;
import net.minecraft.class_5069;
import net.minecraft.class_5071;
import net.minecraft.class_5073;
import net.minecraft.class_5075;
import net.minecraft.class_5077;
import net.minecraft.class_5079;
import net.minecraft.class_5083;
import net.minecraft.class_5085;
import net.minecraft.class_5087;
import net.minecraft.class_5089;
import net.minecraft.class_5091;
import net.minecraft.class_5093;
import net.minecraft.class_5095;
import net.minecraft.class_5097;
import net.minecraft.class_5099;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;

public class ChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T> {
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> SURFACE = register(
		"surface", OverworldChunkGenerator::new, OverworldChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> CAVES = register(
		"caves", CavesChunkGenerator::new, CavesChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> FLOATING_ISLANDS = register(
		"floating_islands", FloatingIslandsChunkGenerator::new, FloatingIslandsChunkGeneratorConfig::new, true
	);
	public static final ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> DEBUG = register(
		"debug", DebugChunkGenerator::new, DebugChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> FLAT = register(
		"flat", FlatChunkGenerator::new, FlatChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<class_5099, class_5017.class_5018> field_23458 = register("_001", class_5017.class_5018::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5019.class_5020> field_23459 = register("_002", class_5019.class_5020::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5021.class_5023> field_23460 = register("_003", class_5021.class_5023::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5027.class_5028> field_23461 = register("_004", class_5027.class_5028::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5029.class_5030> field_23462 = register("_005", class_5029.class_5030::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5031.class_5032> field_23463 = register("_006", class_5031.class_5032::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5034.class_5035> field_23464 = register("_007", class_5034.class_5035::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5040.class_5041> field_23465 = register("_008", class_5040.class_5041::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5042.class_5043> field_23466 = register("_009", class_5042.class_5043::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5045.class_5046> field_23467 = register("_010", class_5045.class_5046::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5047.class_5048> field_23468 = register("_011", class_5047.class_5048::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5049.class_5050> field_23469 = register("_012", class_5049.class_5050::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5051.class_5052> field_23470 = register("_013", class_5051.class_5052::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5053.class_5054> field_23471 = register("_014", class_5053.class_5054::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5055.class_5056> field_23472 = register("_015", class_5055.class_5056::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5057.class_5058> field_23473 = register("_016", class_5057.class_5058::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5059.class_5060> field_23474 = register("_017", class_5059.class_5060::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5061.class_5062> field_23475 = register("_018", class_5061.class_5062::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5063.class_5064> field_23476 = register("_019", class_5063.class_5064::new, class_5099::new, false);
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, class_5065.class_5066> field_23477 = register(
		"_020", class_5065.class_5066::new, OverworldChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<class_5099, class_5067.class_5068> field_23478 = register("_021", class_5067.class_5068::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5069.class_5070> field_23444 = register("_022", class_5069.class_5070::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5071.class_5072> field_23445 = register("_023", class_5071.class_5072::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5073.class_5074> field_23446 = register("_024", class_5073.class_5074::new, class_5099::new, false);
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, class_5075.class_5076> field_23447 = register(
		"_025", class_5075.class_5076::new, OverworldChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<class_5099, class_5077.class_5078> field_23448 = register("_026", class_5077.class_5078::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5097.class_5098> field_23449 = register("_027", class_5097.class_5098::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5079.class_5080> field_23450 = register("_028", class_5079.class_5080::new, class_5099::new, false);
	public static final ChunkGeneratorType<class_5099, class_5083.class_5084> field_23451 = register("_029", class_5083.class_5084::new, class_5099::new, false);
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, class_5085.class_5086> field_23452 = register(
		"_030", class_5085.class_5086::new, OverworldChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<class_5099, class_5087.class_5088> field_23453 = register("_031", class_5087.class_5088::new, class_5099::new, false);
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, class_5089.class_5090> field_23454 = register(
		"_032", class_5089.class_5090::new, OverworldChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, class_5091.class_5092> field_23455 = register(
		"_033", class_5091.class_5092::new, OverworldChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, class_5093.class_5094> field_23456 = register(
		"_034", class_5093.class_5094::new, OverworldChunkGeneratorConfig::new, false
	);
	public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, class_5095.class_5096> field_23457 = register(
		"_035", class_5095.class_5096::new, OverworldChunkGeneratorConfig::new, false
	);
	private final ChunkGeneratorFactory<C, T> factory;
	private final boolean buffetScreenOption;
	private final Supplier<C> configSupplier;

	private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(
		String id, ChunkGeneratorFactory<C, T> factory, Supplier<C> configSupplier, boolean buffetScreenOption
	) {
		return Registry.register(Registry.CHUNK_GENERATOR_TYPE, id, new ChunkGeneratorType<>(factory, buffetScreenOption, configSupplier));
	}

	public ChunkGeneratorType(ChunkGeneratorFactory<C, T> factory, boolean buffetScreenOption, Supplier<C> configSupplier) {
		this.factory = factory;
		this.buffetScreenOption = buffetScreenOption;
		this.configSupplier = configSupplier;
	}

	@Override
	public T create(IWorld iWorld, BiomeSource biomeSource, C chunkGeneratorConfig) {
		return this.factory.create(iWorld, biomeSource, chunkGeneratorConfig);
	}

	public C createConfig() {
		return (C)this.configSupplier.get();
	}

	@Environment(EnvType.CLIENT)
	public boolean isBuffetScreenOption() {
		return this.buffetScreenOption;
	}
}
