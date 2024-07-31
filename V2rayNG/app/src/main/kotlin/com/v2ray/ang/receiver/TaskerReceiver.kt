package com.v2ray.ang.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.tencent.mmkv.MMKV
import com.v2ray.ang.AppConfig
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager

import com.v2ray.ang.util.Utils

class TaskerReceiver : BroadcastReceiver() {
    private val mainStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_MAIN, MMKV.MULTI_PROCESS_MODE) }

    override fun onReceive(context: Context, intent: Intent?) {
        if(intent?.action==AppConfig.BROADCAST_ACTION_SWITCH_VPN){


        try {
            //val bundle = intent?.getBundleExtra(AppConfig.TASKER_EXTRA_BUNDLE)
            val switch = intent?.getBooleanExtra(AppConfig.TASKER_EXTRA_BUNDLE_SWITCH, false)
            val guid = intent?.getStringExtra(AppConfig.TASKER_EXTRA_BUNDLE_GUID)
            Log.e("mytest", "onReceive: $guid")

            if (switch == null || guid == null || TextUtils.isEmpty(guid)) {
                return
            } else if (switch) {
                if (guid == AppConfig.TASKER_DEFAULT_GUID) {
                    Utils.startVServiceFromToggle(context)
                } else {
                    mainStorage?.encode(MmkvManager.KEY_SELECTED_SERVER, guid)
                    V2RayServiceManager.startV2Ray(context)
                }
            } else {
                Utils.stopVService(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        }else if(intent?.action==AppConfig.BROADCAST_ACTION_RESET_VPN_CONFIG){
            Log.e("retsetcofig","rest")
            val configstr = intent?.getStringExtra(AppConfig.TASKER_EXTRA_BUNDLE_CONFIGSTR)
            if(!TextUtils.isEmpty(configstr)){
                val count = AngConfigManager.importBatchConfig(configstr, "mysub", false)
            }
        }
    }
}
