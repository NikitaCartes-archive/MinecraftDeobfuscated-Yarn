package net.minecraft.entity;

import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class FallingBlockEntity extends Entity {
	private static final Logger field_36333 = LogUtils.getLogger();
	private static final int field_35672 = 50;
	private BlockState block = Blocks.SAND.getDefaultState();
	public int timeFalling;
	public boolean dropItem = true;
	private boolean destroyedOnLanding;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private float fallHurtAmount;
	private long discardTime;
	@Nullable
	public NbtCompound blockEntityData;
	protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

	public FallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(entityType, world);
	}

	private FallingBlockEntity(World world, double x, double y, double z, BlockState block) {
		this(EntityType.FALLING_BLOCK, world);
		this.block = block;
		this.intersectionChecked = true;
		this.setPosition(x, y, z);
		this.setVelocity(Vec3d.ZERO);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
		this.setFallingBlockPos(this.getBlockPos());
	}

	/**
	 * Spawns a falling block entity at {@code pos} from the block {@code state}.
	 * @return the spawned entity
	 */
	public static FallingBlockEntity spawnFromBlock(World world, BlockPos pos, BlockState state) {
		FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
			world,
			(double)pos.getX() + 0.5,
			(double)pos.getY(),
			(double)pos.getZ() + 0.5,
			state.contains(Properties.WATERLOGGED) ? state.with(Properties.WATERLOGGED, Boolean.valueOf(false)) : state
		);
		world.setBlockState(pos, state.getFluidState().getBlockState(), Block.NOTIFY_ALL);
		world.spawnEntity(fallingBlockEntity);
		return fallingBlockEntity;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	public void setFallingBlockPos(BlockPos pos) {
		this.dataTracker.set(BLOCK_POS, pos);
	}

	public BlockPos getFallingBlockPos() {
		return this.dataTracker.get(BLOCK_POS);
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
	}

	@Override
	public boolean collides() {
		return !this.isRemoved();
	}

	@Override
	public void tick() {
		if (this.block.isAir()) {
			this.discard();
		} else if (this.world.isClient && this.discardTime > 0L) {
			if (System.currentTimeMillis() >= this.discardTime) {
				super.setRemoved(Entity.RemovalReason.DISCARDED);
			}
		} else {
			Block block = this.block.getBlock();
			this.timeFalling++;
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
			}

			this.move(MovementType.SELF, this.getVelocity());
			if (!this.world.isClient) {
				BlockPos blockPos = this.getBlockPos();
				boolean bl = this.block.getBlock() instanceof ConcretePowderBlock;
				boolean bl2 = bl && this.world.getFluidState(blockPos).isIn(FluidTags.WATER);
				double d = this.getVelocity().lengthSquared();
				if (bl && d > 1.0) {
					BlockHitResult blockHitResult = this.world
						.raycast(
							new RaycastContext(
								new Vec3d(this.prevX, this.prevY, this.prevZ), this.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, this
							)
						);
					if (blockHitResult.getType() != HitResult.Type.MISS && this.world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER)) {
						blockPos = blockHitResult.getBlockPos();
						bl2 = true;
					}
				}

				if (this.onGround || bl2) {
					BlockState blockState = this.world.getBlockState(blockPos);
					this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
					if (!blockState.isOf(Blocks.MOVING_PISTON)) {
						if (!this.destroyedOnLanding) {
							boolean bl3 = blockState.canReplace(new AutomaticItemPlacementContext(this.world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
							boolean bl4 = FallingBlock.canFallThrough(this.world.getBlockState(blockPos.down())) && (!bl || !bl2);
							boolean bl5 = this.block.canPlaceAt(this.world, blockPos) && !bl4;
							if (bl3 && bl5) {
								if (this.block.contains(Properties.WATERLOGGED) && this.world.getFluidState(blockPos).getFluid() == Fluids.WATER) {
									this.block = this.block.with(Properties.WATERLOGGED, Boolean.valueOf(true));
								}

								if (this.world.setBlockState(blockPos, this.block, Block.NOTIFY_ALL)) {
									((ServerWorld)this.world)
										.getChunkManager()
										.threadedAnvilChunkStorage
										.sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.world.getBlockState(blockPos)));
									this.discard();
									if (block instanceof LandingBlock) {
										((LandingBlock)block).onLanding(this.world, blockPos, this.block, blockState, this);
									}

									if (this.blockEntityData != null && this.block.hasBlockEntity()) {
										BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
										if (blockEntity != null) {
											NbtCompound nbtCompound = blockEntity.createNbt();

											for (String string : this.blockEntityData.getKeys()) {
												nbtCompound.put(string, this.blockEntityData.get(string).copy());
											}

											try {
												blockEntity.readNbt(nbtCompound);
											} catch (Exception var15) {
												field_36333.error("Failed to load block entity from falling block", (Throwable)var15);
											}

											blockEntity.markDirty();
										}
									}
								} else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
									this.discard();
									this.onDestroyedOnLanding(block, blockPos);
									this.dropItem(block);
								}
							} else {
								this.discard();
								if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
									this.onDestroyedOnLanding(block, blockPos);
									this.dropItem(block);
								}
							}
						} else {
							this.discard();
							this.onDestroyedOnLanding(block, blockPos);
						}
					}
				} else if (!this.world.isClient
					&& (this.timeFalling > 100 && (blockPos.getY() <= this.world.getBottomY() || blockPos.getY() > this.world.getTopY()) || this.timeFalling > 600)) {
					if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
						this.dropItem(block);
					}

					this.discard();
				}
			}

			this.setVelocity(this.getVelocity().multiply(0.98));
		}
	}

	@Override
	public void setRemoved(Entity.RemovalReason reason) {
		if (this.world.shouldRemoveEntityLater(reason)) {
			this.discardTime = System.currentTimeMillis() + 50L;
		} else {
			super.setRemoved(reason);
		}
	}

	public void onDestroyedOnLanding(Block block, BlockPos pos) {
		if (block instanceof LandingBlock) {
			((LandingBlock)block).onDestroyedOnLanding(this.world, pos, this);
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (!this.hurtEntities) {
			return false;
		} else {
			int i = MathHelper.ceil(fallDistance - 1.0F);
			if (i < 0) {
				return false;
			} else {
				Predicate<Entity> predicate;
				DamageSource damageSource2;
				if (this.block.getBlock() instanceof LandingBlock) {
					LandingBlock landingBlock = (LandingBlock)this.block.getBlock();
					predicate = landingBlock.getEntityPredicate();
					damageSource2 = landingBlock.getDamageSource();
				} else {
					predicate = EntityPredicates.EXCEPT_SPECTATOR;
					damageSource2 = DamageSource.FALLING_BLOCK;
				}

				float f = (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax);
				this.world.getOtherEntities(this, this.getBoundingBox(), predicate).forEach(entity -> entity.damage(damageSource2, f));
				boolean bl = this.block.isIn(BlockTags.ANVIL);
				if (bl && f > 0.0F && this.random.nextFloat() < 0.05F + (float)i * 0.05F) {
					BlockState blockState = AnvilBlock.getLandingState(this.block);
					if (blockState == null) {
						this.destroyedOnLanding = true;
					} else {
						this.block = blockState;
					}
				}

				return false;
			}
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.put("BlockState", NbtHelper.fromBlockState(this.block));
		nbt.putInt("Time", this.timeFalling);
		nbt.putBoolean("DropItem", this.dropItem);
		nbt.putBoolean("HurtEntities", this.hurtEntities);
		nbt.putFloat("FallHurtAmount", this.fallHurtAmount);
		nbt.putInt("FallHurtMax", this.fallHurtMax);
		if (this.blockEntityData != null) {
			nbt.put("TileEntityData", this.blockEntityData);
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.block = NbtHelper.toBlockState(nbt.getCompound("BlockState"));
		this.timeFalling = nbt.getInt("Time");
		if (nbt.contains("HurtEntities", NbtElement.NUMBER_TYPE)) {
			this.hurtEntities = nbt.getBoolean("HurtEntities");
			this.fallHurtAmount = nbt.getFloat("FallHurtAmount");
			this.fallHurtMax = nbt.getInt("FallHurtMax");
		} else if (this.block.isIn(BlockTags.ANVIL)) {
			this.hurtEntities = true;
		}

		if (nbt.contains("DropItem", NbtElement.NUMBER_TYPE)) {
			this.dropItem = nbt.getBoolean("DropItem");
		}

		if (nbt.contains("TileEntityData", NbtElement.COMPOUND_TYPE)) {
			this.blockEntityData = nbt.getCompound("TileEntityData");
		}

		if (this.block.isAir()) {
			this.block = Blocks.SAND.getDefaultState();
		}
	}

	public void setHurtEntities(float fallHurtAmount, int fallHurtMax) {
		this.hurtEntities = true;
		this.fallHurtAmount = fallHurtAmount;
		this.fallHurtMax = fallHurtMax;
	}

	@Override
	public boolean doesRenderOnFire() {
		return false;
	}

	@Override
	public void populateCrashReport(CrashReportSection section) {
		super.populateCrashReport(section);
		section.add("Immitating BlockState", this.block.toString());
	}

	public BlockState getBlockState() {
		return this.block;
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		this.block = Block.getStateFromRawId(packet.getEntityData());
		this.intersectionChecked = true;
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		this.setPosition(d, e, f);
		this.setFallingBlockPos(this.getBlockPos());
	}
}
