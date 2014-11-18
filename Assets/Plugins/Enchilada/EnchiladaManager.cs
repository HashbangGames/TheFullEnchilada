using UnityEngine;

using System;
using System.Collections.Generic;
using System.Collections;
using System.Threading;

#if !UNITY_EDITOR && UNITY_ANDROID
using com.unity3d.player;
using org.json;
#endif

namespace com.hbg.thefullenchilada
{
	public class EnchiladaManager: MonoBehaviour
	{

		#region Static JNI Methods
#if !UNITY_EDITOR && UNITY_ANDROID
		public static Enchilada _enchilada;
#endif

		public static void Init()
		{
#if !UNITY_EDITOR && UNITY_ANDROID
			Enchilada.Init();
#endif
		}

		public static void SetGoogleProducts(string productSkus, Action<bool,string> callback)
		{
			try{
#if !UNITY_EDITOR && UNITY_ANDROID
				Enchilada.SetProductSkus(productSkus);
#endif
				callback(true,string.Empty);
			}catch(Exception e){
				callback(false,e.Message);
			}
		}

		public static void GetGoogleProductDetails(Action<bool,string> callback)
		{
			try
			{
				string response = string.Empty;
#if !UNITY_EDITOR && UNITY_ANDROID
				response = Enchilada.GetProductDetails();
#endif
				callback(true, response);
			}
			catch (Exception e)
			{
				callback(false, e.Message);
			}
		}


		public static void PurchaseItem(string productSku, Action<bool, string> callback)
		{
			try
			{
				
#if !UNITY_EDITOR && UNITY_ANDROID
				EnchiladaEvent.addEventListener((type,success, response) => {
					if (type == EnchiladaEvent.EventTypes.PURCHASE_ITEM)
					{
						callback(success,response);
					}
				});
				Enchilada.PurchaseItem(productSku);
#endif
			}
			catch (Exception e)
			{
				callback(false, e.Message);
			}
		}

		#endregion


		#region JNI Callbacks
		public void OnPurchaseItem(string response)
		{
#if !UNITY_EDITOR && UNITY_ANDROID
			JSONObject responseObject = new JSONObject(response);
			bool success;
			Boolean.TryParse(responseObject.getString("Success"),out success);
			EnchiladaEvent.SendEvent(EnchiladaEvent.EventTypes.PURCHASE_ITEM, success, response);
#endif
		}
		#endregion
	}

}
