package net.minecraft.block.dispenser;

import java.util.Random;
import net.minecraft.class_2967;
import net.minecraft.class_2969;
import net.minecraft.class_2970;
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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.sortme.Projectile;
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
		DispenserBlock.method_10009(Items.field_8107, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.pickupType = ProjectileEntity.PickupType.PICKUP;
				return arrowEntity;
			}
		});
		DispenserBlock.method_10009(Items.field_8087, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.method_7459(itemStack);
				arrowEntity.pickupType = ProjectileEntity.PickupType.PICKUP;
				return arrowEntity;
			}
		});
		DispenserBlock.method_10009(Items.field_8236, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ProjectileEntity projectileEntity = new SpectralArrowEntity(world, position.getX(), position.getY(), position.getZ());
				projectileEntity.pickupType = ProjectileEntity.PickupType.PICKUP;
				return projectileEntity;
			}
		});
		DispenserBlock.method_10009(
			Items.field_8803,
			new ProjectileDispenserBehavior() {
				@Override
				protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
					return SystemUtil.consume(
						new ThrownEggEntity(world, position.getX(), position.getY(), position.getZ()), thrownEggEntity -> thrownEggEntity.method_16940(itemStack)
					);
				}
			}
		);
		DispenserBlock.method_10009(
			Items.field_8543,
			new ProjectileDispenserBehavior() {
				@Override
				protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
					return SystemUtil.consume(
						new SnowballEntity(world, position.getX(), position.getY(), position.getZ()), snowballEntity -> snowballEntity.method_16940(itemStack)
					);
				}
			}
		);
		DispenserBlock.method_10009(
			Items.field_8287,
			new ProjectileDispenserBehavior() {
				@Override
				protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
					return SystemUtil.consume(
						new ThrownExperienceBottleEntity(world, position.getX(), position.getY(), position.getZ()),
						thrownExperienceBottleEntity -> thrownExperienceBottleEntity.method_16940(itemStack)
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
		DispenserBlock.method_10009(
			Items.field_8436,
			new DispenserBehavior() {
				@Override
				public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
					return (new ProjectileDispenserBehavior() {
							@Override
							protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
								return SystemUtil.consume(
									new ThrownPotionEntity(world, position.getX(), position.getY(), position.getZ()), thrownPotionEntity -> thrownPotionEntity.method_7494(itemStack)
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
		DispenserBlock.method_10009(
			Items.field_8150,
			new DispenserBehavior() {
				@Override
				public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
					return (new ProjectileDispenserBehavior() {
							@Override
							protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
								return SystemUtil.consume(
									new ThrownPotionEntity(world, position.getX(), position.getY(), position.getZ()), thrownPotionEntity -> thrownPotionEntity.method_7494(itemStack)
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
				Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
				EntityType<?> entityType = ((SpawnEggItem)itemStack.getItem()).method_8015(itemStack.method_7969());
				entityType.method_5894(
					blockPointer.getWorld(), itemStack, null, blockPointer.getBlockPos().method_10093(direction), SpawnType.field_16470, direction != Direction.UP, false
				);
				itemStack.subtractAmount(1);
				return itemStack;
			}
		};

		for (SpawnEggItem spawnEggItem : SpawnEggItem.iterator()) {
			DispenserBlock.method_10009(spawnEggItem, itemDispenserBehavior);
		}

		DispenserBlock.method_10009(Items.field_8639, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
				double d = blockPointer.getX() + (double)direction.getOffsetX();
				double e = (double)((float)blockPointer.getBlockPos().getY() + 0.2F);
				double f = blockPointer.getZ() + (double)direction.getOffsetZ();
				blockPointer.getWorld().spawnEntity(new FireworkEntity(blockPointer.getWorld(), d, e, f, itemStack));
				itemStack.subtractAmount(1);
				return itemStack;
			}

			@Override
			protected void playSound(BlockPointer blockPointer) {
				blockPointer.getWorld().method_8535(1004, blockPointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.method_10009(Items.field_8814, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
				Position position = DispenserBlock.method_10010(blockPointer);
				double d = position.getX() + (double)((float)direction.getOffsetX() * 0.3F);
				double e = position.getY() + (double)((float)direction.getOffsetY() * 0.3F);
				double f = position.getZ() + (double)((float)direction.getOffsetZ() * 0.3F);
				World world = blockPointer.getWorld();
				Random random = world.random;
				double g = random.nextGaussian() * 0.05 + (double)direction.getOffsetX();
				double h = random.nextGaussian() * 0.05 + (double)direction.getOffsetY();
				double i = random.nextGaussian() * 0.05 + (double)direction.getOffsetZ();
				world.spawnEntity(SystemUtil.consume(new SmallFireballEntity(world, d, e, f, g, h, i), smallFireballEntity -> smallFireballEntity.method_16936(itemStack)));
				itemStack.subtractAmount(1);
				return itemStack;
			}

			@Override
			protected void playSound(BlockPointer blockPointer) {
				blockPointer.getWorld().method_8535(1018, blockPointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.method_10009(Items.field_8533, new class_2967(BoatEntity.Type.OAK));
		DispenserBlock.method_10009(Items.field_8486, new class_2967(BoatEntity.Type.SPRUCE));
		DispenserBlock.method_10009(Items.field_8442, new class_2967(BoatEntity.Type.BIRCH));
		DispenserBlock.method_10009(Items.field_8730, new class_2967(BoatEntity.Type.JUNGLE));
		DispenserBlock.method_10009(Items.field_8138, new class_2967(BoatEntity.Type.DARK_OAK));
		DispenserBlock.method_10009(Items.field_8094, new class_2967(BoatEntity.Type.ACACIA));
		DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13367 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				BucketItem bucketItem = (BucketItem)itemStack.getItem();
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));
				World world = blockPointer.getWorld();
				if (bucketItem.method_7731(null, world, blockPos, null)) {
					bucketItem.method_7728(world, itemStack, blockPos);
					return new ItemStack(Items.field_8550);
				} else {
					return this.field_13367.dispense(blockPointer, itemStack);
				}
			}
		};
		DispenserBlock.method_10009(Items.field_8187, dispenserBehavior);
		DispenserBlock.method_10009(Items.field_8705, dispenserBehavior);
		DispenserBlock.method_10009(Items.field_8714, dispenserBehavior);
		DispenserBlock.method_10009(Items.field_8666, dispenserBehavior);
		DispenserBlock.method_10009(Items.field_8108, dispenserBehavior);
		DispenserBlock.method_10009(Items.field_8478, dispenserBehavior);
		DispenserBlock.method_10009(Items.field_8550, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13368 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				IWorld iWorld = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));
				BlockState blockState = iWorld.method_8320(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)block).method_9700(iWorld, blockPos, blockState);
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
		DispenserBlock.method_10009(Items.field_8884, new class_2969() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				this.field_13364 = true;
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));
				BlockState blockState = world.method_8320(blockPos);
				if (FlintAndSteelItem.method_7825(blockState, world, blockPos)) {
					world.method_8501(blockPos, Blocks.field_10036.method_9564());
				} else if (FlintAndSteelItem.method_17439(blockState)) {
					world.method_8501(blockPos, blockState.method_11657(Properties.field_12548, Boolean.valueOf(true)));
				} else if (blockState.getBlock() instanceof TntBlock) {
					TntBlock.method_10738(world, blockPos);
					world.method_8650(blockPos);
				} else {
					this.field_13364 = false;
				}

				if (this.field_13364 && itemStack.method_7970(1, world.random, null)) {
					itemStack.setAmount(0);
				}

				return itemStack;
			}
		});
		DispenserBlock.method_10009(Items.field_8324, new class_2969() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				this.field_13364 = true;
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));
				if (!BoneMealItem.method_7720(itemStack, world, blockPos) && !BoneMealItem.method_7719(itemStack, world, blockPos, null)) {
					this.field_13364 = false;
				} else if (!world.isClient) {
					world.method_8535(2005, blockPos, 0);
				}

				return itemStack;
			}
		});
		DispenserBlock.method_10009(Blocks.field_10375, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));
				PrimedTntEntity primedTntEntity = new PrimedTntEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
				world.spawnEntity(primedTntEntity);
				world.method_8465(null, primedTntEntity.x, primedTntEntity.y, primedTntEntity.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
				itemStack.subtractAmount(1);
				return itemStack;
			}
		});
		DispenserBehavior dispenserBehavior2 = new class_2969() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				this.field_13364 = !ArmorItem.method_7684(blockPointer, itemStack).isEmpty();
				return itemStack;
			}
		};
		DispenserBlock.method_10009(Items.CREEPER_HEAD, dispenserBehavior2);
		DispenserBlock.method_10009(Items.ZOMBIE_HEAD, dispenserBehavior2);
		DispenserBlock.method_10009(Items.DRAGON_HEAD, dispenserBehavior2);
		DispenserBlock.method_10009(Items.SKELETON_SKULL, dispenserBehavior2);
		DispenserBlock.method_10009(Items.PLAYER_HEAD, dispenserBehavior2);
		DispenserBlock.method_10009(
			Items.WITHER_SKELETON_SKULL,
			new class_2969() {
				@Override
				protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
					World world = blockPointer.getWorld();
					Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
					BlockPos blockPos = blockPointer.getBlockPos().method_10093(direction);
					this.field_13364 = true;
					if (world.method_8623(blockPos) && WitherSkullBlock.method_10899(world, blockPos, itemStack)) {
						world.method_8652(
							blockPos,
							Blocks.field_10177
								.method_9564()
								.method_11657(SkullBlock.field_11505, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().getHorizontal() * 4)),
							3
						);
						BlockEntity blockEntity = world.method_8321(blockPos);
						if (blockEntity instanceof SkullBlockEntity) {
							WitherSkullBlock.method_10898(world, blockPos, (SkullBlockEntity)blockEntity);
						}

						itemStack.subtractAmount(1);
					} else if (ArmorItem.method_7684(blockPointer, itemStack).isEmpty()) {
						this.field_13364 = false;
					}

					return itemStack;
				}
			}
		);
		DispenserBlock.method_10009(Blocks.field_10147, new class_2969() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));
				CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.field_10147;
				this.field_13364 = true;
				if (world.method_8623(blockPos) && carvedPumpkinBlock.method_9733(world, blockPos)) {
					if (!world.isClient) {
						world.method_8652(blockPos, carvedPumpkinBlock.method_9564(), 3);
					}

					itemStack.subtractAmount(1);
				} else {
					ItemStack itemStack2 = ArmorItem.method_7684(blockPointer, itemStack);
					if (itemStack2.isEmpty()) {
						this.field_13364 = false;
					}
				}

				return itemStack;
			}
		});
		DispenserBlock.method_10009(Blocks.field_10603.getItem(), new class_2970());

		for (DyeColor dyeColor : DyeColor.values()) {
			DispenserBlock.method_10009(ShulkerBoxBlock.get(dyeColor).getItem(), new class_2970());
		}

		DispenserBlock.method_10009(Items.field_8868.getItem(), new class_2969() {
			@Override
			protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				if (!world.isClient()) {
					this.field_13364 = false;
					BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().method_11654(DispenserBlock.field_10918));

					for (SheepEntity sheepEntity : world.method_18467(SheepEntity.class, new BoundingBox(blockPos))) {
						if (sheepEntity.isValid() && !sheepEntity.isSheared() && !sheepEntity.isChild()) {
							sheepEntity.dropItems();
							if (itemStack.method_7970(1, world.random, null)) {
								itemStack.setAmount(0);
							}

							this.field_13364 = true;
							break;
						}
					}
				}

				return itemStack;
			}
		});
	}
}
