using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using com.hbg.thefullenchilada;

#if !UNITY_EDITOR && UNITY_ANDROID
using org.json;
#endif

public class GoogleIAPManager : MonoBehaviour
{
    public string ConsumePurchaseToken = "";
    public string GoogleProductSkus;
    Dictionary<string, object> _IAPVariables = new Dictionary<string, object>();


    void Start()
    {
#if !UNITY_EDITOR && UNITY_ANDROID
        Enchilada.Init();
#endif
    }

    void OnGUI()
    {
        GUILayout.BeginArea( new Rect( 15, 15, 500, 800 ) );

        GUILayout.Space( 10f );
        if( GUILayout.Button( "Set Product Skus", GUILayout.Width( 250 ), GUILayout.Height( 50 ) ) )
        {
            EnchiladaManager.SetProducts( GoogleProductSkus, ( success, response ) =>
            {
                if( success )
                {
                    Debug.Log( "Set Skus was successful" );
                }
                else
                {
                    Debug.Log( "Set Skus failed: " + response );
                }
            } );
        }

        GUILayout.Space( 10f );
        if( GUILayout.Button( "Get Product Details", GUILayout.Width( 250 ), GUILayout.Height( 50 ) ) )
        {
            EnchiladaManager.GetProductsDetails( ( success, response ) =>
            {
                if( success )
                {
                    Debug.Log( response );
                }
                else
                {
                    Debug.Log( "Failed to get Product details: " + response );
                }

            } );
        }

        GUILayout.Space( 10f );
        if( GUILayout.Button( "Purchase And Consume Item", GUILayout.Width( 250 ), GUILayout.Height( 50 ) ) )
        {
            EnchiladaManager.PurchaseItemAndConsume( GoogleProductSkus, ( success, response ) =>
            {
#if !UNITY_EDITOR && UNITY_ANDROID
                using( JSONObject jObjectResponse = new JSONObject( response ) )
                {
                    _IAPVariables.Add( "purchaseToken", jObjectResponse.getString( "purchaseToken" ) );
                }
#endif
                Debug.Log( string.Format( "Success:{0} Response: {1}", success, response ) );
            } );
        }

        GUILayout.Space( 10f );
        if( GUILayout.Button( "Purchase Item", GUILayout.Width( 250 ), GUILayout.Height( 50 ) ) )
        {
            EnchiladaManager.PurchaseItem( GoogleProductSkus, ( success, response ) =>
            {
#if !UNITY_EDITOR && UNITY_ANDROID
                using( JSONObject jObjectResponse = new JSONObject( response ) )
                {
                    _IAPVariables.Add( "purchaseToken", jObjectResponse.getString( "purchaseToken" ) );
                }
#endif
                Debug.Log( string.Format( "Success:{0} Response: {1}", success, response ) );
            } );
        }

        GUILayout.Space( 10f );
        if( GUILayout.Button( "Consume Purchase Product", GUILayout.Width( 250 ), GUILayout.Height( 50 ) ) )
        {
            if( _IAPVariables.ContainsKey( "purchaseToken" ) )
            {
                object purchaseToken;
                _IAPVariables.TryGetValue( "purchaseToken", out purchaseToken );
                EnchiladaManager.ConsumePurchase( purchaseToken.ToString(), ( success, response ) =>
                {
                    Debug.Log( string.Format( "Success:{0} Response: {1}", success, response ) );
                } );
            }
            else
            {
                Debug.Log( "You must make a purchase before you can consume it." );
            }
        }

        GUILayout.Space( 10f );
        if( GUILayout.Button( "Consume Purchase Manual", GUILayout.Width( 250 ), GUILayout.Height( 50 ) ) )
        {
            EnchiladaManager.ConsumePurchase( ConsumePurchaseToken, ( success, response ) =>
            {
                Debug.Log( string.Format( "Success:{0} Response: {1}", success, response ) );
            } );
        }


        GUILayout.Space( 10f );
        if( GUILayout.Button( "Get Purchases", GUILayout.Width( 250 ), GUILayout.Height( 50 ) ) )
        {
            EnchiladaManager.GetPurchases( ( success, response ) =>
            {
                Debug.Log( string.Format( "Success:{0} Response: {1}", success, response ) );
            } );
        }


        GUILayout.EndArea();
    }

}
