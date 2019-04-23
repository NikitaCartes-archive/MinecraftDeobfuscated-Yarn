package net.minecraft.block.dispenser;

import java.util.Random;
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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.SpawnType;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public interface DispenserBehavior {
	DispenserBehavior NOOP = (blockPointer, itemStack) -> itemStack;

	ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack);

	static void registerDefaults() {
		DispenserBlock.registerBehavior(Items.field_8107, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.pickupType = ProjectileEntity.PickupPermission.field_7593;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.field_8087, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.initFromStack(itemStack);
				arrowEntity.pickupType = ProjectileEntity.PickupPermission.field_7593;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.field_8236, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ProjectileEntity projectileEntity = new SpectralArrowEntity(world, position.getX(), position.getY(), position.getZ());
				projectileEntity.pickupType = ProjectileEntity.PickupPermission.field_7593;
				return projectileEntity;
			}
		});
		DispenserBlock.registerBehavior(
			Items.field_8803,
			new ProjectileDispenserBehavior() {
				@Override
				protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
					return SystemUtil.consume(
						new ThrownEggEntity(world, position.getX(), position.getY(), position.getZ()), thrownEggEntity -> thrownEggEntity.setItem(itemStack)
					);
				}
			}
		);
		DispenserBlock.registerBehavior(
			Items.field_8543,
			new ProjectileDispenserBehavior() {
				@Override
				protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
					return SystemUtil.consume(
						new SnowballEntity(world, position.getX(), position.getY(), position.getZ()), snowballEntity -> snowballEntity.setItem(itemStack)
					);
				}
			}
		);
		DispenserBlock.registerBehavior(
			Items.field_8287,
			new ProjectileDispenserBehavior() {
				@Override
				protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
					return SystemUtil.consume(
						new ThrownExperienceBottleEntity(world, position.getX(), position.getY(), position.getZ()),
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
			Items.field_8436,
			new DispenserBehavior() {
				@Override
				public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
					return (new ProjectileDispenserBehavior() {
							@Override
							protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
								return SystemUtil.consume(
									new ThrownPotionEntity(world, position.getX(), position.getY(), position.getZ()), thrownPotionEntity -> thrownPotionEntity.setItemStack(itemStack)
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
						.dispense(blockPointer, itemStack);
				}
			}
		);
		DispenserBlock.registerBehavior(
			Items.field_8150,
			new DispenserBehavior() {
				@Override
				public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
					return (new ProjectileDispenserBehavior() {
							@Override
							protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
								return SystemUtil.consume(
									new ThrownPotionEntity(world, position.getX(), position.getY(), position.getZ()), thrownPotionEntity -> thrownPotionEntity.setItemStack(itemStack)
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
						.dispense(blockPointer, itemStack);
				}
			}
		);
		ItemDispenserBehavior itemDispenserBehavior = new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
				EntityType<?> entityType = ((SpawnEggItem)itemStack.getItem()).entityTypeFromTag(itemStack.getTag());
				entityType.spawnFromItemStack(
					blockPointer.getWorld(), itemStack, null, blockPointer.getBlockPos().offset(direction), SpawnType.field_16470, direction != Direction.field_11036, false
				);
				itemStack.subtractAmount(1);
				return itemStack;
			}
		};

		for (SpawnEggItem spawnEggItem : SpawnEggItem.iterator()) {
			DispenserBlock.registerBehavior(spawnEggItem, itemDispenserBehavior);
		}

		DispenserBlock.registerBehavior(Items.field_8639, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
				double d = blockPointer.getX() + (double)direction.getOffsetX();
				double e = (double)((float)blockPointer.getBlockPos().getY() + 0.2F);
				double f = blockPointer.getZ() + (double)direction.getOffsetZ();
				blockPointer.getWorld().spawnEntity(new FireworkEntity(blockPointer.getWorld(), d, e, f, itemStack));
				itemStack.subtractAmount(1);
				return itemStack;
			}

			@Override
			protected void playSound(BlockPointer blockPointer) {
				blockPointer.getWorld().playLevelEvent(1004, blockPointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(Items.field_8814, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
				Position position = DispenserBlock.getOutputLocation(blockPointer);
				double d = position.getX() + (double)((float)direction.getOffsetX() * 0.3F);
				double e = position.getY() + (double)((float)direction.getOffsetY() * 0.3F);
				double f = position.getZ() + (double)((float)direction.getOffsetZ() * 0.3F);
				World world = blockPointer.getWorld();
				Random random = world.random;
				double g = random.nextGaussian() * 0.05 + (double)direction.getOffsetX();
				double h = random.nextGaussian() * 0.05 + (double)direction.getOffsetY();
				double i = random.nextGaussian() * 0.05 + (double)direction.getOffsetZ();
				world.spawnEntity(SystemUtil.consume(new SmallFireballEntity(world, d, e, f, g, h, i), smallFireballEntity -> smallFireballEntity.setItem(itemStack)));
				itemStack.subtractAmount(1);
				return itemStack;
			}

			@Override
			protected void playSound(BlockPointer blockPointer) {
				blockPointer.getWorld().playLevelEvent(1018, blockPointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(Items.field_8533, new BoatDispenserBehavior(BoatEntity.Type.field_7727));
		DispenserBlock.registerBehavior(Items.field_8486, new BoatDispenserBehavior(BoatEntity.Type.field_7728));
		DispenserBlock.registerBehavior(Items.field_8442, new BoatDispenserBehavior(BoatEntity.Type.field_7729));
		DispenserBlock.registerBehavior(Items.field_8730, new BoatDispenserBehavior(BoatEntity.Type.field_7730));
		DispenserBlock.registerBehavior(Items.field_8138, new BoatDispenserBehavior(BoatEntity.Type.field_7723));
		DispenserBlock.registerBehavior(Items.field_8094, new BoatDispenserBehavior(BoatEntity.Type.field_7725));
		DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13367 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				BucketItem bucketItem = (BucketItem)itemStack.getItem();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				World world = blockPointer.getWorld();
				if (bucketItem.placeFluid(null, world, blockPos, null)) {
					bucketItem.onEmptied(world, itemStack, blockPos);
					return new ItemStack(Items.field_8550);
				} else {
					return this.field_13367.dispense(blockPointer, itemStack);
				}
			}
		};
		DispenserBlock.registerBehavior(Items.field_8187, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.field_8705, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.field_8714, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.field_8666, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.field_8108, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.field_8478, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.field_8550, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13368 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				IWorld iWorld = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = iWorld.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)block).tryDrainFluid(iWorld, blockPos, blockState);
					if (!(fluid instanceof BaseFluid)) {
						return super.dispenseStack(blockPointer, itemStack);
					} else {
						Item item = fluid.getBucketItem();
						itemStack.subtractAmount(1);
						if (itemStack.isEmpty()) {
							return new ItemStack(item);
						} else {
							if (blockPointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(new ItemStack(item)) < 0) {
								this.field_13368.dispense(blockPointer, new ItemStack(item));
							}

							return itemStack;
						}
					}
				} else {
					return super.dispenseStack(blockPointer, itemStack);
				}
			}
		});
		DispenserBlock.registerBehavior(Items.field_8884, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				this.success = true;
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = world.getBlockState(blockPos);
				if (FlintAndSteelItem.canSetOnFire(blockState, world, blockPos)) {
					world.setBlockState(blockPos, Blocks.field_10036.getDefaultState());
				} else if (FlintAndSteelItem.canBeLit(blockState)) {
					world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)));
				} else if (blockState.getBlock() instanceof TntBlock) {
					TntBlock.primeTnt(world, blockPos);
					world.clearBlockState(blockPos, false);
				} else {
					this.success = false;
				}

				if (this.success && itemStack.applyDamage(1, world.random, null)) {
					itemStack.setAmount(0);
				}

				return itemStack;
			}
		});
		DispenserBlock.registerBehavior(Items.field_8324, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				this.success = true;
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				if (!BoneMealItem.useOnFertilizable(itemStack, world, blockPos) && !BoneMealItem.useOnGround(itemStack, world, blockPos, null)) {
					this.success = false;
				} else if (!world.isClient) {
					world.playLevelEvent(2005, blockPos, 0);
				}

				return itemStack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.field_10375, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				PrimedTntEntity primedTntEntity = new PrimedTntEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
				world.spawnEntity(primedTntEntity);
				world.playSound(null, primedTntEntity.x, primedTntEntity.y, primedTntEntity.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
				itemStack.subtractAmount(1);
				return itemStack;
			}
		});
		DispenserBehavior dispenserBehavior2 = new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				this.success = !ArmorItem.dispenseArmor(blockPointer, itemStack).isEmpty();
				return itemStack;
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
				protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
					World world = blockPointer.getWorld();
					Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
					BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
					this.success = true;
					if (world.isAir(blockPos) && WitherSkullBlock.canDispense(world, blockPos, itemStack)) {
						world.setBlockState(
							blockPos,
							Blocks.field_10177
								.getDefaultState()
								.with(SkullBlock.ROTATION, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().getHorizontal() * 4)),
							3
						);
						BlockEntity blockEntity = world.getBlockEntity(blockPos);
						if (blockEntity instanceof SkullBlockEntity) {
							WitherSkullBlock.onPlaced(world, blockPos, (SkullBlockEntity)blockEntity);
						}

						itemStack.subtractAmount(1);
					} else if (ArmorItem.dispenseArmor(blockPointer, itemStack).isEmpty()) {
						this.success = false;
					}

					return itemStack;
				}
			}
		);
		DispenserBlock.registerBehavior(Blocks.field_10147, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.field_10147;
				this.success = true;
				if (world.isAir(blockPos) && carvedPumpkinBlock.canDispense(world, blockPos)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, carvedPumpkinBlock.getDefaultState(), 3);
					}

					itemStack.subtractAmount(1);
				} else {
					ItemStack itemStack2 = ArmorItem.dispenseArmor(blockPointer, itemStack);
					if (itemStack2.isEmpty()) {
						this.success = false;
					}
				}

				return itemStack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.field_10603.asItem(), new BlockPlacementDispenserBehavior());

		for (DyeColor dyeColor : DyeColor.values()) {
			DispenserBlock.registerBehavior(ShulkerBoxBlock.get(dyeColor).asItem(), new BlockPlacementDispenserBehavior());
		}

		DispenserBlock.registerBehavior(Items.field_8868.asItem(), new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				if (!world.isClient()) {
					this.success = false;
					BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));

					for (SheepEntity sheepEntity : world.getEntities(SheepEntity.class, new BoundingBox(blockPos))) {
						if (sheepEntity.isAlive() && !sheepEntity.isSheared() && !sheepEntity.isBaby()) {
							sheepEntity.dropItems();
							if (itemStack.applyDamage(1, world.random, null)) {
								itemStack.setAmount(0);
							}

							this.success = true;
							break;
						}
					}
				}

				return itemStack;
			}
		});
	}
}
