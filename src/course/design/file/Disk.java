package course.design.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by ming on 19-1-10 下午6:45
 * <p>
 * 磁盘
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class Disk {

    /**
     * 分配状态
     */
    private static final int USED = 1;
    /**
     * 结束标志
     */
    private static final int EOF = -1;
    /**
     * 磁盘总空间
     */
    private static final int DISK_SIZE = 1024 * 100;
    /**
     * 磁盘块大小
     */
    private static final int DISK_BLOCK_SIZE = 100;
    /**
     * 磁盘块数目
     */
    private static final int DISK_BLOCK_NUM = DISK_SIZE / DISK_BLOCK_SIZE;
    /**
     * 空闲状态
     */
    private static final int FREE = 0;

    /**
     * 磁盘
     */
    private List<DiskBlock> disk = new ArrayList<>();

    /**
     * 文件分配表
     */
    private List<FAT> fileAllocationTable = new ArrayList<>();

    Disk() {
        for (int i = 0; i < DISK_BLOCK_NUM; i++) {
            DiskBlock diskBlock = new DiskBlock();
            disk.add(diskBlock);
            FAT fat = new FAT();
            fat.setFlag(FREE);
            fat.setNext(EOF);
            fileAllocationTable.add(fat);
        }
    }

    public static int getDiskBlockSize() {
        return DISK_BLOCK_SIZE;
    }

    public int malloc(int pre, String data) {
        FAT fat;
        for (int i = 0; i < DISK_BLOCK_NUM; i++) {
            fat = fileAllocationTable.get(i);
            if (fat.getFlag() == FREE) {
                fat.setFlag(USED);
                if (pre != -1) {
                    FAT preFat = fileAllocationTable.get(pre);
                    // 记录数据关系
                    preFat.setNext(i);
                }
                disk.get(i).setData(data);
                return i;
            }
        }
        return -1;
    }


    public void free(int id) {
        do {
            disk.get(id).setData(null);

            FAT fat = fileAllocationTable.get(id);
            id = fat.getNext();

            fat.setFlag(FREE);
            fat.setNext(EOF);
        } while (id != EOF);
    }

    public String getBlockData(int id) {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            stringBuilder.append(disk.get(id).getData());
            FAT fat = fileAllocationTable.get(id);
            id = fat.getNext();
        } while (id != EOF);
        return stringBuilder.toString();
    }


    public int getFreeBlock() {
        FAT fat;
        for (int i = 0; i < DISK_BLOCK_NUM; i++) {
            fat = fileAllocationTable.get(i);
            if (fat.getFlag() == FREE) {
                return i;
            }
        }
        return -1;
    }

    public void showDisk() {
        for (int i = 0; i < DISK_BLOCK_NUM; i++) {
            System.out.print(fileAllocationTable.get(i).getFlag() + " ");
            if ((i + 1) % 64 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * 磁盘块
     */
    private static class DiskBlock {
        /**
         * 数据
         */
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
