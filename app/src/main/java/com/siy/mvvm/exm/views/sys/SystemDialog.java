package com.siy.mvvm.exm.views.sys;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.siy.mvvm.exm.R;
import com.siy.mvvm.exm.utils.GbdUtils;

import java.lang.reflect.Field;

/**
 * 系统弹框
 * Created by Siy on 2018/10/1 0001.
 *
 * @author Siy
 */
public class SystemDialog extends DialogFragment {

    /**
     * 上下文
     */
    private Context context;

    /**
     * 文字与按钮的分割线
     */
    private View mMsgLineView;

    /**
     * 按钮分割线
     */
    private View mBtnLineView;

    /**
     * 标题文字
     */
    private CharSequence title;

    /**
     * 宽
     */
    private int width;

    /**
     * 高
     */
    private int height;

    /**
     * 标题描述
     */
    private CharSequence titleMsg;

    /**
     * 辅助描述
     */
    private CharSequence helpMsg;

    /**
     * 正文文字
     */
    private CharSequence message;

    /**
     * 右边按钮的文字
     */
    private CharSequence positiveBtnText;

    /**
     * 左边按钮文字
     */
    private CharSequence negativeBtnText;

    /**
     * 自定义视图
     */
    private View contentView;

    /**
     * 获取这个View
     *
     * @return
     */
    public View getContentView() {
        return contentView;
    }

    /**
     * 标题TextView
     */
    private TextView mTitleView;

    /**
     * 标题描述
     */
    private TextView mTitleMsgView;

    /**
     * 辅助描述
     */
    private TextView mHelpMsgView;

    /**
     * 正文
     */
    private TextView mMessageView;

    /**
     * 右边按钮文字
     */
    private TextView mPositiveBtn;

    /**
     * 左边文字
     */
    private TextView mNegativeBtn;

    /**
     * 点击返回键是否消失
     */
    private boolean cancleable;

    /**
     * 点击对话框外是否消失
     */
    private boolean outCancleable;

    private int gravity = Gravity.CENTER;

    /**
     * 背景模糊
     */
    private boolean blur = false;

    private BlurView mBlurView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(context, R.style.fullHeightDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(outCancleable);
        setCancelable(cancleable);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (blur) {
            initBlur(context);
        }
        if (contentView != null) {
            return contentView;
        } else {
            View view = inflater.inflate(R.layout.dialog_sys_common, null, false);
            initView(view);
            return view;
        }
    }

