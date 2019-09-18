package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4568 implements class_4570 {
	private static final Logger field_20763 = LogManager.getLogger();
	private final Identifier field_20764;

	public class_4568(Identifier identifier) {
		this.field_20764 = identifier;
	}

	@Override
	public void check(LootTableReporter lootTableReporter) {
		if (lootTableReporter.method_22572(this.field_20764)) {
			lootTableReporter.report("Condition " + this.field_20764 + " is recursively called");
		} else {
			class_4570.super.check(lootTableReporter);
			class_4570 lv = lootTableReporter.method_22576(this.field_20764);
			if (lv == null) {
				lootTableReporter.report("Unknown condition table called " + this.field_20764);
			} else {
				lv.check(lootTableReporter.method_22569(".{" + this.field_20764 + "}", this.field_20764));
			}
		}
	}

	public boolean method_22579(LootContext lootContext) {
		class_4570 lv = lootContext.method_22558(this.field_20764);
		if (lootContext.method_22555(lv)) {
			boolean var3;
			try {
				var3 = lv.test(lootContext);
			} finally {
				lootContext.method_22557(lv);
			}

			return var3;
		} else {
			field_20763.warn("Detected infinite loop in loot tables");
			return false;
		}
	}

	public static class class_4569 extends class_4570.Factory<class_4568> {
		public class_4569() {
			super(new Identifier("reference"), class_4568.class);
		}

		public void method_22582(JsonObject jsonObject, class_4568 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("name", arg.field_20764.toString());
		}

		public class_4568 method_22581(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			return new class_4568(identifier);
		}
	}
}
