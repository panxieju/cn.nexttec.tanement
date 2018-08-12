package cn.nexttec.tanement

import android.app.Application
import cn.nexttec.tanement.bean.Location
import com.facebook.drawee.backends.pipeline.Fresco
import org.acra.ACRA
import org.acra.ReportField
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes
import org.acra.sender.HttpSender

/**
@ReportsCrashes(
        formUri = "http://39.108.51.140/crash/crash_report.php",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        //formUriBasicAuthLogin = "username",
        //formUriBasicAuthPassword = "password",
        formKey = "", // This is required for backward compatibility but not used
        customReportContent = [
            ReportField.APP_VERSION_CODE,
            ReportField.APP_VERSION_NAME,
            ReportField.ANDROID_VERSION,
            ReportField.BRAND,
            ReportField.PHONE_MODEL,
            ReportField.PACKAGE_NAME,
            ReportField.REPORT_ID,
            ReportField.BUILD,
            ReportField.STACK_TRACE
        ],
        mode = ReportingInteractionMode.SILENT)
*/
class TanementApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //ACRA.init(this)
        Fresco.initialize(this)
    }

    companion object {
        var token:String = ""
        val firstLocation: Location = Location()
        val secondLocation :Location = Location()
    }
}