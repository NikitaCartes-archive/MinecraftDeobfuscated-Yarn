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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
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
	private BlockPos field_12132;
	private boolean exactTeleport;

	public EndGatewayBlockEntity() {
		super(BlockEntityType.END_GATEWAY);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		compoundTag.putLong("Age", this.age);
		if (this.field_12132 != null) {
			compoundTag.method_10566("ExitPortal", TagHelper.serializeBlockPos(this.field_12132));
		}

		if (this.exactTeleport) {
			compoundTag.putBoolean("ExactTeleport", this.exactTeleport);
		}

		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.age = compoundTag.getLong("Age");
		if (compoundTag.containsKey("ExitPortal", 10)) {
			this.field_12132 = TagHelper.deserializeBlockPos(compoundTag.getCompound("ExitPortal"));
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
			List<Entity> list = this.world.method_18467(Entity.class, new BoundingBox(this.method_11016()));
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
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 8, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
	}

	public void startTeleportCooldown() {
		if (!this.world.isClient) {
			this.teleportCooldown = 40;
			this.world.method_8427(this.method_11016(), this.method_11010().getBlock(), 1, 0);
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
			if (this.field_12132 == null && this.world.field_9247 instanceof TheEndDimension) {
				this.createPortal();
			}

			if (this.field_12132 != null) {
				BlockPos blockPos = this.exactTeleport ? this.field_12132 : this.method_11419();
				entity.method_5859((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
			}

			this.startTeleportCooldown();
		}
	}

	private BlockPos method_11419() {
		BlockPos blockPos = method_11410(this.world, this.field_12132, 5, false);
		LOGGER.debug("Best exit position for portal at {} is {}", this.field_12132, blockPos);
		return blockPos.up();
	}

	private void createPortal() {
		Vec3d vec3d = new Vec3d((double)this.method_11016().getX(), 0.0, (double)this.method_11016().getZ()).normalize();
		Vec3d vec3d2 = vec3d.multiply(1024.0);

		for (int i = 16; method_11414(this.world, vec3d2).getHighestNonEmptySectionYOffset() > 0 && i-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(-16.0))) {
			LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d2);
		}

		for (int var5 = 16; method_11414(this.world, vec3d2).getHighestNonEmptySectionYOffset() == 0 && var5-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(16.0))) {
			LOGGER.debug("Skipping forward past empty chunk at {}", vec3d2);
		}

		LOGGER.debug("Found chunk at {}", vec3d2);
		WorldChunk worldChunk = method_11414(this.world, vec3d2);
		this.field_12132 = method_11413(worldChunk);
		if (this.field_12132 == null) {
			this.field_12132 = new BlockPos(vec3d2.x + 0.5, 75.0, vec3d2.z + 0.5);
			LOGGER.debug("Failed to find suitable block, settling on {}", this.field_12132);
			Feature.field_13574
				.method_13151(
					this.world,
					(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.method_8398().getChunkGenerator(),
					new Random(this.field_12132.asLong()),
					this.field_12132,
					FeatureConfig.field_13603
				);
		} else {
			LOGGER.debug("Found block at {}", this.field_12132);
		}

		this.field_12132 = method_11410(this.world, this.field_12132, 16, true);
		LOGGER.debug("Creating portal at {}", this.field_12132);
		this.field_12132 = this.field_12132.up(10);
		this.method_11416(this.field_12132);
		this.markDirty();
	}

	private static BlockPos method_11410(BlockView blockView, BlockPos blockPos, int i, boolean bl) {
		BlockPos blockPos2 = null;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (j != 0 || k != 0 || bl) {
					for (int l = 255; l > (blockPos2 == null ? 0 : blockPos2.getY()); l--) {
						BlockPos blockPos3 = new BlockPos(blockPos.getX() + j, l, blockPos.getZ() + k);
						BlockState blockState = blockView.method_8320(blockPos3);
						if (blockState.method_11603(blockView, blockPos3) && (bl || blockState.getBlock() != Blocks.field_9987)) {
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

		for (BlockPos blockPos4 : BlockPos.iterateBoxPositions(blockPos, blockPos2)) {
			BlockState blockState = worldChunk.method_8320(blockPos4);
			BlockPos blockPos5 = blockPos4.up();
			BlockPos blockPos6 = blockPos4.up(2);
			if (blockState.getBlock() == Blocks.field_10471
				&& !worldChunk.method_8320(blockPos5).method_11603(worldChunk, blockPos5)
				&& !worldChunk.method_8320(blockPos6).method_11603(worldChunk, blockPos6)) {
				double e = blockPos4.squaredDistanceToCenter(0.0, 0.0, 0.0);
				if (blockPos3 == null || e < d) {
					blockPos3 = blockPos4;
					d = e;
				}
			}
		}

		return blockPos3;
	}

	private void method_11416(BlockPos blockPos) {
		Feature.field_13564
			.method_13151(
				this.world,
				(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.method_8398().getChunkGenerator(),
				new Random(),
				blockPos,
				EndGatewayFeatureConfig.method_18034(this.method_11016(), false)
			);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_11400(Direction direction) {
		return Block.method_9607(this.method_11010(), this.world, this.method_11016(), direction);
	}

	@Environment(EnvType.CLIENT)
	public int getDrawnSidesCount() {
		int i = 0;

		for (Direction direction : Direction.values()) {
			i += this.method_11400(direction) ? 1 : 0;
		}

		return i;
	}

	public void method_11418(BlockPos blockPos, boolean bl) {
		this.exactTeleport = bl;
		this.field_12132 = blockPos;
	}
}
