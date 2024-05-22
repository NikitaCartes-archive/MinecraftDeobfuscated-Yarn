package net.minecraft.datafixer.fix;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityUuidFix extends AbstractUuidFix {
	public BlockEntityUuidFix(Schema outputSchema) {
		super(outputSchema, TypeReferences.BLOCK_ENTITY);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), typed -> {
			typed = this.updateTyped(typed, "minecraft:conduit", this::updateConduit);
			return this.updateTyped(typed, "minecraft:skull", this::updateSkull);
		});
	}

	private Dynamic<?> updateSkull(Dynamic<?> skullDynamic) {
		return (Dynamic<?>)skullDynamic.get("Owner")
			.get()
			.map(ownerDynamic -> (Dynamic)updateStringUuid(ownerDynamic, "Id", "Id").orElse(ownerDynamic))
			.map(ownerDynamic -> skullDynamic.remove("Owner").set("SkullOwner", ownerDynamic))
			.result()
			.orElse(skullDynamic);
	}

	private Dynamic<?> updateConduit(Dynamic<?> conduitDynamic) {
		return (Dynamic<?>)updateCompoundUuid(conduitDynamic, "target_uuid", "Target").orElse(conduitDynamic);
	}
}
