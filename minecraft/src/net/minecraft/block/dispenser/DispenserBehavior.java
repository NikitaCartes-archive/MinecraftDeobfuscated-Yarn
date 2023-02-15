package net.minecraft.block.dispenser;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public interface DispenserBehavior {
	Logger LOGGER = LogUtils.getLogger();
	DispenserBehavior NOOP = (pointer, stack) -> stack;

	ItemStack dispense(BlockPointer pointer, ItemStack stack);

	static void registerDefaults() {
		DispenserBlock.registerBehavior(Items.ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
				arrowEntity.initFromStack(stack);
				arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				PersistentProjectileEntity persistentProjectileEntity = new SpectralArrowEntity(world, position.getX(), position.getY(), position.getZ());
				persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
				return persistentProjectileEntity;
			}
		});
		DispenserBlock.registerBehavior(Items.EGG, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				return Util.make(new EggEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
			}
		});
		DispenserBlock.registerBehavior(Items.SNOWBALL, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				return Util.make(new SnowballEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
			}
		});
		DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new ProjectileDispenserBehavior() {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				return Util.make(new ExperienceBottleEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
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
		DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
						return Util.make(new PotionEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
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
		DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
						return Util.make(new PotionEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
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
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				EntityType<?> entityType = ((SpawnEggItem)stack.getItem()).getEntityType(stack.getNbt());

				try {
					entityType.spawnFromItemStack(pointer.getWorld(), stack, null, pointer.getPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
				} catch (Exception var6) {
					LOGGER.error("Error while dispensing spawn egg from dispenser at {}", pointer.getPos(), var6);
					return ItemStack.EMPTY;
				}

				stack.decrement(1);
				pointer.getWorld().emitGameEvent(null, GameEvent.ENTITY_PLACE, pointer.getPos());
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
				BlockPos blockPos = pointer.getPos().offset(direction);
				ServerWorld serverWorld = pointer.getWorld();
				Consumer<ArmorStandEntity> consumer = EntityType.copier(armorStandEntityx -> armorStandEntityx.setYaw(direction.asRotation()), serverWorld, stack, null);
				ArmorStandEntity armorStandEntity = EntityType.ARMOR_STAND.spawn(serverWorld, stack.getNbt(), consumer, blockPos, SpawnReason.DISPENSER, false, false);
				if (armorStandEntity != null) {
					stack.decrement(1);
				}

				return stack;
			}
		});
		DispenserBlock.registerBehavior(
			Items.SADDLE,
			new FallibleItemDispenserBehavior() {
				@Override
				public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
					BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
					List<LivingEntity> list = pointer.getWorld()
						.getEntitiesByClass(
							LivingEntity.class,
							new Box(blockPos),
							entity -> !(entity instanceof Saddleable saddleable) ? false : !saddleable.isSaddled() && saddleable.canBeSaddled()
						);
					if (!list.isEmpty()) {
						((Saddleable)list.get(0)).saddle(SoundCategory.BLOCKS);
						stack.decrement(1);
						this.setSuccess(true);
						return stack;
					} else {
						return super.dispenseSilently(pointer, stack);
					}
				}
			}
		);
		ItemDispenserBehavior itemDispenserBehavior2 = new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));

				for (AbstractHorseEntity abstractHorseEntity : pointer.getWorld()
					.getEntitiesByClass(AbstractHorseEntity.class, new Box(blockPos), entity -> entity.isAlive() && entity.hasArmorSlot())) {
					if (abstractHorseEntity.isHorseArmor(stack) && !abstractHorseEntity.hasArmorInSlot() && abstractHorseEntity.isTame()) {
						abstractHorseEntity.getStackReference(401).set(stack.split(1));
						this.setSuccess(true);
						return stack;
					}
				}

				return super.dispenseSilently(pointer, stack);
			}
		};
		DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.WHITE_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.ORANGE_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.CYAN_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.BLUE_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.BROWN_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.BLACK_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.GRAY_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.GREEN_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.LIME_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.PINK_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.PURPLE_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.RED_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(Items.YELLOW_CARPET, itemDispenserBehavior2);
		DispenserBlock.registerBehavior(
			Items.CHEST,
			new FallibleItemDispenserBehavior() {
				@Override
				public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
					BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));

					for (AbstractDonkeyEntity abstractDonkeyEntity : pointer.getWorld()
						.getEntitiesByClass(AbstractDonkeyEntity.class, new Box(blockPos), entity -> entity.isAlive() && !entity.hasChest())) {
						if (abstractDonkeyEntity.isTame() && abstractDonkeyEntity.getStackReference(499).set(stack)) {
							stack.decrement(1);
							this.setSuccess(true);
							return stack;
						}
					}

					return super.dispenseSilently(pointer, stack);
				}
			}
		);
		DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(pointer.getWorld(), stack, pointer.getX(), pointer.getY(), pointer.getX(), true);
				DispenserBehavior.setEntityPosition(pointer, fireworkRocketEntity, direction);
				fireworkRocketEntity.setVelocity((double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ(), 0.5F, 1.0F);
				pointer.getWorld().spawnEntity(fireworkRocketEntity);
				stack.decrement(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().syncWorldEvent(WorldEvents.FIREWORK_ROCKET_SHOOTS, pointer.getPos(), 0);
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
				double g = random.nextTriangular((double)direction.getOffsetX(), 0.11485000000000001);
				double h = random.nextTriangular((double)direction.getOffsetY(), 0.11485000000000001);
				double i = random.nextTriangular((double)direction.getOffsetZ(), 0.11485000000000001);
				SmallFireballEntity smallFireballEntity = new SmallFireballEntity(world, d, e, f, g, h, i);
				world.spawnEntity(Util.make(smallFireballEntity, entity -> entity.setItem(stack)));
				stack.decrement(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().syncWorldEvent(WorldEvents.BLAZE_SHOOTS, pointer.getPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenserBehavior(BoatEntity.Type.OAK));
		DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenserBehavior(BoatEntity.Type.SPRUCE));
		DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenserBehavior(BoatEntity.Type.BIRCH));
		DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenserBehavior(BoatEntity.Type.JUNGLE));
		DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenserBehavior(BoatEntity.Type.DARK_OAK));
		DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenserBehavior(BoatEntity.Type.ACACIA));
		DispenserBlock.registerBehavior(Items.CHERRY_BOAT, new BoatDispenserBehavior(BoatEntity.Type.CHERRY));
		DispenserBlock.registerBehavior(Items.MANGROVE_BOAT, new BoatDispenserBehavior(BoatEntity.Type.MANGROVE));
		DispenserBlock.registerBehavior(Items.BAMBOO_RAFT, new BoatDispenserBehavior(BoatEntity.Type.BAMBOO));
		DispenserBlock.registerBehavior(Items.OAK_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.OAK, true));
		DispenserBlock.registerBehavior(Items.SPRUCE_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.SPRUCE, true));
		DispenserBlock.registerBehavior(Items.BIRCH_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.BIRCH, true));
		DispenserBlock.registerBehavior(Items.JUNGLE_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.JUNGLE, true));
		DispenserBlock.registerBehavior(Items.DARK_OAK_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.DARK_OAK, true));
		DispenserBlock.registerBehavior(Items.ACACIA_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.ACACIA, true));
		DispenserBlock.registerBehavior(Items.CHERRY_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.CHERRY, true));
		DispenserBlock.registerBehavior(Items.MANGROVE_CHEST_BOAT, new BoatDispenserBehavior(BoatEntity.Type.MANGROVE, true));
		DispenserBlock.registerBehavior(Items.BAMBOO_CHEST_RAFT, new BoatDispenserBehavior(BoatEntity.Type.BAMBOO, true));
		DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
			private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				FluidModificationItem fluidModificationItem = (FluidModificationItem)stack.getItem();
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				World world = pointer.getWorld();
				if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
					fluidModificationItem.onEmptied(null, world, stack, blockPos);
					return new ItemStack(Items.BUCKET);
				} else {
					return this.fallbackBehavior.dispense(pointer, stack);
				}
			}
		};
		DispenserBlock.registerBehavior(Items.LAVA_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.WATER_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.POWDER_SNOW_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.SALMON_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.COD_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.AXOLOTL_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.TADPOLE_BUCKET, dispenserBehavior);
		DispenserBlock.registerBehavior(Items.BUCKET, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				WorldAccess worldAccess = pointer.getWorld();
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				BlockState blockState = worldAccess.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable) {
					ItemStack itemStack = ((FluidDrainable)block).tryDrainFluid(worldAccess, blockPos, blockState);
					if (itemStack.isEmpty()) {
						return super.dispenseSilently(pointer, stack);
					} else {
						worldAccess.emitGameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
						Item item = itemStack.getItem();
						stack.decrement(1);
						if (stack.isEmpty()) {
							return new ItemStack(item);
						} else {
							if (pointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(new ItemStack(item)) < 0) {
								this.fallbackBehavior.dispense(pointer, new ItemStack(item));
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
				this.setSuccess(true);
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.getPos().offset(direction);
				BlockState blockState = world.getBlockState(blockPos);
				if (AbstractFireBlock.canPlaceAt(world, blockPos, direction)) {
					world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
					world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
				} else if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
					world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)));
					world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
				} else if (blockState.getBlock() instanceof TntBlock) {
					TntBlock.primeTnt(world, blockPos);
					world.removeBlock(blockPos, false);
				} else {
					this.setSuccess(false);
				}

				if (this.isSuccess() && stack.damage(1, world.random, null)) {
					stack.setCount(0);
				}

				return stack;
			}
		});
		DispenserBlock.registerBehavior(Items.BONE_MEAL, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				this.setSuccess(true);
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				if (!BoneMealItem.useOnFertilizable(stack, world, blockPos) && !BoneMealItem.useOnGround(stack, world, blockPos, null)) {
					this.setSuccess(false);
				} else if (!world.isClient) {
					world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 0);
				}

				return stack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.TNT, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				TntEntity tntEntity = new TntEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
				world.spawnEntity(tntEntity);
				world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.emitGameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
				stack.decrement(1);
				return stack;
			}
		});
		DispenserBehavior dispenserBehavior2 = new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
				return stack;
			}
		};
		DispenserBlock.registerBehavior(Items.CREEPER_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.DRAGON_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.SKELETON_SKULL, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.PIGLIN_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(Items.PLAYER_HEAD, dispenserBehavior2);
		DispenserBlock.registerBehavior(
			Items.WITHER_SKELETON_SKULL,
			new FallibleItemDispenserBehavior() {
				@Override
				protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
					World world = pointer.getWorld();
					Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
					BlockPos blockPos = pointer.getPos().offset(direction);
					if (world.isAir(blockPos) && WitherSkullBlock.canDispense(world, blockPos, stack)) {
						world.setBlockState(
							blockPos,
							Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, Integer.valueOf(RotationPropertyHelper.fromDirection(direction))),
							Block.NOTIFY_ALL
						);
						world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
						BlockEntity blockEntity = world.getBlockEntity(blockPos);
						if (blockEntity instanceof SkullBlockEntity) {
							WitherSkullBlock.onPlaced(world, blockPos, (SkullBlockEntity)blockEntity);
						}

						stack.decrement(1);
						this.setSuccess(true);
					} else {
						this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
					}

					return stack;
				}
			}
		);
		DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
				if (world.isAir(blockPos) && carvedPumpkinBlock.canDispense(world, blockPos)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, carvedPumpkinBlock.getDefaultState(), Block.NOTIFY_ALL);
						world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
					}

					stack.decrement(1);
					this.setSuccess(true);
				} else {
					this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
				}

				return stack;
			}
		});
		DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new BlockPlacementDispenserBehavior());

		for (DyeColor dyeColor : DyeColor.values()) {
			DispenserBlock.registerBehavior(ShulkerBoxBlock.get(dyeColor).asItem(), new BlockPlacementDispenserBehavior());
		}

		DispenserBlock.registerBehavior(
			Items.GLASS_BOTTLE.asItem(),
			new FallibleItemDispenserBehavior() {
				private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

				private ItemStack tryPutFilledBottle(BlockPointer pointer, ItemStack emptyBottleStack, ItemStack filledBottleStack) {
					emptyBottleStack.decrement(1);
					if (emptyBottleStack.isEmpty()) {
						pointer.getWorld().emitGameEvent(null, GameEvent.FLUID_PICKUP, pointer.getPos());
						return filledBottleStack.copy();
					} else {
						if (pointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(filledBottleStack.copy()) < 0) {
							this.fallbackBehavior.dispense(pointer, filledBottleStack.copy());
						}

						return emptyBottleStack;
					}
				}

				@Override
				public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
					this.setSuccess(false);
					ServerWorld serverWorld = pointer.getWorld();
					BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
					BlockState blockState = serverWorld.getBlockState(blockPos);
					if (blockState.isIn(BlockTags.BEEHIVES, state -> state.contains(BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock)
						&& (Integer)blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
						((BeehiveBlock)blockState.getBlock()).takeHoney(serverWorld, blockState, blockPos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
						this.setSuccess(true);
						return this.tryPutFilledBottle(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
					} else if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
						this.setSuccess(true);
						return this.tryPutFilledBottle(pointer, stack, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
					} else {
						return super.dispenseSilently(pointer, stack);
					}
				}
			}
		);
		DispenserBlock.registerBehavior(Items.GLOWSTONE, new FallibleItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.getPos().offset(direction);
				World world = pointer.getWorld();
				BlockState blockState = world.getBlockState(blockPos);
				this.setSuccess(true);
				if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
					if ((Integer)blockState.get(RespawnAnchorBlock.CHARGES) != 4) {
						RespawnAnchorBlock.charge(null, world, blockPos, blockState);
						stack.decrement(1);
					} else {
						this.setSuccess(false);
					}

					return stack;
				} else {
					return super.dispenseSilently(pointer, stack);
				}
			}
		});
		DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenserBehavior());
		DispenserBlock.registerBehavior(Items.HONEYCOMB, new FallibleItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				World world = pointer.getWorld();
				BlockState blockState = world.getBlockState(blockPos);
				Optional<BlockState> optional = HoneycombItem.getWaxedState(blockState);
				if (optional.isPresent()) {
					world.setBlockState(blockPos, (BlockState)optional.get());
					world.syncWorldEvent(WorldEvents.BLOCK_WAXED, blockPos, 0);
					stack.decrement(1);
					this.setSuccess(true);
					return stack;
				} else {
					return super.dispenseSilently(pointer, stack);
				}
			}
		});
		DispenserBlock.registerBehavior(
			Items.POTION,
			new ItemDispenserBehavior() {
				private final ItemDispenserBehavior fallback = new ItemDispenserBehavior();

				@Override
				public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
					if (PotionUtil.getPotion(stack) != Potions.WATER) {
						return this.fallback.dispense(pointer, stack);
					} else {
						ServerWorld serverWorld = pointer.getWorld();
						BlockPos blockPos = pointer.getPos();
						BlockPos blockPos2 = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
						if (!serverWorld.getBlockState(blockPos2).isIn(BlockTags.CONVERTABLE_TO_MUD)) {
							return this.fallback.dispense(pointer, stack);
						} else {
							if (!serverWorld.isClient) {
								for (int i = 0; i < 5; i++) {
									serverWorld.spawnParticles(
										ParticleTypes.SPLASH,
										(double)blockPos.getX() + serverWorld.random.nextDouble(),
										(double)(blockPos.getY() + 1),
										(double)blockPos.getZ() + serverWorld.random.nextDouble(),
										1,
										0.0,
										0.0,
										0.0,
										1.0
									);
								}
							}

							serverWorld.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
							serverWorld.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
							serverWorld.setBlockState(blockPos2, Blocks.MUD.getDefaultState());
							return new ItemStack(Items.GLASS_BOTTLE);
						}
					}
				}
			}
		);
	}

	static void setEntityPosition(BlockPointer pointer, Entity entity, Direction direction) {
		entity.setPosition(
			pointer.getX() + (double)direction.getOffsetX() * (0.5000099999997474 - (double)entity.getWidth() / 2.0),
			pointer.getY() + (double)direction.getOffsetY() * (0.5000099999997474 - (double)entity.getHeight() / 2.0) - (double)entity.getHeight() / 2.0,
			pointer.getZ() + (double)direction.getOffsetZ() * (0.5000099999997474 - (double)entity.getWidth() / 2.0)
		);
	}
}
