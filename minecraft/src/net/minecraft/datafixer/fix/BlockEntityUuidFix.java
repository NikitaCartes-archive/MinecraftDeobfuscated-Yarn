package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
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

	private Dynamic<?> updateSkull(Dynamic<?> root) {
		return (Dynamic<?>)root.get("Owner")
			.get()
			.map(dynamic -> (Dynamic)updateStringUuid(dynamic, "Id", "Id").orElse(dynamic))
			.map(dynamic2 -> root.remove("Owner").set("SkullOwner", dynamic2))
			.orElse(root);
	}

	private Dynamic<?> updateConduit(Dynamic<?> root) {
		return (Dynamic<?>)updateCompoundUuid(root, "target_uuid", "Target").orElse(root);
	}
}
