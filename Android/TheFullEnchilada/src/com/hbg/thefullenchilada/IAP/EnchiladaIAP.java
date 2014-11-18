package com.hbg.thefullenchilada.IAP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.json.JSONException;

import com.android.vending.billing.IInAppBillingService;
import com.hbg.thefullenchilada.Enchilada;

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
	
	public void SetVariableStorage(String key, Object value){
		mVariableStorage.put(key, value);
	}

	public Object GetVariableStorage(String key){
		return mVariableStorage.get(key);
	}
	
	public Map<String,Object> GetVariableStorageMap(){
		return mVariableStorage;
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
	
	public String getProductDetails(){
		try {
			mSkuDetails = mService.getSkuDetails(3, Enchilada.currentActivity.getPackageName(), "inapp", mQuerySkus);
			BillingResponseCodes billingResponse = BillingResponseCodes.getCode( mSkuDetails.getInt("RESPONSE_CODE") );
			Log.i(TAG,billingResponse.toString());
			return GoogleIAPResponseHandler.handleProductsDetails(billingResponse, mSkuDetails);
		} catch (RemoteException e) {
			e.printStackTrace();
			return "";
		}			
	}
	
	
	public void purchaseItem(String sku){
		String developoerPayLoad = UUID.randomUUID().toString();
		try {
			mVariableStorage.put("DeveloperPayload", developoerPayLoad);
			Bundle buyIntentBundle = mService.getBuyIntent(3, Enchilada.currentActivity.getPackageName(),sku, "inapp", developoerPayLoad);
			BillingResponseCodes billingResponse = BillingResponseCodes.getCode( buyIntentBundle.getInt("RESPONSE_CODE") );
			
			if( GoogleIAPResponseHandler.handlePurchaseItem( billingResponse )){
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
			BillingResponseCodes billingResponse = BillingResponseCodes.getCode( ownedItems.getInt("RESPONSE_CODE") );
			if(GoogleIAPResponseHandler.handlePurchaseItem( billingResponse )){
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
			}catch(JSONException je){}
		}
		return response.toString();
	}
	
	
	
}
