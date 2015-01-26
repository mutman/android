package org.meruvian.midas.processor;

import org.meruvian.midas.service.MidasActions;

import android.content.Context;


public class ProcessorFactory {
	public static Processor createProcessor(Context context, String action) {

		if (action.equals(MidasActions.PERSONS_ACTION))
			return new PersonProcessor(context);
		return null;
	}
}
