package weather.pushparaj.mystormy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Pushparaj on 6/2/2015.
 */
public class AlertDialogFragment extends DialogFragment {
    private String title;
    private String message;
    private String buttonText;

    AlertDialogFragment(String title,String message,String buttonText){
        this.title = title;
        this.message = message;
        this.buttonText = buttonText;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(buttonText,null);
        Dialog dialog = builder.create();
        return dialog;
    }
}
