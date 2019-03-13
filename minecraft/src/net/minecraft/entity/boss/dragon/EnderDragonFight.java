package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.TagHelper;
import net.minecraft.util.Void;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonFight {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Predicate<Entity> VALID_ENTITY = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maximumDistance(0.0, 128.0, 0.0, 192.0));
	private final ServerBossBar field_13119 = (ServerBossBar)new ServerBossBar(
			new TranslatableTextComponent("entity.minecraft.ender_dragon"), BossBar.Color.field_5788, BossBar.Overlay.field_5795
		)
		.setDragonMusic(true)
		.setThickenFog(true);
	private final ServerWorld field_13108;
	private final List<Integer> gateways = Lists.<Integer>newArrayList();
	private final BlockPattern endPortalPattern;
	private int dragonSeenTimer;
	private int endCrystalsAlive;
	private int crystalCountTimer;
	private int playerUpdateTimer;
	private boolean dragonKilled;
	private boolean previouslyKilled;
	private UUID dragonUuid;
	private boolean doLegacyCheck = true;
	private BlockPos field_13117;
	private EnderDragonSpawnState dragonSpawnState;
	private int spawnStateTimer;
	private List<EnderCrystalEntity> crystals;

	public EnderDragonFight(ServerWorld serverWorld, CompoundTag compoundTag) {
		this.field_13108 = serverWorld;
		if (compoundTag.containsKey("DragonKilled", 99)) {
			if (compoundTag.hasUuid("DragonUUID")) {
				this.dragonUuid = compoundTag.getUuid("DragonUUID");
			}

			this.dragonKilled = compoundTag.getBoolean("DragonKilled");
			this.previouslyKilled = compoundTag.getBoolean("PreviouslyKilled");
			if (compoundTag.getBoolean("IsRespawning")) {
				this.dragonSpawnState = EnderDragonSpawnState.START;
			}

			if (compoundTag.containsKey("ExitPortalLocation", 10)) {
				this.field_13117 = TagHelper.deserializeBlockPos(compoundTag.getCompound("ExitPortalLocation"));
			}
		} else {
			this.dragonKilled = true;
			this.previouslyKilled = true;
		}

		if (compoundTag.containsKey("Gateways", 9)) {
			ListTag listTag = compoundTag.method_10554("Gateways", 3);

			for (int i = 0; i < listTag.size(); i++) {
				this.gateways.add(listTag.getInt(i));
			}
		} else {
			this.gateways.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
			Collections.shuffle(this.gateways, new Random(serverWorld.getSeed()));
		}

		this.endPortalPattern = BlockPatternBuilder.start()
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ")
			.aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ")
			.where('#', CachedBlockPosition.matchesBlockState(BlockPredicate.make(Blocks.field_9987)))
			.build();
	}

	public CompoundTag method_12530() {
		CompoundTag compoundTag = new CompoundTag();
		if (this.dragonUuid != null) {
			compoundTag.putUuid("DragonUUID", this.dragonUuid);
		}

		compoundTag.putBoolean("DragonKilled", this.dragonKilled);
		compoundTag.putBoolean("PreviouslyKilled", this.previouslyKilled);
		if (this.field_13117 != null) {
			compoundTag.method_10566("ExitPortalLocation", TagHelper.serializeBlockPos(this.field_13117));
		}

		ListTag listTag = new ListTag();

		for (int i : this.gateways) {
			listTag.add(new IntTag(i));
		}

		compoundTag.method_10566("Gateways", listTag);
		return compoundTag;
	}

	public void tick() {
		this.field_13119.setVisible(!this.dragonKilled);
		if (++this.playerUpdateTimer >= 20) {
			this.updatePlayers();
			this.playerUpdateTimer = 0;
		}

		if (!this.field_13119.getPlayers().isEmpty()) {
			this.field_13108.method_14178().method_17297(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 8, Void.INSTANCE);
			boolean bl = this.loadChunks();
			if (this.doLegacyCheck && bl) {
				this.convertFromLegacy();
				this.doLegacyCheck = false;
			}

			if (this.dragonSpawnState != null) {
				if (this.crystals == null && bl) {
					this.dragonSpawnState = null;
					this.respawnDragon();
				}

				this.dragonSpawnState.method_12507(this.field_13108, this, this.crystals, this.spawnStateTimer++, this.field_13117);
			}

			if (!this.dragonKilled) {
				if ((this.dragonUuid == null || ++this.dragonSeenTimer >= 1200) && bl) {
					this.checkDragonSeen();
					this.dragonSeenTimer = 0;
				}

				if (++this.crystalCountTimer >= 100 && bl) {
					this.countAliveCrystals();
					this.crystalCountTimer = 0;
				}
			}
		} else {
			this.field_13108.method_14178().method_17300(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 8, Void.INSTANCE);
		}
	}

	private void convertFromLegacy() {
		LOGGER.info("Scanning for legacy world dragon fight...");
		boolean bl = this.worldContainsEndPortal();
		if (bl) {
			LOGGER.info("Found that the dragon has been killed in this world already.");
			this.previouslyKilled = true;
		} else {
			LOGGER.info("Found that the dragon has not yet been killed in this world.");
			this.previouslyKilled = false;
			this.generateEndPortal(false);
		}

		List<EnderDragonEntity> list = this.field_13108.method_18776();
		if (list.isEmpty()) {
			this.dragonKilled = true;
		} else {
			EnderDragonEntity enderDragonEntity = (EnderDragonEntity)list.get(0);
			this.dragonUuid = enderDragonEntity.getUuid();
			LOGGER.info("Found that there's a dragon still alive ({})", enderDragonEntity);
			this.dragonKilled = false;
			if (!bl) {
				LOGGER.info("But we didn't have a portal, let's remove it.");
				enderDragonEntity.invalidate();
				this.dragonUuid = null;
			}
		}

		if (!this.previouslyKilled && this.dragonKilled) {
			this.dragonKilled = false;
		}
	}

	private void checkDragonSeen() {
		List<EnderDragonEntity> list = this.field_13108.method_18776();
		if (list.isEmpty()) {
			LOGGER.debug("Haven't seen the dragon, respawning it");
			this.createDragon();
		} else {
			LOGGER.debug("Haven't seen our dragon, but found another one to use.");
			this.dragonUuid = ((EnderDragonEntity)list.get(0)).getUuid();
		}
	}

	protected void setSpawnState(EnderDragonSpawnState enderDragonSpawnState) {
		if (this.dragonSpawnState == null) {
			throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
		} else {
			this.spawnStateTimer = 0;
			if (enderDragonSpawnState == EnderDragonSpawnState.END) {
				this.dragonSpawnState = null;
				this.dragonKilled = false;
				EnderDragonEntity enderDragonEntity = this.createDragon();

				for (ServerPlayerEntity serverPlayerEntity : this.field_13119.getPlayers()) {
					Criterions.SUMMONED_ENTITY.method_9124(serverPlayerEntity, enderDragonEntity);
				}
			} else {
				this.dragonSpawnState = enderDragonSpawnState;
			}
		}
	}

	private boolean worldContainsEndPortal() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				WorldChunk worldChunk = this.field_13108.method_8497(i, j);

				for (BlockEntity blockEntity : worldChunk.getBlockEntityMap().values()) {
					if (blockEntity instanceof EndPortalBlockEntity) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Nullable
	private BlockPattern.Result findEndPortal() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				WorldChunk worldChunk = this.field_13108.method_8497(i, j);

				for (BlockEntity blockEntity : worldChunk.getBlockEntityMap().values()) {
					if (blockEntity instanceof EndPortalBlockEntity) {
						BlockPattern.Result result = this.endPortalPattern.method_11708(this.field_13108, blockEntity.method_11016());
						if (result != null) {
							BlockPos blockPos = result.translate(3, 3, 3).method_11683();
							if (this.field_13117 == null && blockPos.getX() == 0 && blockPos.getZ() == 0) {
								this.field_13117 = blockPos;
							}

							return result;
						}
					}
				}
			}
		}

		int i = this.field_13108.method_8598(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.field_13600).getY();

		for (int j = i; j >= 0; j--) {
			BlockPattern.Result result2 = this.endPortalPattern
				.method_11708(this.field_13108, new BlockPos(EndPortalFeature.field_13600.getX(), j, EndPortalFeature.field_13600.getZ()));
			if (result2 != null) {
				if (this.field_13117 == null) {
					this.field_13117 = result2.translate(3, 3, 3).method_11683();
				}

				return result2;
			}
		}

		return null;
	}

	private boolean loadChunks() {
		for (int i = -8; i <= 8; i++) {
			for (int j = 8; j <= 8; j++) {
				Chunk chunk = this.field_13108.method_8402(i, j, ChunkStatus.FULL, false);
				if (!(chunk instanceof WorldChunk)) {
					return false;
				}

				ChunkHolder.LevelType levelType = ((WorldChunk)chunk).method_12225();
				if (!levelType.isAfter(ChunkHolder.LevelType.TICKING)) {
					return false;
				}
			}
		}

		return true;
	}

	private void updatePlayers() {
		Set<ServerPlayerEntity> set = Sets.<ServerPlayerEntity>newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.field_13108.method_18766(VALID_ENTITY)) {
			this.field_13119.method_14088(serverPlayerEntity);
			set.add(serverPlayerEntity);
		}

		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet(this.field_13119.getPlayers());
		set2.removeAll(set);

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.field_13119.method_14089(serverPlayerEntity2);
		}
	}

	private void countAliveCrystals() {
		this.crystalCountTimer = 0;
		this.endCrystalsAlive = 0;

		for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.field_13108)) {
			this.endCrystalsAlive = this.endCrystalsAlive + this.field_13108.method_18467(EnderCrystalEntity.class, spike.method_13968()).size();
		}

		LOGGER.debug("Found {} end crystals still alive", this.endCrystalsAlive);
	}

	public void dragonKilled(EnderDragonEntity enderDragonEntity) {
		if (enderDragonEntity.getUuid().equals(this.dragonUuid)) {
			this.field_13119.setPercent(0.0F);
			this.field_13119.setVisible(false);
			this.generateEndPortal(true);
			this.generateNewEndGateway();
			if (!this.previouslyKilled) {
				this.field_13108.method_8501(this.field_13108.method_8598(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.field_13600), Blocks.field_10081.method_9564());
			}

			this.previouslyKilled = true;
			this.dragonKilled = true;
		}
	}

	private void generateNewEndGateway() {
		if (!this.gateways.isEmpty()) {
			int i = (Integer)this.gateways.remove(this.gateways.size() - 1);
			int j = (int)(96.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			int k = (int)(96.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			this.method_12516(new BlockPos(j, 75, k));
		}
	}

	private void method_12516(BlockPos blockPos) {
		this.field_13108.method_8535(3000, blockPos, 0);
		Feature.field_13564
			.method_13151(
				this.field_13108,
				(ChunkGenerator<? extends ChunkGeneratorConfig>)this.field_13108.method_14178().getChunkGenerator(),
				new Random(),
				blockPos,
				EndGatewayFeatureConfig.createConfig()
			);
	}

	private void generateEndPortal(boolean bl) {
		EndPortalFeature endPortalFeature = new EndPortalFeature(bl);
		if (this.field_13117 == null) {
			this.field_13117 = this.field_13108.method_8598(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.field_13600).down();

			while (this.field_13108.method_8320(this.field_13117).getBlock() == Blocks.field_9987 && this.field_13117.getY() > this.field_13108.getSeaLevel()) {
				this.field_13117 = this.field_13117.down();
			}
		}

		endPortalFeature.method_13163(
			this.field_13108,
			(ChunkGenerator<? extends ChunkGeneratorConfig>)this.field_13108.method_14178().getChunkGenerator(),
			new Random(),
			this.field_13117,
			FeatureConfig.field_13603
		);
	}

	private EnderDragonEntity createDragon() {
		this.field_13108.method_8500(new BlockPos(0, 128, 0));
		EnderDragonEntity enderDragonEntity = EntityType.ENDER_DRAGON.method_5883(this.field_13108);
		enderDragonEntity.method_6831().setPhase(PhaseType.HOLDING_PATTERN);
		enderDragonEntity.setPositionAndAngles(0.0, 128.0, 0.0, this.field_13108.random.nextFloat() * 360.0F, 0.0F);
		this.field_13108.spawnEntity(enderDragonEntity);
		this.dragonUuid = enderDragonEntity.getUuid();
		return enderDragonEntity;
	}

	public void updateFight(EnderDragonEntity enderDragonEntity) {
		if (enderDragonEntity.getUuid().equals(this.dragonUuid)) {
			this.field_13119.setPercent(enderDragonEntity.getHealth() / enderDragonEntity.getHealthMaximum());
			this.dragonSeenTimer = 0;
			if (enderDragonEntity.hasCustomName()) {
				this.field_13119.method_5413(enderDragonEntity.method_5476());
			}
		}
	}

	public int getAliveEndCrystals() {
		return this.endCrystalsAlive;
	}

	public void crystalDestroyed(EnderCrystalEntity enderCrystalEntity, DamageSource damageSource) {
		if (this.dragonSpawnState != null && this.crystals.contains(enderCrystalEntity)) {
			LOGGER.debug("Aborting respawn sequence");
			this.dragonSpawnState = null;
			this.spawnStateTimer = 0;
			this.resetEndCrystals();
			this.generateEndPortal(true);
		} else {
			this.countAliveCrystals();
			Entity entity = this.field_13108.getEntity(this.dragonUuid);
			if (entity instanceof EnderDragonEntity) {
				((EnderDragonEntity)entity).method_6828(enderCrystalEntity, new BlockPos(enderCrystalEntity), damageSource);
			}
		}
	}

	public boolean hasPreviouslyKilled() {
		return this.previouslyKilled;
	}

	public void respawnDragon() {
		if (this.dragonKilled && this.dragonSpawnState == null) {
			BlockPos blockPos = this.field_13117;
			if (blockPos == null) {
				LOGGER.debug("Tried to respawn, but need to find the portal first.");
				BlockPattern.Result result = this.findEndPortal();
				if (result == null) {
					LOGGER.debug("Couldn't find a portal, so we made one.");
					this.generateEndPortal(true);
				} else {
					LOGGER.debug("Found the exit portal & temporarily using it.");
				}

				blockPos = this.field_13117;
			}

			List<EnderCrystalEntity> list = Lists.<EnderCrystalEntity>newArrayList();
			BlockPos blockPos2 = blockPos.up(1);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				List<EnderCrystalEntity> list2 = this.field_13108.method_18467(EnderCrystalEntity.class, new BoundingBox(blockPos2.method_10079(direction, 2)));
				if (list2.isEmpty()) {
					return;
				}

				list.addAll(list2);
			}

			LOGGER.debug("Found all crystals, respawning dragon.");
			this.respawnDragon(list);
		}
	}

	private void respawnDragon(List<EnderCrystalEntity> list) {
		if (this.dragonKilled && this.dragonSpawnState == null) {
			for (BlockPattern.Result result = this.findEndPortal(); result != null; result = this.findEndPortal()) {
				for (int i = 0; i < this.endPortalPattern.getWidth(); i++) {
					for (int j = 0; j < this.endPortalPattern.getHeight(); j++) {
						for (int k = 0; k < this.endPortalPattern.getDepth(); k++) {
							CachedBlockPosition cachedBlockPosition = result.translate(i, j, k);
							if (cachedBlockPosition.getBlockState().getBlock() == Blocks.field_9987 || cachedBlockPosition.getBlockState().getBlock() == Blocks.field_10027) {
								this.field_13108.method_8501(cachedBlockPosition.method_11683(), Blocks.field_10471.method_9564());
							}
						}
					}
				}
			}

			this.dragonSpawnState = EnderDragonSpawnState.START;
			this.spawnStateTimer = 0;
			this.generateEndPortal(false);
			this.crystals = list;
		}
	}

	public void resetEndCrystals() {
		for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.field_13108)) {
			for (EnderCrystalEntity enderCrystalEntity : this.field_13108.method_18467(EnderCrystalEntity.class, spike.method_13968())) {
				enderCrystalEntity.setInvulnerable(false);
				enderCrystalEntity.method_6837(null);
			}
		}
	}
}
