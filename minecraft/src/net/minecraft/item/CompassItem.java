package net.minecraft.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestType;

public class CompassItem extends Item implements Vanishable {
	public CompassItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(
			new Identifier("angle"),
			new ItemPropertyGetter() {
				private final CompassItem.AngleRandomizer targetedAngle = new CompassItem.AngleRandomizer();
				private final CompassItem.AngleRandomizer targetlessAngle = new CompassItem.AngleRandomizer();

				@Environment(EnvType.CLIENT)
				@Override
				public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
					Entity entity = (Entity)(livingEntity != null ? livingEntity : itemStack.method_27319());
					if (entity == null) {
						return 0.0F;
					} else {
						if (world == null) {
							world = entity.world;
						}

						BlockPos blockPos = CompassItem.hasLodestone(itemStack)
							? CompassItem.this.getLodestonePos(world, itemStack.getOrCreateTag())
							: CompassItem.this.getSpawnPos(world);
						long l = world.getTime();
						if (blockPos != null
							&& !(entity.getPos().squaredDistanceTo((double)blockPos.getX() + 0.5, entity.getPos().getY(), (double)blockPos.getZ() + 0.5) < 1.0E-5F)) {
							boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).isMainPlayer();
							double e = 0.0;
							if (bl) {
								e = (double)livingEntity.yaw;
							} else if (entity instanceof ItemFrameEntity) {
								e = CompassItem.getItemFrameAngleOffset((ItemFrameEntity)entity);
							} else if (entity instanceof ItemEntity) {
								e = (double)(180.0F - ((ItemEntity)entity).method_27314(0.5F) / (float) (Math.PI * 2) * 360.0F);
							} else if (livingEntity != null) {
								e = (double)livingEntity.bodyYaw;
							}

							e = MathHelper.floorMod(e / 360.0, 1.0);
							double f = CompassItem.getAngleToPos(Vec3d.method_24953(blockPos), entity) / (float) (Math.PI * 2);
							double g;
							if (bl) {
								if (this.targetedAngle.method_27316(l)) {
									this.targetedAngle.update(l, 0.5 - (e - 0.25));
								}

								g = f + this.targetedAngle.value;
							} else {
								g = 0.5 - (e - 0.25 - f);
							}

							return MathHelper.floorMod((float)g, 1.0F);
						} else {
							if (this.targetlessAngle.method_27316(l)) {
								this.targetlessAngle.update(l, Math.random());
							}

							double d = this.targetlessAngle.value + (double)((float)itemStack.hashCode() / 2.1474836E9F);
							return MathHelper.floorMod((float)d, 1.0F);
						}
					}
				}
			}
		);
	}

	private static boolean hasLodestone(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		return compoundTag != null && (compoundTag.contains("LodestoneDimension") || compoundTag.contains("LodestonePos"));
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return hasLodestone(stack) || super.hasEnchantmentGlint(stack);
	}

	private static Optional<DimensionType> getLodestoneDimension(CompoundTag tag) {
		Identifier identifier = Identifier.tryParse(tag.getString("LodestoneDimension"));
		return identifier != null ? Registry.DIMENSION_TYPE.getOrEmpty(identifier) : Optional.empty();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockPos getSpawnPos(World world) {
		return world.dimension.hasVisibleSky() ? world.getSpawnPos() : null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockPos getLodestonePos(World world, CompoundTag tag) {
		boolean bl = tag.contains("LodestonePos");
		boolean bl2 = tag.contains("LodestoneDimension");
		if (bl && bl2) {
			Optional<DimensionType> optional = getLodestoneDimension(tag);
			if (optional.isPresent() && world.dimension.getType().equals(optional.get())) {
				return NbtHelper.toBlockPos((CompoundTag)tag.get("LodestonePos"));
			}
		}

		return null;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			if (hasLodestone(stack)) {
				CompoundTag compoundTag = stack.getOrCreateTag();
				if (compoundTag.contains("LodestoneTracked") && !compoundTag.getBoolean("LodestoneTracked")) {
					return;
				}

				Optional<DimensionType> optional = getLodestoneDimension(compoundTag);
				if (optional.isPresent()
					&& ((DimensionType)optional.get()).equals(world.dimension.getType())
					&& compoundTag.contains("LodestonePos")
					&& !((ServerWorld)world)
						.getPointOfInterestStorage()
						.method_26339(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos((CompoundTag)compoundTag.get("LodestonePos")))) {
					compoundTag.remove("LodestonePos");
				}
			}
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos blockPos = context.hit.getBlockPos();
		if (context.world.getBlockState(blockPos).getBlock() != Blocks.LODESTONE) {
			return super.useOnBlock(context);
		} else {
			context.world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
			boolean bl = !context.player.abilities.creativeMode && context.stack.getCount() == 1;
			if (bl) {
				this.method_27315(context.world.dimension, blockPos, context.stack.getOrCreateTag());
			} else {
				ItemStack itemStack = new ItemStack(Items.COMPASS, 1);
				CompoundTag compoundTag = context.stack.hasTag() ? context.stack.getTag().copy() : new CompoundTag();
				itemStack.setTag(compoundTag);
				if (!context.player.abilities.creativeMode) {
					context.stack.decrement(1);
				}

				this.method_27315(context.world.dimension, blockPos, compoundTag);
				if (!context.player.inventory.insertStack(itemStack)) {
					context.player.dropItem(itemStack, false);
				}
			}

			return ActionResult.SUCCESS;
		}
	}

	private void method_27315(Dimension dimension, BlockPos blockPos, CompoundTag compoundTag) {
		compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(blockPos));
		compoundTag.putString("LodestoneDimension", DimensionType.getId(dimension.getType()).toString());
		compoundTag.putBoolean("LodestoneTracked", true);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
	}

	@Environment(EnvType.CLIENT)
	private static double getItemFrameAngleOffset(ItemFrameEntity itemFrame) {
		Direction direction = itemFrame.getHorizontalFacing();
		int i = direction.getAxis().isVertical() ? 90 * direction.getDirection().offset() : 0;
		return (double)MathHelper.wrapDegrees(180 + direction.getHorizontal() * 90 + itemFrame.getRotation() * 45 + i);
	}

	@Environment(EnvType.CLIENT)
	private static double getAngleToPos(Vec3d pos, Entity entity) {
		return Math.atan2(pos.getZ() - entity.getZ(), pos.getX() - entity.getX());
	}

	static class AngleRandomizer {
		@Environment(EnvType.CLIENT)
		private double value;
		@Environment(EnvType.CLIENT)
		private double speed;
		@Environment(EnvType.CLIENT)
		private long lastUpdateTime;

		private AngleRandomizer() {
		}

		@Environment(EnvType.CLIENT)
		private boolean method_27316(long l) {
			return this.lastUpdateTime != l;
		}

		@Environment(EnvType.CLIENT)
		private void update(long l, double d) {
			this.lastUpdateTime = l;
			double e = d - this.value;
			e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
			this.speed += e * 0.1;
			this.speed *= 0.8;
			this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
		}
	}
}
