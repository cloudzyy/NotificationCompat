package com.cloud.library.notificationcompatcolor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Stack;

/**
 * 获取系统默认Notification的标题/内容的文字颜色
 * <p>
 * 默认使用方法在自定义布局后调用 或在 builder.setContent(contentView)后调用下列方法
 * <p>
 * NotificationCompatColor.byAuto(context)
 * .setContentTitleColor(contentView, R.id.primary_tv_title)
 * .setContentTextColor(contentView, R.id.primary_tv_time);
 * <p>
 * 经测试Miui10 Miui9返回正确 Flyme返回正确 SanSung s8返回正常 HuWei返回正常 Oppo vivo 返回正常 Smartisan OS返回正常
 *
 * @author cloud
 */
public class NotificationCompatColor {

    private static final int INVALID_COLOR = 0;
    private final int ANDROID_4_CONTENT_TITLE_COLOR = 0xffffffff;
    private final int ANDROID_4_CONTENT_TEXT_COLOR = 0xff999999;
    private final int ANDROID_5_CONTENT_TITLE_COLOR = 0xde000000;
    private final int ANDROID_5_CONTENT_TEXT_COLOR = 0x8a000000;
    private final int DEFAULT_LIGHT_CONTENT_TITLE_COLOR = ANDROID_4_CONTENT_TITLE_COLOR;//0xffffffff;
    private final int DEFAULT_LIGHT_CONTENT_TEXT_COLOR = ANDROID_4_CONTENT_TEXT_COLOR;//0xff999999;// 0x99=153
    private final int DEFAULT_DARK_CONTENT_TITLE_COLOR = 0xff000000;
    private final int DEFAULT_DARK_CONTENT_TEXT_COLOR = 0xff666666;// 0x66=102
    private final String fakeContentTitle = "fakeContentTitle";
    private final String fakeContentText = "fakeContentText";
    private boolean DEBUG = false;
    private int contentTitleColor = INVALID_COLOR;
    private int contentTextColor = INVALID_COLOR;

    private Context context;

    private String fetchMode = "";

    public NotificationCompatColor(Context context) {
        super();
        this.context = context;

        DEBUG = isApkDebugable(context);

        if (DEBUG) {
            log("start ->" + toString());
        }
    }

    /**
     * 获取是不是dug模式
     *
     * @param context
     * @return
     */
    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    @SuppressLint("LongLogTag")
    private void log(String msg) {
        Log.d("cloud--->NotificationColor", msg);
    }

    @Override
    public String toString() {
        return "NotificationCompatColor." + fetchMode + "\ncontentTitleColor=#" + Integer.toHexString(contentTitleColor)
                + "\ncontentTextColor=#" + Integer.toHexString(contentTextColor) + "";
    }

    /**
     * 使用这个方法即可
     *
     * @param context
     * @return
     **/
    public static NotificationCompatColor AutomationUse(Context context) {
        return new NotificationCompatColor(context.getApplicationContext()).byAutomation();
    }

    public int getContentTitleColor() {
        return contentTitleColor;
    }

    public int getContentTextColor() {
        return contentTextColor;
    }


    /**
     * 设置TitleColor
     *
     * @param remoteViews     RemoteViews对象
     * @param contentTitleIds 布局文件
     * @return
     */
    public NotificationCompatColor setContentTitleColor(RemoteViews remoteViews, int contentTitleIds) {
        remoteViews.setTextColor(contentTitleIds, contentTitleColor);
        return this;
    }

    /**
     * 设置TextColor
     *
     * @param remoteViews    RemoteViews对象
     * @param contentTextIds 布局文件
     * @return
     */
    public NotificationCompatColor setContentTextColor(RemoteViews remoteViews, int contentTextIds) {
        remoteViews.setTextColor(contentTextIds, contentTextColor);
        return this;
    }

