package net.minecraft;

import java.io.File;
import java.io.FileNotFoundException;

public class class_3266 extends FileNotFoundException {
	public class_3266(File file, String string) {
		super(String.format("'%s' in ResourcePack '%s'", string, file));
	}
}
