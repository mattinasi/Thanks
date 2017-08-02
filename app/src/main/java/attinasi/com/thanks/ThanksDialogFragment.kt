package attinasi.com.thanks

import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.thanks_card.view.*

class ThanksDialogFragment : DialogFragment() {

    public var data: ThanksData = ThanksData("","")
    public var editComplete: () -> Unit = {}
    public var editCancel: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity.layoutInflater.inflate(R.layout.thanks_card, null)

        initializeCard(view)

        return initializeDialog(view)
    }

    private fun initializeDialog(view: View): Dialog {
        val self = this
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setView(view)
                .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    saveThanks(self, view)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                    cancelThanks(dialog)
                })
        return dialogBuilder.create()
    }

    private fun initializeCard(view: View) {

        view.card_title_text.text = data.title
        view.card_text_edit.setText(data.text)

        view.card_text_view.visibility = View.GONE
        view.card_text_edit.visibility = View.VISIBLE
    }

    private fun saveThanks(self: ThanksDialogFragment, v: View) {
        val newData = ThanksData(v.card_title_text.text.toString(),
                                 v.card_text_edit.text.toString())
        ThanksDataStore.instance.replace(data, newData)

        editComplete()
        self.dialog.cancel()
    }

    private fun cancelThanks(dialog: DialogInterface) {
        editCancel()
        dialog.cancel()
    }
}