    /**
     * 设置多个TitleColor
     *
     * @param remoteViews     RemoteViews对象
     * @param contentTitleIds 布局文件 可变参数
     * @return
     */
    public NotificationCompatColor setContentTitleColor(RemoteViews remoteViews, int... contentTitleIds) {
        for (int tId : contentTitleIds) {
            remoteViews.setTextColor(tId, contentTitleColor);
        }
        return this;
    }

    /**
     * 设置多个TextColor
     *
     * @param remoteViews    RemoteViews对象
     * @param contentTextIds 布局文件 可变参数
     * @return
     */
    public NotificationCompatColor setContentTextColor(RemoteViews remoteViews, int... contentTextIds) {
        for (int cId : contentTextIds) {
            remoteViews.setTextColor(cId, contentTextColor);
        }
        return this;
    }

    /**
     * 先创建默认Notification通过不同方式判断
     * 准确度依次降低：ByText,ById,ByAnyTextView,BySdkVersion
     *
     * @return
     */
    public NotificationCompatColor byAutomation() {
        RemoteViews remoteViews = buildFakeRemoteViews(context);
        if (!fetchNotificationTextColorByText(remoteViews)) {
            if (!fetchNotificationTextColorById(remoteViews)) {
                if (!fetchNotificationTextColorByAnyTextView(remoteViews)) {
                    fetchNotificationTextColorBySdkVersion();
                }
            }
        }
        if (DEBUG) {
            log("end ->" + toString());
        }
        return this;
    }

    /**
     * public该方法用于机型测试
     **/
    public NotificationCompatColor byText() {
        RemoteViews remoteViews = buildFakeRemoteViews(context);
        fetchNotificationTextColorByText(remoteViews);
        if (DEBUG) {
            log("end ->" + toString());
        }
        return this;
    }

