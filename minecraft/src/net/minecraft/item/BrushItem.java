package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BrushItem extends Item {
	public static final int field_42682 = 10;
	private static final int field_42683 = 225;

	public BrushItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		if (playerEntity != null) {
			playerEntity.setCurrentHand(context.getHand());
		}

		return ActionResult.CONSUME;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BRUSH;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 225;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (remainingUseTicks >= 0 && user instanceof PlayerEntity playerEntity) {
			BlockHitResult blockHitResult = Item.raycast(world, playerEntity, RaycastContext.FluidHandling.NONE);
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (blockHitResult.getType() == HitResult.Type.MISS) {
				user.stopUsingItem();
			} else {
				int i = this.getMaxUseTime(stack) - remainingUseTicks + 1;
				if (i == 1 || i % 10 == 0) {
					BlockState blockState = world.getBlockState(blockPos);
					this.addDustParticles(world, blockHitResult, blockState, user.getRotationVec(0.0F));
					SoundEvent soundEvent;
					if (blockState.getBlock() instanceof BrushableBlock brushableBlock) {
						soundEvent = brushableBlock.getBrushingSound();
					} else {
						soundEvent = SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC;
					}

					world.playSound(playerEntity, blockPos, soundEvent, SoundCategory.PLAYERS);
					if (!world.isClient() && world.getBlockEntity(blockPos) instanceof BrushableBlockEntity brushableBlockEntity) {
						boolean bl = brushableBlockEntity.brush(world.getTime(), playerEntity, blockHitResult.getSide());
						if (bl) {
							stack.damage(1, user, userx -> userx.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
						}
					}
				}
			}
		} else {
			user.stopUsingItem();
		}
	}

	public void addDustParticles(World world, BlockHitResult hitResult, BlockState state, Vec3d userRotation) {
		double d = 3.0;
		int i = world.getRandom().nextBetweenExclusive(7, 12);
		BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
		Direction direction = hitResult.getSide();
		BrushItem.DustParticlesOffset dustParticlesOffset = BrushItem.DustParticlesOffset.fromSide(userRotation, direction);
		Vec3d vec3d = hitResult.getPos();

		for (int j = 0; j < i; j++) {
			world.addParticle(
				blockStateParticleEffect,
				vec3d.x - (double)(direction == Direction.WEST ? 1.0E-6F : 0.0F),
				vec3d.y,
				vec3d.z - (double)(direction == Direction.NORTH ? 1.0E-6F : 0.0F),
				dustParticlesOffset.xd() * 3.0 * world.getRandom().nextDouble(),
				0.0,
				dustParticlesOffset.zd() * 3.0 * world.getRandom().nextDouble()
			);
		}
	}

	static record DustParticlesOffset(double xd, double yd, double zd) {
		private static final double field_42685 = 1.0;
		private static final double field_42686 = 0.1;

		public static BrushItem.DustParticlesOffset fromSide(Vec3d userRotation, Direction side) {
			double d = 0.0;

			return switch (side) {
				case DOWN -> new BrushItem.DustParticlesOffset(-userRotation.getX(), 0.0, userRotation.getZ());
				case UP -> new BrushItem.DustParticlesOffset(userRotation.getZ(), 0.0, -userRotation.getX());
				case NORTH -> new BrushItem.DustParticlesOffset(1.0, 0.0, -0.1);
				case SOUTH -> new BrushItem.DustParticlesOffset(-1.0, 0.0, 0.1);
				case WEST -> new BrushItem.DustParticlesOffset(-0.1, 0.0, -1.0);
				case EAST -> new BrushItem.DustParticlesOffset(0.1, 0.0, 1.0);
			};
		}
	}
}
