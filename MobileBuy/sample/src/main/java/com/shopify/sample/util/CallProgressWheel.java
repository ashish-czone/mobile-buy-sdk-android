package com.shopify.sample.util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.shopify.sample.R;

class CallProgressWheel {

    private static final String TAG = "CallProgressWheel";

    private static ProgressDialog progressDialog;

    /** Displays custom loading dialog */
    static void showLoadingDialog(Context context, String message) {
        try {
            if (isDialogShowing()) {
                dismissLoadingDialog();
            }

            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (activity.isFinishing()) {
                    return;
                }
            }

            int dialogTheme = android.R.style.Theme_NoTitleBar;
            progressDialog = new ProgressDialog(context, dialogTheme);
            progressDialog.show();

            if (progressDialog.getWindow() != null) {
                WindowManager.LayoutParams layoutParams;
                layoutParams = progressDialog.getWindow().getAttributes();
                layoutParams.dimAmount = 0.5f;
                progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                progressDialog.setCancelable(false);
                progressDialog.setContentView(R.layout.progresswheel);
            }

            ((ProgressWheel) progressDialog.findViewById(R.id.progress_wheel)).spin();
            // Set Message below progress wheel
            ((TextView) progressDialog.findViewById(R.id.tvProgress)).setText(message);
        } catch (Exception e) {
            Log.e(TAG, "Error while creating loading.", e);
        }
    }

    static void dismissLoadingDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while dismissing loading.", e);
        }
    }

    private static boolean isDialogShowing() {
        try {
            return progressDialog != null && progressDialog.isShowing();
        } catch (Exception e) {
            Log.e(TAG, "Error while checking loading status", e);
            return false;
        }
    }
}
