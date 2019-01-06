package experiment.distribution;

import java.util.LinkedList;
import java.util.List;

/**
 * Create by ming on 19-1-1 下午1:34
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class Memory {
    /**
     * 内存大小
     */
    private int size;

    /**
     * 空闲分区链
     */
    private List<Partition> freePartitionChains = new LinkedList<>();

    /**
     * 已用分区链
     */
    private List<Partition> usedPartitionChains = new LinkedList<>();

    public Memory(int size) {
        this.size = size;
        freePartitionChains.add(new Partition(size, 0));
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Partition> getFreePartitionChains() {
        return freePartitionChains;
    }

    public void setFreePartitionChains(List<Partition> freePartitionChains) {
        this.freePartitionChains = freePartitionChains;
    }

    public List<Partition> getUsedPartitionChains() {
        return usedPartitionChains;
    }

    public void setUsedPartitionChains(List<Partition> usedPartitionChains) {
        this.usedPartitionChains = usedPartitionChains;
    }

    static class Partition {
        @Override
        public String toString() {
            return "Partition{" +
                    "jobId=" + jobId +
                    ", size=" + size +
                    ", head=" + head +
                    '}';
        }

        private int jobId;

        /**
         * 分区大小
         */
        private int size;

        /**
         * 首地址
         */
        private int head;

        Partition() {
        }

        Partition(int jobId, int size, int head) {
            this.jobId = jobId;
            this.size = size;
            this.head = head;
        }

        Partition(int size, int head) {
            this.size = size;
            this.head = head;
        }

        public int getJobId() {
            return jobId;
        }

        public void setJobId(int jobId) {
            this.jobId = jobId;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getHead() {
            return head;
        }

        public void setHead(int head) {
            this.head = head;
        }
    }
}