    /**
     * 创建默认的Notification获取默认通知内布局
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    private RemoteViews buildFakeRemoteViews(Context context) {

        Notification.Builder builder;
        builder = new Notification.Builder(context);
        builder.setContentTitle(fakeContentTitle)
                .setContentText(fakeContentText)
                .setTicker("fackTicker");

        RemoteViews remoteViews = null;

        if (builder != null) {

            //notification.contentView 在android N 被弃用 返回的remoteViews有可能是空的
            //翻遍官方文档找到builder.createContentView() 来获取remoteViews
            // 官方文档原话：Construct a RemoteViews for the final 1U notification layout.
            //理论上返回为默认RemoteViews 经测试 有效

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                remoteViews = builder.createContentView();

            } else {

                Notification notification = builder.getNotification();
                remoteViews = notification.contentView;
            }

        }
        return remoteViews;
    }

    /**
     * 通过我们设置的contentTitle和contentText的文字来获取对应的textView
     * 如果context是AppCompatActivity则可能会出错，这个没有实际测试
     *
     * @param remoteViews
     * @return
     */
    private boolean fetchNotificationTextColorByText(final RemoteViews remoteViews) {
        if (DEBUG) {
            log("fetchNotificationTextColorByText");
        }
        fetchMode = "ByText";
        try {
            if (remoteViews != null) {
                TextView contentTitleTextView = null, contentTextTextView = null;
                View notificationRootView = remoteViews.apply(context, new FrameLayout(context));

                Stack<View> stack = new Stack<View>();
                stack.push(notificationRootView);
                while (!stack.isEmpty()) {
                    View v = stack.pop();
                    if (v instanceof TextView) {
                        final TextView childTextView = ((TextView) v);
                        final CharSequence charSequence = childTextView.getText();
                        if (TextUtils.equals(fakeContentTitle, charSequence)) {
                            contentTitleTextView = childTextView;
                            if (DEBUG) {
                                log("fetchNotificationTextColorByText -> contentTitleTextView -> OK");
                            }
                        } else if (TextUtils.equals(fakeContentText, charSequence)) {
                            contentTextTextView = childTextView;
                            if (DEBUG) {
                                log("fetchNotificationTextColorByText -> contentTextTextView -> OK");
                            }
                        }
                        if ((contentTitleTextView != null) && (contentTextTextView != null)) {
                            break;
                        }

                    }
                    if (v instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) v;
                        final int count = vg.getChildCount();
                        for (int i = 0; i < count; i++) {
                            stack.push(vg.getChildAt(i));
                        }
                    }
                }
                stack.clear();
                return checkAndGuessColor(contentTitleTextView, contentTextTextView);

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkAndGuessColor(TextView contentTitleTextView, TextView contentTextTextView) {

        if (contentTitleTextView != null) {
            contentTitleColor = contentTitleTextView.getTextColors().getDefaultColor();// .getCurrentTextColor();

        }
        if (contentTextTextView != null) {
            contentTextColor = contentTextTextView.getTextColors().getDefaultColor();
        }
        if (DEBUG) {
            log("checkAndGuessColor-> beforeGuess->" + toString());
        }
        if (contentTitleColor != INVALID_COLOR && contentTextColor != INVALID_COLOR) {
            return true;
        }

        if (contentTitleColor != INVALID_COLOR) {
            if (isLightColor(contentTitleColor)) {
                contentTextColor = DEFAULT_LIGHT_CONTENT_TEXT_COLOR;
            } else {
                contentTextColor = DEFAULT_DARK_CONTENT_TEXT_COLOR;
            }
            return true;
        }
        if (contentTextColor != INVALID_COLOR) {
            if (isLightColor(contentTextColor)) {
                contentTitleColor = DEFAULT_LIGHT_CONTENT_TITLE_COLOR;
            } else {
                contentTitleColor = DEFAULT_DARK_CONTENT_TITLE_COLOR;
            }
            return true;
        }
        return false;
    }

    private static boolean isLightColor(int color) {
        return isLightAverageColor(toAverageColor(color));
    }

    private static boolean isLightAverageColor(int averageColor) {
        return averageColor >= 0x80;
    }

    //RGB的平均值
    private static int toAverageColor(int color) {
        return (int) ((Color.red(color) + Color.green(color) + Color.blue(color)) / 3f + 0.5f);
    }

    /**
     * public该方法用于机型测试
     **/
    public NotificationCompatColor byId() {
        RemoteViews remoteViews = buildFakeRemoteViews(context);
        fetchNotificationTextColorById(remoteViews);
        if (DEBUG) {
            log("end ->" + toString());
        }
        return this;
    }

    /**
     * 通过contentTitle/contentText(反射获取)的id来取得TextView
     *
     * @param remoteViews
     * @return
     */
    private boolean fetchNotificationTextColorById(final RemoteViews remoteViews) {
        if (DEBUG) {
            log("fetchNotificationTextColorById");
        }
        fetchMode = "ById";
        try {
            final int systemNotificationContentTitleId = getAndroidInternalResourceId("title");//android.R.id.title;
            final int systemNotificationContentTextId = getAndroidInternalResourceId("text");//获取android.R.id.text
            if (DEBUG) {
                log("systemNotificationContentId -> #" + Integer.toHexString(systemNotificationContentTextId));
            }
            if (remoteViews != null && remoteViews.getLayoutId() > 0) {
                TextView contentTitleTextView = null, contentTextTextView = null;
                View notificationRootView = LayoutInflater.from(context).inflate(remoteViews.getLayoutId(), null);
                View titleView = notificationRootView.findViewById(systemNotificationContentTitleId);
                if (titleView instanceof TextView) {
                    contentTitleTextView = (TextView) titleView;
                }
                if (systemNotificationContentTextId > 0) {
                    View contentView = notificationRootView.findViewById(systemNotificationContentTextId);
                    contentTextTextView = (TextView) contentView;
                }
                return checkAndGuessColor(contentTitleTextView, contentTextTextView);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getAndroidInternalResourceId(String resourceName) {
        //获取"android"包名里的id
        //即com.android.internal.R.id.resourceName
        //实际上如果getIdentifier没有的话，下面反射的方式也应该是没有的
        //defType = "id"，还可以有"layout","drawable"之类的
        final int id = Resources.getSystem().getIdentifier(resourceName, "id", "android");//defType和defPackage必须指定
        if (id > 0) {
            return id;
        }

        try {
            // 如果上面的方法没有返回id 通过反射获取
            // 反射的方法取com.android.internal.R.id.resourceName
            // 通知栏的大图标imageView的id="icon"
            // 标题是"title" 内容是"text"
            Class<?> clazz = Class.forName("com.android.internal.R$id");
            Field field = clazz.getField(resourceName);
            field.setAccessible(true);
            return field.getInt(null);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * public该方法用于机型测试
     **/
    public NotificationCompatColor byAnyTextView() {
        RemoteViews remoteViews = buildFakeRemoteViews(context);
        fetchNotificationTextColorByAnyTextView(remoteViews);
        if (DEBUG) {
            log("end ->" + toString());
        }
        return this;
    }

    /**
     * 随意取一个textView判断是light或者是dark
     *
     * @param remoteViews
     * @return
     */
    private boolean fetchNotificationTextColorByAnyTextView(final RemoteViews remoteViews) {
        fetchMode = "ByAnyTextView";
        try {
            if (remoteViews != null && remoteViews.getLayoutId() > 0) {
                TextView anyTextView = null;
                View notificationRootView = LayoutInflater.from(context).inflate(remoteViews.getLayoutId(), null);
                Stack<View> stack = new Stack<View>();
                stack.push(notificationRootView);
                while (!stack.isEmpty()) {
                    View v = stack.pop();
                    if (v instanceof TextView) {
                        anyTextView = (TextView) v;
                        break;
                    }
                    if (v instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) v;
                        final int count = vg.getChildCount();
                        for (int i = 0; i < count; i++) {
                            stack.push(vg.getChildAt(i));
                        }
                    }
                }
                stack.clear();
                if (anyTextView != null) {
                    if (isLightColor(anyTextView.getTextColors().getDefaultColor())) {
                        contentTitleColor = DEFAULT_LIGHT_CONTENT_TITLE_COLOR;
                        contentTextColor = DEFAULT_LIGHT_CONTENT_TEXT_COLOR;
                    } else {// DARK textColor
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            contentTitleColor = ANDROID_5_CONTENT_TITLE_COLOR;
                            contentTextColor = ANDROID_5_CONTENT_TEXT_COLOR;
                        } else {
                            contentTitleColor = DEFAULT_DARK_CONTENT_TITLE_COLOR;
                            contentTextColor = DEFAULT_DARK_CONTENT_TEXT_COLOR;
                        }
                    }
                    return true;
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * public该方法用于机型测试
     **/
    public NotificationCompatColor bySdkVersion() {
        fetchNotificationTextColorBySdkVersion();
        if (DEBUG) {
            log("end ->" + toString());
        }
        return this;
    }

    /**
     * 按照安卓版本纯猜测 如果是原生安卓准确度100%
     */
    private void fetchNotificationTextColorBySdkVersion() {
        fetchMode = "BySdkVersion";
        final int SDK_INT = Build.VERSION.SDK_INT;
        final boolean isLightColor = (SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                && (SDK_INT < Build.VERSION_CODES.LOLLIPOP);// 安卓3.0到4.4之间是黑色通知栏
        if (isLightColor) {
            contentTitleColor = DEFAULT_LIGHT_CONTENT_TITLE_COLOR;
            contentTextColor = DEFAULT_LIGHT_CONTENT_TEXT_COLOR;
        } else {// DRAK
            if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contentTitleColor = ANDROID_5_CONTENT_TITLE_COLOR;
                contentTextColor = ANDROID_5_CONTENT_TEXT_COLOR;
            } else {
                contentTitleColor = DEFAULT_DARK_CONTENT_TITLE_COLOR;
                contentTextColor = DEFAULT_DARK_CONTENT_TEXT_COLOR;
            }

        }
    }


}
