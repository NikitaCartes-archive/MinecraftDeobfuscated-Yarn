package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.datafixer.Schemas;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestServer extends MinecraftServer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_33157 = 20;
	private final List<GameTestBatch> batches;
	private final BlockPos pos;
	private static final GameRules GAME_RULES = Util.make(new GameRules(), gameRules -> {
		gameRules.get(GameRules.DO_MOB_SPAWNING).set(false, null);
		gameRules.get(GameRules.DO_WEATHER_CYCLE).set(false, null);
	});
	private static final LevelInfo TEST_LEVEL = new LevelInfo(
		"Test Level", GameMode.CREATIVE, false, Difficulty.NORMAL, true, GAME_RULES, DataPackSettings.SAFE_MODE
	);
	@Nullable
	private TestSet testSet;

	public TestServer(
		Thread serverThread,
		LevelStorage.Session session,
		ResourcePackManager dataPackManager,
		ServerResourceManager serverResourceManager,
		Collection<GameTestBatch> batches,
		BlockPos pos,
		DynamicRegistryManager.Impl registryManager
	) {
		this(
			serverThread,
			session,
			dataPackManager,
			serverResourceManager,
			batches,
			pos,
			registryManager,
			registryManager.get(Registry.BIOME_KEY),
			registryManager.get(Registry.DIMENSION_TYPE_KEY)
		);
	}

	private TestServer(
		Thread serverThread,
		LevelStorage.Session session,
		ResourcePackManager dataPackManager,
		ServerResourceManager serverResourceManager,
		Collection<GameTestBatch> batches,
		BlockPos pos,
		DynamicRegistryManager.Impl registryManager,
		Registry<Biome> biomeRegistry,
		Registry<DimensionType> dimensionTypeRegistry
	) {
		super(
			serverThread,
			registryManager,
			session,
			new LevelProperties(
				TEST_LEVEL,
				new GeneratorOptions(
					0L,
					false,
					false,
					GeneratorOptions.getRegistryWithReplacedOverworldGenerator(
						dimensionTypeRegistry, DimensionType.method_39540(registryManager, 0L), new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig(biomeRegistry))
					)
				),
				Lifecycle.stable()
			),
			dataPackManager,
			Proxy.NO_PROXY,
			Schemas.getFixer(),
			serverResourceManager,
			null,
			null,
			null,
			WorldGenerationProgressLogger::new
		);
		this.batches = Lists.<GameTestBatch>newArrayList(batches);
		this.pos = pos;
		if (batches.isEmpty()) {
			throw new IllegalArgumentException("No test batches were given!");
		}
	}

	@Override
	public boolean setupServer() {
		this.setPlayerManager(new PlayerManager(this, this.registryManager, this.saveHandler, 1) {
		});
		this.loadWorld();
		ServerWorld serverWorld = this.getOverworld();
		serverWorld.setSpawnPos(this.pos, 0.0F);
		int i = 20000000;
		serverWorld.setWeather(20000000, 20000000, false, false);
		return true;
	}

	@Override
	public void tick(BooleanSupplier shouldKeepTicking) {
		super.tick(shouldKeepTicking);
		ServerWorld serverWorld = this.getOverworld();
		if (!this.isTesting()) {
			this.runTestBatches(serverWorld);
		}

		if (serverWorld.getTime() % 20L == 0L) {
			LOGGER.info(this.testSet.getResultString());
		}

		if (this.testSet.isDone()) {
			this.stop(false);
			LOGGER.info(this.testSet.getResultString());
			TestFailureLogger.stop();
			LOGGER.info("========= {} GAME TESTS COMPLETE ======================", this.testSet.getTestCount());
			if (this.testSet.failed()) {
				LOGGER.info("{} required tests failed :(", this.testSet.getFailedRequiredTestCount());
				this.testSet.getRequiredTests().forEach(test -> LOGGER.info("   - {}", test.getStructurePath()));
			} else {
				LOGGER.info("All {} required tests passed :)", this.testSet.getTestCount());
			}

			if (this.testSet.hasFailedOptionalTests()) {
				LOGGER.info("{} optional tests failed", this.testSet.getFailedOptionalTestCount());
				this.testSet.getOptionalTests().forEach(test -> LOGGER.info("   - {}", test.getStructurePath()));
			}

			LOGGER.info("====================================================");
		}
	}

	@Override
	public SystemDetails addExtraSystemDetails(SystemDetails details) {
		details.addSection("Type", "Game test server");
		return details;
	}

	@Override
	public void exit() {
		super.exit();
		System.exit(this.testSet.getFailedRequiredTestCount());
	}

	@Override
	public void setCrashReport(CrashReport report) {
		System.exit(1);
	}

	private void runTestBatches(ServerWorld world) {
		Collection<GameTestState> collection = TestUtil.runTestBatches(this.batches, new BlockPos(0, -60, 0), BlockRotation.NONE, world, TestManager.INSTANCE, 8);
		this.testSet = new TestSet(collection);
		LOGGER.info("{} tests are now running!", this.testSet.getTestCount());
	}

	private boolean isTesting() {
		return this.testSet != null;
	}

	@Override
	public boolean isHardcore() {
		return false;
	}

	@Override
	public int getOpPermissionLevel() {
		return 0;
	}

	@Override
	public int getFunctionPermissionLevel() {
		return 4;
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		return false;
	}

	@Override
	public boolean isDedicated() {
		return false;
	}

	@Override
	public int getRateLimit() {
		return 0;
	}

	@Override
	public boolean isUsingNativeTransport() {
		return false;
	}

	@Override
	public boolean areCommandBlocksEnabled() {
		return true;
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return false;
	}

	@Override
	public boolean isHost(GameProfile profile) {
		return false;
	}
}
