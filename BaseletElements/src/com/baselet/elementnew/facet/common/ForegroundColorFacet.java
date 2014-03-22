package com.baselet.elementnew.facet.common;

import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.baselet.elementnew.PropertiesConfig;
import com.baselet.elementnew.facet.KeyValueFacet;

public class ForegroundColorFacet extends KeyValueFacet {
	
	public static ForegroundColorFacet INSTANCE = new ForegroundColorFacet();
	private ForegroundColorFacet() {}

	public static final String KEY = "fg";
	
	@Override
	public KeyValue getKeyValue() {
		return new KeyValue(KEY, false, "red", "foreground " + ColorOwn.EXAMPLE_TEXT);
	}

	@Override
	public void handleValue(String value, DrawHandler drawer, PropertiesConfig propConfig) {
		drawer.setForegroundColor(value);
	}

	public Priority getPriority() {
		return Priority.HIGHER;
	}

}
