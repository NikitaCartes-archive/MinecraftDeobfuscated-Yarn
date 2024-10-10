package net.minecraft.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;

public abstract class AbstractMinecartEntity extends VehicleEntity {
	private static final Vec3d VILLAGER_PASSENGER_ATTACHMENT_POS = new Vec3d(0.0, 0.0, 0.0);
	private static final TrackedData<Integer> CUSTOM_BLOCK_ID = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> CUSTOM_BLOCK_OFFSET = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CUSTOM_BLOCK_PRESENT = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final ImmutableMap<EntityPose, ImmutableList<Integer>> DISMOUNT_FREE_Y_SPACES_NEEDED = ImmutableMap.of(
		EntityPose.STANDING, ImmutableList.of(0, 1, -1), EntityPose.CROUCHING, ImmutableList.of(0, 1, -1), EntityPose.SWIMMING, ImmutableList.of(0, 1)
	);
	protected static final float VELOCITY_SLOWDOWN_MULTIPLIER = 0.95F;
	private boolean onRail;
	private boolean yawFlipped;
	private final MinecartController controller;
	private static final Map<RailShape, Pair<Vec3i, Vec3i>> ADJACENT_RAIL_POSITIONS_BY_SHAPE = Util.make(Maps.newEnumMap(RailShape.class), map -> {
		Vec3i vec3i = Direction.WEST.getVector();
		Vec3i vec3i2 = Direction.EAST.getVector();
		Vec3i vec3i3 = Direction.NORTH.getVector();
		Vec3i vec3i4 = Direction.SOUTH.getVector();
		Vec3i vec3i5 = vec3i.down();
		Vec3i vec3i6 = vec3i2.down();
		Vec3i vec3i7 = vec3i3.down();
		Vec3i vec3i8 = vec3i4.down();
		map.put(RailShape.NORTH_SOUTH, Pair.of(vec3i3, vec3i4));
		map.put(RailShape.EAST_WEST, Pair.of(vec3i, vec3i2));
		map.put(RailShape.ASCENDING_EAST, Pair.of(vec3i5, vec3i2));
		map.put(RailShape.ASCENDING_WEST, Pair.of(vec3i, vec3i6));
		map.put(RailShape.ASCENDING_NORTH, Pair.of(vec3i3, vec3i8));
		map.put(RailShape.ASCENDING_SOUTH, Pair.of(vec3i7, vec3i4));
		map.put(RailShape.SOUTH_EAST, Pair.of(vec3i4, vec3i2));
		map.put(RailShape.SOUTH_WEST, Pair.of(vec3i4, vec3i));
		map.put(RailShape.NORTH_WEST, Pair.of(vec3i3, vec3i));
		map.put(RailShape.NORTH_EAST, Pair.of(vec3i3, vec3i2));
	});

