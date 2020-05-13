package net.minecraft.block.entity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndGatewayBlockEntity extends EndPortalBlockEntity implements Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	private long age;
	private int teleportCooldown;
	@Nullable
	private BlockPos exitPortalPos;
	private boolean exactTeleport;

	public EndGatewayBlockEntity() {
		super(BlockEntityType.END_GATEWAY);
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
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
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

	@Override
	public void tick() {
		boolean bl = this.isRecentlyGenerated();
		boolean bl2 = this.needsCooldownBeforeTeleporting();
		this.age++;
		if (bl2) {
			this.teleportCooldown--;
		} else if (!this.world.isClient) {
			List<Entity> list = this.world.getNonSpectatingEntities(Entity.class, new Box(this.getPos()));
			if (!list.isEmpty()) {
				this.tryTeleportingEntity(((Entity)list.get(0)).getRootVehicle());
			}

			if (this.age % 2400L == 0L) {
				this.startTeleportCooldown();
			}
		}

		if (bl != this.isRecentlyGenerated() || bl2 != this.needsCooldownBeforeTeleporting()) {
			this.markDirty();
		}
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

	public void startTeleportCooldown() {
		if (!this.world.isClient) {
			this.teleportCooldown = 40;
			this.world.addSyncedBlockEvent(this.getPos(), this.getCachedState().getBlock(), 1, 0);
			this.markDirty();
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

	public void tryTeleportingEntity(Entity entity) {
		if (this.world instanceof ServerWorld && !this.needsCooldownBeforeTeleporting()) {
			this.teleportCooldown = 100;
			if (this.exitPortalPos == null && this.world.getDimension() instanceof TheEndDimension) {
				this.createPortal((ServerWorld)this.world);
			}

			if (this.exitPortalPos != null) {
				BlockPos blockPos = this.exactTeleport ? this.exitPortalPos : this.findBestPortalExitPos();
				entity.teleport((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
			}

			this.startTeleportCooldown();
		}
	}

	private BlockPos findBestPortalExitPos() {
		BlockPos blockPos = findExitPortalPos(this.world, this.exitPortalPos, 5, false);
		LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortalPos, blockPos);
		return blockPos.up();
	}

	private void createPortal(ServerWorld world) {
		Vec3d vec3d = new Vec3d((double)this.getPos().getX(), 0.0, (double)this.getPos().getZ()).normalize();
		Vec3d vec3d2 = vec3d.multiply(1024.0);

		for (int i = 16; getChunk(world, vec3d2).getHighestNonEmptySectionYOffset() > 0 && i-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(-16.0))) {
			LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d2);
		}

		for (int var6 = 16; getChunk(world, vec3d2).getHighestNonEmptySectionYOffset() == 0 && var6-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(16.0))) {
			LOGGER.debug("Skipping forward past empty chunk at {}", vec3d2);
		}

		LOGGER.debug("Found chunk at {}", vec3d2);
		WorldChunk worldChunk = getChunk(world, vec3d2);
		this.exitPortalPos = findPortalPosition(worldChunk);
		if (this.exitPortalPos == null) {
			this.exitPortalPos = new BlockPos(vec3d2.x + 0.5, 75.0, vec3d2.z + 0.5);
			LOGGER.debug("Failed to find suitable block, settling on {}", this.exitPortalPos);
			Feature.END_ISLAND
				.configure(FeatureConfig.DEFAULT)
				.generate(world, world.getStructureAccessor(), world.getChunkManager().getChunkGenerator(), new Random(this.exitPortalPos.asLong()), this.exitPortalPos);
		} else {
			LOGGER.debug("Found block at {}", this.exitPortalPos);
		}

		this.exitPortalPos = findExitPortalPos(world, this.exitPortalPos, 16, true);
		LOGGER.debug("Creating portal at {}", this.exitPortalPos);
		this.exitPortalPos = this.exitPortalPos.up(10);
		this.createPortal(world, this.exitPortalPos);
		this.markDirty();
	}

	private static BlockPos findExitPortalPos(BlockView world, BlockPos pos, int searchRadius, boolean bl) {
		BlockPos blockPos = null;

		for (int i = -searchRadius; i <= searchRadius; i++) {
			for (int j = -searchRadius; j <= searchRadius; j++) {
				if (i != 0 || j != 0 || bl) {
					for (int k = 255; k > (blockPos == null ? 0 : blockPos.getY()); k--) {
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

	private void createPortal(ServerWorld world, BlockPos pos) {
		Feature.END_GATEWAY
			.configure(EndGatewayFeatureConfig.createConfig(this.getPos(), false))
			.generate(world, world.getStructureAccessor(), world.getChunkManager().getChunkGenerator(), new Random(), pos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldDrawSide(Direction direction) {
		return Block.shouldDrawSide(this.getCachedState(), this.world, this.getPos(), direction);
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
