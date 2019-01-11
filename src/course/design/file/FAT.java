package course.design.file;

/**
 * Create by ming on 19-1-10 下午6:39
 * <p>
 * FAT
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class FAT {

    /** 标识位 */
    private int flag;
    /** 下一个磁盘块的编号 */
    private int next;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
