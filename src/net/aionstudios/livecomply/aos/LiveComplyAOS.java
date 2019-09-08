package net.aionstudios.livecomply.aos;

import net.aionstudios.api.API;
import net.aionstudios.api.context.ContextManager;
import net.aionstudios.api.cron.CronDateTime;
import net.aionstudios.api.cron.CronManager;
import net.aionstudios.api.error.ErrorManager;
import net.aionstudios.livecomply.aos.ajax.AjaxClockInAction;
import net.aionstudios.livecomply.aos.ajax.AjaxClockOutAction;
import net.aionstudios.livecomply.aos.ajax.AjaxClockTenAction;
import net.aionstudios.livecomply.aos.ajax.AjaxClockThirtyAction;
import net.aionstudios.livecomply.aos.ajax.AjaxContext;
import net.aionstudios.livecomply.aos.ajax.AjaxPullEIDAction;
import net.aionstudios.livecomply.aos.ajax.AjaxUpdateBreaksAction;
import net.aionstudios.livecomply.aos.errors.InvalidClockEventError;

public class LiveComplyAOS {
	
	public static InvalidClockEventError invalidClockEventError;
	
	public static void main(String[] args) {
		API.initAPI("Live Comply AOS", 26752, true, "Live Comply");
		//API.getServer().getSecureServer().startServer(certificate, storePassword, keyPassword, certificateAlias, port);
		
		/*Ajax*/
		AjaxContext ac = new AjaxContext();
		AjaxPullEIDAction peidac = new AjaxPullEIDAction();
		AjaxClockInAction ciac = new AjaxClockInAction();
		AjaxUpdateBreaksAction ubac = new AjaxUpdateBreaksAction();
		AjaxClockOutAction coac = new AjaxClockOutAction();
		AjaxClockTenAction cteac = new AjaxClockTenAction();
		AjaxClockThirtyAction cthac = new AjaxClockThirtyAction();
		ac.registerAction(peidac);
		ac.registerAction(ciac);
		ac.registerAction(ubac);
		ac.registerAction(coac);
		ac.registerAction(cteac);
		ac.registerAction(cthac);
		
		ContextManager.registerContext(ac);
		
		/*Errors*/
		invalidClockEventError = new InvalidClockEventError();
		ErrorManager.registerError(invalidClockEventError);
	}

}
