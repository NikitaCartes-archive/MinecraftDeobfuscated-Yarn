package net.minecraft.block.entity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
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
	private int field_12130;
	private BlockPos portalExitPos;
	private boolean exitPosExact;

	public EndGatewayBlockEntity() {
		super(BlockEntityType.END_GATEWAY);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putLong("Age", this.age);
		if (this.portalExitPos != null) {
			compoundTag.put("ExitPortal", TagHelper.serializeBlockPos(this.portalExitPos));
		}

		if (this.exitPosExact) {
			compoundTag.putBoolean("ExactTeleport", this.exitPosExact);
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.age = compoundTag.getLong("Age");
		if (compoundTag.containsKey("ExitPortal", 10)) {
			this.portalExitPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("ExitPortal"));
		}

		this.exitPosExact = compoundTag.getBoolean("ExactTeleport");
	}

	@Environment(EnvType.CLIENT)
	@Override
	public double getSquaredRenderDistance() {
		return 65536.0;
	}

	@Override
	public void tick() {
		boolean bl = this.method_11420();
		boolean bl2 = this.method_11421();
		this.age++;
		if (bl2) {
			this.field_12130--;
		} else if (!this.world.isClient) {
			List<Entity> list = this.world.getVisibleEntities(Entity.class, new BoundingBox(this.getPos()));
			if (!list.isEmpty()) {
				this.method_11409((Entity)list.get(0));
			}

			if (this.age % 2400L == 0L) {
				this.method_11411();
			}
		}

		if (bl != this.method_11420() || bl2 != this.method_11421()) {
			this.markDirty();
		}
	}

	public boolean method_11420() {
		return this.age < 200L;
	}

	public boolean method_11421() {
		return this.field_12130 > 0;
	}

	@Environment(EnvType.CLIENT)
	public float method_11417(float f) {
		return MathHelper.clamp(((float)this.age + f) / 200.0F, 0.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public float method_11412(float f) {
		return 1.0F - MathHelper.clamp(((float)this.field_12130 - f) / 40.0F, 0.0F, 1.0F);
	}

	@Nullable
	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 8, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	public void method_11411() {
		if (!this.world.isClient) {
			this.field_12130 = 40;
			this.world.addBlockAction(this.getPos(), this.getCachedState().getBlock(), 1, 0);
			this.markDirty();
		}
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.field_12130 = 40;
			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	public void method_11409(Entity entity) {
		if (!this.world.isClient && !this.method_11421()) {
			this.field_12130 = 100;
			if (this.portalExitPos == null && this.world.dimension instanceof TheEndDimension) {
				this.method_11422();
			}

			if (this.portalExitPos != null) {
				BlockPos blockPos = this.exitPosExact ? this.portalExitPos : this.getBestPortalExitPos();
				entity.method_5859((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
			}

			this.method_11411();
		}
	}

	private BlockPos getBestPortalExitPos() {
		BlockPos blockPos = method_11410(this.world, this.portalExitPos, 5, false);
		LOGGER.debug("Best exit position for portal at {} is {}", this.portalExitPos, blockPos);
		return blockPos.up();
	}

	private void method_11422() {
		Vec3d vec3d = new Vec3d((double)this.getPos().getX(), 0.0, (double)this.getPos().getZ()).normalize();
		Vec3d vec3d2 = vec3d.multiply(1024.0);

		for (int i = 16; getChunk(this.world, vec3d2).method_12031() > 0 && i-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(-16.0))) {
			LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d2);
		}

		for (int var5 = 16; getChunk(this.world, vec3d2).method_12031() == 0 && var5-- > 0; vec3d2 = vec3d2.add(vec3d.multiply(16.0))) {
			LOGGER.debug("Skipping forward past empty chunk at {}", vec3d2);
		}

		LOGGER.debug("Found chunk at {}", vec3d2);
		WorldChunk worldChunk = getChunk(this.world, vec3d2);
		this.portalExitPos = method_11413(worldChunk);
		if (this.portalExitPos == null) {
			this.portalExitPos = new BlockPos(vec3d2.x + 0.5, 75.0, vec3d2.z + 0.5);
			LOGGER.debug("Failed to find suitable block, settling on {}", this.portalExitPos);
			Feature.field_13574
				.method_13151(
					this.world,
					(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.getChunkManager().getChunkGenerator(),
					new Random(this.portalExitPos.asLong()),
					this.portalExitPos,
					FeatureConfig.field_13603
				);
		} else {
			LOGGER.debug("Found block at {}", this.portalExitPos);
		}

		this.portalExitPos = method_11410(this.world, this.portalExitPos, 16, true);
		LOGGER.debug("Creating portal at {}", this.portalExitPos);
		this.portalExitPos = this.portalExitPos.up(10);
		this.generatePortal(this.portalExitPos);
		this.markDirty();
	}

	private static BlockPos method_11410(BlockView blockView, BlockPos blockPos, int i, boolean bl) {
		BlockPos blockPos2 = null;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (j != 0 || k != 0 || bl) {
					for (int l = 255; l > (blockPos2 == null ? 0 : blockPos2.getY()); l--) {
						BlockPos blockPos3 = new BlockPos(blockPos.getX() + j, l, blockPos.getZ() + k);
						BlockState blockState = blockView.getBlockState(blockPos3);
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

	private static WorldChunk getChunk(World world, Vec3d vec3d) {
		return world.getWorldChunk(MathHelper.floor(vec3d.x / 16.0), MathHelper.floor(vec3d.z / 16.0));
	}

	@Nullable
	private static BlockPos method_11413(WorldChunk worldChunk) {
		ChunkPos chunkPos = worldChunk.getPos();
		BlockPos blockPos = new BlockPos(chunkPos.getXStart(), 30, chunkPos.getZStart());
		int i = worldChunk.method_12031() + 16 - 1;
		BlockPos blockPos2 = new BlockPos(chunkPos.getXEnd(), i, chunkPos.getZEnd());
		BlockPos blockPos3 = null;
		double d = 0.0;

		for (BlockPos blockPos4 : BlockPos.iterateBoxPositions(blockPos, blockPos2)) {
			BlockState blockState = worldChunk.getBlockState(blockPos4);
			BlockPos blockPos5 = blockPos4.up();
			BlockPos blockPos6 = blockPos4.up(2);
			if (blockState.getBlock() == Blocks.field_10471
				&& !worldChunk.getBlockState(blockPos5).method_11603(worldChunk, blockPos5)
				&& !worldChunk.getBlockState(blockPos6).method_11603(worldChunk, blockPos6)) {
				double e = blockPos4.squaredDistanceToCenter(0.0, 0.0, 0.0);
				if (blockPos3 == null || e < d) {
					blockPos3 = blockPos4;
					d = e;
				}
			}
		}

		return blockPos3;
	}

	private void generatePortal(BlockPos blockPos) {
		Feature.field_13564
			.method_13151(
				this.world,
				(ChunkGenerator<? extends ChunkGeneratorConfig>)this.world.getChunkManager().getChunkGenerator(),
				new Random(),
				blockPos,
				new EndGatewayFeatureConfig(false)
			);
		BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
		if (blockEntity instanceof EndGatewayBlockEntity) {
			EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
			endGatewayBlockEntity.portalExitPos = new BlockPos(this.getPos());
			endGatewayBlockEntity.markDirty();
		} else {
			LOGGER.warn("Couldn't save exit portal at {}", blockPos);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_11400(Direction direction) {
		return Block.shouldDrawSide(this.getCachedState(), this.world, this.getPos(), direction);
	}

	@Environment(EnvType.CLIENT)
	public int method_11415() {
		int i = 0;

		for (Direction direction : Direction.values()) {
			i += this.method_11400(direction) ? 1 : 0;
		}

		return i;
	}

	public void method_11418(BlockPos blockPos) {
		this.exitPosExact = true;
		this.portalExitPos = blockPos;
	}
}
