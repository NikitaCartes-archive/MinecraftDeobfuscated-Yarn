package net.minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1065 extends class_3268 {
	private final class_1064 field_5291;

	public class_1065(class_1064 arg) {
		super("minecraft", "realms");
		this.field_5291 = arg;
	}

	@Nullable
	@Override
	protected InputStream method_14416(class_3264 arg, class_2960 arg2) {
		if (arg == class_3264.field_14188) {
			File file = this.field_5291.method_4630(arg2);
			if (file != null && file.exists()) {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException var5) {
				}
			}
		}

		return super.method_14416(arg, arg2);
	}

	@Nullable
	@Override
	protected InputStream method_14417(String string) {
		File file = this.field_5291.method_4631(string);
		if (file != null && file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException var4) {
			}
		}

		return super.method_14417(string);
	}

	@Override
	public Collection<class_2960> method_14408(class_3264 arg, String string, int i, Predicate<String> predicate) {
		Collection<class_2960> collection = super.method_14408(arg, string, i, predicate);
		collection.addAll((Collection)this.field_5291.method_4632(string, i, predicate).stream().map(class_2960::new).collect(Collectors.toList()));
		return collection;
	}
}
