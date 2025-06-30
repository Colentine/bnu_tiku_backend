package com.ht.bnu_tiku_backend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 图像表
 *
 * @TableName image_file
 */
@TableName(value = "image_file")
@Data
public class ImageFile {
    /**
     * 图像表主键ID，唯一标识一张图像
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图片链接或本地路径
     */
    private String imageUrl;

    /**
     * 关联用户表的ID，表示上传该图像的用户
     */
    private Long uploadedBy;

    /**
     * 图像上传时间，默认为当前时间
     */
    private Date uploadedAt;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ImageFile other = (ImageFile) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getImageUrl() == null ? other.getImageUrl() == null : this.getImageUrl().equals(other.getImageUrl()))
                && (this.getUploadedBy() == null ? other.getUploadedBy() == null : this.getUploadedBy().equals(other.getUploadedBy()))
                && (this.getUploadedAt() == null ? other.getUploadedAt() == null : this.getUploadedAt().equals(other.getUploadedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getImageUrl() == null) ? 0 : getImageUrl().hashCode());
        result = prime * result + ((getUploadedBy() == null) ? 0 : getUploadedBy().hashCode());
        result = prime * result + ((getUploadedAt() == null) ? 0 : getUploadedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", imageUrl=").append(imageUrl);
        sb.append(", uploadedBy=").append(uploadedBy);
        sb.append(", uploadedAt=").append(uploadedAt);
        sb.append("]");
        return sb.toString();
    }
}