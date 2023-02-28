package com.hyphenate.easemob.easeui.widget.sticker.businesslogic;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class StickerDownloadTask {

	private String packageId;
	private String stickerId;

	StickerDownloadTask(String packageId, String stickerId){
		this.packageId = packageId;
		this.stickerId = stickerId;
	}

	public void downloadSticker(){
//		StickerEntity stickerEntity = StickerPackageApiTask.getStickerSync(this.packageId, this.stickerId);
//		if (stickerEntity != null){
//			DownloadUtil downloadThumb = new DownloadUtil(stickerEntity.getThumbnail());
//			String savePath = StickerPackageStorageTask.getStickerThumbFilePath(this.packageId, this.stickerId);
//			downloadThumb.download(savePath);
//			stickerEntity.setLocalThumbUrl(savePath);
//			DownloadUtil downloadImage = new DownloadUtil(stickerEntity.getUrl());
//			savePath = StickerPackageStorageTask.getStickerImageFilePath(this.packageId, this.stickerId);
//			downloadImage.download(savePath);
//			stickerEntity.setLocalUrl(savePath);
//		}
	}

}