    private void initBlur(Context context) {
        Activity activity = getActivityFromContext(context);
        if (activity == null) {
            return;
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        mBlurView = decorView.findViewWithTag("blurView");
        if (mBlurView == null) {
            mBlurView = new BlurView(activity);
            mBlurView.setTag("blurView");
            mBlurView.setAlpha(0f);
            decorView.addView(mBlurView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBlurView != null) {
            Activity activity = getActivityFromContext(context);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.removeView(mBlurView);
        }
    }

    private Activity getActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        DetachableDialogShowListener showListener;
        dialog.setOnShowListener(showListener = DetachableDialogShowListener.wrap(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (blur && mBlurView != null) {
                            mBlurView.show();
                        }

                        if (mOnShowListener != null) {
                            mOnShowListener.onShow(SystemDialog.this);
                        }
                    }
                })
        );
        showListener.clearOnDetach(dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0x00000000));

            WindowManager.LayoutParams attrs = getDialog().getWindow().getAttributes();
            if (width == 0) {
                //如果宽度等于0就用默认的
                //设置宽度
                attrs.width = GbdUtils.getDeviceSize(context)[0];
                //左右间隔
                attrs.width = (attrs.width - GbdUtils.dip2px(context, 36) * 2);
            } else {
                attrs.width = width;
            }

            if (height > 0) {
                attrs.height = height;
            }

            if (blur && mBlurView != null) {
                attrs.dimAmount = 0.1f;
            }

            //设置dialog显示的位置
            window.setGravity(gravity);
            window.setAttributes(attrs);
        }

        if (blur && mBlurView != null) {
            mBlurView.blur();
        }
    }

    private boolean validateView(View view) {
        return view != null;
    }

    /**
     * 设置辩题
     *
     * @param title 标题文字，如果为null("")就隐藏标题视图
     */
    public void setTitle(CharSequence title) {
        if (validateView(mTitleView)) {
            if (!TextUtils.isEmpty(title)) {
                mTitleView.setVisibility(View.VISIBLE);
                mTitleView.setText(title);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置标题描述
     *
     * @param titleMsg
     */
    public void setTitleMsg(CharSequence titleMsg) {
        if (validateView(mTitleMsgView)) {
            if (!TextUtils.isEmpty(titleMsg)) {
                mTitleView.setVisibility(View.VISIBLE);
                mTitleMsgView.setText(titleMsg);
            } else {
                mTitleMsgView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置辅助描述
     *
     * @param helpMsg
     */
    public void setHelpMsg(CharSequence helpMsg) {
        if (validateView(mHelpMsgView)) {
            if (!TextUtils.isEmpty(helpMsg)) {
                mHelpMsgView.setVisibility(View.VISIBLE);
                mHelpMsgView.setText(helpMsg);
            } else {
                mHelpMsgView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置正文
     *
     * @param message
     */
    public void setMessage(CharSequence message) {
        if (validateView(mMessageView)) {
            if (!TextUtils.isEmpty(message)) {
                mMessageView.setVisibility(View.VISIBLE);
                mMessageView.setText(message);
            } else {
                mMessageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置右边按钮的文字
     * 不设置按钮就隐藏
     *
     * @param positiveStr
     */
    public void setPositiveStr(CharSequence positiveStr) {
        if (validateView(mPositiveBtn)) {
            if (!TextUtils.isEmpty(positiveStr)) {
                mPositiveBtn.setVisibility(View.VISIBLE);
                mPositiveBtn.setText(positiveStr);
            } else {
                mPositiveBtn.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置左边按钮的文字
     * 不设置按钮就隐藏
     *
     * @param negativeStr
     */
    public void setNegativeStr(CharSequence negativeStr) {
        if (validateView(mNegativeBtn)) {
            if (!TextUtils.isEmpty(negativeStr)) {
                mNegativeBtn.setVisibility(View.VISIBLE);
                mNegativeBtn.setText(negativeStr);
            }
        }
    }

    /**
     * 设置右边按钮的点击事件
     * 不设置默认，点击隐藏对话框
     *
     * @param clickListener
     */
    public void setPositiveClickListener(final OnClickListener clickListener) {
        if (validateView(mPositiveBtn)) {
            mPositiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onClick(SystemDialog.this, DialogInterface.BUTTON_POSITIVE);
                    } else {
                        dismiss();
                    }
                }
            });
        }
    }

    /**
     * 设置左边按钮的点击事件
     * 不设置默认，点击隐藏对话框
     *
     * @param clickListener
     */
    public void setNegativeClickListener(final OnClickListener clickListener) {
        if (validateView(mNegativeBtn)) {
            mNegativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onClick(SystemDialog.this, DialogInterface.BUTTON_NEGATIVE);
                    } else {
                        getDialog().dismiss();
                    }
                }
            });
        }
    }

    /**
     * 实例化视图
     *
     * @param view
     */
    private void initView(View view) {
        mMsgLineView = view.findViewById(R.id.dialog_msg_line);

        mBtnLineView = view.findViewById(R.id.dialog_btn_line);

        //标题
        mTitleView = view.findViewById(R.id.dialog_title_tv);
        setTitle(title);

        //标题描述
        mTitleMsgView = view.findViewById(R.id.dialog_title_msg_tv);
        setTitleMsg(titleMsg);

        //辅助描述
        mHelpMsgView = view.findViewById(R.id.dialog_help_tv);
        setHelpMsg(helpMsg);

        //正文
        mMessageView = view.findViewById(R.id.dialog_msg_tv);
        mMessageView.setMovementMethod(ScrollingMovementMethod.getInstance());
        setMessage(message);

        //右邊按鈕
        mPositiveBtn = view.findViewById(R.id.dialog_positive_btn);
        setPositiveStr(positiveBtnText);
        setPositiveClickListener(positiveClickListener);

        //左边按钮
        mNegativeBtn = view.findViewById(R.id.dialog_negative_btn);
        setNegativeStr(negativeBtnText);
        setNegativeClickListener(negativeClickListener);

        judgeLineShow();
    }

    /**
     * 显示Dialog
     */
    public void show() {
        try {
            if (context instanceof FragmentActivity && !((FragmentActivity) context).isFinishing()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!((FragmentActivity) context).isDestroyed() && !isAdded()) {
                        show(((FragmentActivity) context).getSupportFragmentManager(), SystemDialog.class.getName());
                    }
                } else {
                    if (!isAdded()) {
                        show(((FragmentActivity) context).getSupportFragmentManager(), SystemDialog.class.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写show(FragmentManager,String),主要是为了兼容原来项目的代码，防止状态丢失错误
     *
     * @param manager
     * @param tag
     */
    @Override
    public void show(FragmentManager manager, String tag) {
        //利用反射修改这里的值
//        mDismissed = false;
//        mShownByMe = true;
        try {
            Field field1 = DialogFragment.class.getDeclaredField("mDismissed");
            Field field2 = DialogFragment.class.getDeclaredField("mShownByMe");
            field1.setAccessible(true);
            field2.setAccessible(true);
            field1.set(this, false);
            field2.set(this, true);
            field1.setAccessible(false);
            field2.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * 是否消失
     *
     * @return
     */
    public boolean isDismiss() {
        try {
            Field field1 = DialogFragment.class.getDeclaredField("mDismissed");
            field1.setAccessible(true);
            boolean dismissed = (boolean) field1.get(this);
            field1.setAccessible(false);
            return dismissed;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断那是否显示分割线（文字与按钮之间，按钮与按钮之间）
     */
    private void judgeLineShow() {
        if (validateView(mMsgLineView)) {
            if (TextUtils.isEmpty(positiveBtnText) && TextUtils.isEmpty(negativeBtnText)) {
                //如果2个按钮的文字都为空，就不显示文字与按钮分隔线
                mMsgLineView.setVisibility(View.GONE);
            } else {
                mMsgLineView.setVisibility(View.VISIBLE);
            }
        }

        if (validateView(mBtnLineView)) {
            boolean positiveShow = !TextUtils.isEmpty(positiveBtnText) && TextUtils.isEmpty(negativeBtnText);
            boolean negativeShow = TextUtils.isEmpty(positiveBtnText) && !TextUtils.isEmpty(negativeBtnText);

            if (positiveShow || negativeShow) {
                mBtnLineView.setVisibility(View.GONE);

                if (positiveShow) {
                    mPositiveBtn.setBackgroundResource(R.drawable.selector_dialog_btn_bg);
                }

                if (negativeShow) {
                    mNegativeBtn.setBackgroundResource(R.drawable.selector_dialog_btn_bg);
                }

            } else {
                mBtnLineView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(this);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mBlurView != null && blur) {
            mBlurView.hide();
        }

        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
    }

    /**
     * 设置显示监听
     *
     * @param onShowListener
     */
    public void setOnShowListener(OnShowListener onShowListener) {
        this.mOnShowListener = onShowListener;
    }

    /**
     * 设置取消监听
     *
     * @param onCancelListener
     */
    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    /**
     * 设置消失监听
     *
     * @param onDismissListener
     */
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public static final class Builder {
        /**
         * 上下文
         */
        private Context context;

        /**
         * 标题文字
         */
        private CharSequence title;

        /**
         * 标题描述
         */
        private CharSequence titleMsg;

        /**
         * 辅助描述
         */
        private CharSequence helpMsg;

        /**
         * 正文文字
         */
        private CharSequence message;

        /**
         * 位置
         */
        private int gravity;

        /**
         * 右边按钮的文字
         */
        private CharSequence positiveBtnText;

        /**
         * 左边按钮文字
         */
        private CharSequence negativeBtnText;

        /**
         * 右边按钮点击事件
         */
        private OnClickListener positiveClickListener;

        /**
         * 左边按钮点击事件
         */
        private OnClickListener negativeClickListener;

        /**
         * 点击返回键是否消失
         */
        private boolean cancleable = false;

        /**
         * 点击对话框外是否消失
         */
        private boolean outCancleable = false;

        /**
         * 宽
         */
        private int width;

        /**
         * 高
         */
        private int height;

        private boolean blur = false;

        /**
         * 自定义视图
         */
        private View contentView;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 标题
         *
         * @param title
         * @return
         */
        public Builder title(CharSequence title) {
            this.title = title;
            return this;
        }

        /**
         * 标题描述
         *
         * @param titleMsg
         * @return
         */
        public Builder titleMsg(CharSequence titleMsg) {
            this.titleMsg = titleMsg;
            return this;
        }

        /**
         * 辅助描述
         *
         * @param helpMsg
         * @return
         */
        public Builder helpMsg(CharSequence helpMsg) {
            this.helpMsg = helpMsg;
            return this;
        }

        /**
         * 正文
         *
         * @param message
         * @return
         */
        public Builder message(CharSequence message) {
            this.message = message;
            return this;
        }

        /**
         * 右边按钮的文字
         *
         * @param positiveBtnText
         * @return
         */
        public Builder positiveBtnText(CharSequence positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        /**
         * 左边按钮的文字
         *
         * @param negativeBtnText
         * @return
         */
        public Builder negativeBtnText(CharSequence negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        /**
         * 自定义视图
         *
         * @param view
         * @return
         */
        public Builder contentView(View view) {
            contentView = view;
            return this;
        }

        /**
         * 右边按钮点击事件
         * 不设置默认，点击隐藏对话框
         *
         * @param positiveClickListener
         * @return
         */
        public Builder positiveClickListener(OnClickListener positiveClickListener) {
            this.positiveClickListener = positiveClickListener;
            return this;
        }

        /**
         * 左边按钮点击事件
         * 不设置默认，点击隐藏对话框
         *
         * @param negativeClickListener
         * @return
         */
        public Builder negativeClickListener(OnClickListener negativeClickListener) {
            this.negativeClickListener = negativeClickListener;
            return this;
        }

        public Builder cancelable(boolean cancleable) {
            this.cancleable = cancleable;
            return this;
        }

        public Builder outCancleable(boolean outCancleable) {
            this.outCancleable = outCancleable;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }


        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder blur(boolean blur) {
            this.blur = blur;
            return this;
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public SystemDialog create() {
            final SystemDialog dialog = new SystemDialog();
            dialog.context = context;
            dialog.contentView = contentView;
            dialog.title = title;
            dialog.titleMsg = titleMsg;
            dialog.helpMsg = helpMsg;
            dialog.message = message;
            dialog.positiveBtnText = positiveBtnText;
            dialog.negativeBtnText = negativeBtnText;
            dialog.positiveClickListener = positiveClickListener;
            dialog.negativeClickListener = negativeClickListener;
            dialog.cancleable = cancleable;
            dialog.outCancleable = outCancleable;
            dialog.width = width;
            dialog.height = height;
            dialog.blur = blur;
            dialog.gravity = gravity;
            return dialog;
        }
    }

    private OnShowListener mOnShowListener;

    /**
     * 显示监听器
     */
    public interface OnShowListener {
        /**
         * 对话框显示的时候调用
         *
         * @param dialog
         */
        void onShow(DialogFragment dialog);
    }

    private OnCancelListener mOnCancelListener;

    public interface OnCancelListener {
        /**
         * 对话框取消的时候调用
         *
         * @param dialog
         */
        void onCancel(DialogFragment dialog);
    }

    private OnDismissListener mOnDismissListener;

    public interface OnDismissListener {
        /**
         * 对话框消失的时候调用
         *
         * @param dialog
         */
        void onDismiss(DialogFragment dialog);
    }

    /**
     * 右边按钮点击事件
     */
    private OnClickListener positiveClickListener;

    /**
     * 左边按钮点击事件
     */
    private OnClickListener negativeClickListener;


    public interface OnClickListener {

        /**
         * 当对话框按钮点击时调用
         *
         * @param dialog
         * @param which  {@link DialogInterface#BUTTON_NEGATIVE 取消}，{@link DialogInterface#BUTTON_POSITIVE}
         */
        void onClick(DialogFragment dialog, int which);
    }
}
