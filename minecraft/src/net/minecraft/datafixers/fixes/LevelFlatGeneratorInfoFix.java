package net.minecraft.datafixers.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.datafixers.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix extends DataFix {
	private static final Splitter SPLIT_ON_SEMICOLON = Splitter.on(';').limit(5);
	private static final Splitter SPLIT_ON_COMMA = Splitter.on(',');
	private static final Splitter SPLIT_ON_LOWER_X = Splitter.on('x').limit(2);
	private static final Splitter SPLIT_ON_ASTERISK = Splitter.on('*').limit(2);
	private static final Splitter SPLIT_ON_COLON = Splitter.on(':').limit(3);

	public LevelFlatGeneratorInfoFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.LEVEL), typed -> typed.update(DSL.remainderFinder(), this::method_5090)
		);
	}

	private Dynamic<?> method_5090(Dynamic<?> dynamic) {
		return dynamic.getString("generatorName").equalsIgnoreCase("flat")
			? dynamic.update("generatorOptions", dynamicx -> DataFixUtils.orElse(dynamicx.getStringValue().map(this::transform).map(dynamicx::createString), dynamicx))
			: dynamic;
	}

	@VisibleForTesting
	String transform(String string) {
		if (string.isEmpty()) {
			return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
		} else {
			Iterator<String> iterator = SPLIT_ON_SEMICOLON.split(string).iterator();
			String string2 = (String)iterator.next();
			int i;
			String string3;
			if (iterator.hasNext()) {
				i = NumberUtils.toInt(string2, 0);
				string3 = (String)iterator.next();
			} else {
				i = 0;
				string3 = string2;
			}

			if (i >= 0 && i <= 3) {
				StringBuilder stringBuilder = new StringBuilder();
				Splitter splitter = i < 3 ? SPLIT_ON_LOWER_X : SPLIT_ON_ASTERISK;
				stringBuilder.append((String)StreamSupport.stream(SPLIT_ON_COMMA.split(string3).spliterator(), false).map(stringx -> {
					List<String> list = splitter.splitToList(stringx);
					int j;
					String string2x;
					if (list.size() == 2) {
						j = NumberUtils.toInt((String)list.get(0));
						string2x = (String)list.get(1);
					} else {
						j = 1;
						string2x = (String)list.get(0);
					}

					List<String> list2 = SPLIT_ON_COLON.splitToList(string2x);
					int k = ((String)list2.get(0)).equals("minecraft") ? 1 : 0;
					String string3x = (String)list2.get(k);
					int l = i == 3 ? EntityBlockStateFix.getNumericalBlockId("minecraft:" + string3x) : NumberUtils.toInt(string3x, 0);
					int m = k + 1;
					int n = list2.size() > m ? NumberUtils.toInt((String)list2.get(m), 0) : 0;
					return (j == 1 ? "" : j + "*") + BlockStateFlattening.lookupState(l << 4 | n).getString("Name");
				}).collect(Collectors.joining(",")));

				while (iterator.hasNext()) {
					stringBuilder.append(';').append((String)iterator.next());
				}

				return stringBuilder.toString();
			} else {
				return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
			}
		}
	}
}
