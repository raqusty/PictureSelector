package com.luck.picture.lib;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.adapter.PictureWeChatPreviewGalleryAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;


/**
 * @author：luck
 * @date：2019-11-30 17:16
 * @describe：PictureSelector WeChatStyle
 */
public class PictureSelectorPreviewWeChatStyleActivity extends PicturePreviewActivity {
    /**
     * alpha duration
     */
    private final static int ALPHA_DURATION = 300;
    private RecyclerView mRvGallery;
    private View bottomLine;
    private PictureWeChatPreviewGalleryAdapter mGalleryAdapter;

    private TextView mSelectNumText;
    private ImageView mIsFireImage;
    private TextView mIsFireText;

    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_preview;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mRvGallery = findViewById(R.id.rv_gallery);
        bottomLine = findViewById(R.id.bottomLine);
        mSelectNumText = findViewById(R.id.select_complete);
        mIsFireImage = findViewById(R.id.is_fire_image);
        mIsFireText = findViewById(R.id.is_fire_text);
        mSelectNumText.setOnClickListener(this);

        mCbOriginal.setTextSize(16);
        mGalleryAdapter = new PictureWeChatPreviewGalleryAdapter(config);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        layoutManager.setOrientation(WrapContentLinearLayoutManager.HORIZONTAL);
        mRvGallery.setLayoutManager(layoutManager);
        mRvGallery.addItemDecoration(new GridSpacingItemDecoration(Integer.MAX_VALUE,
                ScreenUtils.dip2px(this, 8), false));
        mRvGallery.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setItemClickListener((position, media, v) -> {
            if (viewPager != null && media != null) {
                if (isEqualsDirectory(media.getParentFolderName(), currentDirectory)) {
                    int newPosition = isBottomPreview ? position : isShowCamera ? media.position - 1 : media.position;
                    viewPager.setCurrentItem(newPosition);
                } else {
                    // TODO The picture is not in the album directory, click invalid
                }
            }
        });
        if (isBottomPreview) {
            if (selectData != null && selectData.size() > position) {
                int size = selectData.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = selectData.get(i);
                    media.setChecked(false);
                }
                LocalMedia media = selectData.get(position);
                media.setChecked(true);
            }
        } else {
            int size = selectData != null ? selectData.size() : 0;
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectData.get(i);
                if (isEqualsDirectory(media.getParentFolderName(), currentDirectory)) {
                    media.setChecked(isShowCamera ? media.position - 1 == position : media.position == position);
                }
            }
        }

        mIsFireImage.setOnClickListener(v -> {
            setFireItem();
        });

        mIsFireText.setOnClickListener(v -> {
            setFireItem();
        });

    }

    private void setFireItem(){
        LocalMedia media = adapter.getItem(position);
//        selectData.remove(media);
        boolean isFire = !media.isFire();
        media.setFire(isFire);

        mIsFireImage.setSelected(isFire);
    }

    /**
     * Is it the same directory
     *
     * @param parentFolderName
     * @param currentDirectory
     * @return
     */
    private boolean isEqualsDirectory(String parentFolderName, String currentDirectory) {
        return isBottomPreview
                || TextUtils.isEmpty(parentFolderName)
                || TextUtils.isEmpty(currentDirectory)
                || currentDirectory.equals(getString(R.string.picture_camera_roll))
                || parentFolderName.equals(currentDirectory);
    }

    @Override
    public void initPictureSelectorStyle() {
        super.initPictureSelectorStyle();

        onSelectNumChange(false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.picture_send) {
            boolean enable = selectData.size() != 0;
            if (enable) {
                mTvPictureOk.performClick();
            } else {
                btnCheck.performClick();
                boolean isNewEnableStatus = selectData.size() != 0;
                if (isNewEnableStatus) {
                    mTvPictureOk.performClick();
                }
            }
        }
    }

    @Override
    protected void onUpdateSelectedChange(LocalMedia media) {
        onChangeMediaStatus(media);
    }

    @Override
    protected void onSelectedChange(boolean isAddRemove, LocalMedia media) {
        if (isAddRemove) {
            media.setChecked(true);
            if (config.selectionMode == PictureConfig.SINGLE) {
                mGalleryAdapter.addSingleMediaToData(media);
            }
        } else {
            media.setChecked(false);
            mGalleryAdapter.removeMediaToData(media);
            if (isBottomPreview) {
                if (selectData != null && selectData.size() > position) {
                    selectData.get(position).setChecked(true);
                }
                if (mGalleryAdapter.isDataEmpty()) {
                    onActivityBackPressed();
                } else {
                    int currentItem = viewPager.getCurrentItem();
                    adapter.remove(currentItem);
                    adapter.removeCacheView(currentItem);
                    position = currentItem;
                    tvTitle.setText(getString(R.string.picture_preview_image_num,
                            position + 1, adapter.getSize()));
                    check.setSelected(true);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        int itemCount = mGalleryAdapter.getItemCount();
        if (itemCount > 5) {
            mRvGallery.smoothScrollToPosition(itemCount - 1);
        }
    }

    @Override
    protected void onPageSelectedChange(int position,LocalMedia media) {
        super.onPageSelectedChange(position,media);
        if (!config.previewEggs) {
            onChangeMediaStatus(media);
            setSelectItem(media);
        }
    }

    private void setSelectItem(LocalMedia media){
        if (isSelected(media)){
            mIsFireImage.setVisibility(View.VISIBLE);
            mIsFireText.setVisibility(View.VISIBLE);
            mIsFireImage.setSelected(media.isFire());
        }else {
            mIsFireText.setVisibility(View.INVISIBLE);
            mIsFireImage.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * onChangeMediaStatus
     *
     * @param media
     */
    private void onChangeMediaStatus(LocalMedia media) {
        if (mGalleryAdapter != null) {
            int itemCount = mGalleryAdapter.getItemCount();
            if (itemCount > 0) {
                boolean isChangeData = false;
                for (int i = 0; i < itemCount; i++) {
                    LocalMedia item = mGalleryAdapter.getItem(i);
                    if (item == null || TextUtils.isEmpty(item.getPath())) {
                        continue;
                    }
                    boolean isOldChecked = item.isChecked();
                    boolean isNewChecked = item.getPath().equals(media.getPath()) || item.getId() == media.getId();
                    if (!isChangeData) {
                        isChangeData = (isOldChecked && !isNewChecked) || (!isOldChecked && isNewChecked);
                    }
                    item.setChecked(isNewChecked);
                }
                if (isChangeData) {
                    mGalleryAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onSelectNumChange(boolean isRefresh) {
        boolean enable = selectData.size() != 0;
        if (enable) {
            initCompleteText(selectData.size());
            if (mRvGallery.getVisibility() == View.GONE) {
                mRvGallery.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
                mRvGallery.setVisibility(View.VISIBLE);
                bottomLine.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
                bottomLine.setVisibility(View.VISIBLE);
                // 重置一片内存区域 不然在其他地方添加也影响这里的数量
                mGalleryAdapter.setNewData(selectData);
            }
        } else {
            mRvGallery.animate().alpha(0).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
            mRvGallery.setVisibility(View.GONE);
            bottomLine.animate().alpha(0).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
            bottomLine.setVisibility(View.GONE);
        }
        LocalMedia media = adapter.getItem(position);
        setSelectItem(media);
    }


    /**
     * initCompleteText
     */
    @Override
    protected void initCompleteText(int startCount) {
        mSelectNumText.setText("完成("+startCount+")");
    }
}
