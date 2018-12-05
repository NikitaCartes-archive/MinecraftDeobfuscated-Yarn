package net.minecraft;

import java.io.PrintStream;
import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.PumpkinCarvedBlock;
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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DebugPrintStreamLogger;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Language;
import net.minecraft.util.PrintStreamLogger;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
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
		DispenserBlock.registerBehavior(Items.field_8803, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				return new ThrownEggEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});
		DispenserBlock.registerBehavior(Items.field_8543, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				return new SnowballEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});
		DispenserBlock.registerBehavior(Items.field_8287, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
				return new ThrownExperienceBottleEntity(world, position.getX(), position.getY(), position.getZ());
			}

			@Override
			protected float getVariation() {
				return super.getVariation() * 0.5F;
			}

			@Override
			protected float getForce() {
				return super.getForce() * 1.25F;
			}
		});
		DispenserBlock.registerBehavior(Items.field_8436, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
						return new ThrownPotionEntity(world, position.getX(), position.getY(), position.getZ(), itemStack.copy());
					}

					@Override
					protected float getVariation() {
						return super.getVariation() * 0.5F;
					}

					@Override
					protected float getForce() {
						return super.getForce() * 1.25F;
					}
				}).dispense(blockPointer, itemStack);
			}
		});
		DispenserBlock.registerBehavior(Items.field_8150, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected Projectile createProjectile(World world, Position position, ItemStack itemStack) {
						return new ThrownPotionEntity(world, position.getX(), position.getY(), position.getZ(), itemStack.copy());
					}

					@Override
					protected float getVariation() {
						return super.getVariation() * 0.5F;
					}

					@Override
					protected float getForce() {
						return super.getForce() * 1.25F;
					}
				}).dispense(blockPointer, itemStack);
			}
		});
		ItemDispenserBehavior itemDispenserBehavior = new ItemDispenserBehavior() {
			@Override
			public ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.field_10918);
				EntityType<?> entityType = ((SpawnEggItem)itemStack.getItem()).method_8015(itemStack.getTag());
				if (entityType != null) {
					entityType.spawnFromItemStack(
						blockPointer.getWorld(), itemStack, null, blockPointer.getBlockPos().method_10093(direction), class_3730.field_16470, direction != Direction.UP, false
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
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.field_10918);
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
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.field_10918);
				Position position = DispenserBlock.getOutputLocation(blockPointer);
				double d = position.getX() + (double)((float)direction.getOffsetX() * 0.3F);
				double e = position.getY() + (double)((float)direction.getOffsetY() * 0.3F);
				double f = position.getZ() + (double)((float)direction.getOffsetZ() * 0.3F);
				World world = blockPointer.getWorld();
				Random random = world.random;
				double g = random.nextGaussian() * 0.05 + (double)direction.getOffsetX();
				double h = random.nextGaussian() * 0.05 + (double)direction.getOffsetY();
				double i = random.nextGaussian() * 0.05 + (double)direction.getOffsetZ();
				world.spawnEntity(new SmallFireballEntity(world, d, e, f, g, h, i));
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
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));
				World world = blockPointer.getWorld();
				if (bucketItem.method_7731(null, world, blockPos, null)) {
					bucketItem.method_7728(world, itemStack, blockPos);
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
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));
				BlockState blockState = iWorld.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof class_2263) {
					Fluid fluid = ((class_2263)block).method_9700(iWorld, blockPos, blockState);
					if (!(fluid instanceof BaseFluid)) {
						return super.method_10135(blockPointer, itemStack);
					} else {
						Item item = fluid.getBucketItem();
						itemStack.subtractAmount(1);
						if (itemStack.isEmpty()) {
							return new ItemStack(item);
						} else {
							if (blockPointer.<DispenserBlockEntity>getBlockEntity().method_11075(new ItemStack(item)) < 0) {
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
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));
				if (FlintAndSteelItem.method_7825(world, blockPos)) {
					world.setBlockState(blockPos, Blocks.field_10036.getDefaultState());
				} else {
					Block block = world.getBlockState(blockPos).getBlock();
					if (block instanceof TntBlock) {
						((TntBlock)block).method_10738(world, blockPos);
						world.clearBlockState(blockPos);
					} else {
						this.field_13364 = false;
					}
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
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));
				if (!BoneMealItem.method_7720(itemStack, world, blockPos) && !BoneMealItem.method_7719(itemStack, world, blockPos, null)) {
					this.field_13364 = false;
				} else if (!world.isRemote) {
					world.fireWorldEvent(2005, blockPos, 0);
				}

				return itemStack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.field_10375, new ItemDispenserBehavior() {
			@Override
			protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
				World world = blockPointer.getWorld();
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));
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
					Direction direction = blockPointer.getBlockState().get(DispenserBlock.field_10918);
					BlockPos blockPos = blockPointer.getBlockPos().method_10093(direction);
					this.field_13364 = true;
					if (world.isAir(blockPos) && WitherSkullBlock.method_10899(world, blockPos, itemStack)) {
						world.setBlockState(
							blockPos,
							Blocks.field_10177
								.getDefaultState()
								.with(SkullBlock.field_11505, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().getHorizontal() * 4)),
							3
						);
						BlockEntity blockEntity = world.getBlockEntity(blockPos);
						if (blockEntity instanceof SkullBlockEntity) {
							WitherSkullBlock.method_10898(world, blockPos, (SkullBlockEntity)blockEntity);
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
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));
				PumpkinCarvedBlock pumpkinCarvedBlock = (PumpkinCarvedBlock)Blocks.field_10147;
				this.field_13364 = true;
				if (world.isAir(blockPos) && pumpkinCarvedBlock.method_9733(world, blockPos)) {
					if (!world.isRemote) {
						world.setBlockState(blockPos, pumpkinCarvedBlock.getDefaultState(), 3);
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
				if (!world.isRemote()) {
					this.field_13364 = false;
					BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));

					for (SheepEntity sheepEntity : world.getVisibleEntities(SheepEntity.class, new BoundingBox(blockPos))) {
						if (sheepEntity.isValid() && !sheepEntity.isSheared() && !sheepEntity.isChild()) {
							sheepEntity.method_6636();
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
				if (EntityType.getId(EntityType.PLAYER) == null) {
					throw new IllegalStateException("Failed loading EntityTypes");
				} else {
					BrewingRecipeRegistry.registerDefaults();
					EntitySelectorOptions.method_9960();
					registerDispenserBehaviors();
					ArgumentTypes.register();
					if (SharedConstants.isDevelopment) {
						checkTranslations("block", Registry.BLOCK, Block::getTranslationKey);
						checkTranslations("biome", Registry.BIOME, Biome::getTranslationKey);
						checkTranslations("enchantment", Registry.ENCHANTMENT, Enchantment::getTranslationKey);
						checkTranslations("item", Registry.ITEM, Item::getTranslationKey);
						checkTranslations("effect", Registry.STATUS_EFFECT, StatusEffect::getTranslationKey);
						checkTranslations("entity", Registry.ENTITY_TYPE, EntityType::getTranslationKey);
					}

					setOutputStreams();
				}
			}
		}
	}

	private static <T> void checkTranslations(String string, Registry<T> registry, Function<T, String> function) {
		Language language = Language.getInstance();
		registry.iterator().forEachRemaining(object -> {
			String string2 = (String)function.apply(object);
			if (!language.hasTranslation(string2)) {
				LOGGER.warn("Missing translation for {}: {} (key: '{}')", string, registry.getId((T)object), string2);
			}
		});
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
			Direction direction = blockPointer.getBlockState().get(DispenserBlock.field_10918);
			World world = blockPointer.getWorld();
			double d = blockPointer.getX() + (double)((float)direction.getOffsetX() * 1.125F);
			double e = blockPointer.getY() + (double)((float)direction.getOffsetY() * 1.125F);
			double f = blockPointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125F);
			BlockPos blockPos = blockPointer.getBlockPos().method_10093(direction);
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
			super(world, null, itemStack, blockPos, direction2, 0.5F, 0.0F, 0.5F);
			this.field_13362 = direction;
		}

		@Override
		public BlockPos getPos() {
			return this.pos;
		}

		@Override
		public boolean canPlace() {
			return this.world.getBlockState(this.pos).method_11587(this);
		}

		@Override
		public boolean method_7717() {
			return this.canPlace();
		}

		@Override
		public Direction method_7715() {
			return Direction.DOWN;
		}

		@Override
		public Direction[] method_7718() {
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
		public Direction method_8042() {
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
				Direction direction = blockPointer.getBlockState().get(DispenserBlock.field_10918);
				BlockPos blockPos = blockPointer.getBlockPos().method_10093(direction);
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
