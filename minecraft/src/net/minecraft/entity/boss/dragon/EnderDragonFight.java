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
	private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(
			new TranslatableTextComponent("entity.minecraft.ender_dragon"), BossBar.Color.field_5788, BossBar.Overlay.field_5795
		)
		.setDragonMusic(true)
		.setThickenFog(true);
	private final ServerWorld world;
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
	private BlockPos exitPortalLocation;
	private EnderDragonSpawnState dragonSpawnState;
	private int spawnStateTimer;
	private List<EnderCrystalEntity> crystals;

	public EnderDragonFight(ServerWorld serverWorld, CompoundTag compoundTag) {
		this.world = serverWorld;
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
				this.exitPortalLocation = TagHelper.deserializeBlockPos(compoundTag.getCompound("ExitPortalLocation"));
			}
		} else {
			this.dragonKilled = true;
			this.previouslyKilled = true;
		}

		if (compoundTag.containsKey("Gateways", 9)) {
			ListTag listTag = compoundTag.getList("Gateways", 3);

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

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		if (this.dragonUuid != null) {
			compoundTag.putUuid("DragonUUID", this.dragonUuid);
		}

		compoundTag.putBoolean("DragonKilled", this.dragonKilled);
		compoundTag.putBoolean("PreviouslyKilled", this.previouslyKilled);
		if (this.exitPortalLocation != null) {
			compoundTag.put("ExitPortalLocation", TagHelper.serializeBlockPos(this.exitPortalLocation));
		}

		ListTag listTag = new ListTag();

		for (int i : this.gateways) {
			listTag.add(new IntTag(i));
		}

		compoundTag.put("Gateways", listTag);
		return compoundTag;
	}

	public void tick() {
		this.bossBar.setVisible(!this.dragonKilled);
		if (++this.playerUpdateTimer >= 20) {
			this.updatePlayers();
			this.playerUpdateTimer = 0;
		}

		if (!this.bossBar.getPlayers().isEmpty()) {
			this.world.getChunkManager().addTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 8, Void.INSTANCE);
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

				this.dragonSpawnState.run(this.world, this, this.crystals, this.spawnStateTimer++, this.exitPortalLocation);
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
			this.world.getChunkManager().removeTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 8, Void.INSTANCE);
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

		List<EnderDragonEntity> list = this.world.method_18205(EnderDragonEntity.class, EntityPredicates.VALID_ENTITY);
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
		List<EnderDragonEntity> list = this.world.method_18205(EnderDragonEntity.class, EntityPredicates.VALID_ENTITY);
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

				for (ServerPlayerEntity serverPlayerEntity : this.bossBar.getPlayers()) {
					Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity, enderDragonEntity);
				}
			} else {
				this.dragonSpawnState = enderDragonSpawnState;
			}
		}
	}

	private boolean worldContainsEndPortal() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				WorldChunk worldChunk = this.world.getWorldChunk(i, j);

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
				WorldChunk worldChunk = this.world.getWorldChunk(i, j);

				for (BlockEntity blockEntity : worldChunk.getBlockEntityMap().values()) {
					if (blockEntity instanceof EndPortalBlockEntity) {
						BlockPattern.Result result = this.endPortalPattern.searchAround(this.world, blockEntity.getPos());
						if (result != null) {
							BlockPos blockPos = result.translate(3, 3, 3).getBlockPos();
							if (this.exitPortalLocation == null && blockPos.getX() == 0 && blockPos.getZ() == 0) {
								this.exitPortalLocation = blockPos;
							}

							return result;
						}
					}
				}
			}
		}

		int i = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN).getY();

		for (int j = i; j >= 0; j--) {
			BlockPattern.Result result2 = this.endPortalPattern
				.searchAround(this.world, new BlockPos(EndPortalFeature.ORIGIN.getX(), j, EndPortalFeature.ORIGIN.getZ()));
			if (result2 != null) {
				if (this.exitPortalLocation == null) {
					this.exitPortalLocation = result2.translate(3, 3, 3).getBlockPos();
				}

				return result2;
			}
		}

		return null;
	}

	private boolean loadChunks() {
		for (int i = -8; i <= 8; i++) {
			for (int j = 8; j <= 8; j++) {
				Chunk chunk = this.world.getChunk(i, j, ChunkStatus.FULL, false);
				if (!(chunk instanceof WorldChunk)) {
					return false;
				}

				ChunkHolder.LevelType levelType = ((WorldChunk)chunk).getLevelType();
				if (!levelType.isAfter(ChunkHolder.LevelType.TICKING)) {
					return false;
				}
			}
		}

		return true;
	}

	private void updatePlayers() {
		Set<ServerPlayerEntity> set = Sets.<ServerPlayerEntity>newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers(ServerPlayerEntity.class, VALID_ENTITY)) {
			this.bossBar.addPlayer(serverPlayerEntity);
			set.add(serverPlayerEntity);
		}

		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet(this.bossBar.getPlayers());
		set2.removeAll(set);

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.bossBar.removePlayer(serverPlayerEntity2);
		}
	}

	private void countAliveCrystals() {
		this.crystalCountTimer = 0;
		this.endCrystalsAlive = 0;

		for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.world)) {
			this.endCrystalsAlive = this.endCrystalsAlive + this.world.getVisibleEntities(EnderCrystalEntity.class, spike.getBoundingBox()).size();
		}

		LOGGER.debug("Found {} end crystals still alive", this.endCrystalsAlive);
	}

	public void dragonKilled(EnderDragonEntity enderDragonEntity) {
		if (enderDragonEntity.getUuid().equals(this.dragonUuid)) {
			this.bossBar.setPercent(0.0F);
			this.bossBar.setVisible(false);
			this.generateEndPortal(true);
			this.generateNewEndGateway();
			if (!this.previouslyKilled) {
				this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN), Blocks.field_10081.getDefaultState());
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
			this.generateEndGateway(new BlockPos(j, 75, k));
		}
	}

	private void generateEndGateway(BlockPos blockPos) {
		this.world.playEvent(3000, blockPos, 0);
		Feature.field_13564
			.generate(
				this.world,
				(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.getChunkManager().getChunkGenerator(),
				new Random(),
				blockPos,
				EndGatewayFeatureConfig.createConfig()
			);
	}

	private void generateEndPortal(boolean bl) {
		EndPortalFeature endPortalFeature = new EndPortalFeature(bl);
		if (this.exitPortalLocation == null) {
			this.exitPortalLocation = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN).down();

			while (this.world.getBlockState(this.exitPortalLocation).getBlock() == Blocks.field_9987 && this.exitPortalLocation.getY() > this.world.getSeaLevel()) {
				this.exitPortalLocation = this.exitPortalLocation.down();
			}
		}

		endPortalFeature.method_13163(
			this.world,
			(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.getChunkManager().getChunkGenerator(),
			new Random(),
			this.exitPortalLocation,
			FeatureConfig.DEFAULT
		);
	}

	private EnderDragonEntity createDragon() {
		this.world.getWorldChunk(new BlockPos(0, 128, 0));
		EnderDragonEntity enderDragonEntity = new EnderDragonEntity(this.world);
		enderDragonEntity.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
		enderDragonEntity.setPositionAndAngles(0.0, 128.0, 0.0, this.world.random.nextFloat() * 360.0F, 0.0F);
		this.world.spawnEntity(enderDragonEntity);
		this.dragonUuid = enderDragonEntity.getUuid();
		return enderDragonEntity;
	}

	public void updateFight(EnderDragonEntity enderDragonEntity) {
		if (enderDragonEntity.getUuid().equals(this.dragonUuid)) {
			this.bossBar.setPercent(enderDragonEntity.getHealth() / enderDragonEntity.getHealthMaximum());
			this.dragonSeenTimer = 0;
			if (enderDragonEntity.hasCustomName()) {
				this.bossBar.setName(enderDragonEntity.getDisplayName());
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
			Entity entity = this.world.method_14190(this.dragonUuid);
			if (entity instanceof EnderDragonEntity) {
				((EnderDragonEntity)entity).crystalDestroyed(enderCrystalEntity, new BlockPos(enderCrystalEntity), damageSource);
			}
		}
	}

	public boolean hasPreviouslyKilled() {
		return this.previouslyKilled;
	}

	public void respawnDragon() {
		if (this.dragonKilled && this.dragonSpawnState == null) {
			BlockPos blockPos = this.exitPortalLocation;
			if (blockPos == null) {
				LOGGER.debug("Tried to respawn, but need to find the portal first.");
				BlockPattern.Result result = this.findEndPortal();
				if (result == null) {
					LOGGER.debug("Couldn't find a portal, so we made one.");
					this.generateEndPortal(true);
				} else {
					LOGGER.debug("Found the exit portal & temporarily using it.");
				}

				blockPos = this.exitPortalLocation;
			}

			List<EnderCrystalEntity> list = Lists.<EnderCrystalEntity>newArrayList();
			BlockPos blockPos2 = blockPos.up(1);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				List<EnderCrystalEntity> list2 = this.world.getVisibleEntities(EnderCrystalEntity.class, new BoundingBox(blockPos2.offset(direction, 2)));
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
								this.world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.field_10471.getDefaultState());
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
		for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.world)) {
			for (EnderCrystalEntity enderCrystalEntity : this.world.getVisibleEntities(EnderCrystalEntity.class, spike.getBoundingBox())) {
				enderCrystalEntity.setInvulnerable(false);
				enderCrystalEntity.setBeamTarget(null);
			}
		}
	}
}
