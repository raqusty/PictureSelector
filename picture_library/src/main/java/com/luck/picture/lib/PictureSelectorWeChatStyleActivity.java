package com.luck.picture.lib;


import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.AttrsUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 13:27
 * @describe：PictureSelector WeChatStyle
 */
public class PictureSelectorWeChatStyleActivity extends PictureSelectorActivity {
    private TextView mPictureSendView;
    private RelativeLayout rlAlbum;


    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_selector;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        rlAlbum = findViewById(R.id.rlAlbum);
        mPictureSendView = findViewById(R.id.picture_send);
        mTvPictureOk.setOnClickListener(this);
        mTvPictureOk.setText(getString(R.string.picture_send));
        mTvPicturePreview.setTextSize(16);
        mCbOriginal.setTextSize(16);
        boolean isChooseMode = config.selectionMode ==
                PictureConfig.SINGLE && config.isSingleDirectReturn;
        mTvPictureOk.setVisibility(isChooseMode ? View.GONE : View.VISIBLE);
        if (rlAlbum.getLayoutParams() != null
                && rlAlbum.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlAlbum.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }
    }

    @Override
    public void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(config.style.pictureBottomBgColor);
            } else {
                mBottomLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
            }
            if (config.style.pictureRightTextSize != 0) {
                mTvPictureOk.setTextSize(config.style.pictureRightTextSize);
            }
            if (config.style.pictureOriginalFontColor == 0) {
                mCbOriginal.setTextColor(ContextCompat
                        .getColor(this, R.color.picture_color_white));
            }
            if (config.isOriginalControl) {
                if (config.style.pictureOriginalControlStyle == 0) {
                    mCbOriginal.setButtonDrawable(ContextCompat
                            .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
                }
            }
            if (config.style.pictureContainerBackgroundColor != 0) {
                container.setBackgroundColor(config.style.pictureContainerBackgroundColor);
            }
        } else {
            int pictureBottomBgColor = AttrsUtils.getTypeValueColor(getContext(), R.attr.picture_bottom_bg);
            mBottomLayout.setBackgroundColor(pictureBottomBgColor != 0
                    ? pictureBottomBgColor : ContextCompat.getColor(getContext(), R.color.picture_color_grey));

            mCbOriginal.setTextColor(ContextCompat
                    .getColor(this, R.color.picture_color_white));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.picture_icon_wechat_down);
            mIvArrow.setImageDrawable(drawable);
            if (config.isOriginalControl) {
                mCbOriginal.setButtonDrawable(ContextCompat
                        .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
            }
        }
        mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.c33f94d83));

        super.initPictureSelectorStyle();
        goneParentView();
    }

    /**
     * Hide views that are not needed by the parent container
     */
    private void goneParentView() {
        mTvPictureRight.setVisibility(View.GONE);
        mTvPictureImgNum.setVisibility(View.GONE);
        mPictureSendView.setVisibility(View.GONE);
    }

    @Override
    protected void changeImageNumber(List<LocalMedia> selectData) {
        if (mTvPictureOk == null) {
            return;
        }
        int size = selectData.size();
        boolean enable = size != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setSelected(true);
            mTvPicturePreview.setEnabled(true);
            mTvPicturePreview.setSelected(true);
            initCompleteText(selectData);

            mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.c464646));
            mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.f94d83));
        } else {
            mTvPictureOk.setEnabled(false);
            mTvPictureOk.setSelected(false);
            mTvPicturePreview.setEnabled(false);
            mTvPicturePreview.setSelected(false);

            mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.c66464646));
            mTvPictureOk.setText(getString(R.string.picture_send));
            mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.c33f94d83));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.picture_send) {
            if (folderWindow != null
                    && folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                mTvPictureOk.performClick();
            }
        }
    }

    @Override
    protected void onChangeData(List<LocalMedia> list) {
        super.onChangeData(list);
        initCompleteText(list);
    }

    @Override
    protected void initCompleteText(List<LocalMedia> list) {
        int size = list.size();
        mTvPictureOk.setText("完成("+size+")");
    }
}
