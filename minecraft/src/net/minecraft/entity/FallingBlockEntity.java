package net.minecraft.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_7320;
import net.minecraft.class_7322;
import net.minecraft.class_7323;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ConfiguredStructureFeatureTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class FallingBlockEntity extends Entity {
	public static final Vec3i[] field_38524 = new Vec3i[]{
		new Vec3i(0, 0, 0),
		new Vec3i(1, 0, 0),
		new Vec3i(0, 0, 1),
		new Vec3i(0, 0, -1),
		new Vec3i(-1, 0, 0),
		new Vec3i(1, 0, -1),
		new Vec3i(1, 0, 1),
		new Vec3i(-1, 0, 1),
		new Vec3i(-1, 0, -1)
	};
	private static final Logger field_36333 = LogUtils.getLogger();
	public static final Predicate<Entity> field_38525 = EntityPredicates.EXCEPT_SPECTATOR.and(entity -> !(entity instanceof FallingBlockEntity));
	private BlockState block = Blocks.SAND.getDefaultState();
	public int timeFalling;
	public boolean dropItem = true;
	private boolean destroyedOnLanding;
	private boolean hurtEntities;
	private int fallHurtMax = 40;
	private float fallHurtAmount;
	@Nullable
	public NbtCompound blockEntityData;
	private boolean field_38519;
	@Nullable
	private Direction field_38520 = null;
	@Nullable
	private PlayerEntity field_38521 = null;
	private boolean field_38522;
	private float field_38523;
	private float field_38518;
	protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

	public FallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(entityType, world);
	}

	public FallingBlockEntity(World world, double x, double y, double z, BlockState block) {
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

	public FallingBlockEntity(ServerPlayerEntity serverPlayerEntity, BlockState blockState, Direction direction) {
		this(
			serverPlayerEntity.world,
			serverPlayerEntity.getX(),
			serverPlayerEntity.getY() + (double)serverPlayerEntity.getStandingEyeHeight(),
			serverPlayerEntity.getZ(),
			blockState
		);
		this.field_38521 = serverPlayerEntity;
		if (this.block.contains(Properties.FACING)) {
			this.block = this.block.with(Properties.FACING, Direction.random(this.world.random));
		}

		if (this.block.isOf(Blocks.END_PORTAL_FRAME) && this.world instanceof ServerWorld serverWorld) {
			BlockPos blockPos = serverPlayerEntity.getBlockPos();
			BlockPos blockPos2 = serverWorld.locateStructure(ConfiguredStructureFeatureTags.EYE_OF_ENDER_LOCATED, blockPos, 100, false);
			if (blockPos2 != null) {
				BlockPos blockPos3 = blockPos2.subtract(blockPos);
				Direction direction2 = Direction.getFacing((float)blockPos3.getX(), (float)blockPos3.getY(), (float)blockPos3.getZ());
				this.block = this.block.with(class_7322.field_38553, direction2);
			}
		}

		this.field_38520 = direction;
	}

	/**
	 * Spawns a falling block entity at {@code pos} from the block {@code state}.
	 * @return the spawned entity
	 */
	public static FallingBlockEntity spawnFromBlock(World world, BlockPos pos, BlockState state) {
		return method_42818(world, pos, state, Vec3d.ZERO);
	}

	public static FallingBlockEntity method_42818(World world, BlockPos blockPos, BlockState blockState, Vec3d vec3d) {
		boolean bl = blockState.contains(Properties.WATERLOGGED);
		FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
			world,
			(double)blockPos.getX() + 0.5,
			(double)blockPos.getY(),
			(double)blockPos.getZ() + 0.5,
			bl ? blockState.with(Properties.WATERLOGGED, Boolean.valueOf(false)) : blockState
		);
		fallingBlockEntity.setVelocity(vec3d);
		if (vec3d.lengthSquared() > 0.0) {
			fallingBlockEntity.field_38520 = Direction.getFacing(vec3d.x, 0.0, vec3d.z);
		}

		world.setBlockState(blockPos, bl ? blockState.getFluidState().getBlockState() : Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
		world.spawnEntity(fallingBlockEntity);
		return fallingBlockEntity;
	}

	@Override
	public boolean isAttackable() {
		return true;
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

	public float method_42815(float f) {
		return MathHelper.lerp(f, this.field_38518, this.field_38523);
	}

	@Override
	public void tick() {
		if (this.block.isAir()) {
			this.discard();
		} else {
			Block block = this.block.getBlock();
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
			}

			this.move(MovementType.SELF, this.getVelocity());
			Vec3d vec3d = this.getPos().subtract(this.prevX, this.prevY, this.prevZ);
			this.field_38518 = this.field_38523;
			this.field_38523 = (float)((double)this.field_38523 + vec3d.length() * 50.0);
			double d = this.getVelocity().y < 0.0 ? 3.0 : 2.0;
			if (!this.field_38519 && !this.getFallingBlockPos().isWithinDistance(new BlockPos(this.getPos()), d)) {
				this.field_38519 = true;
			}

			if (this.field_38519 && !this.world.isClient()) {
				List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()), field_38525);
				list.removeIf(entity -> !this.method_42816(entity));
				if (!list.isEmpty()) {
					this.setVelocity(this.getVelocity().negate().multiply(0.1));
					this.velocityDirty = true;
				}
			}

			this.timeFalling++;
			if (!this.world.isClient) {
				BlockPos blockPos = this.getBlockPos();
				FluidState fluidState = this.world.getFluidState(blockPos);
				boolean bl = this.block.getBlock() instanceof ConcretePowderBlock;
				boolean bl2 = bl && fluidState.isIn(FluidTags.WATER);
				double e = this.getVelocity().lengthSquared();
				if (bl && e > 1.0) {
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

				if (!this.field_38522 && fluidState.isIn(FluidTags.LAVA)) {
					BlockState blockState = this.method_42821(this.block);
					if (blockState != null) {
						this.block = blockState;
						this.field_38522 = true;
					}
				}

				if (this.onGround || bl2) {
					BlockState blockState = this.world.getBlockState(blockPos);
					if (!blockState.isAir() && !blockState.isFullCube(this.world, blockPos) && this.getY() - (double)blockPos.getY() > 0.8) {
						blockPos = blockPos.up();
						blockState = this.world.getBlockState(blockPos);
					}

					this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
					if (!blockState.isOf(Blocks.MOVING_PISTON) && !this.destroyedOnLanding) {
						boolean bl3 = blockState.canReplace(new AutomaticItemPlacementContext(this.world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
						boolean bl4 = FallingBlock.canFallThrough(this.world.getBlockState(blockPos.down())) && (!bl || !bl2);
						boolean bl5 = this.block.canPlaceAt(this.world, blockPos) && !bl4;
						if (bl3 && !bl4) {
							if (!bl5 && !this.block.isOf(Blocks.GENERIC_ITEM_BLOCK)) {
								BlockState blockState2 = class_7323.method_42879(this.block);
								if (blockState2 != null && blockState2.canPlaceAt(this.world, blockPos)) {
									this.block = blockState2;
									bl5 = true;
								}
							} else if (this.block.isOf(Blocks.GENERIC_ITEM_BLOCK)) {
								BlockState blockState2 = class_7323.method_42880(this.block);
								if (blockState2 != null && blockState2.canPlaceAt(this.world, blockPos)) {
									this.block = blockState2;
								}
							}
						}

						if (bl3 && bl5) {
							if (this.block.contains(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
								this.block = this.block.with(Properties.WATERLOGGED, Boolean.valueOf(true));
							}

							if (block instanceof BedBlock) {
								BlockPos blockPos2 = blockPos.offset(BedBlock.getOppositePartDirection(this.block));
								if (this.world.getBlockState(blockPos2).isAir()) {
									BedPart bedPart = this.block.get(BedBlock.PART);

									bedPart = switch (bedPart) {
										case HEAD -> BedPart.FOOT;
										case FOOT -> BedPart.HEAD;
									};
									this.world.setBlockState(blockPos2, this.block.with(BedBlock.PART, bedPart), Block.NOTIFY_ALL);
								}
							}

							BlockState blockState2 = Block.postProcessState(this.block, this.world, blockPos);
							if (this.world.setBlockState(blockPos, blockState2)) {
								((ServerWorld)this.world)
									.getChunkManager()
									.threadedAnvilChunkStorage
									.sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.world.getBlockState(blockPos)));
								if (this.field_38521 != null && this.block.isIn(BlockTags.FRAGILE) && this.world.random.nextBoolean()) {
									this.world.breakBlock(blockPos, false, this.field_38521);
									((ServerWorld)this.world)
										.getChunkManager()
										.threadedAnvilChunkStorage
										.sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.world.getBlockState(blockPos)));
									this.discard();
									return;
								}

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
										} catch (Exception var25) {
											field_36333.error("Failed to load block entity from falling block", (Throwable)var25);
										}

										blockEntity.markDirty();
									}
								}

								boolean bl6 = false;
								if (!this.field_38522 && this.world.getBlockState(blockPos.down()).isOf(Blocks.CRAFTING_TABLE)) {
									this.field_38520 = this.field_38520 == null ? Direction.Type.HORIZONTAL.random(this.world.random) : this.field_38520;

									for (Vec3i vec3i : field_38524) {
										if (this.method_42819(blockPos.add(vec3i), this.field_38520)) {
											bl6 = true;
											break;
										}
									}
								}

								if (!bl6 && this.block.isOf(Blocks.CRAFTING_TABLE)) {
									BlockPos blockPos3 = blockPos.down();
									this.field_38520 = this.field_38520 == null ? Direction.Type.HORIZONTAL.random(this.world.random) : this.field_38520;

									for (Vec3i vec3i2 : field_38524) {
										if (this.method_42819(blockPos3.add(vec3i2), this.field_38520)) {
											bl6 = true;
											break;
										}
									}

									if (!bl6) {
										for (Direction direction : Direction.Type.HORIZONTAL) {
											BlockPos blockPos4 = blockPos3.offset(direction);
											if (this.world.getBlockState(blockPos4).isAir()) {
												for (Vec3i vec3i3 : field_38524) {
													if (this.method_42819(blockPos4.add(vec3i3), this.field_38520)) {
														bl6 = true;
														break;
													}
												}
											}

											if (bl6) {
												break;
											}
										}
									}
								}
							}
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

	@Nullable
	private BlockState method_42821(BlockState blockState) {
		ItemStack itemStack = class_7320.method_42867(blockState);
		if (itemStack.isEmpty()) {
			return null;
		} else {
			SimpleInventory simpleInventory = new SimpleInventory(itemStack);
			Optional<SmeltingRecipe> optional = this.world.getServer().getRecipeManager().getFirstMatch(RecipeType.SMELTING, simpleInventory, this.world);
			return !optional.isEmpty() && ((SmeltingRecipe)optional.get()).getOutput().getCount() <= 1
				? (BlockState)class_7320.method_42858(((SmeltingRecipe)optional.get()).getOutput()).orElse(null)
				: null;
		}
	}

	private boolean method_42819(BlockPos blockPos, Direction direction) {
		Vec3i vec3i = direction.getOpposite().getVector();
		Vec3i vec3i2 = direction.getOpposite().rotateYClockwise().getVector();
		int i = 0;
		CraftingInventory craftingInventory = new CraftingInventory(null, 3, 3);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				BlockPos blockPos2 = blockPos.add(vec3i.getX() * j + vec3i2.getX() * k, 0, vec3i.getZ() * j + vec3i2.getZ() * k);
				BlockState blockState = this.world.getBlockState(blockPos2);
				ItemStack itemStack = class_7320.method_42867(blockState);
				if (!itemStack.isEmpty()) {
					craftingInventory.setStack(i, itemStack);
				}

				i++;
			}
		}

		Optional<CraftingRecipe> optional = this.world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, this.world);
		Optional<BlockState> optional2 = optional.flatMap(craftingRecipe -> class_7320.method_42858(craftingRecipe.getOutput()));
		optional2.ifPresent(blockStatex -> {
			for (BlockPos blockPos2x : BlockPos.iterate(blockPos.add(-1, 0, -1), blockPos.add(1, 0, 1))) {
				if (!class_7320.method_42867(this.world.getBlockState(blockPos2x)).isEmpty()) {
					this.world.setBlockState(blockPos2x, blockStatex, Block.NOTIFY_ALL);
				}
			}
		});
		return optional.isPresent();
	}

	private boolean method_42816(Entity entity) {
		DamageSource damageSource = this.field_38521 != null ? DamageSource.thrownProjectile(this, this.field_38521) : DamageSource.FALLING_BLOCK;
		if (this.getVelocity().lengthSquared() > 0.125) {
			if (entity instanceof SkeletonEntity skeletonEntity && class_7320.method_42867(this.block).isOf(Items.SPYGLASS)) {
				skeletonEntity.damage(damageSource, 0.0F);
				if (skeletonEntity.method_42827() < 2) {
					skeletonEntity.method_42828();
				}

				this.remove(Entity.RemovalReason.KILLED);
				return true;
			}

			if (entity instanceof LivingEntity livingEntity
				&& livingEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
				&& (this.block.isOf(Blocks.CARVED_PUMPKIN) || this.block.isOf(Blocks.BARREL) || entity instanceof PlayerEntity)) {
				livingEntity.damage(damageSource, entity instanceof PlayerEntity ? 0.125F : 0.0F);
				ItemStack itemStack = class_7320.method_42867(this.block);
				livingEntity.equipStack(EquipmentSlot.HEAD, itemStack);
				if (this.block.isOf(Blocks.BARREL)) {
					this.world.playSoundFromEntity(null, entity, SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}

				this.remove(Entity.RemovalReason.KILLED);
				return true;
			}
		}

		if ((this.block.isOf(Blocks.CACTUS) || this.block.isOf(Blocks.POINTED_DRIPSTONE)) && entity instanceof SheepEntity sheepEntity) {
			if (sheepEntity.isShearable()) {
				sheepEntity.sheared(SoundCategory.NEUTRAL);
			}
		} else if (this.block.isIn(BlockTags.WOOL) && entity instanceof SheepEntity sheepEntity2) {
			sheepEntity2.damage(damageSource, 0.0F);
			if (sheepEntity2.isShearable()) {
				sheepEntity2.sheared(SoundCategory.NEUTRAL);
			}

			if (this.block.isOf(Blocks.WHITE_WOOL)) {
				sheepEntity2.setColor(DyeColor.WHITE);
			} else if (this.block.isOf(Blocks.ORANGE_WOOL)) {
				sheepEntity2.setColor(DyeColor.ORANGE);
			} else if (this.block.isOf(Blocks.MAGENTA_WOOL)) {
				sheepEntity2.setColor(DyeColor.MAGENTA);
			} else if (this.block.isOf(Blocks.LIGHT_BLUE_WOOL)) {
				sheepEntity2.setColor(DyeColor.LIGHT_BLUE);
			} else if (this.block.isOf(Blocks.YELLOW_WOOL)) {
				sheepEntity2.setColor(DyeColor.YELLOW);
			} else if (this.block.isOf(Blocks.LIME_WOOL)) {
				sheepEntity2.setColor(DyeColor.LIME);
			} else if (this.block.isOf(Blocks.PINK_WOOL)) {
				sheepEntity2.setColor(DyeColor.PINK);
			} else if (this.block.isOf(Blocks.GRAY_WOOL)) {
				sheepEntity2.setColor(DyeColor.GRAY);
			} else if (this.block.isOf(Blocks.LIGHT_GRAY_WOOL)) {
				sheepEntity2.setColor(DyeColor.LIGHT_GRAY);
			} else if (this.block.isOf(Blocks.CYAN_WOOL)) {
				sheepEntity2.setColor(DyeColor.CYAN);
			} else if (this.block.isOf(Blocks.PURPLE_WOOL)) {
				sheepEntity2.setColor(DyeColor.PURPLE);
			} else if (this.block.isOf(Blocks.BLUE_WOOL)) {
				sheepEntity2.setColor(DyeColor.BLUE);
			} else if (this.block.isOf(Blocks.BROWN_WOOL)) {
				sheepEntity2.setColor(DyeColor.BROWN);
			} else if (this.block.isOf(Blocks.GREEN_WOOL)) {
				sheepEntity2.setColor(DyeColor.GREEN);
			} else if (this.block.isOf(Blocks.RED_WOOL)) {
				sheepEntity2.setColor(DyeColor.RED);
			} else if (this.block.isOf(Blocks.BLACK_WOOL)) {
				sheepEntity2.setColor(DyeColor.BLACK);
			}

			sheepEntity2.setSheared(false);
			this.remove(Entity.RemovalReason.KILLED);
			return true;
		}

		float f = 10.0F;
		float g = this.block.getBlock().getHardness();
		float h = (float)Math.ceil(this.getVelocity().length() * 10.0 * (double)g);
		if (h > 0.0F && (entity instanceof LivingEntity || entity instanceof EndCrystalEntity)) {
			entity.damage(damageSource, h);
			entity.setVelocity(entity.getVelocity().add(this.getVelocity().multiply(0.5 * (double)MathHelper.sqrt(this.block.getBlock().getBlastResistance()))));
			if (entity.isOnGround()) {
				entity.setVelocity(entity.getVelocity().add(0.0, 0.5, 0.0));
			}

			return true;
		} else {
			return false;
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

	@Override
	public boolean isPushable() {
		return false;
	}
}
