using UnityEngine;
using System;
using System.Collections;

namespace com.hbg.thefullenchilada
{
    public class EnchiladaEvent
    {

        public enum EventTypes { PURCHASE_ITEM, CONSUME_FAILED }
        public delegate void EnchiladaEventHandler( EventTypes EventType, bool success, string response );
        private static event EnchiladaEventHandler EnchiladaEvents;

        //Call this event (trigger)
        public static void SendEvent( EventTypes EventType, bool success, string response )
        {
            if( null != EnchiladaEvents )
            {
                EnchiladaEvents( EventType, success, response );
            }
        }

        //Subscribte to the event 
        public static void addEventListener( EnchiladaEventHandler handler )
        {
            EnchiladaEvent.EnchiladaEvents += handler;
        }

        //UnSubscribte to the event
        public static void removeEventListener( EnchiladaEventHandler handler )
        {
            EnchiladaEvent.EnchiladaEvents -= handler;
        }

    }
}
