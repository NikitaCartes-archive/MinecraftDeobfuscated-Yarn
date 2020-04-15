package net.minecraft.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
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
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestType;

public class CompassItem extends Item implements Vanishable {
	public CompassItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(new Identifier("angle"), new ItemPropertyGetter() {
			private final CompassItem.AngleRandomizer targetedAngle = new CompassItem.AngleRandomizer();
			private final CompassItem.AngleRandomizer targetlessAngle = new CompassItem.AngleRandomizer();

			@Environment(EnvType.CLIENT)
			@Override
			public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
				if (livingEntity == null && !itemStack.isInFrame()) {
					return 0.0F;
				} else {
					boolean bl = livingEntity != null;
					Entity entity = (Entity)(bl ? livingEntity : itemStack.getFrame());
					if (world == null) {
						world = entity.world;
					}

					CompoundTag compoundTag = itemStack.getOrCreateTag();
					BlockPos blockPos = CompassItem.hasLodestone(compoundTag) ? CompassItem.this.getLodestonePos(world, compoundTag) : CompassItem.this.getSpawnPos(world);
					double g;
					if (blockPos != null) {
						double d = bl ? (double)entity.yaw : CompassItem.getItemFrameAngleOffset((ItemFrameEntity)entity);
						d = MathHelper.floorMod(d / 360.0, 1.0);
						double e = 0.5 - (d - 0.25);
						boolean bl2 = !bl && entity.getHorizontalFacing().getAxis().isVertical();
						boolean bl3 = bl2 && entity.getHorizontalFacing() == Direction.UP;
						double f = CompassItem.getAngleToPos(Vec3d.method_24953(blockPos), entity) / (float) (Math.PI * 2) * (double)(bl3 ? -1 : 1);
						g = 0.5 - (d - 0.25 - f) * (double)(bl2 ? -1 : 1);
						if (bl) {
							this.targetedAngle.update(world, e);
							g -= e - this.targetedAngle.get();
						}
					} else {
						this.targetlessAngle.update(world, Math.random());
						g = this.targetlessAngle.get();
					}

					return MathHelper.floorMod((float)g, 1.0F);
				}
			}
		});
	}

	private static boolean hasLodestone(CompoundTag tag) {
		return tag.contains("LodestoneDimension") || tag.contains("LodestonePos");
	}

	private static boolean hasLodestone(ItemStack stack) {
		return hasLodestone(stack.getOrCreateTag());
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

	@Environment(EnvType.CLIENT)
	private static double getItemFrameAngleOffset(ItemFrameEntity itemFrame) {
		Direction direction = itemFrame.getHorizontalFacing();
		return direction.getAxis().isVertical()
			? (double)MathHelper.wrapDegrees(itemFrame.getRotation() * -45)
			: (double)MathHelper.wrapDegrees(180 + itemFrame.getHorizontalFacing().getHorizontal() * 90 + itemFrame.getRotation() * 45);
	}

	@Environment(EnvType.CLIENT)
	private static double getAngleToPos(Vec3d pos, Entity entity) {
		return Math.atan2(pos.getZ() - entity.getZ(), pos.getX() - entity.getX());
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			CompoundTag compoundTag = stack.getOrCreateTag();
			if (hasLodestone(compoundTag)) {
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
		if (context.world.getBlockState(blockPos).getBlock() == Blocks.LODESTONE) {
			context.world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
			CompoundTag compoundTag = context.stack.getOrCreateTag();
			compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(blockPos));
			compoundTag.putString("LodestoneDimension", DimensionType.getId(context.world.dimension.getType()).toString());
			compoundTag.putBoolean("LodestoneTracked", true);
			return ActionResult.SUCCESS;
		} else {
			return super.useOnBlock(context);
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
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
		public double get() {
			return this.value;
		}

		@Environment(EnvType.CLIENT)
		private void update(World world, double target) {
			if (world.getTime() != this.lastUpdateTime) {
				this.lastUpdateTime = world.getTime();
				double d = target - this.value;
				d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
				this.speed += d * 0.1;
				this.speed *= 0.8;
				this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
			}
		}
	}
}
