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
		private static IntPtr _jmSetProductsGoogle = IntPtr.Zero;
		private static IntPtr _jmGetProductDetailsGoogle = IntPtr.Zero;
		private static IntPtr _jmPurchaseItem = IntPtr.Zero;
		private static IntPtr _instance = IntPtr.Zero;

		static Enchilada()
		{
			try
			{
				{
					string strName = "com/hbg/thefullenchilada/Enchilada";
					IntPtr localRef = AndroidJNI.FindClass(strName);
					if (localRef != IntPtr.Zero)
					{
						Debug.Log(string.Format("Found {0} class", strName));
						_jcEnchilada = AndroidJNI.NewGlobalRef(localRef);
						AndroidJNI.DeleteLocalRef(localRef);
					}
					else
					{
						Debug.LogError(string.Format("Failed to find {0} class", strName));
					}
				}
			}
			catch (System.Exception ex)
			{
				Debug.LogError(string.Format("Exception loading JNI - {0}", ex));
			}
		}

		private static void JNIFind()
		{
			try
			{

				{
					string strField = "currentActivity";
					_jfCurrentActivity = AndroidJNI.GetStaticFieldID(_jcEnchilada, strField, "Lcom/hbg/thefullenchilada/Enchilada;");
					if (_jfCurrentActivity != IntPtr.Zero)
					{
						Debug.Log(string.Format("Found {0} field", strField));
					}
					else
					{
						Debug.LogError(string.Format("Failed to find {0} field", strField));
						return;
					}
				}

				{
					string strMethod = "setProductsGoogle";
					_jmSetProductsGoogle = AndroidJNI.GetMethodID(_jcEnchilada, strMethod, "(Ljava/lang/String;)V");
					if (_jmSetProductsGoogle != IntPtr.Zero)
					{
						Debug.Log(string.Format("Found {0} Method", strMethod));
					}
					else
					{
						Debug.LogError(string.Format("Failed to find {0} Method", strMethod));
						return;
					}
				}

				{
					string strMethod = "getProductsDetailsGoogle";
					_jmGetProductDetailsGoogle = AndroidJNI.GetMethodID(_jcEnchilada, strMethod, "()Ljava/lang/String;");
					if (_jmGetProductDetailsGoogle != IntPtr.Zero)
					{
						Debug.Log(string.Format("Found {0} Method", strMethod));
					}
					else
					{
						Debug.LogError(string.Format("Failed to find {0} Method", strMethod));
						return;
					}
				}

				{
					string strMethod = "purchaseItem";
					_jmPurchaseItem = AndroidJNI.GetMethodID(_jcEnchilada, strMethod, "(Ljava/lang/String;)V");
					if (_jmPurchaseItem != IntPtr.Zero)
					{
						Debug.Log(string.Format("Found {0} Method", strMethod));
					}
					else
					{
						Debug.LogError(string.Format("Failed to find {0} Method", strMethod));
						return;
					}
				}

			}
			catch (System.Exception ex)
			{
				Debug.LogError(string.Format("Exception loading JNI - {0}", ex));
			}
		}


		public static void Init()
		{
			_instance = Enchilada.currentActivity;
		}

		public static void SetProductSkus(string productSkus)
		{
			JNIFind();

			if (_jfCurrentActivity == IntPtr.Zero)
			{
				Debug.LogError("_jcEnchilada is not initialized");
				return;
			}
			if (_jmSetProductsGoogle == IntPtr.Zero)
			{
				Debug.LogError("_jmSetProductsGoogle is not initialized");
				return;
			}

			IntPtr arg1 = AndroidJNI.NewStringUTF(productSkus);
			AndroidJNI.CallVoidMethod(_instance, _jmSetProductsGoogle, new jvalue[] { new jvalue() { l = arg1 } });
			AndroidJNI.DeleteLocalRef(arg1);
		}

		public static string GetProductDetails()
		{
			JNIFind();

			if (_jfCurrentActivity == IntPtr.Zero)
			{
				Debug.LogError("_jcEnchilada is not initialized");
				return string.Empty;
			}
			if (_jmGetProductDetailsGoogle == IntPtr.Zero)
			{
				Debug.LogError("_jmGetProductDetailsGoogle is not initialized");
				return string.Empty;
			}

			return AndroidJNI.CallStringMethod(_instance, _jmGetProductDetailsGoogle, new jvalue[0]);
		}

		public static void PurchaseItem(string productSku)
		{
			if (_instance == IntPtr.Zero)
			{
				Debug.LogError("_jcEnchilada is not initialized");
				return;
			}
			if (_jmPurchaseItem == IntPtr.Zero)
			{
				Debug.LogError("_jmGetProductDetailsGoogle is not initialized");
				return;
			}

			IntPtr arg1 = AndroidJNI.NewStringUTF(productSku);
			AndroidJNI.CallVoidMethod(_instance, _jmPurchaseItem, new jvalue[] { new jvalue() { l = arg1 } });
			AndroidJNI.DeleteLocalRef(arg1);
		}

		public static IntPtr currentActivity
		{
			get
			{
				JNIFind();

				if (_jcEnchilada == IntPtr.Zero)
				{
					Debug.LogError("_jcEnchilada is not initialized");
					return IntPtr.Zero;
				}
				if (_jfCurrentActivity == IntPtr.Zero)
				{
					Debug.LogError("_jfCurrentActivity is not initialized");
					return IntPtr.Zero;
				}

				IntPtr result = AndroidJNI.GetStaticObjectField(_jcEnchilada, _jfCurrentActivity);
				if (result == IntPtr.Zero)
				{
					Debug.LogError("Failed to get current activity");
					return IntPtr.Zero;
				}

				IntPtr globalRef = AndroidJNI.NewGlobalRef(result);
				AndroidJNI.DeleteLocalRef(result);
				return globalRef;
			}
		}

	}
}

#endif