package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.EndConfiguredFeatures;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.slf4j.Logger;

public class EndGatewayBlockEntity extends EndPortalBlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_31368 = 200;
	private static final int field_31369 = 40;
	private static final int field_31370 = 2400;
	private static final int field_31371 = 1;
	private static final int field_31372 = 10;
	private long age;
	private int teleportCooldown;
	@Nullable
	private BlockPos exitPortalPos;
	private boolean exactTeleport;

	public EndGatewayBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.END_GATEWAY, blockPos, blockState);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putLong("Age", this.age);
		if (this.exitPortalPos != null) {
			nbt.put("ExitPortal", NbtHelper.fromBlockPos(this.exitPortalPos));
		}

		if (this.exactTeleport) {
			nbt.putBoolean("ExactTeleport", true);
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.age = nbt.getLong("Age");
		if (nbt.contains("ExitPortal", NbtElement.COMPOUND_TYPE)) {
			BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("ExitPortal"));
			if (World.isValid(blockPos)) {
				this.exitPortalPos = blockPos;
			}
		}

		this.exactTeleport = nbt.getBoolean("ExactTeleport");
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, EndGatewayBlockEntity blockEntity) {
		blockEntity.age++;
		if (blockEntity.needsCooldownBeforeTeleporting()) {
			blockEntity.teleportCooldown--;
		}
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, EndGatewayBlockEntity blockEntity) {
		boolean bl = blockEntity.isRecentlyGenerated();
		boolean bl2 = blockEntity.needsCooldownBeforeTeleporting();
		blockEntity.age++;
		if (bl2) {
			blockEntity.teleportCooldown--;
		} else {
			List<Entity> list = world.getEntitiesByClass(Entity.class, new Box(pos), EndGatewayBlockEntity::canTeleport);
			if (!list.isEmpty()) {
				tryTeleportingEntity(world, pos, state, (Entity)list.get(world.random.nextInt(list.size())), blockEntity);
			}

			if (blockEntity.age % 2400L == 0L) {
				startTeleportCooldown(world, pos, state, blockEntity);
			}
		}

		if (bl != blockEntity.isRecentlyGenerated() || bl2 != blockEntity.needsCooldownBeforeTeleporting()) {
			markDirty(world, pos, state);
		}
	}

	public static boolean canTeleport(Entity entity) {
		return EntityPredicates.EXCEPT_SPECTATOR.test(entity) && !entity.getRootVehicle().hasPortalCooldown();
	}

	public boolean isRecentlyGenerated() {
		return this.age < 200L;
	}

	public boolean needsCooldownBeforeTeleporting() {
		return this.teleportCooldown > 0;
	}

	public float getRecentlyGeneratedBeamHeight(float tickDelta) {
		return MathHelper.clamp(((float)this.age + tickDelta) / 200.0F, 0.0F, 1.0F);
	}

	public float getCooldownBeamHeight(float tickDelta) {
		return 1.0F - MathHelper.clamp(((float)this.teleportCooldown - tickDelta) / 40.0F, 0.0F, 1.0F);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}

	private static void startTeleportCooldown(World world, BlockPos pos, BlockState state, EndGatewayBlockEntity blockEntity) {
		if (!world.isClient) {
			blockEntity.teleportCooldown = 40;
			world.addSyncedBlockEvent(pos, state.getBlock(), 1, 0);
			markDirty(world, pos, state);
		}
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			this.teleportCooldown = 40;
			return true;
		} else {
			return super.onSyncedBlockEvent(type, data);
		}
	}

	public static void tryTeleportingEntity(World world, BlockPos pos, BlockState state, Entity entity, EndGatewayBlockEntity blockEntity) {
		if (world instanceof ServerWorld && !blockEntity.needsCooldownBeforeTeleporting()) {
			ServerWorld serverWorld = (ServerWorld)world;
			blockEntity.teleportCooldown = 100;
			if (blockEntity.exitPortalPos == null && world.getRegistryKey() == World.END) {
				BlockPos blockPos = setupExitPortalLocation(serverWorld, pos);
				blockPos = blockPos.up(10);
				LOGGER.debug("Creating portal at {}", blockPos);
				createPortal(serverWorld, blockPos, EndGatewayFeatureConfig.createConfig(pos, false));
				blockEntity.exitPortalPos = blockPos;
			}

			if (blockEntity.exitPortalPos != null) {
				BlockPos blockPos = blockEntity.exactTeleport ? blockEntity.exitPortalPos : findBestPortalExitPos(world, blockEntity.exitPortalPos);
				Entity entity3;
				if (entity instanceof EnderPearlEntity) {
					Entity entity2 = ((EnderPearlEntity)entity).getOwner();
					if (entity2 instanceof ServerPlayerEntity) {
						Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity)entity2, state);
					}

					if (entity2 != null) {
						entity3 = entity2;
						entity.discard();
					} else {
						entity3 = entity;
					}
				} else {
					entity3 = entity.getRootVehicle();
				}

				entity3.resetPortalCooldown();
				entity3.teleport((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5);
			}

			startTeleportCooldown(world, pos, state, blockEntity);
		}
	}

	private static BlockPos findBestPortalExitPos(World world, BlockPos pos) {
		BlockPos blockPos = findExitPortalPos(world, pos.add(0, 2, 0), 5, false);
		LOGGER.debug("Best exit position for portal at {} is {}", pos, blockPos);
		return blockPos.up();
	}

	/**
	 * Finds teleport location and creates an island to teleport to (if there is none).
	 * 
	 * <p>This does not create an exit portal.
	 * 
	 * @return the position of the exit portal
	 */
	private static BlockPos setupExitPortalLocation(ServerWorld world, BlockPos pos) {
		Vec3d vec3d = findTeleportLocation(world, pos);
		WorldChunk worldChunk = getChunk(world, vec3d);
		BlockPos blockPos = findPortalPosition(worldChunk);
		if (blockPos == null) {
			BlockPos blockPos2 = BlockPos.ofFloored(vec3d.x + 0.5, 75.0, vec3d.z + 0.5);
			LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", blockPos2);
			world.getRegistryManager()
				.getOptional(RegistryKeys.CONFIGURED_FEATURE)
				.flatMap(registry -> registry.getEntry(EndConfiguredFeatures.END_ISLAND))
				.ifPresent(
					reference -> ((ConfiguredFeature)reference.value())
							.generate(world, world.getChunkManager().getChunkGenerator(), Random.create(blockPos2.asLong()), blockPos2)
				);
			blockPos = blockPos2;
		} else {
			LOGGER.debug("Found suitable block to teleport to: {}", blockPos);
		}

		return findExitPortalPos(world, blockPos, 16, true);
	}

	private static Vec3d findTeleportLocation(ServerWorld world, BlockPos pos) {
		Vec3d vec3d = new Vec3d((double)pos.getX(), 0.0, (double)pos.getZ()).normalize();
		int i = 1024;
		Vec3d vec3d2 = vec3d.multiply(1024.0);

		for (int j = 16; !isChunkEmpty(world, vec3d2) && j-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(-16.0))) {
			LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d2);
		}

		for (int var6 = 16; isChunkEmpty(world, vec3d2) && var6-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(16.0))) {
			LOGGER.debug("Skipping forward past empty chunk at {}", vec3d2);
		}

		LOGGER.debug("Found chunk at {}", vec3d2);
		return vec3d2;
	}

	private static boolean isChunkEmpty(ServerWorld world, Vec3d pos) {
		return getChunk(world, pos).getHighestNonEmptySectionYOffset() <= world.getBottomY();
	}

	private static BlockPos findExitPortalPos(BlockView world, BlockPos pos, int searchRadius, boolean force) {
		BlockPos blockPos = null;

		for (int i = -searchRadius; i <= searchRadius; i++) {
			for (int j = -searchRadius; j <= searchRadius; j++) {
				if (i != 0 || j != 0 || force) {
					for (int k = world.getTopY() - 1; k > (blockPos == null ? world.getBottomY() : blockPos.getY()); k--) {
						BlockPos blockPos2 = new BlockPos(pos.getX() + i, k, pos.getZ() + j);
						BlockState blockState = world.getBlockState(blockPos2);
						if (blockState.isFullCube(world, blockPos2) && (force || !blockState.isOf(Blocks.BEDROCK))) {
							blockPos = blockPos2;
							break;
						}
					}
				}
			}
		}

		return blockPos == null ? pos : blockPos;
	}

	private static WorldChunk getChunk(World world, Vec3d pos) {
		return world.getChunk(MathHelper.floor(pos.x / 16.0), MathHelper.floor(pos.z / 16.0));
	}

	@Nullable
	private static BlockPos findPortalPosition(WorldChunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 30, chunkPos.getStartZ());
		int i = chunk.getHighestNonEmptySectionYOffset() + 16 - 1;
		BlockPos blockPos2 = new BlockPos(chunkPos.getEndX(), i, chunkPos.getEndZ());
		BlockPos blockPos3 = null;
		double d = 0.0;

		for (BlockPos blockPos4 : BlockPos.iterate(blockPos, blockPos2)) {
			BlockState blockState = chunk.getBlockState(blockPos4);
			BlockPos blockPos5 = blockPos4.up();
			BlockPos blockPos6 = blockPos4.up(2);
			if (blockState.isOf(Blocks.END_STONE)
				&& !chunk.getBlockState(blockPos5).isFullCube(chunk, blockPos5)
				&& !chunk.getBlockState(blockPos6).isFullCube(chunk, blockPos6)) {
				double e = blockPos4.getSquaredDistanceFromCenter(0.0, 0.0, 0.0);
				if (blockPos3 == null || e < d) {
					blockPos3 = blockPos4;
					d = e;
				}
			}
		}

		return blockPos3;
	}

	private static void createPortal(ServerWorld world, BlockPos pos, EndGatewayFeatureConfig config) {
		Feature.END_GATEWAY.generateIfValid(config, world, world.getChunkManager().getChunkGenerator(), Random.create(), pos);
	}

	@Override
	public boolean shouldDrawSide(Direction direction) {
		return Block.shouldDrawSide(this.getCachedState(), this.world, this.getPos(), direction, this.getPos().offset(direction));
	}

	public int getDrawnSidesCount() {
		int i = 0;

		for (Direction direction : Direction.values()) {
			i += this.shouldDrawSide(direction) ? 1 : 0;
		}

		return i;
	}

	public void setExitPortalPos(BlockPos pos, boolean exactTeleport) {
		this.exactTeleport = exactTeleport;
		this.exitPortalPos = pos;
	}
}
