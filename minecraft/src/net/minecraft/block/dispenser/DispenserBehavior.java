package net.minecraft.block.dispenser;

import java.util.Random;
import net.minecraft.block.BeeHiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public interface DispenserBehavior {
	DispenserBehavior NOOP = (blockPointer, itemStack) -> itemStack;

	ItemStack dispense(BlockPointer pointer, ItemStack stack);

	static void registerDefaults() {
		DispenserBlock.registerBehavior(Items.ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(position, stack.getX(), stack.getY(), stack.getZ());
				arrowEntity.pickupType = ProjectileEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(position, stack.getX(), stack.getY(), stack.getZ());
				arrowEntity.initFromStack(itemStack);
				arrowEntity.pickupType = ProjectileEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
				ProjectileEntity projectileEntity = new SpectralArrowEntity(position, stack.getX(), stack.getY(), stack.getZ());
				projectileEntity.pickupType = ProjectileEntity.PickupPermission.ALLOWED;
				return projectileEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.EGG, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
				return Util.make(new ThrownEggEntity(position, stack.getX(), stack.getY(), stack.getZ()), thrownEggEntity -> thrownEggEntity.setItem(itemStack));
			}
		});
		DispenserBlock.registerBehavior(Items.SNOWBALL, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
				return Util.make(new SnowballEntity(position, stack.getX(), stack.getY(), stack.getZ()), snowballEntity -> snowballEntity.setItem(itemStack));
			}
		});
		DispenserBlock.registerBehavior(
			Items.EXPERIENCE_BOTTLE,
			new ProjectileDispenserBehavior() {
				@Override
				protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
					return Util.make(
						new ThrownExperienceBottleEntity(position, stack.getX(), stack.getY(), stack.getZ()),
						thrownExperienceBottleEntity -> thrownExperienceBottleEntity.setItem(itemStack)
					);
				}

				@Override
				protected float getVariation() {
					return super.getVariation() * 0.5F;
				}

				@Override
				protected float getForce() {
					return super.getForce() * 1.25F;
				}
			}
		);
		DispenserBlock.registerBehavior(
			Items.SPLASH_POTION,
			new DispenserBehavior() {
				@Override
				public ItemStack dispense(BlockPointer location, ItemStack stack) {
					return (new ProjectileDispenserBehavior() {
							@Override
							protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
								return Util.make(
									new ThrownPotionEntity(position, stack.getX(), stack.getY(), stack.getZ()), thrownPotionEntity -> thrownPotionEntity.setItemStack(itemStack)
								);
							}

							@Override
							protected float getVariation() {
								return super.getVariation() * 0.5F;
							}

							@Override
							protected float getForce() {
								return super.getForce() * 1.25F;
							}
						})
						.dispense(location, stack);
				}
			}
		);
		DispenserBlock.registerBehavior(
			Items.LINGERING_POTION,
			new DispenserBehavior() {
				@Override
				public ItemStack dispense(BlockPointer location, ItemStack stack) {
					return (new ProjectileDispenserBehavior() {
							@Override
							protected Projectile createProjectile(World position, Position stack, ItemStack itemStack) {
								return Util.make(
									new ThrownPotionEntity(position, stack.getX(), stack.getY(), stack.getZ()), thrownPotionEntity -> thrownPotionEntity.setItemStack(itemStack)
								);
							}

							@Override
							protected float getVariation() {
								return super.getVariation() * 0.5F;
							}

							@Override
							protected float getForce() {
								return super.getForce() * 1.25F;
							}
						})
						.dispense(location, stack);
				}
			}
		);
		ItemDispenserBehavior itemDispenserBehavior = new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				EntityType<?> entityType = ((SpawnEggItem)stack.getItem()).getEntityType(stack.getTag());
				entityType.spawnFromItemStack(
					pointer.getWorld(), stack, null, pointer.getBlockPos().offset(direction), SpawnType.DISPENSER, direction != Direction.UP, false
				);
				stack.decrement(1);
				return stack;
			}
		};

		for (SpawnEggItem spawnEggItem : SpawnEggItem.getAll()) {
			DispenserBlock.registerBehavior(spawnEggItem, itemDispenserBehavior);
		}

		DispenserBlock.registerBehavior(Items.ARMOR_STAND, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.getBlockPos().offset(direction);
				World world = pointer.getWorld();
				ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5);
				EntityType.loadFromEntityTag(world, null, armorStandEntity, stack.getTag());
				armorStandEntity.yaw = direction.asRotation();
				world.spawnEntity(armorStandEntity);
				stack.decrement(1);
				return stack;
			}
		});
		DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				double d = (double)direction.getOffsetX();
				double e = (double)direction.getOffsetY();
				double f = (double)direction.getOffsetZ();
				double g = pointer.getX() + d;
				double h = (double)((float)pointer.getBlockPos().getY() + 0.2F);
				double i = pointer.getZ() + f;
				FireworkEntity fireworkEntity = new FireworkEntity(pointer.getWorld(), stack, g, h, i, true);
				fireworkEntity.setVelocity(d, e, f, 0.5F, 1.0F);
				pointer.getWorld().spawnEntity(fireworkEntity);
				stack.decrement(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().playLevelEvent(1004, pointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				Position position = DispenserBlock.getOutputLocation(pointer);
				double d = position.getX() + (double)((float)direction.getOffsetX() * 0.3F);
				double e = position.getY() + (double)((float)direction.getOffsetY() * 0.3F);
				double f = position.getZ() + (double)((float)direction.getOffsetZ() * 0.3F);
				World world = pointer.getWorld();
				Random random = world.random;
				double g = random.nextGaussian() * 0.05 + (double)direction.getOffsetX();
				double h = random.nextGaussian() * 0.05 + (double)direction.getOffsetY();
				double i = random.nextGaussian() * 0.05 + (double)direction.getOffsetZ();
				world.spawnEntity(Util.make(new SmallFireballEntity(world, d, e, f, g, h, i), smallFireballEntity -> smallFireballEntity.setItem(stack)));
				stack.decrement(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().playLevelEvent(1018, pointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenserBehavior(BoatEntity.Type.OAK));
		DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenserBehavior(BoatEntity.Type.SPRUCE));
		DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenserBehavior(BoatEntity.Type.BIRCH));
		DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenserBehavior(BoatEntity.Type.JUNGLE));
		DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenserBehavior(BoatEntity.Type.DARK_OAK));
		DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenserBehavior(BoatEntity.Type.ACACIA));
		DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13367 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				BucketItem bucketItem = (BucketItem)stack.getItem();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				World world = pointer.getWorld();
				if (bucketItem.placeFluid(null, world, blockPos, null)) {
					bucketItem.onEmptied(world, stack, blockPos);
					return new ItemStack(Items.BUCKET);
				} else {
					return this.field_13367.dispense(pointer, stack);
				}
			}
		};
		DispenserBlock.registerBehavior(Items.LAVA_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.WATER_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.SALMON_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.COD_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.BUCKET, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13368 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				IWorld iWorld = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = iWorld.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)block).tryDrainFluid(iWorld, blockPos, blockState);
					if (!(fluid instanceof BaseFluid)) {
						return super.dispenseSilently(pointer, stack);
					} else {
						Item item = fluid.getBucketItem();
						stack.decrement(1);
						if (stack.isEmpty()) {
							return new ItemStack(item);
						} else {
							if (pointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(new ItemStack(item)) < 0) {
								this.field_13368.dispense(pointer, new ItemStack(item));
							}

							return stack;
						}
					}
				} else {
					return super.dispenseSilently(pointer, stack);
				}
			}
		});
		DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				this.success = true;
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = world.getBlockState(blockPos);
				if (FlintAndSteelItem.canIgnite(blockState, world, blockPos)) {
					world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
				} else if (FlintAndSteelItem.isIgnitable(blockState)) {
					world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)));
				} else if (blockState.getBlock() instanceof TntBlock) {
					TntBlock.primeTnt(world, blockPos);
					world.removeBlock(blockPos, false);
				} else {
					this.success = false;
				}

				if (this.success && stack.damage(1, world.random, null)) {
					stack.setCount(0);
				}

				return stack;
			}
		});
		DispenserBlock.registerBehavior(Items.BONE_MEAL, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				this.success = true;
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				if (!BoneMealItem.useOnFertilizable(stack, world, blockPos) && !BoneMealItem.useOnGround(stack, world, blockPos, null)) {
					this.success = false;
				} else if (!world.isClient) {
					world.playLevelEvent(2005, blockPos, 0);
				}

				return stack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.TNT, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				TntEntity tntEntity = new TntEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
				world.spawnEntity(tntEntity);
				world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
				stack.decrement(1);
				return stack;
			}
		});
		DispenserBehavior dispenserBehavior2 = new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				this.success = ArmorItem.dispenseArmor(pointer, stack);
				return stack;
			}
		};
		DispenserBlock.registerBehavior(Items.CREEPER_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.DRAGON_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.SKELETON_SKULL, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.PLAYER_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(
			Items.WITHER_SKELETON_SKULL,
			new FallibleItemDispenserBehavior() {
				@Override
				protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
					World world = pointer.getWorld();
					Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
					BlockPos blockPos = pointer.getBlockPos().offset(direction);
					if (world.isAir(blockPos) && WitherSkullBlock.canDispense(world, blockPos, stack)) {
						world.setBlockState(
							blockPos,
							Blocks.WITHER_SKELETON_SKULL
								.getDefaultState()
								.with(SkullBlock.ROTATION, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().getHorizontal() * 4)),
							3
						);
						BlockEntity blockEntity = world.getBlockEntity(blockPos);
						if (blockEntity instanceof SkullBlockEntity) {
							WitherSkullBlock.onPlaced(world, blockPos, (SkullBlockEntity)blockEntity);
						}

						stack.decrement(1);
						this.success = true;
					} else {
						this.success = ArmorItem.dispenseArmor(pointer, stack);
					}

					return stack;
				}
			}
		);
		DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
				if (world.isAir(blockPos) && carvedPumpkinBlock.canDispense(world, blockPos)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, carvedPumpkinBlock.getDefaultState(), 3);
					}

					stack.decrement(1);
					this.success = true;
				} else {
					this.success = ArmorItem.dispenseArmor(pointer, stack);
				}

				return stack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new BlockPlacementDispenserBehavior());

		for (DyeColor dyeColor : DyeColor.values()) {
			DispenserBlock.registerBehavior(ShulkerBoxBlock.get(dyeColor).asItem(), new BlockPlacementDispenserBehavior());
		}

		DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new FallibleItemDispenserBehavior() {
			private final ItemDispenserBehavior field_20533 = new ItemDispenserBehavior();

			private ItemStack method_22141(BlockPointer blockPointer, ItemStack emptyBottleStack, ItemStack filledBottleStack) {
				emptyBottleStack.decrement(1);
				if (emptyBottleStack.isEmpty()) {
					return filledBottleStack.copy();
				} else {
					if (blockPointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(filledBottleStack.copy()) < 0) {
						this.field_20533.dispense(blockPointer, filledBottleStack.copy());
					}

					return emptyBottleStack;
				}
			}

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				this.success = false;
				IWorld iWorld = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = iWorld.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block.matches(BlockTags.BEEHIVES) && (Integer)blockState.get(BeeHiveBlock.HONEY_LEVEL) >= 5) {
					((BeeHiveBlock)blockState.getBlock()).emptyHoney(iWorld.getWorld(), blockState, blockPos, null, BeeHiveBlockEntity.BeeState.BEE_RELEASED);
					this.success = true;
					return this.method_22141(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
				} else if (iWorld.getFluidState(blockPos).matches(FluidTags.WATER)) {
					this.success = true;
					return this.method_22141(pointer, stack, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
				} else {
					return super.dispenseSilently(pointer, stack);
				}
			}
		});
		DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				if (!world.isClient()) {
					this.success = false;
					BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));

					for (SheepEntity sheepEntity : world.getNonSpectatingEntities(SheepEntity.class, new Box(blockPos))) {
						if (sheepEntity.isAlive() && !sheepEntity.isSheared() && !sheepEntity.isBaby()) {
							sheepEntity.dropItems();
							if (stack.damage(1, world.random, null)) {
								stack.setCount(0);
							}

							this.success = true;
							break;
						}
					}

					if (!this.success) {
						BlockState blockState = world.getBlockState(blockPos);
						if (blockState.matches(BlockTags.BEEHIVES)) {
							int i = (Integer)blockState.get(BeeHiveBlock.HONEY_LEVEL);
							if (i >= 5) {
								if (stack.damage(1, world.random, null)) {
									stack.setCount(0);
								}

								BeeHiveBlock.dropHoneycomb(world, blockPos);
								((BeeHiveBlock)blockState.getBlock()).emptyHoney(world, blockState, blockPos, null, BeeHiveBlockEntity.BeeState.BEE_RELEASED);
								this.success = true;
							}
						}
					}
				}

				return stack;
			}
		});
	}
}
