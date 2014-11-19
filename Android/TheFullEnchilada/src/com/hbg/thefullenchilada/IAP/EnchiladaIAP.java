package com.hbg.thefullenchilada.IAP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;
import com.hbg.thefullenchilada.Enchilada;
import com.unity3d.player.UnityPlayer;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class EnchiladaIAP {
	private final String TAG = "Unity Enchilada";
	private Map<String, Object> mVariableStorage;

	private Bundle mQuerySkus;
	private Bundle mSkuDetails;

	ArrayList<String> mGoogleProducts;
	IInAppBillingService mService;
	
	ServiceConnection mServiceConn = new ServiceConnection() {
	   @Override
	   public void onServiceDisconnected(ComponentName name) {
	       mService = null;
	   }

	   @Override
	   public void onServiceConnected(ComponentName name,IBinder service) {
	       mService = IInAppBillingService.Stub.asInterface(service);
	   }
	};		
	
	public ServiceConnection getServiceConn(){
		return mServiceConn; 
	}
	
	public void setQuerySkus(Bundle mQuerySkus) {
		this.mQuerySkus = mQuerySkus;
	}

	public void setSkuDetails(Bundle mSkuDetails) {
		this.mSkuDetails = mSkuDetails;
	}	
	
	public void bindServiceIntent(){
		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		Enchilada.currentActivity.bindService(serviceIntent, getServiceConn(), Context.BIND_AUTO_CREATE);			
	}
	
	public void unbindServiceIntent(){
		if (mService != null) {
			Enchilada.currentActivity.unbindService(mServiceConn);
	    } 			
	}
	
	public IInAppBillingService GetService(){
		return mService;
	}
	
	public void setProducts(String productList){
		mGoogleProducts = new ArrayList<String>();
		List<String> prodList = Arrays.asList(productList.split("\\s*,\\s*"));
		for(String p : prodList){
			Log.i(TAG,p);
			mGoogleProducts.add(p);
		}
		mQuerySkus = new Bundle();
		mQuerySkus.putStringArrayList("ITEM_ID_LIST", mGoogleProducts);		
	}
	
	public String getProductsDetails(){
		ResponseObject response = new ResponseObject();
		try {
			mSkuDetails = mService.getSkuDetails(3, Enchilada.currentActivity.getPackageName(), "inapp", mQuerySkus);
			IAPResponseCodes billingResponse = IAPResponseCodes.getCode( mSkuDetails.getInt("RESPONSE_CODE") );
			Log.i(TAG,billingResponse.toString());
			if(handleResponseCode(billingResponse)){
				
				ArrayList<String> responseList = mSkuDetails.getStringArrayList("DETAILS_LIST");
				Log.i(TAG,"" + responseList.size());
				
			    JSONArray productList = new JSONArray();
				for (String thisResponse : responseList) {
					try {
						JSONObject object = new JSONObject(thisResponse);
						productList.put(object);
						response.put("Success", true);
						response.put("productList", productList);
					} catch (JSONException e) {
						try{
							response.put("Success", false);
							response.put("Error", e.getMessage());
						}catch(JSONException je){
							//this should never happen.
							e.printStackTrace();
						}
					}
				}				
			}
			
			return response.toString();
			
		} catch (RemoteException e) {
			try{
				response.put("Success", false);
				response.put("Error",e.getMessage());
				return response.toString();
			}catch(JSONException je){
				//this should never happen.
				e.printStackTrace();
			}
		}
		return response.toString();
	}
	
	public void purchaseItem(String sku){
		String developoerPayLoad = UUID.randomUUID().toString();
		try {
			mVariableStorage.put("DeveloperPayload", developoerPayLoad);
			Bundle buyIntentBundle = mService.getBuyIntent(3, Enchilada.currentActivity.getPackageName(),sku, "inapp", developoerPayLoad);
			IAPResponseCodes billingResponse = IAPResponseCodes.getCode( buyIntentBundle.getInt("RESPONSE_CODE") );
			
			if( handleResponseCode( billingResponse )){
				PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
				
				Random r = new Random();
				int arbRequestCode = r.nextInt(5000 - 1000) + 1000;
				mVariableStorage.put("PurchaseRequestCode", arbRequestCode);
				Enchilada.currentActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
						arbRequestCode, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
						   Integer.valueOf(0));			
			}
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (SendIntentException e) {
			e.printStackTrace();
		}				
	}
	
	public void purchaseItemAndConsume(String sku){
		mVariableStorage.put("AutoConsumeProduct", sku);
		purchaseItem(sku);
	}
	
	public String getPurchases(){
		return getPurchases(null);
	}
	
	public String getPurchases(String token){
		ResponseObject response = new ResponseObject();
		try {
			Bundle ownedItems;
			if(null != token && token.length() > 0){
				ownedItems = mService.getPurchases(3, Enchilada.currentActivity.getPackageName(), "inapp", token);
			}else{
				ownedItems = mService.getPurchases(3, Enchilada.currentActivity.getPackageName(), "inapp", null);
			}
			IAPResponseCodes billingResponse = IAPResponseCodes.getCode( ownedItems.getInt("RESPONSE_CODE") );
			if(handleResponseCode( billingResponse )){
				response.put("Success", true);
				response.put("Error", "");
				ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
				ArrayList<String>  purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				ArrayList<String>  signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
				String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
				
				response.put("INAPP_PURCHASE_DATA_LIST", purchaseDataList);
				response.put("INAPP_PURCHASE_ITEM_LIST", ownedSkus);
				response.put("INAPP_DATA_SIGNATURE", signatureList);
				response.put("INAPP_CONTINUATION_TOKEN", continuationToken);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			try{
				response.put("Success", false);
				response.put("Error", e.getMessage());
			}catch(JSONException je){}
		} catch (JSONException e) {
			e.printStackTrace();
			try{
				response.put("Success", false);
				response.put("Error", e.getMessage());
			}catch(JSONException je){
				// this should never happen
				e.printStackTrace();
			}
		}
		return response.toString();
	}
	
	public String consumePurchase(String token){
		ResponseObject response = new ResponseObject();
		try {
			int consumedItem = mService.consumePurchase(3, Enchilada.currentActivity.getPackageName(), token);
			IAPResponseCodes billingResponse = IAPResponseCodes.getCode( consumedItem );
			if(handleResponseCode(billingResponse)){
				response.put("Success", true);
				response.put("Error", "");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			try{
				response.put("Success", false);
				response.put("Error", e.getMessage());
			}catch(JSONException je){}
		} catch (JSONException e) {
			e.printStackTrace();
			try{
				response.put("Success", false);
				response.put("Error", e.getMessage());
			}catch(JSONException je){
				// this should never happen
				e.printStackTrace();
			}
		}
		return response.toString();
	}
	
	public void handleOnActivityResult(int requestCode, int resultCode, Intent data){
		int rCodeComparer = (Integer) mVariableStorage.get("PurchaseRequestCode");
		if (requestCode == rCodeComparer) {
			
			mVariableStorage.remove("PurchaseRequestCode");
			String developerPayload = data.getStringExtra("developerPayload");
			String storedDeveloperPayload = (String) mVariableStorage.get("DeveloperPayload");
			Log.i(TAG,developerPayload);
			Log.i(TAG,storedDeveloperPayload);
			if( developerPayload == storedDeveloperPayload ){
				int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
				String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
				String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
				if (resultCode == android.app.Activity.RESULT_OK) {
				   try {
					  ResponseObject rObject = new ResponseObject(); 
				      JSONObject jo = new JSONObject(purchaseData);
				      jo.put("RESPONSE_CODE", responseCode);
				      jo.put("INAPP_DATA_SIGNATURE", dataSignature);
				      
				      rObject.put("Success", true);
				      rObject.put("PurchaseData", jo);
				      
				      if(mVariableStorage.containsKey("AutoConsumeProduct")){
				    	  mVariableStorage.remove("AutoConsumeProduct");
				    	  String purchaseToken = jo.get("purchaseToken").toString();
				    	  JSONObject consumeResult = new JSONObject(consumePurchase(purchaseToken));
				    	  if( consumeResult.getBoolean("Success") ){
				    		  UnityPlayer.UnitySendMessage("EnchiladaManager", "OnPurchaseItem", rObject.toString());
				    	  }else{
				    		  UnityPlayer.UnitySendMessage("EnchiladaManager", "OnConsumeFailed", rObject.toString());
				    	  }
				      }else{
				    	  UnityPlayer.UnitySendMessage("EnchiladaManager", "OnPurchaseItem", rObject.toString());
				      }
				      
				    }
				    catch (JSONException e) {
				       e.printStackTrace();
				       ResponseObject rObject = new ResponseObject();
				       try{
				    	   rObject.put("Success", false);
				    	   rObject.put("Error", e.getMessage());
				    	   UnityPlayer.UnitySendMessage("EnchiladaManager", "OnPurchaseItem", rObject.toString());
				       }catch(JSONException je){
				    	   je.printStackTrace();
				       }
				    }
				}
				
			}else{
				Log.i(TAG,"Developer Payload mismatch.");
			}
		}else{
			Log.i(TAG,"Request code mismatch");
		}		
	}	
	
	
	private Boolean handleResponseCode(IAPResponseCodes billingResponse){
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
