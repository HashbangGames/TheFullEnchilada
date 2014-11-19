#if UNITY_ANDROID && !UNITY_EDITOR

using System;
using UnityEngine;

namespace com.hbg.thefullenchilada
{
    public class Enchilada
    {
        private const string LOG_TAG = "UnityPlayer";
        private static IntPtr _jcEnchilada = IntPtr.Zero;
        private static IntPtr _jfCurrentActivity = IntPtr.Zero;
        private static IntPtr _jmSetProducts = IntPtr.Zero;
        private static IntPtr _jmGetProductsDetails = IntPtr.Zero;
        private static IntPtr _jmPurchaseItem = IntPtr.Zero;
        private static IntPtr _jmPurchaseItemAndConsume = IntPtr.Zero;
        private static IntPtr _jmConsumePurchase = IntPtr.Zero;
        private static IntPtr _jmGetPurchases = IntPtr.Zero;
        private static IntPtr _jmGetPurchases1 = IntPtr.Zero;
        private static IntPtr _instance = IntPtr.Zero;

        static Enchilada()
        {
            try
            {
                {
                    string strName = "com/hbg/thefullenchilada/Enchilada";
                    IntPtr localRef = AndroidJNI.FindClass( strName );
                    if( localRef != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} class", strName ) );
                        _jcEnchilada = AndroidJNI.NewGlobalRef( localRef );
                        AndroidJNI.DeleteLocalRef( localRef );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} class", strName ) );
                    }
                }
            }
            catch( System.Exception ex )
            {
                Debug.LogError( string.Format( "Exception loading JNI - {0}", ex ) );
            }
        }

        private static void JNIFind()
        {
            try
            {

                {
                    string strField = "currentActivity";
                    _jfCurrentActivity = AndroidJNI.GetStaticFieldID( _jcEnchilada, strField, "Lcom/hbg/thefullenchilada/Enchilada;" );
                    if( _jfCurrentActivity != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} field", strField ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} field", strField ) );
                        return;
                    }
                }

                {
                    string strMethod = "setProducts";
                    _jmSetProducts = AndroidJNI.GetMethodID( _jcEnchilada, strMethod, "(Ljava/lang/String;)V" );
                    if( _jmSetProducts != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} Method", strMethod ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} Method", strMethod ) );
                        return;
                    }
                }

                {
                    string strMethod = "getProductsDetails";
                    _jmGetProductsDetails = AndroidJNI.GetMethodID( _jcEnchilada, strMethod, "()Ljava/lang/String;" );
                    if( _jmGetProductsDetails != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} Method", strMethod ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} Method", strMethod ) );
                        return;
                    }
                }

                {
                    string strMethod = "purchaseItem";
                    _jmPurchaseItem = AndroidJNI.GetMethodID( _jcEnchilada, strMethod, "(Ljava/lang/String;)V" );
                    if( _jmPurchaseItem != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} Method", strMethod ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} Method", strMethod ) );
                        return;
                    }
                }

                {
                    string strMethod = "purchaseItemAndConsume";
                    _jmPurchaseItemAndConsume = AndroidJNI.GetMethodID( _jcEnchilada, strMethod, "(Ljava/lang/String;)V" );
                    if( _jmPurchaseItemAndConsume != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} Method", strMethod ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} Method", strMethod ) );
                        return;
                    }
                }

                {
                    string strMethod = "consumePurchase";
                    _jmConsumePurchase = AndroidJNI.GetMethodID( _jcEnchilada, strMethod, "(Ljava/lang/String;)Ljava/lang/String;" );
                    if( _jmConsumePurchase != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} Method", strMethod ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} Method", strMethod ) );
                        return;
                    }
                }

                {
                    string strMethod = "getPurchases";
                    _jmGetPurchases = AndroidJNI.GetMethodID( _jcEnchilada, strMethod, "()Ljava/lang/String;" );
                    if( _jmGetPurchases != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} Method", strMethod ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} Method", strMethod ) );
                        return;
                    }
                }

                {
                    string strMethod = "getPurchases";
                    _jmGetPurchases1 = AndroidJNI.GetMethodID( _jcEnchilada, strMethod, "(Ljava/lang/String;)Ljava/lang/String;" );
                    if( _jmGetPurchases1 != IntPtr.Zero )
                    {
                        Debug.Log( string.Format( "Found {0} Method", strMethod ) );
                    }
                    else
                    {
                        Debug.LogError( string.Format( "Failed to find {0} Method", strMethod ) );
                        return;
                    }
                }

            }
            catch( System.Exception ex )
            {
                Debug.LogError( string.Format( "Exception loading JNI - {0}", ex ) );
            }
        }


        public static void Init()
        {
            _instance = Enchilada.currentActivity;
        }

        public static void SetProducts( string productSkus )
        {
            JNIFind();

            if( _jfCurrentActivity == IntPtr.Zero )
            {
                Debug.LogError( "_jcEnchilada is not initialized" );
                return;
            }
            if( _jmSetProducts == IntPtr.Zero )
            {
                Debug.LogError( "_jmSetProducts is not initialized" );
                return;
            }

            IntPtr arg1 = AndroidJNI.NewStringUTF( productSkus );
            AndroidJNI.CallVoidMethod( _instance, _jmSetProducts, new jvalue[] { new jvalue() { l = arg1 } } );
            AndroidJNI.DeleteLocalRef( arg1 );
        }

        public static string GetProductsDetails()
        {
            JNIFind();

            if( _jfCurrentActivity == IntPtr.Zero )
            {
                Debug.LogError( "_jcEnchilada is not initialized" );
                return string.Empty;
            }
            if( _jmGetProductsDetails == IntPtr.Zero )
            {
                Debug.LogError( "_jmGetProductsDetails is not initialized" );
                return string.Empty;
            }

            return AndroidJNI.CallStringMethod( _instance, _jmGetProductsDetails, new jvalue[0] );
        }

        public static void PurchaseItem( string productSku )
        {
            if( _instance == IntPtr.Zero )
            {
                Debug.LogError( "_jcEnchilada is not initialized" );
                return;
            }
            if( _jmPurchaseItem == IntPtr.Zero )
            {
                Debug.LogError( "_jmPurchaseItem is not initialized" );
                return;
            }

            IntPtr arg1 = AndroidJNI.NewStringUTF( productSku );
            AndroidJNI.CallVoidMethod( _instance, _jmPurchaseItem, new jvalue[] { new jvalue() { l = arg1 } } );
            AndroidJNI.DeleteLocalRef( arg1 );
        }

        public static void PurchaseItemAndConsume( string productSku )
        {
            if( _instance == IntPtr.Zero )
            {
                Debug.LogError( "_jcEnchilada is not initialized" );
                return;
            }
            if( _jmPurchaseItemAndConsume == IntPtr.Zero )
            {
                Debug.LogError( "_jmPurchaseItemAndConsume is not initialized" );
                return;
            }

            IntPtr arg1 = AndroidJNI.NewStringUTF( productSku );
            AndroidJNI.CallVoidMethod( _instance, _jmPurchaseItemAndConsume, new jvalue[] { new jvalue() { l = arg1 } } );
            AndroidJNI.DeleteLocalRef( arg1 );
        }

        public static string ConsumePurchase( string purchaseToken )
        {
            if( _instance == IntPtr.Zero )
            {
                Debug.LogError( "_jcEnchilada is not initialized" );
                return string.Empty;
            }
            if( _jmConsumePurchase == IntPtr.Zero )
            {
                Debug.LogError( "_jmConsumePurchase is not initialized" );
                return string.Empty;
            }

            IntPtr arg1 = AndroidJNI.NewStringUTF( purchaseToken );
            string response = AndroidJNI.CallStringMethod( _instance, _jmConsumePurchase, new jvalue[] { new jvalue() { l = arg1 } } );
            AndroidJNI.DeleteLocalRef( arg1 );
            return response;
        }

        public static string GetPurchases()
        {
            if( _instance == IntPtr.Zero )
            {
                Debug.LogError( "_jcEnchilada is not initialized" );
                return string.Empty;
            }
            if( _jmGetPurchases == IntPtr.Zero )
            {
                Debug.LogError( "_jmGetPurchases is not initialized" );
                return string.Empty;
            }

            return AndroidJNI.CallStringMethod( _instance, _jmGetPurchases, new jvalue[0] );
        }

        public static string GetPurchases( string continuationToken )
        {
            if( _instance == IntPtr.Zero )
            {
                Debug.LogError( "_jcEnchilada is not initialized" );
                return string.Empty;
            }
            if( _jmGetPurchases1 == IntPtr.Zero )
            {
                Debug.LogError( "_jmGetPurchases1 is not initialized" );
                return string.Empty;
            }
            IntPtr arg1 = AndroidJNI.NewStringUTF( continuationToken );
            string response = AndroidJNI.CallStringMethod( _instance, _jmGetPurchases1, new jvalue[] { new jvalue() { l = arg1 } } );
            AndroidJNI.DeleteLocalRef( arg1 );
            return response;
        }


        public static IntPtr currentActivity
        {
            get
            {
                JNIFind();

                if( _jcEnchilada == IntPtr.Zero )
                {
                    Debug.LogError( "_jcEnchilada is not initialized" );
                    return IntPtr.Zero;
                }
                if( _jfCurrentActivity == IntPtr.Zero )
                {
                    Debug.LogError( "_jfCurrentActivity is not initialized" );
                    return IntPtr.Zero;
                }

                IntPtr result = AndroidJNI.GetStaticObjectField( _jcEnchilada, _jfCurrentActivity );
                if( result == IntPtr.Zero )
                {
                    Debug.LogError( "Failed to get current activity" );
                    return IntPtr.Zero;
                }

                IntPtr globalRef = AndroidJNI.NewGlobalRef( result );
                AndroidJNI.DeleteLocalRef( result );
                return globalRef;
            }
        }

    }
}

#endif