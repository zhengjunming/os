package experiment.distribution;

import java.util.Comparator;

/**
 * Create by ming on 19-1-1 下午12:28
 * <p>
 * 动态内存分配
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class DynamicDistribution {

    /**
     * 不可分割的最小分区
     */
    private static final int MIN_SIZE = 5;

    private Memory memory;

    public DynamicDistribution(int size) {
        memory = new Memory(size);
    }

    public Memory getMemory() {
        return memory;
    }

    public void firstFit(Job job) {
        memory.getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getHead));
        if (fit(job)) {
            System.out.println("无可用内存空间");
        }
    }

    public void bestFit(Job job) {
        memory.getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        if (fit(job)) {
            System.out.println("无可用内存空间");
        }
    }

    private boolean fit(Job job) {
        boolean flag = false;
        for (int i = 0; i < memory.getFreePartitionChains().size(); i++) {
            if (memory.getFreePartitionChains().get(i).getSize() >= job.getSize()) {
                allocateMemory(job, i);
                flag = true;
                break;
            }
        }
        return !flag;
    }

    private void allocateMemory(Job job, int index) {
        if (memory.getFreePartitionChains().get(index).getSize() - job.getSize() <= MIN_SIZE) {
            memory.getUsedPartitionChains().add(new Memory.Partition(
                    job.getId(), memory.getFreePartitionChains().get(index).getSize(),
                    memory.getFreePartitionChains().get(index).getHead())
            );
            memory.getFreePartitionChains().remove(index);
            System.out.println("作业" + job.getId() + "成功分配" + memory.getFreePartitionChains().get(index).getSize() + "K内存");
        } else {
            Memory.Partition partition = new Memory.Partition(job.getId(), job.getSize(), memory.getFreePartitionChains().get(index).getHead());
            memory.getUsedPartitionChains().add(partition);
            memory.getFreePartitionChains().get(index).setSize(memory.getFreePartitionChains().get(index).getSize() - job.getSize());
            memory.getFreePartitionChains().get(index).setHead(memory.getFreePartitionChains().get(index).getHead() + job.getSize());
            System.out.println("作业" + job.getId() + "成功分配" + job.getSize() + "K内存");
        }
    }

    public void recoveryMemory(int jobId) {
        Memory.Partition partition = new Memory.Partition();
        for (int i = 0; i < memory.getUsedPartitionChains().size(); i++) {
            if (memory.getUsedPartitionChains().get(i).getJobId() == jobId) {
                partition = memory.getUsedPartitionChains().get(i);
                partition.setJobId(0);
                memory.getUsedPartitionChains().remove(i);
                break;
            }
        }
        memory.getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getHead));
        if (partition.getHead() == 0) {
            if (partition.getHead() + partition.getSize() == memory.getFreePartitionChains().get(0).getHead()) {
                memory.getFreePartitionChains().get(0).setHead(0);
                memory.getFreePartitionChains().get(0).setSize(partition.getSize() + memory.getFreePartitionChains().get(0).getSize());
            } else {
                memory.getFreePartitionChains().add(partition);
            }
        } else if (partition.getHead() + partition.getSize() == memory.getSize()) {
            int size = memory.getFreePartitionChains().size() - 1;
            if (partition.getHead() == memory.getFreePartitionChains().get(size).getHead() +
                    memory.getFreePartitionChains().get(size).getSize()) {
                memory.getFreePartitionChains().get(size)
                        .setSize(memory.getFreePartitionChains().get(size).getSize() + partition.getSize());
            } else {
                memory.getFreePartitionChains().add(partition);
            }
        } else if (memory.getFreePartitionChains().size() == 1) {
            if (partition.getHead() == memory.getFreePartitionChains().get(0).getHead() + memory.getFreePartitionChains().get(0).getSize()) {
                memory.getFreePartitionChains().get(0).setSize(memory.getFreePartitionChains().get(0).getSize() + partition.getSize());
            } else if (partition.getSize() + partition.getHead() == memory.getFreePartitionChains().get(0).getHead()) {
                memory.getFreePartitionChains().get(0).setSize(memory.getFreePartitionChains().get(0).getSize() + partition.getSize());
                memory.getFreePartitionChains().get(0).setHead(partition.getHead());
            } else {
                memory.getFreePartitionChains().add(partition);
            }
        } else if (memory.getFreePartitionChains().size() == 0) {
            memory.getFreePartitionChains().add(partition);
        } else {
            for (int i = 1; i < memory.getFreePartitionChains().size(); i++) {
                if (partition.getHead() >= memory.getFreePartitionChains().get(i - 1).getHead()
                        && partition.getHead() <= memory.getFreePartitionChains().get(i).getHead()) {
                    if (partition.getHead() == memory.getFreePartitionChains().get(i - 1).getHead() + memory.getFreePartitionChains().get(i - 1).getSize()
                            && partition.getHead() + partition.getSize() == memory.getFreePartitionChains().get(i).getHead()) {
                        // 三个合一
                        memory.getFreePartitionChains().get(i - 1).setSize(memory.getFreePartitionChains().get(i - 1).getSize() +
                                memory.getFreePartitionChains().get(i).getSize() + partition.getSize());
                        memory.getFreePartitionChains().iterator().remove();
                    } else if (partition.getHead() + partition.getSize() == memory.getFreePartitionChains().get(i).getHead()) {
                        memory.getFreePartitionChains().get(i).setSize(partition.getSize() + memory.getFreePartitionChains().get(i).getSize());
                        memory.getFreePartitionChains().get(i).setHead(partition.getHead());
                    } else if (partition.getHead() == memory.getFreePartitionChains().get(i - 1).getHead() + memory.getFreePartitionChains().get(i - 1).getSize()) {
                        memory.getFreePartitionChains().get(i - 1).setSize(memory.getFreePartitionChains().get(i - 1).getSize() + partition.getSize());
                    } else {
                        memory.getFreePartitionChains().add(partition);
                    }
                }
            }
        }
        memory.getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getHead));
    }

    public void print() {
        System.out.println("已申请到内存的作业列表：");
        System.out.print("[作业号, 首地址, 分区大小] --> ");
        for (int i = 0; i < memory.getUsedPartitionChains().size(); i++) {
            System.out.printf("[%d, %dK, %dK]", memory.getUsedPartitionChains().get(i).getJobId(),
                    memory.getUsedPartitionChains().get(i).getHead(), memory.getUsedPartitionChains().get(i).getSize());
            if (i != memory.getUsedPartitionChains().size() - 1) {
                System.out.print(" --> ");
            }
        }
        System.out.println();

        System.out.println("空闲内存分区链：");
        System.out.print("[首地址, 分区大小] --> ");
        for (int i = 0; i < memory.getFreePartitionChains().size(); i++) {
            System.out.printf("[%dK, %dK]", memory.getFreePartitionChains().get(i).getHead(),
                    memory.getFreePartitionChains().get(i).getSize());
            if (i != memory.getFreePartitionChains().size() - 1) {
                System.out.print(" --> ");
            }
        }
        System.out.println("\n");
    }
}
