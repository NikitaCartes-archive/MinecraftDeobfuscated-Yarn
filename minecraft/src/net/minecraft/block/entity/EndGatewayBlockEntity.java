package net.minecraft.block.entity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndGatewayBlockEntity extends EndPortalBlockEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	private long age;
	private int teleportCooldown;
	@Nullable
	private BlockPos exitPortalPos;
	private boolean exactTeleport;

	public EndGatewayBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.END_GATEWAY, blockPos, blockState);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putLong("Age", this.age);
		if (this.exitPortalPos != null) {
			tag.put("ExitPortal", NbtHelper.fromBlockPos(this.exitPortalPos));
		}

		if (this.exactTeleport) {
			tag.putBoolean("ExactTeleport", this.exactTeleport);
		}

		return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.age = tag.getLong("Age");
		if (tag.contains("ExitPortal", 10)) {
			this.exitPortalPos = NbtHelper.toBlockPos(tag.getCompound("ExitPortal"));
		}

		this.exactTeleport = tag.getBoolean("ExactTeleport");
	}

	@Environment(EnvType.CLIENT)
	@Override
	public double getSquaredRenderDistance() {
		return 256.0;
	}

	public static void clientTick(World world, BlockPos blockPos, BlockState blockState, EndGatewayBlockEntity endGatewayBlockEntity) {
		endGatewayBlockEntity.age++;
		if (endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
			endGatewayBlockEntity.teleportCooldown--;
		}
	}

	public static void serverTick(World world, BlockPos blockPos, BlockState blockState, EndGatewayBlockEntity endGatewayBlockEntity) {
		boolean bl = endGatewayBlockEntity.isRecentlyGenerated();
		boolean bl2 = endGatewayBlockEntity.needsCooldownBeforeTeleporting();
		endGatewayBlockEntity.age++;
		if (bl2) {
			endGatewayBlockEntity.teleportCooldown--;
		} else {
			List<Entity> list = world.getEntitiesByClass(Entity.class, new Box(blockPos), EndGatewayBlockEntity::method_30276);
			if (!list.isEmpty()) {
				tryTeleportingEntity(world, blockPos, blockState, (Entity)list.get(world.random.nextInt(list.size())), endGatewayBlockEntity);
			}

			if (endGatewayBlockEntity.age % 2400L == 0L) {
				startTeleportCooldown(world, blockPos, blockState, endGatewayBlockEntity);
			}
		}

		if (bl != endGatewayBlockEntity.isRecentlyGenerated() || bl2 != endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
			markDirty(world, blockPos, blockState);
		}
	}

	public static boolean method_30276(Entity entity) {
		return EntityPredicates.EXCEPT_SPECTATOR.test(entity) && !entity.getRootVehicle().hasNetherPortalCooldown();
	}

	public boolean isRecentlyGenerated() {
		return this.age < 200L;
	}

	public boolean needsCooldownBeforeTeleporting() {
		return this.teleportCooldown > 0;
	}

	@Environment(EnvType.CLIENT)
	public float getRecentlyGeneratedBeamHeight(float tickDelta) {
		return MathHelper.clamp(((float)this.age + tickDelta) / 200.0F, 0.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public float getCooldownBeamHeight(float tickDelta) {
		return 1.0F - MathHelper.clamp(((float)this.teleportCooldown - tickDelta) / 40.0F, 0.0F, 1.0F);
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 8, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	private static void startTeleportCooldown(World world, BlockPos blockPos, BlockState blockState, EndGatewayBlockEntity endGatewayBlockEntity) {
		if (!world.isClient) {
			endGatewayBlockEntity.teleportCooldown = 40;
			world.addSyncedBlockEvent(blockPos, blockState.getBlock(), 1, 0);
			markDirty(world, blockPos, blockState);
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

	public static void tryTeleportingEntity(World world, BlockPos blockPos, BlockState blockState, Entity entity, EndGatewayBlockEntity endGatewayBlockEntity) {
		if (world instanceof ServerWorld && !endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
			ServerWorld serverWorld = (ServerWorld)world;
			endGatewayBlockEntity.teleportCooldown = 100;
			if (endGatewayBlockEntity.exitPortalPos == null && world.getRegistryKey() == World.END) {
				BlockPos blockPos2 = method_31699(serverWorld, blockPos);
				blockPos2 = blockPos2.up(10);
				LOGGER.debug("Creating portal at {}", blockPos2);
				createPortal(serverWorld, blockPos2, EndGatewayFeatureConfig.createConfig(blockPos, false));
				endGatewayBlockEntity.exitPortalPos = blockPos2;
			}

			if (endGatewayBlockEntity.exitPortalPos != null) {
				BlockPos blockPos2 = endGatewayBlockEntity.exactTeleport
					? endGatewayBlockEntity.exitPortalPos
					: findBestPortalExitPos(world, endGatewayBlockEntity.exitPortalPos);
				Entity entity3;
				if (entity instanceof EnderPearlEntity) {
					Entity entity2 = ((EnderPearlEntity)entity).getOwner();
					if (entity2 instanceof ServerPlayerEntity) {
						Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity)entity2, blockState);
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

				entity3.resetNetherPortalCooldown();
				entity3.teleport((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
			}

			startTeleportCooldown(world, blockPos, blockState, endGatewayBlockEntity);
		}
	}

	private static BlockPos findBestPortalExitPos(World world, BlockPos blockPos) {
		BlockPos blockPos2 = findExitPortalPos(world, blockPos.add(0, 2, 0), 5, false);
		LOGGER.debug("Best exit position for portal at {} is {}", blockPos, blockPos2);
		return blockPos2.up();
	}

	private static BlockPos method_31699(ServerWorld serverWorld, BlockPos blockPos) {
		Vec3d vec3d = method_31701(serverWorld, blockPos);
		WorldChunk worldChunk = getChunk(serverWorld, vec3d);
		BlockPos blockPos2 = findPortalPosition(worldChunk);
		if (blockPos2 == null) {
			blockPos2 = new BlockPos(vec3d.x + 0.5, 75.0, vec3d.z + 0.5);
			LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", blockPos2);
			ConfiguredFeatures.END_ISLAND.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), new Random(blockPos2.asLong()), blockPos2);
		} else {
			LOGGER.debug("Found suitable block to teleport to: {}", blockPos2);
		}

		return findExitPortalPos(serverWorld, blockPos2, 16, true);
	}

	private static Vec3d method_31701(ServerWorld serverWorld, BlockPos blockPos) {
		Vec3d vec3d = new Vec3d((double)blockPos.getX(), 0.0, (double)blockPos.getZ()).normalize();
		int i = 1024;
		Vec3d vec3d2 = vec3d.multiply(1024.0);

		for (int j = 16; !method_31698(serverWorld, vec3d2) && j-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(-16.0))) {
			LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d2);
		}

		for (int var6 = 16; method_31698(serverWorld, vec3d2) && var6-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(16.0))) {
			LOGGER.debug("Skipping forward past empty chunk at {}", vec3d2);
		}

		LOGGER.debug("Found chunk at {}", vec3d2);
		return vec3d2;
	}

	private static boolean method_31698(ServerWorld serverWorld, Vec3d vec3d) {
		return getChunk(serverWorld, vec3d).getHighestNonEmptySectionYOffset() <= serverWorld.getSectionCount();
	}

	private static BlockPos findExitPortalPos(BlockView world, BlockPos pos, int searchRadius, boolean bl) {
		BlockPos blockPos = null;

		for (int i = -searchRadius; i <= searchRadius; i++) {
			for (int j = -searchRadius; j <= searchRadius; j++) {
				if (i != 0 || j != 0 || bl) {
					for (int k = world.getTopHeightLimit() - 1; k > (blockPos == null ? world.getSectionCount() : blockPos.getY()); k--) {
						BlockPos blockPos2 = new BlockPos(pos.getX() + i, k, pos.getZ() + j);
						BlockState blockState = world.getBlockState(blockPos2);
						if (blockState.isFullCube(world, blockPos2) && (bl || !blockState.isOf(Blocks.BEDROCK))) {
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
				double e = blockPos4.getSquaredDistance(0.0, 0.0, 0.0, true);
				if (blockPos3 == null || e < d) {
					blockPos3 = blockPos4;
					d = e;
				}
			}
		}

		return blockPos3;
	}

	private static void createPortal(ServerWorld serverWorld, BlockPos blockPos, EndGatewayFeatureConfig endGatewayFeatureConfig) {
		Feature.END_GATEWAY.configure(endGatewayFeatureConfig).generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), new Random(), blockPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldDrawSide(Direction direction) {
		return Block.shouldDrawSide(this.getCachedState(), this.world, this.getPos(), direction, this.getPos().offset(direction));
	}

	@Environment(EnvType.CLIENT)
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
