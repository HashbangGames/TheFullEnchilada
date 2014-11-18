package com.hbg.thefullenchilada;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.unity3d.player.*;

public class Enchilada extends UnityPlayerNativeActivity{
	public static Enchilada currentActivity;
	
	private final String TAG = "Unity Enchilada";
	private Bundle mQuerySkus;
	private Bundle mSkuDetails;

	ArrayList<String> mGoogleProducts;
	IInAppBillingService mService;
	
	Map<String,Object>  mVariableStorage = new HashMap<String,Object>();

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// call UnityPlayerActivity.onCreate()
	    super.onCreate(savedInstanceState);
	    
	    currentActivity = this;
	    
		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);			
	};

	@Override
	public void onBackPressed() {
	    // instead of calling UnityPlayerActivity.onBackPressed() we just ignore the back button event
	    // super.onBackPressed();		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				if (resultCode == RESULT_OK) {
				   try {
					  ResponseObject rObject = new ResponseObject(); 
				      JSONObject jo = new JSONObject(purchaseData);
				      jo.put("RESPONSE_CODE", responseCode);
				      jo.put("INAPP_DATA_SIGNATURE", dataSignature);
				      
				      rObject.put("Success", true);
				      rObject.put("PurchaseData", jo);
				      
				      UnityPlayer.UnitySendMessage("EnchiladaManager", "OnPurchaseItem", rObject.toString());
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
	};
	
	@Override
	public void onDestroy(){
		if (mService != null) {
	        unbindService(mServiceConn);
	    } 			
	}
	
	public void setProductsGoogle(String productList){
		mGoogleProducts = new ArrayList<String>();
		List<String> prodList = Arrays.asList(productList.split("\\s*,\\s*"));
		for(String p : prodList){
			Log.i(TAG,p);
			mGoogleProducts.add(p);
		}
		mQuerySkus = new Bundle();
		mQuerySkus.putStringArrayList("ITEM_ID_LIST", mGoogleProducts);
	}
	
	public String getProductsDetailsGoogle(){
		try {
			mSkuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", mQuerySkus);
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
			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),sku, "inapp", developoerPayLoad);
			BillingResponseCodes billingResponse = BillingResponseCodes.getCode( buyIntentBundle.getInt("RESPONSE_CODE") );
			
			if( GoogleIAPResponseHandler.handlePurchaseItem( billingResponse )){
				PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
				
				Random r = new Random();
				int arbRequestCode = r.nextInt(5000 - 1000) + 1000;
				mVariableStorage.put("PurchaseRequestCode", arbRequestCode);
				startIntentSenderForResult(pendingIntent.getIntentSender(),
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
		
		try {
			Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
			BillingResponseCodes billingResponse = BillingResponseCodes.getCode( ownedItems.getInt("RESPONSE_CODE") );
			if(GoogleIAPResponseHandler.handlePurchaseItem( billingResponse )){
				
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
