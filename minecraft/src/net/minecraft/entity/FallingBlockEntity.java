package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class FallingBlockEntity extends Entity {
	private BlockState block = Blocks.field_10102.getDefaultState();
	public int timeFalling;
	public boolean dropItem = true;
	private boolean destroyedOnLanding;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private float fallHurtAmount = 2.0F;
	public CompoundTag blockEntityData;
	protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

	public FallingBlockEntity(World world) {
		super(EntityType.FALLING_BLOCK, world);
	}

	public FallingBlockEntity(World world, double d, double e, double f, BlockState blockState) {
		this(world);
		this.block = blockState;
		this.field_6033 = true;
		this.setPosition(d, e + (double)((1.0F - this.getHeight()) / 2.0F), f);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
		this.setFallingBlockPos(new BlockPos(this));
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	public void setFallingBlockPos(BlockPos blockPos) {
		this.dataTracker.set(BLOCK_POS, blockPos);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getFallingBlockPos() {
		return this.dataTracker.get(BLOCK_POS);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
	}

	@Override
	public boolean doesCollide() {
		return !this.invalid;
	}

	@Override
	public void update() {
		if (this.block.isAir()) {
			this.invalidate();
		} else {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			Block block = this.block.getBlock();
			if (this.timeFalling++ == 0) {
				BlockPos blockPos = new BlockPos(this);
				if (this.world.getBlockState(blockPos).getBlock() == block) {
					this.world.clearBlockState(blockPos);
				} else if (!this.world.isClient) {
					this.invalidate();
					return;
				}
			}

			if (!this.isUnaffectedByGravity()) {
				this.velocityY -= 0.04F;
			}

			this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
			if (!this.world.isClient) {
				BlockPos blockPos = new BlockPos(this);
				boolean bl = this.block.getBlock() instanceof ConcretePowderBlock;
				boolean bl2 = bl && this.world.getFluidState(blockPos).matches(FluidTags.field_15517);
				double d = this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ;
				if (bl && d > 1.0) {
					BlockHitResult blockHitResult = this.world
						.rayTrace(
							new RayTraceContext(
								new Vec3d(this.prevX, this.prevY, this.prevZ),
								new Vec3d(this.x, this.y, this.z),
								RayTraceContext.ShapeType.field_17558,
								RayTraceContext.FluidHandling.field_1345,
								this
							)
						);
					if (blockHitResult.getType() != HitResult.Type.NONE && this.world.getFluidState(blockHitResult.getBlockPos()).matches(FluidTags.field_15517)) {
						blockPos = blockHitResult.getBlockPos();
						bl2 = true;
					}
				}

				if (this.onGround || bl2) {
					BlockState blockState = this.world.getBlockState(blockPos);
					this.velocityX *= 0.7F;
					this.velocityZ *= 0.7F;
					this.velocityY *= -0.5;
					if (blockState.getBlock() != Blocks.field_10008) {
						this.invalidate();
						if (!this.destroyedOnLanding) {
							if (bl2
								|| blockState.method_11587(new AutomaticItemPlacementContext(this.world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))
									&& this.block.canPlaceAt(this.world, blockPos)) {
								if (this.block.contains(Properties.WATERLOGGED) && this.world.getFluidState(blockPos).getFluid() == Fluids.WATER) {
									this.block = this.block.with(Properties.WATERLOGGED, Boolean.valueOf(true));
								}

								if (this.world.setBlockState(blockPos, this.block, 3)) {
									if (block instanceof FallingBlock) {
										((FallingBlock)block).onLanding(this.world, blockPos, this.block, blockState);
									}

									if (this.blockEntityData != null && block instanceof BlockEntityProvider) {
										BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
										if (blockEntity != null) {
											CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());

											for (String string : this.blockEntityData.getKeys()) {
												Tag tag = this.blockEntityData.getTag(string);
												if (!"x".equals(string) && !"y".equals(string) && !"z".equals(string)) {
													compoundTag.put(string, tag.copy());
												}
											}

											blockEntity.fromTag(compoundTag);
											blockEntity.markDirty();
										}
									}
								} else if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
									this.dropItem(block);
								}
							} else if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
								this.dropItem(block);
							}
						} else if (block instanceof FallingBlock) {
							((FallingBlock)block).onDestroyedOnLanding(this.world, blockPos);
						}
					}
				} else if (!this.world.isClient && (this.timeFalling > 100 && (blockPos.getY() < 1 || blockPos.getY() > 256) || this.timeFalling > 600)) {
					if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
						this.dropItem(block);
					}

					this.invalidate();
				}
			}

			this.velocityX *= 0.98F;
			this.velocityY *= 0.98F;
			this.velocityZ *= 0.98F;
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
		if (this.hurtEntities) {
			int i = MathHelper.ceil(f - 1.0F);
			if (i > 0) {
				List<Entity> list = Lists.<Entity>newArrayList(this.world.getVisibleEntities(this, this.getBoundingBox()));
				boolean bl = this.block.matches(BlockTags.field_15486);
				DamageSource damageSource = bl ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

				for (Entity entity : list) {
					entity.damage(damageSource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
				}

				if (bl && (double)this.random.nextFloat() < 0.05F + (double)i * 0.05) {
					BlockState blockState = AnvilBlock.getLandingState(this.block);
					if (blockState == null) {
						this.destroyedOnLanding = true;
					} else {
						this.block = blockState;
					}
				}
			}
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.put("BlockState", TagHelper.serializeBlockState(this.block));
		compoundTag.putInt("Time", this.timeFalling);
		compoundTag.putBoolean("DropItem", this.dropItem);
		compoundTag.putBoolean("HurtEntities", this.hurtEntities);
		compoundTag.putFloat("FallHurtAmount", this.fallHurtAmount);
		compoundTag.putInt("FallHurtMax", this.fallHurtMax);
		if (this.blockEntityData != null) {
			compoundTag.put("TileEntityData", this.blockEntityData);
		}
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		this.block = TagHelper.deserializeBlockState(compoundTag.getCompound("BlockState"));
		this.timeFalling = compoundTag.getInt("Time");
		if (compoundTag.containsKey("HurtEntities", 99)) {
			this.hurtEntities = compoundTag.getBoolean("HurtEntities");
			this.fallHurtAmount = compoundTag.getFloat("FallHurtAmount");
			this.fallHurtMax = compoundTag.getInt("FallHurtMax");
		} else if (this.block.matches(BlockTags.field_15486)) {
			this.hurtEntities = true;
		}

		if (compoundTag.containsKey("DropItem", 99)) {
			this.dropItem = compoundTag.getBoolean("DropItem");
		}

		if (compoundTag.containsKey("TileEntityData", 10)) {
			this.blockEntityData = compoundTag.getCompound("TileEntityData");
		}

		if (this.block.isAir()) {
			this.block = Blocks.field_10102.getDefaultState();
		}
	}

	@Environment(EnvType.CLIENT)
	public World getWorldClient() {
		return this.world;
	}

	public void setHurtEntities(boolean bl) {
		this.hurtEntities = bl;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean doesRenderOnFire() {
		return false;
	}

	@Override
	public void populateCrashReport(CrashReportSection crashReportSection) {
		super.populateCrashReport(crashReportSection);
		crashReportSection.add("Immitating BlockState", this.block.toString());
	}

	public BlockState getBlockState() {
		return this.block;
	}

	@Override
	public boolean method_5833() {
		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
	}
}
