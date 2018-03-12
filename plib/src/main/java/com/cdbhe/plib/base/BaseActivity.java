package com.cdbhe.plib.base;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdbhe.plib.R;
import com.cdbhe.plib.utils.ActivityStack;
import com.cdbhe.plib.utils.LoadingDialog;
import com.cdbhe.plib.utils.LogUtils;
import com.cdbhe.plib.utils.MeasureUnitTranUtil;
import com.jaeger.library.StatusBarUtil;

/**
 * Activity基类
 * Created by Kevin on 2018/1/30.
 */

public abstract class BaseActivity extends BasePermissionsActivity {
    private ViewDataBinding viewDataBinding;//数据绑定
    public LoadingDialog loadingDialog;//菊花模态加载框
    private ImageView action_esc;//标题栏返回按钮
    private TextView action_title;//标题栏标题
    private ImageView action_more;//标题栏更多图标
    protected OnTitleBarListener onTitleBarListener;//标题栏更多点击回调接口
    private RelativeLayout rlTitleBar;//标题栏

    public abstract int getContentViewResId();
    public abstract void init(Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
        getSupportActionBar().hide();//设置无ActionBar
        viewDataBinding = DataBindingUtil.setContentView(this,getContentViewResId());//初始化DataBinding
        ActivityStack.getInstance().pushActivity(this);//入栈
        initLoadingDialog();//初始化LoadingDialog
        initTitleBar();//初始化TitleBar
        init(savedInstanceState);
    }

    /**
     * 初始化菊花模态加载框
     */
    public void initLoadingDialog(){
        loadingDialog = new LoadingDialog();
        loadingDialog.createLoadingDialog(this);
    }

    /**
     * 显示模态框
     */
    protected void showDialog(){
        loadingDialog.showDialog();
    }

    /**
     * 关闭模态框
     */
    protected void closeDialog(){
        loadingDialog.closeDialog();
    }

    /**
     * 设置模态框样式
     * @param indicatorName
     * @param indicatorColor
     */
    protected void setDialogIndicator(String indicatorName,String indicatorColor){
        loadingDialog.setDialogIndicator(indicatorName,indicatorColor);
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar(){
        //初始化titleBar引入布局
        rlTitleBar = LayoutInflater.from(this).inflate(R.layout.common_title_bar,null).findViewById(R.id.common_title_bar);
        RelativeLayout.LayoutParams params = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                MeasureUnitTranUtil.dip2px(this,50));
        rlTitleBar.setLayoutParams(params);
        action_esc = rlTitleBar.findViewById(R.id.action_esc);
        action_title = rlTitleBar.findViewById(R.id.action_title);
        action_more = rlTitleBar.findViewById(R.id.action_more);
        action_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        action_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTitleBarListener.clickMore(v);
            }
        });

        //动态判断添加
        View mRootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        Class cls = mRootView.getClass();
        if(cls == LinearLayout.class){//线性布局
            LinearLayout linearLayout = (LinearLayout)mRootView;
            linearLayout.addView(rlTitleBar,0);
        }else if(cls == RelativeLayout.class){//相对布局
            RelativeLayout relativeLayout = (RelativeLayout)mRootView;
            relativeLayout.addView(rlTitleBar,0);
        }
    }

    /**
     * 点击更多
     */
    public interface OnTitleBarListener{
        void clickMore(View view);
    }

    /**
     * 显示标题栏
     */
    protected void showTitleBar(){
        rlTitleBar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题栏
     */
    protected void hideTitleBar(){
        rlTitleBar.setVisibility(View.GONE);
    }

    /**
     * 隐藏返回
     */
    protected void hideEsc(){
        action_esc.setVisibility(View.GONE);
    }

    /**
     * 设置标题
     * @param title
     */
    protected void setTitle(String title){
        action_title.setText(title);
    }

    /**
     * 设置标题 标题为String引用
     * @param title
     */
    public void setTitle(int title){
        action_title.setText(title);
    }

    /**
     * 显示更多图标 添加更多按钮点击监听
     * @param onTitleBarListener
     */
    protected void showMore(OnTitleBarListener onTitleBarListener){
        this.onTitleBarListener = onTitleBarListener;
        action_more.setVisibility(View.VISIBLE);
    }

    /**
     * 替换更多图标
     * @param iconId
     */
    protected void replaceMoreIcon(int iconId){
        action_more.setImageResource(iconId);
    }

    /**
     * 获取数据绑定
     * @return
     */
    protected ViewDataBinding getViewDataBinding(){
        return viewDataBinding;
    }

    /**
     * 检查网络是否可用
     * @return
     */
    protected boolean checkNetWork() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }
}
