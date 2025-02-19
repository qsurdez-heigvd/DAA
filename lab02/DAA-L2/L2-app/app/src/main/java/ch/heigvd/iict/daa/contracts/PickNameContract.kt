package ch.heigvd.iict.daa.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ch.heigvd.iict.daa.template.EditNameActivity

class PickNameContract : ActivityResultContract<String?, String?>() {

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK) return null
        return intent?.getStringExtra(EditNameActivity.NAME_PARAMETER_KEY)
    }

    override fun createIntent(context: Context, input: String?): Intent {
    return Intent(context, EditNameActivity::class.java).apply {
        putExtra(EditNameActivity.NAME_PARAMETER_KEY, input)
    }
}

}
