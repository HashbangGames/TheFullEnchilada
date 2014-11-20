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
    public class EnchiladaManager : MonoBehaviour
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

        public static void SetProducts( string productSkus, Action<bool, string> callback )
        {
            try
            {
#if !UNITY_EDITOR && UNITY_ANDROID
                Enchilada.SetProducts( productSkus );
#endif
                callback( true, string.Empty );
            }
            catch( Exception e )
            {
                callback( false, e.Message );
            }
        }

        public static void GetProductsDetails( Action<bool, string> callback )
        {
            try
            {
                string response = string.Empty;
#if !UNITY_EDITOR && UNITY_ANDROID
                response = Enchilada.GetProductsDetails();
#endif
                callback( true, response );
            }
            catch( Exception e )
            {
                callback( false, e.Message );
            }
        }

        public static void PurchaseItem( string productSku, Action<bool, string> callback )
        {
            try
            {

#if !UNITY_EDITOR && UNITY_ANDROID
                EnchiladaEvent.addEventListener( ( type, success, response ) =>
                {
                    if( type == EnchiladaEvent.EventTypes.PURCHASE_ITEM )
                    {
                        callback( success, response );
                    }
                } );
                Enchilada.PurchaseItem( productSku );
#endif
            }
            catch( Exception e )
            {
                callback( false, e.Message );
            }
        }

        public static void PurchaseItemAndConsume( string productSku, Action<bool, string> callback )
        {
            try
            {

#if !UNITY_EDITOR && UNITY_ANDROID
                EnchiladaEvent.addEventListener( ( type, success, response ) =>
                {
                    if( type == EnchiladaEvent.EventTypes.PURCHASE_ITEM )
                    {
                        callback( success, response );
                    }
                } );
                Enchilada.PurchaseItemAndConsume( productSku );
#endif
            }
            catch( Exception e )
            {
                callback( false, e.Message );
            }
        }

        public static void ConsumePurchase( string purchaseToken, Action<bool, string> callback )
        {
            string response = string.Empty;
#if !UNITY_EDITOR && UNITY_ANDROID
            response = Enchilada.ConsumePurchase( purchaseToken );
            using( JSONObject jResponseObject = new JSONObject( response ) )
            {
                bool success;
                Boolean.TryParse( jResponseObject.getString( "Success" ), out success );
                callback( success, response );
            }
#endif
        }

        public static void GetPurchases( Action<bool, string> callback )
        {
            string response = string.Empty;
#if !UNITY_EDITOR && UNITY_ANDROID
            response = Enchilada.GetPurchases();
            using( JSONObject jResponseObject = new JSONObject( response ) )
            {
                bool success;
                Boolean.TryParse( jResponseObject.getString( "Success" ), out success );
                callback( success, response );
            }
#endif
        }

        #endregion

        #region JNI Callbacks
        public void OnPurchaseItem( string response )
        {
#if !UNITY_EDITOR && UNITY_ANDROID
            using( JSONObject responseObject = new JSONObject( response ) )
            {
                bool success;
                Boolean.TryParse( responseObject.getString( "Success" ), out success );
                EnchiladaEvent.SendEvent( EnchiladaEvent.EventTypes.PURCHASE_ITEM, success, response );
            }
#endif
        }

        public void OnConsumeFailed( string response )
        {
#if !UNITY_EDITOR && UNITY_ANDROID
            using( JSONObject responseObject = new JSONObject( response ) )
            {
                bool success;
                Boolean.TryParse( responseObject.getString( "Success" ), out success );
                EnchiladaEvent.SendEvent( EnchiladaEvent.EventTypes.CONSUME_FAILED, success, response );
            }
#endif
        }

        #endregion
    }

}
