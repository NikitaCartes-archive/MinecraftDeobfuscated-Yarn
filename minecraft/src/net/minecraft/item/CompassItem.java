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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestType;

public class CompassItem extends Item {
	public CompassItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(new Identifier("angle"), new ItemPropertyGetter() {
			@Environment(EnvType.CLIENT)
			private double angle;
			@Environment(EnvType.CLIENT)
			private double step;
			@Environment(EnvType.CLIENT)
			private long lastTick;

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
					boolean bl2 = CompassItem.method_26363(compoundTag);
					BlockPos blockPos = bl2 ? CompassItem.this.method_26358(world, compoundTag) : CompassItem.this.method_26357(world);
					double f;
					if (blockPos != null) {
						double d = bl ? (double)entity.yaw : CompassItem.method_26361((ItemFrameEntity)entity);
						d = MathHelper.floorMod(d / 360.0, 1.0);
						double e = CompassItem.method_26362(Vec3d.method_24953(blockPos), entity) / (float) (Math.PI * 2);
						f = 0.5 - (d - 0.25 - e);
					} else {
						f = Math.random();
					}

					if (bl && !bl2) {
						f = this.getAngle(world, f);
					}

					return MathHelper.floorMod((float)f, 1.0F);
				}
			}

			@Environment(EnvType.CLIENT)
			private double getAngle(World world, double entityYaw) {
				if (world.getTime() != this.lastTick) {
					this.lastTick = world.getTime();
					double d = entityYaw - this.angle;
					d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
					this.step += d * 0.1;
					this.step *= 0.8;
					this.angle = MathHelper.floorMod(this.angle + this.step, 1.0);
				}

				return this.angle;
			}
		});
	}

	private static boolean method_26363(CompoundTag compoundTag) {
		return compoundTag.contains("LodestoneDimension") || compoundTag.contains("LodestonePos");
	}

	private static boolean method_26365(ItemStack itemStack) {
		return method_26363(itemStack.getOrCreateTag());
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return method_26365(stack);
	}

	private static Optional<DimensionType> method_26364(CompoundTag compoundTag) {
		Identifier identifier = Identifier.tryParse(compoundTag.getString("LodestoneDimension"));
		return identifier != null ? Registry.DIMENSION_TYPE.getOrEmpty(identifier) : Optional.empty();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockPos method_26357(World world) {
		return world.dimension.hasVisibleSky() ? world.getSpawnPos() : null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockPos method_26358(World world, CompoundTag compoundTag) {
		boolean bl = compoundTag.contains("LodestonePos");
		boolean bl2 = compoundTag.contains("LodestonePos");
		if (bl && bl2) {
			Optional<DimensionType> optional = method_26364(compoundTag);
			if (optional.isPresent() && world.dimension.getType().equals(optional.get())) {
				return NbtHelper.toBlockPos((CompoundTag)compoundTag.get("LodestonePos"));
			}
		}

		return null;
	}

	@Environment(EnvType.CLIENT)
	private static double method_26361(ItemFrameEntity itemFrameEntity) {
		return (double)MathHelper.wrapDegrees(180 + itemFrameEntity.getHorizontalFacing().getHorizontal() * 90);
	}

	@Environment(EnvType.CLIENT)
	private static double method_26362(Vec3d vec3d, Entity entity) {
		return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX());
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			CompoundTag compoundTag = stack.getOrCreateTag();
			if (method_26363(compoundTag)) {
				Optional<DimensionType> optional = method_26364(compoundTag);
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
			context.world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			CompoundTag compoundTag = context.stack.getOrCreateTag();
			compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(blockPos));
			compoundTag.putString("LodestoneDimension", DimensionType.getId(context.world.dimension.getType()).toString());
		}

		return super.useOnBlock(context);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return method_26365(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
	}
}
