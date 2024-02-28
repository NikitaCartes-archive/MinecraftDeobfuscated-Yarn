package net.minecraft.entity.decoration.painting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PaintingVariantTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class PaintingEntity extends AbstractDecorationEntity implements VariantHolder<RegistryEntry<PaintingVariant>> {
	private static final TrackedData<RegistryEntry<PaintingVariant>> VARIANT = DataTracker.registerData(
		PaintingEntity.class, TrackedDataHandlerRegistry.PAINTING_VARIANT
	);
	private static final RegistryKey<PaintingVariant> DEFAULT_VARIANT = PaintingVariants.KEBAB;
	public static final MapCodec<RegistryEntry<PaintingVariant>> VARIANT_MAP_CODEC = Registries.PAINTING_VARIANT.getEntryCodec().fieldOf("variant");
	public static final Codec<RegistryEntry<PaintingVariant>> VARIANT_ENTRY_CODEC = VARIANT_MAP_CODEC.codec();

	private static RegistryEntry<PaintingVariant> getDefaultVariant() {
		return Registries.PAINTING_VARIANT.entryOf(DEFAULT_VARIANT);
	}

	public PaintingEntity(EntityType<? extends PaintingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(VARIANT, getDefaultVariant());
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (VARIANT.equals(data)) {
			this.updateAttachmentPosition();
		}
	}

	public void setVariant(RegistryEntry<PaintingVariant> variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	public RegistryEntry<PaintingVariant> getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	public static Optional<PaintingEntity> placePainting(World world, BlockPos pos, Direction facing) {
		PaintingEntity paintingEntity = new PaintingEntity(world, pos);
		List<RegistryEntry<PaintingVariant>> list = new ArrayList();
		Registries.PAINTING_VARIANT.iterateEntries(PaintingVariantTags.PLACEABLE).forEach(list::add);
		if (list.isEmpty()) {
			return Optional.empty();
		} else {
			paintingEntity.setFacing(facing);
			list.removeIf(variant -> {
				paintingEntity.setVariant(variant);
				return !paintingEntity.canStayAttached();
			});
			if (list.isEmpty()) {
				return Optional.empty();
			} else {
				int i = list.stream().mapToInt(PaintingEntity::getSize).max().orElse(0);
				list.removeIf(variant -> getSize(variant) < i);
				Optional<RegistryEntry<PaintingVariant>> optional = Util.getRandomOrEmpty(list, paintingEntity.random);
				if (optional.isEmpty()) {
					return Optional.empty();
				} else {
					paintingEntity.setVariant((RegistryEntry<PaintingVariant>)optional.get());
					paintingEntity.setFacing(facing);
					return Optional.of(paintingEntity);
				}
			}
		}
	}

	private static int getSize(RegistryEntry<PaintingVariant> variant) {
		return variant.value().getWidth() * variant.value().getHeight();
	}

	private PaintingEntity(World world, BlockPos pos) {
		super(EntityType.PAINTING, world, pos);
	}

	public PaintingEntity(World world, BlockPos pos, Direction direction, RegistryEntry<PaintingVariant> variant) {
		this(world, pos);
		this.setVariant(variant);
		this.setFacing(direction);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		writeVariantToNbt(nbt, this.getVariant());
		nbt.putByte("facing", (byte)this.facing.getHorizontal());
		super.writeCustomDataToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		RegistryEntry<PaintingVariant> registryEntry = (RegistryEntry<PaintingVariant>)VARIANT_ENTRY_CODEC.parse(NbtOps.INSTANCE, nbt)
			.result()
			.orElseGet(PaintingEntity::getDefaultVariant);
		this.setVariant(registryEntry);
		this.facing = Direction.fromHorizontal(nbt.getByte("facing"));
		super.readCustomDataFromNbt(nbt);
		this.setFacing(this.facing);
	}

	public static void writeVariantToNbt(NbtCompound nbt, RegistryEntry<PaintingVariant> variant) {
		VARIANT_ENTRY_CODEC.encodeStart(NbtOps.INSTANCE, variant).result().ifPresent(nbtElement -> nbt.copyFrom((NbtCompound)nbtElement));
	}

	@Override
	public int getWidthPixels() {
		return this.getVariant().value().getWidth();
	}

	@Override
	public int getHeightPixels() {
		return this.getVariant().value().getHeight();
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		if (this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
			if (entity instanceof PlayerEntity playerEntity && playerEntity.isInCreativeMode()) {
				return;
			}

			this.dropItem(Items.PAINTING);
		}
	}

	@Override
	public void onPlace() {
		this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.setPosition(x, y, z);
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
		this.setPosition(x, y, z);
	}

	@Override
	public Vec3d getSyncedPos() {
		return Vec3d.of(this.attachmentPos);
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		this.setFacing(Direction.byId(packet.getEntityData()));
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(Items.PAINTING);
	}
}
