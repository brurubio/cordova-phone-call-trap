package io.gvox.phonecalltrap;

import android.content.BroadcastReceiver;
import android.content.Context;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.content.Intent;
import org.apache.cordova.PluginResult;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONArray;

public class PhoneCallTrap extends CordovaPlugin {

    CallStateListener listener;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        prepareListener();

        listener.setCallbackContext(callbackContext);

        return true;
    }

    private void prepareListener() {
        if (listener == null) {
            listener = new CallStateListener();
            TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}

public class CallStateListener extends BroadcastReceiver {

    private static String LOG_TAG = "MyReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        mtelephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                JSONObject obj = new JSONObject();
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        try {
                            obj.put("msg", "IDLE");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        try {
                            obj.put("msg", "OFFHOOK");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    break;

                    case TelephonyManager.CALL_STATE_RINGING:
                        try {
                            obj.put("msg", "RINGING");
                            obj.put("number", incomingNumber);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    break;
                }

                PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                result.setKeepCallback(true);

                callbackContext.sendPluginResult(result);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}