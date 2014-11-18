package com.hbg.thefullenchilada;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

public class GoogleIAPResponseHandler {
	static final String TAG = "Unity Enchilada";
	
	public static String handleProductsDetails(BillingResponseCodes billingResponse, Bundle skuDetails ){
		ResponseObject response = new ResponseObject();
		switch(billingResponse){
			case BILLING_RESPONSE_RESULT_OK:
				Log.i(TAG,billingResponse.toString());
	
				ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
				Log.i(TAG,"" + responseList.size());
				
			    JSONArray productList = new JSONArray();
				for (String thisResponse : responseList) {
					try {
						JSONObject object = new JSONObject(thisResponse);
						productList.put(object);
						response.put("Success", true);
						response.put("productList", productList);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case BILLING_RESPONSE_RESULT_USER_CANCELED:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: User Cancelled");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: Service Unavailable");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				break;
			case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: Billing Unavailable");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				break;
			case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: Item Unavailable");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				break;
			case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: Developer Error");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				break;
			case BILLING_RESPONSE_RESULT_ERROR:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: Result Error");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				break;
			case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: Item Already Owned");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				break;
			case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
				Log.i(TAG,billingResponse.toString());
				try {
					response.put("Error", "Billing Response: Item Not Owned");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				break;
			default:
				Log.i(TAG,"Default case for billingResponse");
				break;
		}
		
		return response.toString();
		
	}
	
	public static Boolean handlePurchaseItem(BillingResponseCodes billingResponse){
		switch(billingResponse){
		case BILLING_RESPONSE_RESULT_OK:
			Log.i(TAG,billingResponse.toString());
			return true;
		case BILLING_RESPONSE_RESULT_USER_CANCELED:
			Log.i(TAG,billingResponse.toString());
			return false;		
		case BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE:
			Log.i(TAG,billingResponse.toString());
			return false;		
		case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
			Log.i(TAG,billingResponse.toString());
			return false;		
		case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
			Log.i(TAG,billingResponse.toString());
			return false;		
		case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
			Log.i(TAG,billingResponse.toString());
			return false;		
		case BILLING_RESPONSE_RESULT_ERROR:
			Log.i(TAG,billingResponse.toString());
			return false;		
		case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
			Log.i(TAG,billingResponse.toString());
			return false;		
		case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
			Log.i(TAG,billingResponse.toString());
			return false;		
		default:
			Log.i(TAG,"Default case for billingResponse");
			return false;		
		}
	}
}
