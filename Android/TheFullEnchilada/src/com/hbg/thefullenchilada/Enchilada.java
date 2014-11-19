package com.hbg.thefullenchilada;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hbg.thefullenchilada.IAP.*;
import com.unity3d.player.*;

public class Enchilada extends UnityPlayerNativeActivity{
	public static Enchilada currentActivity;
	
	private final String TAG = "Unity Enchilada";
	private EnchiladaIAP mEnchiladaIAP;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// call UnityPlayerActivity.onCreate()
	    super.onCreate(savedInstanceState);
	    Log.i(TAG,"Enchilada OnCreate ..");
	    
	    currentActivity = this;
	    mEnchiladaIAP = new EnchiladaIAP();
	    mEnchiladaIAP.bindServiceIntent();
	    
	};

	@Override
	public void onBackPressed() {
	    // instead of calling UnityPlayerActivity.onBackPressed() we just ignore the back button event
	    // super.onBackPressed();		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mEnchiladaIAP.handleOnActivityResult(requestCode,resultCode,data);
	};
	
	@Override
	public void onDestroy(){
		mEnchiladaIAP.unbindServiceIntent();		
	}
	
	public void setProducts(String productList){
		mEnchiladaIAP.setProducts(productList);
	}
	
	public String getProductsDetails(){
		return mEnchiladaIAP.getProductsDetails();
	}	

	public void purchaseItem(String sku){
		mEnchiladaIAP.purchaseItem(sku);
	}
	
	public void purchaseItemAndConsume(String sku){
		mEnchiladaIAP.purchaseItemAndConsume(sku);
	}
	
	public String getPurchases(){
		return mEnchiladaIAP.getPurchases();
	}

	public String getPurchases(String token){
		return mEnchiladaIAP.getPurchases(token);
	}

	public String consumePurchase(String token){
		return mEnchiladaIAP.consumePurchase(token);
	}
}
