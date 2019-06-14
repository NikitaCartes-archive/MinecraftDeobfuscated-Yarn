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
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class FallingBlockEntity extends Entity {
	private BlockState field_7188 = Blocks.field_10102.method_9564();
	public int timeFalling;
	public boolean dropItem = true;
	private boolean destroyedOnLanding;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private float fallHurtAmount = 2.0F;
	public CompoundTag blockEntityData;
	protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

	public FallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(entityType, world);
	}

	public FallingBlockEntity(World world, double d, double e, double f, BlockState blockState) {
		this(EntityType.field_6089, world);
		this.field_7188 = blockState;
		this.field_6033 = true;
		this.setPosition(d, e + (double)((1.0F - this.getHeight()) / 2.0F), f);
		this.method_18799(Vec3d.ZERO);
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
		this.setFallingBlockPos(new BlockPos(this));
	}

	@Override
	public boolean canPlayerAttack() {
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
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public void tick() {
		if (this.field_7188.isAir()) {
			this.remove();
		} else {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			Block block = this.field_7188.getBlock();
			if (this.timeFalling++ == 0) {
				BlockPos blockPos = new BlockPos(this);
				if (this.field_6002.method_8320(blockPos).getBlock() == block) {
					this.field_6002.clearBlockState(blockPos, false);
				} else if (!this.field_6002.isClient) {
					this.remove();
					return;
				}
			}

			if (!this.hasNoGravity()) {
				this.method_18799(this.method_18798().add(0.0, -0.04, 0.0));
			}

			this.method_5784(MovementType.field_6308, this.method_18798());
			if (!this.field_6002.isClient) {
				BlockPos blockPos = new BlockPos(this);
				boolean bl = this.field_7188.getBlock() instanceof ConcretePowderBlock;
				boolean bl2 = bl && this.field_6002.method_8316(blockPos).matches(FluidTags.field_15517);
				double d = this.method_18798().lengthSquared();
				if (bl && d > 1.0) {
					BlockHitResult blockHitResult = this.field_6002
						.method_17742(
							new RayTraceContext(
								new Vec3d(this.prevX, this.prevY, this.prevZ),
								new Vec3d(this.x, this.y, this.z),
								RayTraceContext.ShapeType.field_17558,
								RayTraceContext.FluidHandling.field_1345,
								this
							)
						);
					if (blockHitResult.getType() != HitResult.Type.field_1333 && this.field_6002.method_8316(blockHitResult.getBlockPos()).matches(FluidTags.field_15517)) {
						blockPos = blockHitResult.getBlockPos();
						bl2 = true;
					}
				}

				if (this.onGround || bl2) {
					BlockState blockState = this.field_6002.method_8320(blockPos);
					this.method_18799(this.method_18798().multiply(0.7, -0.5, 0.7));
					if (blockState.getBlock() != Blocks.field_10008) {
						this.remove();
						if (!this.destroyedOnLanding) {
							if (bl2
								|| blockState.canReplace(new AutomaticItemPlacementContext(this.field_6002, blockPos, Direction.field_11033, ItemStack.EMPTY, Direction.field_11036))
									&& this.field_7188.canPlaceAt(this.field_6002, blockPos)) {
								if (this.field_7188.method_11570(Properties.field_12508) && this.field_6002.method_8316(blockPos).getFluid() == Fluids.WATER) {
									this.field_7188 = this.field_7188.method_11657(Properties.field_12508, Boolean.valueOf(true));
								}

								if (this.field_6002.method_8652(blockPos, this.field_7188, 3)) {
									if (block instanceof FallingBlock) {
										((FallingBlock)block).method_10127(this.field_6002, blockPos, this.field_7188, blockState);
									}

									if (this.blockEntityData != null && block instanceof BlockEntityProvider) {
										BlockEntity blockEntity = this.field_6002.method_8321(blockPos);
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
								} else if (this.dropItem && this.field_6002.getGameRules().getBoolean(GameRules.field_19393)) {
									this.method_5706(block);
								}
							} else if (this.dropItem && this.field_6002.getGameRules().getBoolean(GameRules.field_19393)) {
								this.method_5706(block);
							}
						} else if (block instanceof FallingBlock) {
							((FallingBlock)block).onDestroyedOnLanding(this.field_6002, blockPos);
						}
					}
				} else if (!this.field_6002.isClient && (this.timeFalling > 100 && (blockPos.getY() < 1 || blockPos.getY() > 256) || this.timeFalling > 600)) {
					if (this.dropItem && this.field_6002.getGameRules().getBoolean(GameRules.field_19393)) {
						this.method_5706(block);
					}

					this.remove();
				}
			}

			this.method_18799(this.method_18798().multiply(0.98));
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
		if (this.hurtEntities) {
			int i = MathHelper.ceil(f - 1.0F);
			if (i > 0) {
				List<Entity> list = Lists.<Entity>newArrayList(this.field_6002.method_8335(this, this.method_5829()));
				boolean bl = this.field_7188.matches(BlockTags.field_15486);
				DamageSource damageSource = bl ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

				for (Entity entity : list) {
					entity.damage(damageSource, (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
				}

				if (bl && (double)this.random.nextFloat() < 0.05F + (double)i * 0.05) {
					BlockState blockState = AnvilBlock.method_9346(this.field_7188);
					if (blockState == null) {
						this.destroyedOnLanding = true;
					} else {
						this.field_7188 = blockState;
					}
				}
			}
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.put("BlockState", TagHelper.serializeBlockState(this.field_7188));
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
		this.field_7188 = TagHelper.deserializeBlockState(compoundTag.getCompound("BlockState"));
		this.timeFalling = compoundTag.getInt("Time");
		if (compoundTag.containsKey("HurtEntities", 99)) {
			this.hurtEntities = compoundTag.getBoolean("HurtEntities");
			this.fallHurtAmount = compoundTag.getFloat("FallHurtAmount");
			this.fallHurtMax = compoundTag.getInt("FallHurtMax");
		} else if (this.field_7188.matches(BlockTags.field_15486)) {
			this.hurtEntities = true;
		}

		if (compoundTag.containsKey("DropItem", 99)) {
			this.dropItem = compoundTag.getBoolean("DropItem");
		}

		if (compoundTag.containsKey("TileEntityData", 10)) {
			this.blockEntityData = compoundTag.getCompound("TileEntityData");
		}

		if (this.field_7188.isAir()) {
			this.field_7188 = Blocks.field_10102.method_9564();
		}
	}

	@Environment(EnvType.CLIENT)
	public World method_6966() {
		return this.field_6002;
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
		crashReportSection.add("Immitating BlockState", this.field_7188.toString());
	}

	public BlockState method_6962() {
		return this.field_7188;
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this, Block.method_9507(this.method_6962()));
	}
}
