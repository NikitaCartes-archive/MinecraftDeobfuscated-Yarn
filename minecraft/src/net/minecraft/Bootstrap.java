package net.minecraft;

import java.io.PrintStream;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.PrimedTNTEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.effect.StatusEffect;
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
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.block.BlockItem;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DebugPrintStreamLogger;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Language;
import net.minecraft.util.PrintStreamLogger;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
	public static final PrintStream OUTPUT = System.out;
	private static boolean initialized;
	private static final Logger LOGGER = LogManager.getLogger();

	static void registerDispenserBehaviors() {
		DispenserBlock.registerBehavior(Items.field_8107, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.pickupType = ProjectileEntity.PickupType.PICKUP;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.field_8087, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.initFromStack(itemStack);
				arrowEntity.pickupType = ProjectileEntity.PickupType.PICKUP;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.field_8236, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				ProjectileEntity projectileEntity = new SpectralArrowEntity(world, position.getX(), position.getY(), position.getZ());
				projectileEntity.pickupType = ProjectileEntity.PickupType.PICKUP;
				return projectileEntity;
			}
		});
		DispenserBlock.registerBehavior(
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
		DispenserBlock.registerBehavior(
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
		DispenserBlock.registerBehavior(
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
			public ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
				EntityType<?> entityType = ((SpawnEggItem)itemStack.getItem()).method_8015(itemStack.getTag());
				if (entityType != null) {
					entityType.spawnFromItemStack(
						blockPointer.getWorld(), itemStack, null, blockPointer.getBlockPos().offset(direction), SpawnType.field_16470, direction != Direction.UP, false
					);
				}

				itemStack.subtractAmount(1);
				return itemStack;
			}
		};

		for (SpawnEggItem spawnEggItem : SpawnEggItem.method_8017()) {
			DispenserBlock.registerBehavior(spawnEggItem, itemDispenserBehavior);
		}

		DispenserBlock.registerBehavior(Items.field_8639, new ItemDispenserBehavior() {
			@Override
			public ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
				double d = blockPointer.getX() + (double)direction.getOffsetX();
				double e = (double)((float)blockPointer.getBlockPos().getY() + 0.2F);
				double f = blockPointer.getZ() + (double)direction.getOffsetZ();
				FireworkEntity fireworkEntity = new FireworkEntity(blockPointer.getWorld(), d, e, f, itemStack);
				blockPointer.getWorld().spawnEntity(fireworkEntity);
				itemStack.subtractAmount(1);
				return itemStack;
			}

			@Override
			protected void playSound(BlockPointer blockPointer) {
				blockPointer.getWorld().fireWorldEvent(1004, blockPointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(Items.field_8814, new ItemDispenserBehavior() {
			@Override
			public ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
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
				world.spawnEntity(SystemUtil.consume(new SmallFireballEntity(world, d, e, f, g, h, i), smallFireballEntity -> smallFireballEntity.method_16936(itemStack)));
				itemStack.subtractAmount(1);
				return itemStack;
			}

			@Override
			protected void playSound(BlockPointer blockPointer) {
				blockPointer.getWorld().fireWorldEvent(1018, blockPointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(Items.field_8533, new Bootstrap.DispenseBoat(BoatEntity.Type.OAK));
		DispenserBlock.registerBehavior(Items.field_8486, new Bootstrap.DispenseBoat(BoatEntity.Type.SPRUCE));
		DispenserBlock.registerBehavior(Items.field_8442, new Bootstrap.DispenseBoat(BoatEntity.Type.BIRCH));
		DispenserBlock.registerBehavior(Items.field_8730, new Bootstrap.DispenseBoat(BoatEntity.Type.JUNGLE));
		DispenserBlock.registerBehavior(Items.field_8138, new Bootstrap.DispenseBoat(BoatEntity.Type.DARK_OAK));
		DispenserBlock.registerBehavior(Items.field_8094, new Bootstrap.DispenseBoat(BoatEntity.Type.ACACIA));
		DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13367 = new ItemDispenserBehavior();

			@Override
			public ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				BucketItem bucketItem = (BucketItem)itemStack.getItem();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				World world = blockPointer.getWorld();
				if (bucketItem.method_7731(null, world, blockPos, null)) {
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
			public ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				IWorld iWorld = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = iWorld.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)block).tryDrainFluid(iWorld, blockPos, blockState);
					if (!(fluid instanceof BaseFluid)) {
						return super.method_10135(blockPointer, itemStack);
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
					return super.method_10135(blockPointer, itemStack);
				}
			}
		});
		DispenserBlock.registerBehavior(Items.field_8884, new Bootstrap.class_2969() {
			@Override
			protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				this.field_13364 = true;
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = world.getBlockState(blockPos);
				if (FlintAndSteelItem.method_7825(blockState, world, blockPos)) {
					world.setBlockState(blockPos, Blocks.field_10036.getDefaultState());
				} else if (FlintAndSteelItem.method_17439(blockState)) {
					world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)));
				} else if (blockState.getBlock() instanceof TntBlock) {
					TntBlock.primeTnt(world, blockPos);
					world.clearBlockState(blockPos);
				} else {
					this.field_13364 = false;
				}

				if (this.field_13364 && itemStack.applyDamage(1, world.random, null)) {
					itemStack.setAmount(0);
				}

				return itemStack;
			}
		});
		DispenserBlock.registerBehavior(Items.field_8324, new Bootstrap.class_2969() {
			@Override
			protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				this.field_13364 = true;
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				if (!BoneMealItem.method_7720(itemStack, world, blockPos) && !BoneMealItem.method_7719(itemStack, world, blockPos, null)) {
					this.field_13364 = false;
				} else if (!world.isClient) {
					world.fireWorldEvent(2005, blockPos, 0);
				}

				return itemStack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.field_10375, new ItemDispenserBehavior() {
			@Override
			protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				PrimedTNTEntity primedTNTEntity = new PrimedTNTEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
				world.spawnEntity(primedTNTEntity);
				world.playSound(null, primedTNTEntity.x, primedTNTEntity.y, primedTNTEntity.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
				itemStack.subtractAmount(1);
				return itemStack;
			}
		});
		Bootstrap.class_2969 lv = new Bootstrap.class_2969() {
			@Override
			protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				this.field_13364 = !ArmorItem.dispenseArmor(blockPointer, itemStack).isEmpty();
				return itemStack;
			}
		};
		DispenserBlock.registerBehavior(Items.field_8681, lv);
		DispenserBlock.registerBehavior(Items.field_8470, lv);
		DispenserBlock.registerBehavior(Items.field_8712, lv);
		DispenserBlock.registerBehavior(Items.field_8398, lv);
		DispenserBlock.registerBehavior(Items.field_8575, lv);
		DispenserBlock.registerBehavior(
			Items.field_8791,
			new Bootstrap.class_2969() {
				@Override
				protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
					World world = blockPointer.getWorld();
					Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
					BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
					this.field_13364 = true;
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
						this.field_13364 = false;
					}

					return itemStack;
				}
			}
		);
		DispenserBlock.registerBehavior(Blocks.field_10147, new Bootstrap.class_2969() {
			@Override
			protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));
				CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.field_10147;
				this.field_13364 = true;
				if (world.isAir(blockPos) && carvedPumpkinBlock.method_9733(world, blockPos)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, carvedPumpkinBlock.getDefaultState(), 3);
					}

					itemStack.subtractAmount(1);
				} else {
					ItemStack itemStack2 = ArmorItem.dispenseArmor(blockPointer, itemStack);
					if (itemStack2.isEmpty()) {
						this.field_13364 = false;
					}
				}

				return itemStack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.field_10603.getItem(), new Bootstrap.class_2970());

		for (DyeColor dyeColor : DyeColor.values()) {
			DispenserBlock.registerBehavior(ShulkerBoxBlock.get(dyeColor).getItem(), new Bootstrap.class_2970());
		}

		DispenserBlock.registerBehavior(Items.field_8868.getItem(), new Bootstrap.class_2969() {
			@Override
			protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				if (!world.isClient()) {
					this.field_13364 = false;
					BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().get(DispenserBlock.FACING));

					for (SheepEntity sheepEntity : world.getVisibleEntities(SheepEntity.class, new BoundingBox(blockPos))) {
						if (sheepEntity.isValid() && !sheepEntity.isSheared() && !sheepEntity.isChild()) {
							sheepEntity.dropItems();
							if (itemStack.applyDamage(1, world.random, null)) {
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

	public static void initialize() {
		if (!initialized) {
			initialized = true;
			if (Registry.REGISTRIES.isEmpty()) {
				throw new IllegalStateException("Unable to load registries");
			} else {
				FireBlock.registerDefaultFlammables();
				ComposterBlock.registerDefaultCompostableItems();
				if (EntityType.getId(EntityType.PLAYER) == null) {
					throw new IllegalStateException("Failed loading EntityTypes");
				} else {
					BrewingRecipeRegistry.registerDefaults();
					EntitySelectorOptions.method_9960();
					registerDispenserBehaviors();
					ArgumentTypes.register();
					setOutputStreams();
				}
			}
		}
	}

	private static <T> void checkTranslations(Registry<T> registry, Function<T, String> function, Set<String> set) {
		Language language = Language.getInstance();
		registry.iterator().forEachRemaining(object -> {
			String string = (String)function.apply(object);
			if (!language.hasTranslation(string)) {
				set.add(string);
			}
		});
	}

	public static Set<String> method_17597() {
		Set<String> set = new TreeSet();
		checkTranslations(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
		checkTranslations(Registry.STATUS_EFFECT, StatusEffect::getTranslationKey, set);
		checkTranslations(Registry.ITEM, Item::getTranslationKey, set);
		checkTranslations(Registry.ENCHANTMENT, Enchantment::getTranslationKey, set);
		checkTranslations(Registry.BIOME, Biome::getTranslationKey, set);
		checkTranslations(Registry.BLOCK, Block::getTranslationKey, set);
		checkTranslations(Registry.CUSTOM_STAT, identifier -> "stat." + identifier.toString().replace(':', '.'), set);
		return set;
	}

	public static void method_17598() {
		if (!initialized) {
			throw new IllegalArgumentException("Not bootstrapped");
		} else if (!SharedConstants.isDevelopment) {
			method_17597().forEach(string -> LOGGER.error("Missing translations: " + string));
		}
	}

	private static void setOutputStreams() {
		if (LOGGER.isDebugEnabled()) {
			System.setErr(new DebugPrintStreamLogger("STDERR", System.err));
			System.setOut(new DebugPrintStreamLogger("STDOUT", OUTPUT));
		} else {
			System.setErr(new PrintStreamLogger("STDERR", System.err));
			System.setOut(new PrintStreamLogger("STDOUT", OUTPUT));
		}
	}

	@Environment(EnvType.CLIENT)
	public static void println(String string) {
		OUTPUT.println(string);
	}

	public static class DispenseBoat extends ItemDispenserBehavior {
		private final ItemDispenserBehavior itemDropper = new ItemDispenserBehavior();
		private final BoatEntity.Type type;

		public DispenseBoat(BoatEntity.Type type) {
			this.type = type;
		}

		@Override
		public ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
			Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
			World world = blockPointer.getWorld();
			double d = blockPointer.getX() + (double)((float)direction.getOffsetX() * 1.125F);
			double e = blockPointer.getY() + (double)((float)direction.getOffsetY() * 1.125F);
			double f = blockPointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125F);
			BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
			double g;
			if (world.getFluidState(blockPos).matches(FluidTags.field_15517)) {
				g = 1.0;
			} else {
				if (!world.getBlockState(blockPos).isAir() || !world.getFluidState(blockPos.down()).matches(FluidTags.field_15517)) {
					return this.itemDropper.dispense(blockPointer, itemStack);
				}

				g = 0.0;
			}

			BoatEntity boatEntity = new BoatEntity(world, d, e + g, f);
			boatEntity.setBoatType(this.type);
			boatEntity.yaw = direction.asRotation();
			world.spawnEntity(boatEntity);
			itemStack.subtractAmount(1);
			return itemStack;
		}

		@Override
		protected void playSound(BlockPointer blockPointer) {
			blockPointer.getWorld().fireWorldEvent(1000, blockPointer.getBlockPos(), 0);
		}
	}

	static class class_2968 extends ItemPlacementContext {
		private final Direction field_13362;

		public class_2968(World world, BlockPos blockPos, Direction direction, ItemStack itemStack, Direction direction2) {
			super(
				world,
				null,
				itemStack,
				new BlockHitResult(new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5), direction2, blockPos, false)
			);
			this.field_13362 = direction;
		}

		@Override
		public BlockPos getBlockPos() {
			return this.field_17543.getBlockPos();
		}

		@Override
		public boolean canPlace() {
			return this.world.getBlockState(this.field_17543.getBlockPos()).method_11587(this);
		}

		@Override
		public boolean method_7717() {
			return this.canPlace();
		}

		@Override
		public Direction getPlayerFacing() {
			return Direction.DOWN;
		}

		@Override
		public Direction[] getPlacementFacings() {
			switch (this.field_13362) {
				case DOWN:
				default:
					return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
				case UP:
					return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
				case NORTH:
					return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
				case SOUTH:
					return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
				case WEST:
					return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
				case EAST:
					return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
			}
		}

		@Override
		public Direction getPlayerHorizontalFacing() {
			return this.field_13362.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.field_13362;
		}

		@Override
		public boolean isPlayerSneaking() {
			return false;
		}

		@Override
		public float getPlayerYaw() {
			return (float)(this.field_13362.getHorizontal() * 90);
		}
	}

	public abstract static class class_2969 extends ItemDispenserBehavior {
		protected boolean field_13364 = true;

		@Override
		protected void playSound(BlockPointer blockPointer) {
			blockPointer.getWorld().fireWorldEvent(this.field_13364 ? 1000 : 1001, blockPointer.getBlockPos(), 0);
		}
	}

	static class class_2970 extends Bootstrap.class_2969 {
		private class_2970() {
		}

		@Override
		protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
			this.field_13364 = false;
			Item item = itemStack.getItem();
			if (item instanceof BlockItem) {
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
				BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
				Direction direction2 = blockPointer.getWorld().isAir(blockPos.down()) ? direction : Direction.UP;
				this.field_13364 = ((BlockItem)item).place(new Bootstrap.class_2968(blockPointer.getWorld(), blockPos, direction, itemStack, direction2))
					== ActionResult.SUCCESS;
				if (this.field_13364) {
					itemStack.subtractAmount(1);
				}
			}

			return itemStack;
		}
	}
}