	protected AbstractMinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.intersectionChecked = true;
		if (areMinecartImprovementsEnabled(world)) {
			this.controller = new ExperimentalMinecartController(this);
		} else {
			this.controller = new DefaultMinecartController(this);
		}
	}

	protected AbstractMinecartEntity(EntityType<?> type, World world, double x, double y, double z) {
		this(type, world);
		this.initPosition(x, y, z);
	}

	public void initPosition(double x, double y, double z) {
		this.setPosition(x, y, z);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	@Nullable
	public static <T extends AbstractMinecartEntity> T create(
		World world, double x, double y, double z, EntityType<T> type, SpawnReason reason, ItemStack stack, @Nullable PlayerEntity player
	) {
		T abstractMinecartEntity = (T)type.create(world, reason);
		if (abstractMinecartEntity != null) {
			abstractMinecartEntity.initPosition(x, y, z);
			EntityType.copier(world, stack, player).accept(abstractMinecartEntity);
			if (abstractMinecartEntity.getController() instanceof ExperimentalMinecartController experimentalMinecartController) {
				BlockPos blockPos = abstractMinecartEntity.getRailOrMinecartPos();
				BlockState blockState = world.getBlockState(blockPos);
				experimentalMinecartController.adjustToRail(blockPos, blockState, true);
			}
		}

		return abstractMinecartEntity;
	}

	public MinecartController getController() {
		return this.controller;
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.EVENTS;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(CUSTOM_BLOCK_ID, Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
		builder.add(CUSTOM_BLOCK_OFFSET, 6);
		builder.add(CUSTOM_BLOCK_PRESENT, false);
	}

	@Override
	public boolean collidesWith(Entity other) {
		return AbstractBoatEntity.canCollide(this, other);
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
		return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
	}

	@Override
	protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		boolean bl = passenger instanceof VillagerEntity || passenger instanceof WanderingTraderEntity;
		return bl ? VILLAGER_PASSENGER_ATTACHMENT_POS : super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		Direction direction = this.getMovementDirection();
		if (direction.getAxis() == Direction.Axis.Y) {
			return super.updatePassengerForDismount(passenger);
		} else {
			int[][] is = Dismounting.getDismountOffsets(direction);
			BlockPos blockPos = this.getBlockPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			ImmutableList<EntityPose> immutableList = passenger.getPoses();

			for (EntityPose entityPose : immutableList) {
				EntityDimensions entityDimensions = passenger.getDimensions(entityPose);
				float f = Math.min(entityDimensions.width(), 1.0F) / 2.0F;

				for (int i : DISMOUNT_FREE_Y_SPACES_NEEDED.get(entityPose)) {
					for (int[] js : is) {
						mutable.set(blockPos.getX() + js[0], blockPos.getY() + i, blockPos.getZ() + js[1]);
						double d = this.getWorld()
							.getDismountHeight(Dismounting.getCollisionShape(this.getWorld(), mutable), () -> Dismounting.getCollisionShape(this.getWorld(), mutable.down()));
						if (Dismounting.canDismountInBlock(d)) {
							Box box = new Box((double)(-f), 0.0, (double)(-f), (double)f, (double)entityDimensions.height(), (double)f);
							Vec3d vec3d = Vec3d.ofCenter(mutable, d);
							if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d))) {
								passenger.setPose(entityPose);
								return vec3d;
							}
						}
					}
				}
			}

			double e = this.getBoundingBox().maxY;
			mutable.set((double)blockPos.getX(), e, (double)blockPos.getZ());

			for (EntityPose entityPose2 : immutableList) {
				double g = (double)passenger.getDimensions(entityPose2).height();
				int j = MathHelper.ceil(e - (double)mutable.getY() + g);
				double h = Dismounting.getCeilingHeight(mutable, j, pos -> this.getWorld().getBlockState(pos).getCollisionShape(this.getWorld(), pos));
				if (e + g <= h) {
					passenger.setPose(entityPose2);
					break;
				}
			}

			return super.updatePassengerForDismount(passenger);
		}
	}

	@Override
	protected float getVelocityMultiplier() {
		BlockState blockState = this.getWorld().getBlockState(this.getBlockPos());
		return blockState.isIn(BlockTags.RAILS) ? 1.0F : super.getVelocityMultiplier();
	}

	@Override
	public void animateDamage(float yaw) {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setDamageWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() + this.getDamageWobbleStrength() * 10.0F);
	}

	@Override
	public boolean canHit() {
		return !this.isRemoved();
	}

	public static Pair<Vec3i, Vec3i> getAdjacentRailPositionsByShape(RailShape shape) {
		return (Pair<Vec3i, Vec3i>)ADJACENT_RAIL_POSITIONS_BY_SHAPE.get(shape);
	}

	@Override
	public Direction getMovementDirection() {
		return this.controller.getHorizontalFacing();
	}

	@Override
	protected double getGravity() {
		return this.isTouchingWater() ? 0.005 : 0.04;
	}

	@Override
	public void tick() {
		if (this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}

		if (this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		this.attemptTickInVoid();
		this.tickPortalTeleportation();
		this.controller.tick();
		this.updateWaterState();
		if (this.isInLava()) {
			this.setOnFireFromLava();
			this.fallDistance *= 0.5F;
		}

		this.firstUpdate = false;
	}

	public boolean isFirstUpdate() {
		return this.firstUpdate;
	}

	public BlockPos getRailOrMinecartPos() {
		int i = MathHelper.floor(this.getX());
		int j = MathHelper.floor(this.getY());
		int k = MathHelper.floor(this.getZ());
		if (areMinecartImprovementsEnabled(this.getWorld())) {
			double d = this.getY() - 0.1 - 1.0E-5F;
			if (this.getWorld().getBlockState(BlockPos.ofFloored((double)i, d, (double)k)).isIn(BlockTags.RAILS)) {
				j = MathHelper.floor(d);
			}
		} else if (this.getWorld().getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
			j--;
		}

		return new BlockPos(i, j, k);
	}

	protected double getMaxSpeed(ServerWorld world) {
		return this.controller.getMaxSpeed(world);
	}

	public void onActivatorRail(int x, int y, int z, boolean powered) {
	}

	@Override
	public void lerpPosAndRotation(int step, double x, double y, double z, double yaw, double pitch) {
		super.lerpPosAndRotation(step, x, y, z, yaw, pitch);
	}

	@Override
	public void applyGravity() {
		super.applyGravity();
	}

	@Override
	public void refreshPosition() {
		super.refreshPosition();
	}

	@Override
	public boolean updateWaterState() {
		return super.updateWaterState();
	}

	@Override
	public Vec3d getMovement() {
		return this.controller.limitSpeed(super.getMovement());
	}

	@Override
	public void resetLerp() {
		this.controller.resetLerp();
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
		this.controller.setPos(x, y, z, yaw, pitch, interpolationSteps);
	}

	@Override
	public double getLerpTargetX() {
		return this.controller.getLerpTargetX();
	}

	@Override
	public double getLerpTargetY() {
		return this.controller.getLerpTargetY();
	}

	@Override
	public double getLerpTargetZ() {
		return this.controller.getLerpTargetZ();
	}

	@Override
	public float getLerpTargetPitch() {
		return this.controller.getLerpTargetPitch();
	}

	@Override
	public float getLerpTargetYaw() {
		return this.controller.getLerpTargetYaw();
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.controller.setLerpTargetVelocity(x, y, z);
	}

	protected void moveOnRail(ServerWorld world) {
		this.controller.moveOnRail(world);
	}

	protected void moveOffRail(ServerWorld world) {
		double d = this.getMaxSpeed(world);
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(MathHelper.clamp(vec3d.x, -d, d), vec3d.y, MathHelper.clamp(vec3d.z, -d, d));
		if (this.isOnGround()) {
			this.setVelocity(this.getVelocity().multiply(0.5));
		}

		this.move(MovementType.SELF, this.getVelocity());
		if (!this.isOnGround()) {
			this.setVelocity(this.getVelocity().multiply(0.95));
		}
	}

	protected double moveAlongTrack(BlockPos pos, RailShape shape, double remainingMovement) {
		return this.controller.moveAlongTrack(pos, shape, remainingMovement);
	}

	@Override
	public void move(MovementType type, Vec3d movement) {
		if (areMinecartImprovementsEnabled(this.getWorld())) {
			Vec3d vec3d = this.getPos().add(movement);
			super.move(type, movement);
			boolean bl = this.controller.handleCollision();
			if (bl) {
				super.move(type, vec3d.subtract(this.getPos()));
			}

			if (type.equals(MovementType.PISTON)) {
				this.onRail = false;
			}
		} else {
			super.move(type, movement);
		}
	}

	@Override
	public boolean isOnRail() {
		return this.onRail;
	}

	public void setOnRail(boolean onRail) {
		this.onRail = onRail;
	}

	public boolean isYawFlipped() {
		return this.yawFlipped;
	}

	public void setYawFlipped(boolean yawFlipped) {
		this.yawFlipped = yawFlipped;
	}

	public Vec3d getLaunchDirection(BlockPos railPos) {
		BlockState blockState = this.getWorld().getBlockState(railPos);
		if (blockState.isOf(Blocks.POWERED_RAIL) && (Boolean)blockState.get(PoweredRailBlock.POWERED)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			if (railShape == RailShape.EAST_WEST) {
				if (this.willHitBlockAt(railPos.west())) {
					return new Vec3d(1.0, 0.0, 0.0);
				}

				if (this.willHitBlockAt(railPos.east())) {
					return new Vec3d(-1.0, 0.0, 0.0);
				}
			} else if (railShape == RailShape.NORTH_SOUTH) {
				if (this.willHitBlockAt(railPos.north())) {
					return new Vec3d(0.0, 0.0, 1.0);
				}

				if (this.willHitBlockAt(railPos.south())) {
					return new Vec3d(0.0, 0.0, -1.0);
				}
			}

			return Vec3d.ZERO;
		} else {
			return Vec3d.ZERO;
		}
	}

	public boolean willHitBlockAt(BlockPos pos) {
		return this.getWorld().getBlockState(pos).isSolidBlock(this.getWorld(), pos);
	}

	protected Vec3d applySlowdown(Vec3d velocity) {
		double d = this.controller.getSpeedRetention();
		Vec3d vec3d = velocity.multiply(d, 0.0, d);
		if (this.isTouchingWater()) {
			vec3d = vec3d.multiply(0.95F);
		}

		return vec3d;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.getBoolean("CustomDisplayTile")) {
			this.setCustomBlock(NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("DisplayState")));
			this.setCustomBlockOffset(nbt.getInt("DisplayOffset"));
		}

		this.yawFlipped = nbt.getBoolean("FlippedRotation");
		this.firstUpdate = nbt.getBoolean("HasTicked");
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.hasCustomBlock()) {
			nbt.putBoolean("CustomDisplayTile", true);
			nbt.put("DisplayState", NbtHelper.fromBlockState(this.getContainedBlock()));
			nbt.putInt("DisplayOffset", this.getBlockOffset());
		}

		nbt.putBoolean("FlippedRotation", this.yawFlipped);
		nbt.putBoolean("HasTicked", this.firstUpdate);
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.getWorld().isClient) {
			if (!entity.noClip && !this.noClip) {
				if (!this.hasPassenger(entity)) {
					double d = entity.getX() - this.getX();
					double e = entity.getZ() - this.getZ();
					double f = d * d + e * e;
					if (f >= 1.0E-4F) {
						f = Math.sqrt(f);
						d /= f;
						e /= f;
						double g = 1.0 / f;
						if (g > 1.0) {
							g = 1.0;
						}

						d *= g;
						e *= g;
						d *= 0.1F;
						e *= 0.1F;
						d *= 0.5;
						e *= 0.5;
						if (entity instanceof AbstractMinecartEntity abstractMinecartEntity) {
							this.pushAwayFromMinecart(abstractMinecartEntity, d, e);
						} else {
							this.addVelocity(-d, 0.0, -e);
							entity.addVelocity(d / 4.0, 0.0, e / 4.0);
						}
					}
				}
			}
		}
	}

	private void pushAwayFromMinecart(AbstractMinecartEntity entity, double xDiff, double zDiff) {
		double d;
		double e;
		if (areMinecartImprovementsEnabled(this.getWorld())) {
			d = this.getVelocity().x;
			e = this.getVelocity().z;
		} else {
			d = entity.getX() - this.getX();
			e = entity.getZ() - this.getZ();
		}

		Vec3d vec3d = new Vec3d(d, 0.0, e).normalize();
		Vec3d vec3d2 = new Vec3d(
				(double)MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)), 0.0, (double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0))
			)
			.normalize();
		double f = Math.abs(vec3d.dotProduct(vec3d2));
		if (!(f < 0.8F) || areMinecartImprovementsEnabled(this.getWorld())) {
			Vec3d vec3d3 = this.getVelocity();
			Vec3d vec3d4 = entity.getVelocity();
			if (entity.isSelfPropelling() && !this.isSelfPropelling()) {
				this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
				this.addVelocity(vec3d4.x - xDiff, 0.0, vec3d4.z - zDiff);
				entity.setVelocity(vec3d4.multiply(0.95, 1.0, 0.95));
			} else if (!entity.isSelfPropelling() && this.isSelfPropelling()) {
				entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
				entity.addVelocity(vec3d3.x + xDiff, 0.0, vec3d3.z + zDiff);
				this.setVelocity(vec3d3.multiply(0.95, 1.0, 0.95));
			} else {
				double g = (vec3d4.x + vec3d3.x) / 2.0;
				double h = (vec3d4.z + vec3d3.z) / 2.0;
				this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
				this.addVelocity(g - xDiff, 0.0, h - zDiff);
				entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
				entity.addVelocity(g + xDiff, 0.0, h + zDiff);
			}
		}
	}

	public BlockState getContainedBlock() {
		return !this.hasCustomBlock() ? this.getDefaultContainedBlock() : Block.getStateFromRawId(this.getDataTracker().get(CUSTOM_BLOCK_ID));
	}

	public BlockState getDefaultContainedBlock() {
		return Blocks.AIR.getDefaultState();
	}

	public int getBlockOffset() {
		return !this.hasCustomBlock() ? this.getDefaultBlockOffset() : this.getDataTracker().get(CUSTOM_BLOCK_OFFSET);
	}

	public int getDefaultBlockOffset() {
		return 6;
	}

	public void setCustomBlock(BlockState state) {
		this.getDataTracker().set(CUSTOM_BLOCK_ID, Block.getRawIdFromState(state));
		this.setCustomBlockPresent(true);
	}

	public void setCustomBlockOffset(int offset) {
		this.getDataTracker().set(CUSTOM_BLOCK_OFFSET, offset);
		this.setCustomBlockPresent(true);
	}

	public boolean hasCustomBlock() {
		return this.getDataTracker().get(CUSTOM_BLOCK_PRESENT);
	}

	public void setCustomBlockPresent(boolean present) {
		this.getDataTracker().set(CUSTOM_BLOCK_PRESENT, present);
	}

	public static boolean areMinecartImprovementsEnabled(World world) {
		return world.getEnabledFeatures().contains(FeatureFlags.MINECART_IMPROVEMENTS);
	}

	@Override
	public abstract ItemStack getPickBlockStack();

	public boolean isRideable() {
		return false;
	}

	public boolean isSelfPropelling() {
		return false;
	}
}
