package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;

public class RenameBlockEntityFix extends DataFix {
	private final String name;
	private final UnaryOperator<String> renamer;

	private RenameBlockEntityFix(Schema outputSchema, String name, UnaryOperator<String> renamer) {
		super(outputSchema, true);
		this.name = name;
		this.renamer = renamer;
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
		TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
		return this.fixTypeEverywhere(this.name, taggedChoiceType, taggedChoiceType2, ops -> pair -> pair.mapFirst(this.renamer));
	}

	public static DataFix create(Schema outputSchema, String name, UnaryOperator<String> renamer) {
		return new RenameBlockEntityFix(outputSchema, name, renamer);
	}
}
