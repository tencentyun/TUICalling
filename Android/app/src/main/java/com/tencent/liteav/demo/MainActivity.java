package com.tencent.liteav.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.tencent.imsdk.BaseConstants;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.liteav.basic.UserModel;
import com.tencent.liteav.basic.UserModelManager;
import com.tencent.liteav.debug.GenerateTestUserSig;
import com.tencent.liteav.demo.tpnspush.TPNSPushSetting;
import com.tencent.liteav.trtccalling.TUICallingImpl;
import com.tencent.qcloud.tuicore.TUILogin;

import java.util.ArrayList;
import java.util.List;

/**
 * 音视频通话主界面,分别为:
 * 语音通话,
 * 视频通话,
 * 多人语音通话,
 * 多人视频通话
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context                 mContext;
    private List<TRTCItemEntity>    mTRTCItemEntityList;
    private TRTCRecyclerViewAdapter mTRTCRecyclerViewAdapter;
    private RecyclerView            mRvList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trtc_main);
        mContext = getApplicationContext();
        initStatusBar();
        login();
        initView();
        initData();
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void login() {
        // 初始化并登录
        TUICallingImpl.sharedInstance(this);
        final UserModel userModel = UserModelManager.getInstance().getUserModel();
        V2TIMSDKConfig config = new V2TIMSDKConfig();
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_DEBUG);
        TUILogin.init(this, GenerateTestUserSig.SDKAPPID, null, new V2TIMSDKListener() {

            @Override
            public void onKickedOffline() {
                Log.d(TAG, "You have been kicked off the line. Please login again!");
                ToastUtils.showLong(getString(R.string.trtccalling_user_kicked_offline));
                startLoginActivity();
            }

            @Override
            public void onUserSigExpired() {
                Log.d(TAG, "Your user signature information has expired");
                ToastUtils.showLong(getString(R.string.trtccalling_user_sig_expired));
                startLoginActivity();
            }
        });
        TUILogin.login(userModel.userId, userModel.userSig, new V2TIMCallback() {
            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "login fail code: " + code + " msg:" + msg);
                //userSig过期,需要重新登录;userSig具有时效性,具体请查看GenerateTestUserSig.java文件
                if (BaseConstants.ERR_USER_SIG_EXPIRED == code) {
                    ToastUtils.showLong(R.string.user_sig_expired);
                    startLoginActivity();
                }
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "login onSuccess");
                //注册TPNS
                registerTPNSPush();
            }
        });
    }

    //进入主界面,登录成功后注册,注册TPNS离线推送服务;
    //退出或注销登录时,需要进行反注册
    private void registerTPNSPush() {
        TPNSPushSetting.getInstance().init();
        TPNSPushSetting.getInstance().bindUserID(TUILogin.getUserId());
    }

    //反注册TPNS服务
    private void unregisterTPNSPush() {
        TPNSPushSetting.getInstance().unBindUserID(TUILogin.getUserId());
        TPNSPushSetting.getInstance().unInit();
    }

    private void initView() {
        mRvList = (RecyclerView) findViewById(R.id.main_recycler_view);
        mTRTCItemEntityList = createTRTCItems();
        mTRTCRecyclerViewAdapter = new TRTCRecyclerViewAdapter(mContext, mTRTCItemEntityList,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        TRTCItemEntity entity = mTRTCItemEntityList.get(position);
                        Intent intent = new Intent(mContext, entity.mTargetClass);
                        intent.putExtra("TITLE", entity.mTitle);
                        intent.putExtra("TYPE", entity.mType);
                        startActivity(intent);
                    }
                });
        mRvList.setLayoutManager(new GridLayoutManager(mContext, 1));
        mRvList.setAdapter(mTRTCRecyclerViewAdapter);
        TextView logoutTv = (TextView) findViewById(R.id.tv_login_out);
        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    private void initData() {
        TUICallingImpl.sharedInstance(this).enableFloatWindow(true);
    }

    protected List<TRTCItemEntity> createTRTCItems() {
        List<TRTCItemEntity> list = new ArrayList<>();
        list.add(new TRTCItemEntity(getString(R.string.audio_call),
                getString(R.string.app_tv_voice_call_tips),
                R.drawable.ic_audio_call, TUICallingEntranceActivity.TYPE_AUDIO_CALL,
                TUICallingEntranceActivity.class));
        list.add(new TRTCItemEntity(getString(R.string.video_call),
                getString(R.string.app_tv_video_call_tips),
                R.drawable.ic_video_call, TUICallingEntranceActivity.TYPE_VIDEO_CALL,
                TUICallingEntranceActivity.class));
        list.add(new TRTCItemEntity(getString(R.string.app_item_multi_audio_call),
                getString(R.string.app_tv_voice_call_tips),
                R.drawable.ic_audio_call, TUICallingEntranceActivity.TYPE_MULTI_AUDIO_CALL,
                TUICallingEntranceActivity.class));
        list.add(new TRTCItemEntity(getString(R.string.app_item_multi_video_call),
                getString(R.string.app_tv_video_call_tips),
                R.drawable.ic_video_call, TUICallingEntranceActivity.TYPE_MULTI_VIDEO_CALL,
                TUICallingEntranceActivity.class));
        return list;
    }

    public class TRTCItemEntity {
        public String mTitle;
        public String mContent;
        public int    mIconId;
        public Class  mTargetClass;
        public int    mType;

        public TRTCItemEntity(String title, String content, int iconId, int type, Class targetClass) {
            mTitle = title;
            mContent = content;
            mIconId = iconId;
            mTargetClass = targetClass;
            mType = type;
        }
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }

    private void showLogoutDialog() {
        final Dialog dialog = new Dialog(this, R.style.logoutDialogStyle);
        dialog.setContentView(R.layout.calling_logout_dialog);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_positive);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_negative);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                unregisterTPNSPush();
                startLoginActivity();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void logout() {
        TUILogin.logout(new V2TIMCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "logout success ");
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "logout fail : code = " + code + " , msg = " + msg);
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public class TRTCRecyclerViewAdapter extends
            RecyclerView.Adapter<TRTCRecyclerViewAdapter.ViewHolder> {
        private Context              mContext;
        private List<TRTCItemEntity> mItemEntities;
        private OnItemClickListener  onItemClickListener;

        public TRTCRecyclerViewAdapter(Context context, List<TRTCItemEntity> list,
                                       OnItemClickListener onItemClickListener) {
            this.mContext = context;
            this.mItemEntities = list;
            this.onItemClickListener = onItemClickListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView        mItemImg;
            private TextView         mTitleTv;
            private TextView         mDescription;
            private ConstraintLayout mClItem;
            private View             mBottomLine;

            public ViewHolder(View itemView) {
                super(itemView);
                initView(itemView);
            }

            public void bind(final TRTCItemEntity model, final OnItemClickListener listener) {
                mTitleTv.setText(model.mTitle);
                mDescription.setText(model.mContent);
                if (model.mIconId != 0) {
                    mItemImg.setImageResource(model.mIconId);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(getLayoutPosition());
                    }
                });
                boolean isShow = getLayoutPosition() == mItemEntities.size() - 1;
                mBottomLine.setVisibility((isShow ? View.VISIBLE : View.GONE));
            }

            private void initView(final View itemView) {
                mItemImg = (ImageView) itemView.findViewById(R.id.img_item);
                mTitleTv = (TextView) itemView.findViewById(R.id.tv_title);
                mClItem = (ConstraintLayout) itemView.findViewById(R.id.item_cl);
                mDescription = (TextView) itemView.findViewById(R.id.tv_description);
                mBottomLine = itemView.findViewById(R.id.bottom_line);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.activity_entry_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TRTCItemEntity item = mItemEntities.get(position);
            holder.bind(item, onItemClickListener);
        }

        @Override
        public int getItemCount() {
            return mItemEntities.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
