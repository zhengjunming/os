package course.design.file;

/**
 * Create by ming on 19-1-10 下午7:00
 * <p>
 * 文件
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class File {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件所在的第一个盘块
     */
    private int firstDiskBlock;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFirstDiskBlock() {
        return firstDiskBlock;
    }

    public void setFirstDiskBlock(int firstDiskBlock) {
        this.firstDiskBlock = firstDiskBlock;
    }
}
