package com.siy.mvvm.exm.views.sys;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.ViewTreeObserver;
import android.view.Window;

/**
 * 解决Android5.0以下Dialog引起的内存泄漏 ，详情请看：https://www.cnblogs.com/endure/p/7664320.html
 * <p>
 * Created by Siy on 2018/10/29.
 *
 * @author Siy
 */
public final class DetachableDialogShowListener implements DialogInterface.OnShowListener {

    private DialogInterface.OnShowListener delegateOrNull;

    public static DetachableDialogShowListener wrap(DialogInterface.OnShowListener delegate) {
        return new DetachableDialogShowListener(delegate);
    }

    private DetachableDialogShowListener(DialogInterface.OnShowListener delegate) {
        this.delegateOrNull = delegate;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (delegateOrNull != null) {
            delegateOrNull.onShow(dialog);
        }
    }

    public void clearOnDetach(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.getDecorView()
                        .getViewTreeObserver()
                        .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                            @Override
                            public void onWindowAttached() {
                            }

                            @Override
                            public void onWindowDetached() {
                                if (delegateOrNull != null) {
                                    delegateOrNull = null;
                                }
                            }
                        });
            }
        }
    }
}
