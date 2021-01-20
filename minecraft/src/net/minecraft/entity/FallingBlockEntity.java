package net.minecraft.entity;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
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

public class FallingBlockEntity extends Entity {
	private BlockState block = Blocks.SAND.getDefaultState();
	public int timeFalling;
	public boolean dropItem = true;
	private boolean destroyedOnLanding;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private float fallHurtAmount;
	public CompoundTag blockEntityData;
	protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

	public FallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(entityType, world);
	}

	public FallingBlockEntity(World world, double x, double y, double z, BlockState block) {
		this(EntityType.FALLING_BLOCK, world);
		this.block = block;
		this.inanimate = true;
		this.setPosition(x, y + (double)((1.0F - this.getHeight()) / 2.0F), z);
		this.setVelocity(Vec3d.ZERO);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
		this.setFallingBlockPos(this.getBlockPos());
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	public void setFallingBlockPos(BlockPos pos) {
		this.dataTracker.set(BLOCK_POS, pos);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getFallingBlockPos() {
		return this.dataTracker.get(BLOCK_POS);
	}

	@Override
	protected boolean canClimb() {
		return false;
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
		} else {
			Block block = this.block.getBlock();
			if (this.timeFalling++ == 0) {
				BlockPos blockPos = this.getBlockPos();
				if (this.world.getBlockState(blockPos).isOf(block)) {
					this.world.removeBlock(blockPos, false);
				} else if (!this.world.isClient) {
					this.discard();
					return;
				}
			}

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
						this.discard();
						if (!this.destroyedOnLanding) {
							boolean bl3 = blockState.canReplace(new AutomaticItemPlacementContext(this.world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
							boolean bl4 = FallingBlock.canFallThrough(this.world.getBlockState(blockPos.down())) && (!bl || !bl2);
							boolean bl5 = this.block.canPlaceAt(this.world, blockPos) && !bl4;
							if (bl3 && bl5) {
								if (this.block.contains(Properties.WATERLOGGED) && this.world.getFluidState(blockPos).getFluid() == Fluids.WATER) {
									this.block = this.block.with(Properties.WATERLOGGED, Boolean.valueOf(true));
								}

								if (this.world.setBlockState(blockPos, this.block, 3)) {
									if (block instanceof LandingBlock) {
										((LandingBlock)block).onLanding(this.world, blockPos, this.block, blockState, this);
									}

									if (this.blockEntityData != null && this.block.hasBlockEntity()) {
										BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
										if (blockEntity != null) {
											CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());

											for (String string : this.blockEntityData.getKeys()) {
												Tag tag = this.blockEntityData.get(string);
												if (!"x".equals(string) && !"y".equals(string) && !"z".equals(string)) {
													compoundTag.put(string, tag.copy());
												}
											}

											blockEntity.fromTag(compoundTag);
											blockEntity.markDirty();
										}
									}
								} else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
									this.onDestroyedOnLanding(block, blockPos);
									this.dropItem(block);
								}
							} else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
								this.onDestroyedOnLanding(block, blockPos);
								this.dropItem(block);
							}
						} else {
							this.onDestroyedOnLanding(block, blockPos);
						}
					}
				} else if (!this.world.isClient
					&& (
						this.timeFalling > 100 && (blockPos.getY() <= this.world.getBottomSectionLimit() || blockPos.getY() > this.world.getTopHeightLimit())
							|| this.timeFalling > 600
					)) {
					if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
						this.dropItem(block);
					}

					this.discard();
				}
			}

			this.setVelocity(this.getVelocity().multiply(0.98));
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
				if (bl && (double)this.random.nextFloat() < 0.05F + (double)i * 0.05) {
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
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.put("BlockState", NbtHelper.fromBlockState(this.block));
		tag.putInt("Time", this.timeFalling);
		tag.putBoolean("DropItem", this.dropItem);
		tag.putBoolean("HurtEntities", this.hurtEntities);
		tag.putFloat("FallHurtAmount", this.fallHurtAmount);
		tag.putInt("FallHurtMax", this.fallHurtMax);
		if (this.blockEntityData != null) {
			tag.put("TileEntityData", this.blockEntityData);
		}
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.block = NbtHelper.toBlockState(tag.getCompound("BlockState"));
		this.timeFalling = tag.getInt("Time");
		if (tag.contains("HurtEntities", 99)) {
			this.hurtEntities = tag.getBoolean("HurtEntities");
			this.fallHurtAmount = tag.getFloat("FallHurtAmount");
			this.fallHurtMax = tag.getInt("FallHurtMax");
		} else if (this.block.isIn(BlockTags.ANVIL)) {
			this.hurtEntities = true;
		}

		if (tag.contains("DropItem", 99)) {
			this.dropItem = tag.getBoolean("DropItem");
		}

		if (tag.contains("TileEntityData", 10)) {
			this.blockEntityData = tag.getCompound("TileEntityData");
		}

		if (this.block.isAir()) {
			this.block = Blocks.SAND.getDefaultState();
		}
	}

	@Environment(EnvType.CLIENT)
	public World getWorldClient() {
		return this.world;
	}

	public void setHurtEntities(float fallHurtAmount, int fallHurtMax) {
		this.hurtEntities = true;
		this.fallHurtAmount = fallHurtAmount;
		this.fallHurtMax = fallHurtMax;
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		this.block = Block.getStateFromRawId(packet.getEntityData());
		this.inanimate = true;
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		this.setPosition(d, e + (double)((1.0F - this.getHeight()) / 2.0F), f);
		this.setFallingBlockPos(this.getBlockPos());
	}
}
