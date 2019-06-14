package net.minecraft.block.entity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndGatewayBlockEntity extends EndPortalBlockEntity implements Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	private long age;
	private int teleportCooldown;
	private BlockPos exitPortalPos;
	private boolean exactTeleport;

	public EndGatewayBlockEntity() {
		super(BlockEntityType.field_11906);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putLong("Age", this.age);
		if (this.exitPortalPos != null) {
			compoundTag.put("ExitPortal", TagHelper.serializeBlockPos(this.exitPortalPos));
		}

		if (this.exactTeleport) {
			compoundTag.putBoolean("ExactTeleport", this.exactTeleport);
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.age = compoundTag.getLong("Age");
		if (compoundTag.containsKey("ExitPortal", 10)) {
			this.exitPortalPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("ExitPortal"));
		}

		this.exactTeleport = compoundTag.getBoolean("ExactTeleport");
	}

	@Environment(EnvType.CLIENT)
	@Override
	public double getSquaredRenderDistance() {
		return 65536.0;
	}

	@Override
	public void tick() {
		boolean bl = this.isRecentlyGenerated();
		boolean bl2 = this.needsCooldownBeforeTeleporting();
		this.age++;
		if (bl2) {
			this.teleportCooldown--;
		} else if (!this.world.isClient) {
			List<Entity> list = this.world.method_18467(Entity.class, new Box(this.getPos()));
			if (!list.isEmpty()) {
				this.tryTeleportingEntity((Entity)list.get(0));
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
	public float getRecentlyGeneratedBeamHeight(float f) {
		return MathHelper.clamp(((float)this.age + f) / 200.0F, 0.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public float getCooldownBeamHeight(float f) {
		return 1.0F - MathHelper.clamp(((float)this.teleportCooldown - f) / 40.0F, 0.0F, 1.0F);
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
			this.world.method_8427(this.getPos(), this.method_11010().getBlock(), 1, 0);
			this.markDirty();
		}
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.teleportCooldown = 40;
			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	public void tryTeleportingEntity(Entity entity) {
		if (!this.world.isClient && !this.needsCooldownBeforeTeleporting()) {
			this.teleportCooldown = 100;
			if (this.exitPortalPos == null && this.world.field_9247 instanceof TheEndDimension) {
				this.createPortal();
			}

			if (this.exitPortalPos != null) {
				BlockPos blockPos = this.exactTeleport ? this.exitPortalPos : this.findBestPortalExitPos();
				entity.method_20620((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
			}

			this.startTeleportCooldown();
		}
	}

	private BlockPos findBestPortalExitPos() {
		BlockPos blockPos = findExitPortalPos(this.world, this.exitPortalPos, 5, false);
		LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortalPos, blockPos);
		return blockPos.up();
	}

	private void createPortal() {
		Vec3d vec3d = new Vec3d((double)this.getPos().getX(), 0.0, (double)this.getPos().getZ()).normalize();
		Vec3d vec3d2 = vec3d.multiply(1024.0);

		for (int i = 16; method_11414(this.world, vec3d2).getHighestNonEmptySectionYOffset() > 0 && i-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(-16.0))) {
			LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d2);
		}

		for (int var5 = 16; method_11414(this.world, vec3d2).getHighestNonEmptySectionYOffset() == 0 && var5-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(16.0))) {
			LOGGER.debug("Skipping forward past empty chunk at {}", vec3d2);
		}

		LOGGER.debug("Found chunk at {}", vec3d2);
		WorldChunk worldChunk = method_11414(this.world, vec3d2);
		this.exitPortalPos = method_11413(worldChunk);
		if (this.exitPortalPos == null) {
			this.exitPortalPos = new BlockPos(vec3d2.x + 0.5, 75.0, vec3d2.z + 0.5);
			LOGGER.debug("Failed to find suitable block, settling on {}", this.exitPortalPos);
			Feature.field_13574
				.method_13151(
					this.world,
					(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.method_8398().getChunkGenerator(),
					new Random(this.exitPortalPos.asLong()),
					this.exitPortalPos,
					FeatureConfig.field_13603
				);
		} else {
			LOGGER.debug("Found block at {}", this.exitPortalPos);
		}

		this.exitPortalPos = findExitPortalPos(this.world, this.exitPortalPos, 16, true);
		LOGGER.debug("Creating portal at {}", this.exitPortalPos);
		this.exitPortalPos = this.exitPortalPos.up(10);
		this.createPortal(this.exitPortalPos);
		this.markDirty();
	}

	private static BlockPos findExitPortalPos(BlockView blockView, BlockPos blockPos, int i, boolean bl) {
		BlockPos blockPos2 = null;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (j != 0 || k != 0 || bl) {
					for (int l = 255; l > (blockPos2 == null ? 0 : blockPos2.getY()); l--) {
						BlockPos blockPos3 = new BlockPos(blockPos.getX() + j, l, blockPos.getZ() + k);
						BlockState blockState = blockView.method_8320(blockPos3);
						if (Block.method_9614(blockState.method_11628(blockView, blockPos3)) && (bl || blockState.getBlock() != Blocks.field_9987)) {
							blockPos2 = blockPos3;
							break;
						}
					}
				}
			}
		}

		return blockPos2 == null ? blockPos : blockPos2;
	}

	private static WorldChunk method_11414(World world, Vec3d vec3d) {
		return world.method_8497(MathHelper.floor(vec3d.x / 16.0), MathHelper.floor(vec3d.z / 16.0));
	}

	@Nullable
	private static BlockPos method_11413(WorldChunk worldChunk) {
		ChunkPos chunkPos = worldChunk.getPos();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 30, chunkPos.getStartZ());
		int i = worldChunk.getHighestNonEmptySectionYOffset() + 16 - 1;
		BlockPos blockPos2 = new BlockPos(chunkPos.getEndX(), i, chunkPos.getEndZ());
		BlockPos blockPos3 = null;
		double d = 0.0;

		for (BlockPos blockPos4 : BlockPos.iterate(blockPos, blockPos2)) {
			BlockState blockState = worldChunk.method_8320(blockPos4);
			BlockPos blockPos5 = blockPos4.up();
			BlockPos blockPos6 = blockPos4.up(2);
			if (blockState.getBlock() == Blocks.field_10471
				&& !Block.method_9614(worldChunk.method_8320(blockPos5).method_11628(worldChunk, blockPos5))
				&& !Block.method_9614(worldChunk.method_8320(blockPos6).method_11628(worldChunk, blockPos6))) {
				double e = blockPos4.getSquaredDistance(0.0, 0.0, 0.0, true);
				if (blockPos3 == null || e < d) {
					blockPos3 = blockPos4;
					d = e;
				}
			}
		}

		return blockPos3;
	}

	private void createPortal(BlockPos blockPos) {
		Feature.field_13564
			.method_13151(
				this.world,
				(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.method_8398().getChunkGenerator(),
				new Random(),
				blockPos,
				EndGatewayFeatureConfig.createConfig(this.getPos(), false)
			);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldDrawSide(Direction direction) {
		return Block.method_9607(this.method_11010(), this.world, this.getPos(), direction);
	}

	@Environment(EnvType.CLIENT)
	public int getDrawnSidesCount() {
		int i = 0;

		for (Direction direction : Direction.values()) {
			i += this.shouldDrawSide(direction) ? 1 : 0;
		}

		return i;
	}

	public void setExitPortalPos(BlockPos blockPos, boolean bl) {
		this.exactTeleport = bl;
		this.exitPortalPos = blockPos;
	}
}
