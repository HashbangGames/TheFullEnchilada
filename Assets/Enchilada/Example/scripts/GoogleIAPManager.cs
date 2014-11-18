using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using com.hbg.thefullenchilada;

public class GoogleIAPManager : MonoBehaviour {
	public string GoogleProductSkus;

	void Start()
	{
#if !UNITY_EDITOR && UNITY_ANDROID
		Enchilada.Init();
#endif
	}

	void OnGUI()
	{
		GUILayout.BeginArea(new Rect(15, 15, 500, 800));
		
		GUILayout.Space(10f);
		if (GUILayout.Button("Set Product Skus", GUILayout.Width(250), GUILayout.Height(50)))
		{
			EnchiladaManager.SetGoogleProducts(GoogleProductSkus, (success, response) =>
			{
				if (success)
				{
					Debug.Log("Set Skus was successful");
				}
				else
				{
					Debug.Log("Set Skus failed: " + response);
				}
			});
		}

		GUILayout.Space(10f);
		if (GUILayout.Button("Get Product Details", GUILayout.Width(250), GUILayout.Height(50)))
		{
			EnchiladaManager.GetGoogleProductDetails((success, response) =>
			{
				if (success)
				{
					Debug.Log(response);
				}
				else
				{
					Debug.Log("Failed to get Product details: " + response);
				}
			
			});
		}

		GUILayout.Space(10f);
		if (GUILayout.Button("Purchase Product", GUILayout.Width(250), GUILayout.Height(50)))
		{
			EnchiladaManager.PurchaseItem(GoogleProductSkus, (success, response) => {
				Debug.Log(string.Format("Success:{0} Response: {1}", success, response));
			});
		}
		GUILayout.EndArea();
	}

}
