package allogica.trackingTimeDesktopApp.controller;

import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

public class ActivityController implements IController {
	
	
	private Activity activity;
	private ActivityForm frame;
	private ActivityEnd activityEnd;
	private ActivityStart activityStart;
	
	public void executa(Object view) {
		frame = (CalculoForm) view;
		calculo = new Calculo();
		
	}
	
}
