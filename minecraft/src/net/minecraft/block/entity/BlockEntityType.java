package net.minecraft.block.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final BlockEntityType<FurnaceBlockEntity> field_11903 = create("furnace", BlockEntityType.Builder.create(FurnaceBlockEntity::new));
	public static final BlockEntityType<ChestBlockEntity> field_11914 = create("chest", BlockEntityType.Builder.create(ChestBlockEntity::new));
	public static final BlockEntityType<TrappedChestBlockEntity> field_11891 = create(
		"trapped_chest", BlockEntityType.Builder.create(TrappedChestBlockEntity::new)
	);
	public static final BlockEntityType<EnderChestBlockEntity> field_11901 = create("ender_chest", BlockEntityType.Builder.create(EnderChestBlockEntity::new));
	public static final BlockEntityType<JukeboxBlockEntity> field_11907 = create("jukebox", BlockEntityType.Builder.create(JukeboxBlockEntity::new));
	public static final BlockEntityType<DispenserBlockEntity> field_11887 = create("dispenser", BlockEntityType.Builder.create(DispenserBlockEntity::new));
	public static final BlockEntityType<DropperBlockEntity> field_11899 = create("dropper", BlockEntityType.Builder.create(DropperBlockEntity::new));
	public static final BlockEntityType<SignBlockEntity> field_11911 = create("sign", BlockEntityType.Builder.create(SignBlockEntity::new));
	public static final BlockEntityType<MobSpawnerBlockEntity> field_11889 = create("mob_spawner", BlockEntityType.Builder.create(MobSpawnerBlockEntity::new));
	public static final BlockEntityType<PistonBlockEntity> field_11897 = create("piston", BlockEntityType.Builder.create(PistonBlockEntity::new));
	public static final BlockEntityType<BrewingStandBlockEntity> field_11894 = create(
		"brewing_stand", BlockEntityType.Builder.create(BrewingStandBlockEntity::new)
	);
	public static final BlockEntityType<EnchantingTableBlockEntity> field_11912 = create(
		"enchanting_table", BlockEntityType.Builder.create(EnchantingTableBlockEntity::new)
	);
	public static final BlockEntityType<EndPortalBlockEntity> field_11898 = create("end_portal", BlockEntityType.Builder.create(EndPortalBlockEntity::new));
	public static final BlockEntityType<BeaconBlockEntity> field_11890 = create("beacon", BlockEntityType.Builder.create(BeaconBlockEntity::new));
	public static final BlockEntityType<SkullBlockEntity> field_11913 = create("skull", BlockEntityType.Builder.create(SkullBlockEntity::new));
	public static final BlockEntityType<DaylightDetectorBlockEntity> field_11900 = create(
		"daylight_detector", BlockEntityType.Builder.create(DaylightDetectorBlockEntity::new)
	);
	public static final BlockEntityType<HopperBlockEntity> field_11888 = create("hopper", BlockEntityType.Builder.create(HopperBlockEntity::new));
	public static final BlockEntityType<ComparatorBlockEntity> field_11908 = create("comparator", BlockEntityType.Builder.create(ComparatorBlockEntity::new));
	public static final BlockEntityType<BannerBlockEntity> field_11905 = create("banner", BlockEntityType.Builder.create(BannerBlockEntity::new));
	public static final BlockEntityType<StructureBlockBlockEntity> field_11895 = create(
		"structure_block", BlockEntityType.Builder.create(StructureBlockBlockEntity::new)
	);
	public static final BlockEntityType<EndGatewayBlockEntity> field_11906 = create("end_gateway", BlockEntityType.Builder.create(EndGatewayBlockEntity::new));
	public static final BlockEntityType<CommandBlockBlockEntity> field_11904 = create(
		"command_block", BlockEntityType.Builder.create(CommandBlockBlockEntity::new)
	);
	public static final BlockEntityType<ShulkerBoxBlockEntity> field_11896 = create("shulker_box", BlockEntityType.Builder.create(ShulkerBoxBlockEntity::new));
	public static final BlockEntityType<BedBlockEntity> field_11910 = create("bed", BlockEntityType.Builder.create(BedBlockEntity::new));
	public static final BlockEntityType<ConduitBlockEntity> field_11902 = create("conduit", BlockEntityType.Builder.create(ConduitBlockEntity::new));
	public static final BlockEntityType<BarrelBlockEntity> field_16411 = create("barrel", BlockEntityType.Builder.create(BarrelBlockEntity::new));
	public static final BlockEntityType<SmokerBlockEntity> field_16414 = create("smoker", BlockEntityType.Builder.create(SmokerBlockEntity::new));
	public static final BlockEntityType<BlastFurnaceBlockEntity> field_16415 = create(
		"blast_furnace", BlockEntityType.Builder.create(BlastFurnaceBlockEntity::new)
	);
	public static final BlockEntityType<LecternBlockEntity> field_16412 = create("lectern", BlockEntityType.Builder.create(LecternBlockEntity::new));
	public static final BlockEntityType<BellBlockEntity> field_16413 = create("bell", BlockEntityType.Builder.create(BellBlockEntity::new));
	public static final BlockEntityType<JigsawBlockEntity> field_16549 = create("jigsaw", BlockEntityType.Builder.create(JigsawBlockEntity::new));
	public static final BlockEntityType<CampfireBlockEntity> field_17380 = create("campfire", BlockEntityType.Builder.create(CampfireBlockEntity::new));
	private final Supplier<? extends T> supplier;
	private final Type<?> type;

	@Nullable
	public static Identifier getId(BlockEntityType<?> blockEntityType) {
		return Registry.BLOCK_ENTITY.getId(blockEntityType);
	}

	private static <T extends BlockEntity> BlockEntityType<T> create(String string, BlockEntityType.Builder<T> builder) {
		Type<?> type = null;

		try {
			type = Schemas.getFixer()
				.getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
				.getChoiceType(TypeReferences.BLOCK_ENTITY, string);
		} catch (IllegalStateException var4) {
			if (SharedConstants.isDevelopment) {
				throw var4;
			}

			LOGGER.warn("No data fixer registered for block entity {}", string);
		}

		return Registry.register(Registry.BLOCK_ENTITY, string, builder.build(type));
	}

	public BlockEntityType(Supplier<? extends T> supplier, Type<?> type) {
		this.supplier = supplier;
		this.type = type;
	}

	@Nullable
	public T instantiate() {
		return (T)this.supplier.get();
	}

	public static final class Builder<T extends BlockEntity> {
		private final Supplier<? extends T> supplier;

		private Builder(Supplier<? extends T> supplier) {
			this.supplier = supplier;
		}

		public static <T extends BlockEntity> BlockEntityType.Builder<T> create(Supplier<? extends T> supplier) {
			return new BlockEntityType.Builder<>(supplier);
		}

		public BlockEntityType<T> build(Type<?> type) {
			return new BlockEntityType<>(this.supplier, type);
		}
	}
}
