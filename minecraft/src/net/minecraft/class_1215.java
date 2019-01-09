package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1215 extends DataFix {
	private static final Logger field_5740 = LogManager.getLogger();

	public class_1215(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(class_1208.field_5726);
		Type<?> type2 = type.findFieldType("Level");
		Type<?> type3 = type2.findFieldType("TileEntities");
		if (!(type3 instanceof ListType)) {
			throw new IllegalStateException("Tile entity type is not a list type.");
		} else {
			ListType<?> listType = (ListType<?>)type3;
			OpticFinder<? extends List<?>> opticFinder = DSL.fieldFinder("TileEntities", (Type<? extends List<?>>)listType);
			Type<?> type4 = this.getInputSchema().getType(class_1208.field_5726);
			OpticFinder<?> opticFinder2 = type4.findField("Level");
			OpticFinder<?> opticFinder3 = opticFinder2.type().findField("Sections");
			Type<?> type5 = opticFinder3.type();
			if (!(type5 instanceof ListType)) {
				throw new IllegalStateException("Expecting sections to be a list.");
			} else {
				Type<?> type6 = ((ListType)type5).getElement();
				OpticFinder<?> opticFinder4 = DSL.typeFinder(type6);
				return TypeRewriteRule.seq(
					new class_3553(this.getOutputSchema(), "AddTrappedChestFix", class_1208.field_5727).makeRule(),
					this.fixTypeEverywhereTyped("Trapped Chest fix", type4, typed -> typed.updateTyped(opticFinder2, typedx -> {
							Optional<? extends Typed<?>> optional = typedx.getOptionalTyped(opticFinder3);
							if (!optional.isPresent()) {
								return typedx;
							} else {
								List<? extends Typed<?>> list = ((Typed)optional.get()).getAllTyped(opticFinder4);
								IntSet intSet = new IntOpenHashSet();

								for (Typed<?> typed2 : list) {
									class_1215.class_1216 lv = new class_1215.class_1216(typed2, this.getInputSchema());
									if (!lv.method_5079()) {
										for (int i = 0; i < 4096; i++) {
											int j = lv.method_5075(i);
											if (lv.method_5180(j)) {
												intSet.add(lv.method_5077() << 12 | i);
											}
										}
									}
								}

								Dynamic<?> dynamic = typedx.get(DSL.remainderFinder());
								int k = dynamic.get("xPos").asInt(0);
								int l = dynamic.get("zPos").asInt(0);
								TaggedChoiceType<String> taggedChoiceType = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(class_1208.field_5727);
								return typedx.updateTyped(opticFinder, typedxx -> typedxx.updateTyped(taggedChoiceType.finder(), typedxxx -> {
										Dynamic<?> dynamicx = typedxxx.getOrCreate(DSL.remainderFinder());
										int kx = dynamicx.get("x").asInt(0) - (k << 4);
										int lx = dynamicx.get("y").asInt(0);
										int m = dynamicx.get("z").asInt(0) - (l << 4);
										return intSet.contains(class_1191.method_5051(kx, lx, m)) ? typedxxx.update(taggedChoiceType.finder(), pair -> pair.mapFirst(string -> {
												if (!Objects.equals(string, "minecraft:chest")) {
													field_5740.warn("Block Entity was expected to be a chest");
												}

												return "minecraft:trapped_chest";
											})) : typedxxx;
									}));
							}
						}))
				);
			}
		}
	}

	public static final class class_1216 extends class_1191.class_1193 {
		@Nullable
		private IntSet field_5741;

		public class_1216(Typed<?> typed, Schema schema) {
			super(typed, schema);
		}

		@Override
		protected boolean method_5076() {
			this.field_5741 = new IntOpenHashSet();

			for (int i = 0; i < this.field_5692.size(); i++) {
				Dynamic<?> dynamic = (Dynamic<?>)this.field_5692.get(i);
				String string = dynamic.get("Name").asString("");
				if (Objects.equals(string, "minecraft:trapped_chest")) {
					this.field_5741.add(i);
				}
			}

			return this.field_5741.isEmpty();
		}

		public boolean method_5180(int i) {
			return this.field_5741.contains(i);
		}
	}
}
