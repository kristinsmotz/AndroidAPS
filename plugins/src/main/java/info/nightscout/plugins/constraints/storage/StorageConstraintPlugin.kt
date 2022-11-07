package info.nightscout.plugins.constraints.storage

import android.os.Environment
import android.os.StatFs
import dagger.android.HasAndroidInjector
import info.nightscout.interfaces.Constants
import info.nightscout.androidaps.annotations.OpenForTesting
import info.nightscout.androidaps.interfaces.Constraint
import info.nightscout.androidaps.interfaces.Constraints
import info.nightscout.androidaps.interfaces.PluginBase
import info.nightscout.interfaces.PluginDescription
import info.nightscout.interfaces.PluginType
import info.nightscout.androidaps.interfaces.ResourceHelper
import info.nightscout.androidaps.plugins.general.overview.events.EventNewNotification
import info.nightscout.interfaces.notifications.Notification
import info.nightscout.plugins.R
import info.nightscout.rx.bus.RxBus
import info.nightscout.rx.logging.AAPSLogger
import info.nightscout.rx.logging.LTag
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class StorageConstraintPlugin @Inject constructor(
    injector: HasAndroidInjector,
    aapsLogger: AAPSLogger,
    rh: ResourceHelper,
    private val rxBus: RxBus
) : PluginBase(
    PluginDescription()
        .mainType(PluginType.CONSTRAINTS)
        .neverVisible(true)
        .alwaysEnabled(true)
        .showInList(false)
        .pluginName(R.string.storage),
    aapsLogger, rh, injector
), Constraints {

    override fun isClosedLoopAllowed(value: Constraint<Boolean>): Constraint<Boolean> {
        val diskFree = availableInternalMemorySize()
        if (diskFree < Constants.MINIMUM_FREE_SPACE) {
            aapsLogger.debug(LTag.CONSTRAINTS, "Closed loop disabled. Internal storage free (Mb):$diskFree")
            value.set(aapsLogger, false, rh.gs(R.string.disk_full, Constants.MINIMUM_FREE_SPACE), this)
            val notification = Notification(Notification.DISK_FULL, rh.gs(R.string.disk_full, Constants.MINIMUM_FREE_SPACE), Notification.NORMAL)
            rxBus.send(EventNewNotification(notification))
        }
        return value
    }

    fun availableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val blocksAvailable = stat.availableBlocksLong
        val size = 1048576 // block size of 1 Mb
        return blocksAvailable * blockSize / size
    }
}