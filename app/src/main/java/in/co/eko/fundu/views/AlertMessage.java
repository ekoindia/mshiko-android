package in.co.eko.fundu.views;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.interfaces.AlertMessageInterface;
import in.co.eko.fundu.models.LinkAccountItem;

public class AlertMessage {

    @SuppressWarnings("deprecation")
    public static void showLinkAccountResponseDialog(final Context context, ArrayList<LinkAccountItem> linkAccountItems) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("ALERT");
        View view = LayoutInflater.from(context).inflate(R.layout.link_account_alert_dialog, null);
        LinearLayout alertDialogContainer = (LinearLayout) view.findViewById(R.id.alertDialogContainer);
        alertDialog.setView(view);
        setResponseToAlert(context, alertDialogContainer, linkAccountItems);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    public static void showAlertDialogWithCallback(final Context context, String message, final AlertMessageInterface alertMessageInterface) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertMessageInterface.clickOK();
                dialog.dismiss();

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertMessageInterface.clickCancel();
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    public static void showAlertDialog(final Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private static void setResponseToAlert(Context context, LinearLayout alertDialogContainer, ArrayList<LinkAccountItem> linkAccountItems) {
        View customView;
        for (LinkAccountItem linkAccountItem : linkAccountItems) {
            customView = LayoutInflater.from(context).inflate(R.layout.link_account_alert_item, null);
            TextView additionalIdValue = (TextView) customView.findViewById(R.id.additionalIdValue);
            TextView additionalIdMessage = (TextView) customView.findViewById(R.id.additionalIdMessage);

            additionalIdValue.setText(linkAccountItem.getAdditional_id_value() + " :-");
            String message = linkAccountItem.getFundu_db_message();
            if (message == null)
                message = linkAccountItem.getSb_message();
            additionalIdMessage.setText(message);
            int textColor = Color.RED;
            if ((linkAccountItem.getSb_status() != null && linkAccountItem.getSb_status().equalsIgnoreCase("SUCCESS")) || (linkAccountItem.getFundu_db_status() != null && linkAccountItem.getFundu_db_status().equalsIgnoreCase("SUCCESS"))) {
                textColor = Color.parseColor("#009900");
            }
            additionalIdMessage.setTextColor(textColor);

            alertDialogContainer.addView(customView);
        }
    }


}
