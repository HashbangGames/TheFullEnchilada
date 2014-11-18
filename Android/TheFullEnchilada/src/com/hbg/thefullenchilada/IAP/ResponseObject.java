package com.hbg.thefullenchilada.IAP;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseObject extends JSONObject {
	public ResponseObject(){
		try {
			this.put("Error", "");
			this.put("Success", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
