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
import net.minecraft.class_2876;
import net.minecraft.class_3033;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManagerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonFight {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Predicate<Entity> field_13113 = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maximumDistance(0.0, 128.0, 0.0, 192.0));
	private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(
			new TranslatableTextComponent("entity.minecraft.ender_dragon"), BossBar.Color.field_5788, BossBar.Overlay.field_5795
		)
		.setDragonMusic(true)
		.setThickenFog(true);
	private final ServerWorld world;
	private final List<Integer> gateways = Lists.<Integer>newArrayList();
	private final BlockPattern field_13110;
	private int field_13107;
	private int field_13106;
	private int field_13105;
	private int field_13122;
	private boolean dragonKilled;
	private boolean previouslyKilled;
	private UUID dragonUuid;
	private boolean field_13111 = true;
	private BlockPos exitPortalLocation;
	private class_2876 field_13120;
	private int field_13118;
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
				this.field_13120 = class_2876.field_13097;
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

		this.field_13110 = BlockPatternBuilder.start()
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ")
			.aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ")
			.where('#', BlockProxy.method_11678(BlockPredicate.make(Blocks.field_9987)))
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
			listTag.add((Tag)(new IntTag(i)));
		}

		compoundTag.put("Gateways", listTag);
		return compoundTag;
	}

	public void method_12538() {
		this.bossBar.setVisible(!this.dragonKilled);
		if (++this.field_13122 >= 20) {
			this.method_12520();
			this.field_13122 = 0;
		}

		EnderDragonFight.class_2883 lv = new EnderDragonFight.class_2883();
		if (!this.bossBar.method_14092().isEmpty()) {
			if (this.field_13111 && lv.method_12539()) {
				this.convertFromLegacy();
				this.field_13111 = false;
			}

			if (this.field_13120 != null) {
				if (this.crystals == null && lv.method_12539()) {
					this.field_13120 = null;
					this.method_12522();
				}

				this.field_13120.method_12507(this.world, this, this.crystals, this.field_13118++, this.exitPortalLocation);
			}

			if (!this.dragonKilled) {
				if ((this.dragonUuid == null || ++this.field_13107 >= 1200) && lv.method_12539()) {
					this.method_12525();
					this.field_13107 = 0;
				}

				if (++this.field_13105 >= 100 && lv.method_12539()) {
					this.method_12535();
					this.field_13105 = 0;
				}
			}
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
			this.method_12518(false);
		}

		List<EnderDragonEntity> list = this.world.getEntities(EnderDragonEntity.class, EntityPredicates.VALID_ENTITY);
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

	private void method_12525() {
		List<EnderDragonEntity> list = this.world.getEntities(EnderDragonEntity.class, EntityPredicates.VALID_ENTITY);
		if (list.isEmpty()) {
			LOGGER.debug("Haven't seen the dragon, respawning it");
			this.createDragon();
		} else {
			LOGGER.debug("Haven't seen our dragon, but found another one to use.");
			this.dragonUuid = ((EnderDragonEntity)list.get(0)).getUuid();
		}
	}

	public void method_12521(class_2876 arg) {
		if (this.field_13120 == null) {
			throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
		} else {
			this.field_13118 = 0;
			if (arg == class_2876.field_13099) {
				this.field_13120 = null;
				this.dragonKilled = false;
				EnderDragonEntity enderDragonEntity = this.createDragon();

				for (ServerPlayerEntity serverPlayerEntity : this.bossBar.method_14092()) {
					CriterionCriterions.SUMMONED_ENTITY.handle(serverPlayerEntity, enderDragonEntity);
				}
			} else {
				this.field_13120 = arg;
			}
		}
	}

	private boolean worldContainsEndPortal() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				WorldChunk worldChunk = this.world.getChunk(i, j);

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
	private BlockPattern.Result method_12531() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				WorldChunk worldChunk = this.world.getChunk(i, j);

				for (BlockEntity blockEntity : worldChunk.getBlockEntityMap().values()) {
					if (blockEntity instanceof EndPortalBlockEntity) {
						BlockPattern.Result result = this.field_13110.searchAround(this.world, blockEntity.getPos());
						if (result != null) {
							BlockPos blockPos = result.translate(3, 3, 3).getPos();
							if (this.exitPortalLocation == null && blockPos.getX() == 0 && blockPos.getZ() == 0) {
								this.exitPortalLocation = blockPos;
							}

							return result;
						}
					}
				}
			}
		}

		int i = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, class_3033.field_13600).getY();

		for (int j = i; j >= 0; j--) {
			BlockPattern.Result result2 = this.field_13110.searchAround(this.world, new BlockPos(class_3033.field_13600.getX(), j, class_3033.field_13600.getZ()));
			if (result2 != null) {
				if (this.exitPortalLocation == null) {
					this.exitPortalLocation = result2.translate(3, 3, 3).getPos();
				}

				return result2;
			}
		}

		return null;
	}

	private boolean method_12537(int i, int j, int k, int l) {
		if (this.method_12533(i, j, k, l)) {
			return true;
		} else {
			this.method_12534(i, j, k, l);
			return false;
		}
	}

	private boolean method_12533(int i, int j, int k, int l) {
		boolean bl = true;

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				ServerChunkManagerEntry.class_3194 lv = this.world.getChunk(m, n).method_12225();
				bl &= lv != null && lv.method_14014(ServerChunkManagerEntry.class_3194.field_13875);
			}
		}

		return bl;
	}

	private void method_12534(int i, int j, int k, int l) {
		for (int m = i - 1; m <= j + 1; m++) {
			this.world.getChunk(m, k - 1);
			this.world.getChunk(m, l + 1);
		}

		for (int m = k - 1; m <= l + 1; m++) {
			this.world.getChunk(i - 1, m);
			this.world.getChunk(j + 1, m);
		}
	}

	private void method_12520() {
		Set<ServerPlayerEntity> set = Sets.<ServerPlayerEntity>newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers(ServerPlayerEntity.class, field_13113)) {
			this.bossBar.method_14088(serverPlayerEntity);
			set.add(serverPlayerEntity);
		}

		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet(this.bossBar.method_14092());
		set2.removeAll(set);

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.bossBar.method_14089(serverPlayerEntity2);
		}
	}

	private void method_12535() {
		this.field_13105 = 0;
		this.field_13106 = 0;

		for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.world)) {
			this.field_13106 = this.field_13106 + this.world.getVisibleEntities(EnderCrystalEntity.class, spike.getBoundingBox()).size();
		}

		LOGGER.debug("Found {} end crystals still alive", this.field_13106);
	}

	public void dragonKilled(EnderDragonEntity enderDragonEntity) {
		if (enderDragonEntity.getUuid().equals(this.dragonUuid)) {
			this.bossBar.setPercent(0.0F);
			this.bossBar.setVisible(false);
			this.method_12518(true);
			this.method_12519();
			if (!this.previouslyKilled) {
				this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, class_3033.field_13600), Blocks.field_10081.getDefaultState());
			}

			this.previouslyKilled = true;
			this.dragonKilled = true;
		}
	}

	private void method_12519() {
		if (!this.gateways.isEmpty()) {
			int i = (Integer)this.gateways.remove(this.gateways.size() - 1);
			int j = (int)(96.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			int k = (int)(96.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			this.method_12516(new BlockPos(j, 75, k));
		}
	}

	private void method_12516(BlockPos blockPos) {
		this.world.fireWorldEvent(3000, blockPos, 0);
		Feature.field_13564
			.generate(
				this.world,
				(ChunkGenerator<? extends ChunkGeneratorSettings>)this.world.getChunkManager().getChunkGenerator(),
				new Random(),
				blockPos,
				new EndGatewayFeatureConfig(false)
			);
	}

	private void method_12518(boolean bl) {
		class_3033 lv = new class_3033(bl);
		if (this.exitPortalLocation == null) {
			this.exitPortalLocation = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, class_3033.field_13600).down();

			while (this.world.getBlockState(this.exitPortalLocation).getBlock() == Blocks.field_9987 && this.exitPortalLocation.getY() > this.world.getSeaLevel()) {
				this.exitPortalLocation = this.exitPortalLocation.down();
			}
		}

		lv.method_13163(
			this.world,
			(ChunkGenerator<? extends ChunkGeneratorSettings>)this.world.getChunkManager().getChunkGenerator(),
			new Random(),
			this.exitPortalLocation,
			FeatureConfig.DEFAULT
		);
	}

	private EnderDragonEntity createDragon() {
		this.world.getChunk(new BlockPos(0, 128, 0));
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
			this.field_13107 = 0;
			if (enderDragonEntity.hasCustomName()) {
				this.bossBar.setName(enderDragonEntity.getDisplayName());
			}
		}
	}

	public int method_12517() {
		return this.field_13106;
	}

	public void crystalDestroyed(EnderCrystalEntity enderCrystalEntity, DamageSource damageSource) {
		if (this.field_13120 != null && this.crystals.contains(enderCrystalEntity)) {
			LOGGER.debug("Aborting respawn sequence");
			this.field_13120 = null;
			this.field_13118 = 0;
			this.method_12524();
			this.method_12518(true);
		} else {
			this.method_12535();
			Entity entity = this.world.getEntityByUuid(this.dragonUuid);
			if (entity instanceof EnderDragonEntity) {
				((EnderDragonEntity)entity).crystalDestroyed(enderCrystalEntity, new BlockPos(enderCrystalEntity), damageSource);
			}
		}
	}

	public boolean method_12536() {
		return this.previouslyKilled;
	}

	public void method_12522() {
		if (this.dragonKilled && this.field_13120 == null) {
			BlockPos blockPos = this.exitPortalLocation;
			if (blockPos == null) {
				LOGGER.debug("Tried to respawn, but need to find the portal first.");
				BlockPattern.Result result = this.method_12531();
				if (result == null) {
					LOGGER.debug("Couldn't find a portal, so we made one.");
					this.method_12518(true);
				} else {
					LOGGER.debug("Found the exit portal & temporarily using it.");
				}

				blockPos = this.exitPortalLocation;
			}

			List<EnderCrystalEntity> list = Lists.<EnderCrystalEntity>newArrayList();
			BlockPos blockPos2 = blockPos.up(1);

			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				List<EnderCrystalEntity> list2 = this.world.getVisibleEntities(EnderCrystalEntity.class, new BoundingBox(blockPos2.method_10079(direction, 2)));
				if (list2.isEmpty()) {
					return;
				}

				list.addAll(list2);
			}

			LOGGER.debug("Found all crystals, respawning dragon.");
			this.method_12529(list);
		}
	}

	private void method_12529(List<EnderCrystalEntity> list) {
		if (this.dragonKilled && this.field_13120 == null) {
			for (BlockPattern.Result result = this.method_12531(); result != null; result = this.method_12531()) {
				for (int i = 0; i < this.field_13110.getWidth(); i++) {
					for (int j = 0; j < this.field_13110.getHeight(); j++) {
						for (int k = 0; k < this.field_13110.getDepth(); k++) {
							BlockProxy blockProxy = result.translate(i, j, k);
							if (blockProxy.getBlockState().getBlock() == Blocks.field_9987 || blockProxy.getBlockState().getBlock() == Blocks.field_10027) {
								this.world.setBlockState(blockProxy.getPos(), Blocks.field_10471.getDefaultState());
							}
						}
					}
				}
			}

			this.field_13120 = class_2876.field_13097;
			this.field_13118 = 0;
			this.method_12518(false);
			this.crystals = list;
		}
	}

	public void method_12524() {
		for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.world)) {
			for (EnderCrystalEntity enderCrystalEntity : this.world.getVisibleEntities(EnderCrystalEntity.class, spike.getBoundingBox())) {
				enderCrystalEntity.setInvulnerable(false);
				enderCrystalEntity.setBeamTarget(null);
			}
		}
	}

	static enum LoadState {
		field_13125,
		field_13123,
		field_13124;
	}

	class class_2883 {
		private EnderDragonFight.LoadState loadState = EnderDragonFight.LoadState.field_13125;

		private class_2883() {
		}

		private boolean method_12539() {
			if (this.loadState == EnderDragonFight.LoadState.field_13125) {
				this.loadState = EnderDragonFight.this.method_12537(-8, 8, -8, 8) ? EnderDragonFight.LoadState.field_13124 : EnderDragonFight.LoadState.field_13123;
			}

			return this.loadState == EnderDragonFight.LoadState.field_13124;
		}
	}
}
