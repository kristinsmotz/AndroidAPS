package info.nightscout.androidaps.interfaces

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import info.nightscout.interfaces.data.maintenance.PrefsFile

interface ImportExportPrefs {

    fun importSharedPreferences(activity: FragmentActivity, importFile: PrefsFile)
    fun importSharedPreferences(activity: FragmentActivity)
    fun importSharedPreferences(fragment: Fragment)
    fun prefsFileExists(): Boolean
    fun verifyStoragePermissions(fragment: Fragment, onGranted: Runnable)
    fun exportSharedPreferences(f: Fragment)
    fun exportUserEntriesCsv(activity: FragmentActivity)
}