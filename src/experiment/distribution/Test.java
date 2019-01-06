package experiment.distribution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Create by ming on 19-1-1 下午1:28
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class Test {

    public static void main(String[] args) {

        List<Job> jobs = Arrays.asList(new Job(1, 130), new Job(2, 60),
                new Job(3, 100), new Job(4, 200), new Job(5, 140),
                new Job(6, 60), new Job(7, 50), new Job(8, 60));

        System.out.println("动态分区分配");
        System.out.println("-----------------------------------------------------------");
        System.out.println("作业详情如下：");
        System.out.println("作业号      请求内存大小");
        for (Job job : jobs) {
            System.out.println(job.getId() + "          " + job.getSize() + "K");
        }
        System.out.println("请求操作的次序为：\n" +
                "作业1申请内存 ---> 作业2申请内存 ---> 作业2申请内存 ---> 作业2释放内存\n" +
                "作业4申请内存 ---> 作业3释放内存 ---> 作业1释放内存 ---> 作业5申请内存\n" +
                "作业6申请内存 ---> 作业7申请内存 ---> 作业8申请内存\n");

        System.out.println("1. 使用首次适应算法（内存大小为600K）：\n");
        DynamicDistribution ff = new DynamicDistribution(600);
        ff.firstFit(jobs.get(0));
        ff.print();
        ff.firstFit(jobs.get(1));
        ff.print();
        ff.firstFit(jobs.get(2));
        ff.print();
        System.out.println("作业2释放60K内存");
        ff.recoveryMemory(jobs.get(1).getId());
        ff.print();
        ff.firstFit(jobs.get(3));
        ff.print();
        System.out.println("作业3释放100K内存");
        ff.recoveryMemory(jobs.get(2).getId());
        ff.print();
        System.out.println("作业1释放130K内存");
        ff.recoveryMemory(jobs.get(0).getId());
        ff.print();
        ff.firstFit(jobs.get(4));
        ff.print();
        ff.firstFit(jobs.get(5));
        ff.print();
        ff.firstFit(jobs.get(6));
        ff.print();
        ff.firstFit(jobs.get(7));
        ff.print();

        System.out.println("\n\n2. 使用最佳适应算法（内存大小为600K）：\n");
        DynamicDistribution bf = new DynamicDistribution(600);
        bf.firstFit(jobs.get(0));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        bf.firstFit(jobs.get(1));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        bf.firstFit(jobs.get(2));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        System.out.println("作业2释放60K内存");
        bf.recoveryMemory(jobs.get(1).getId());
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        bf.firstFit(jobs.get(3));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        System.out.println("作业3释放100K内存");
        bf.recoveryMemory(jobs.get(2).getId());
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        System.out.println("作业1释放130K内存");
        bf.recoveryMemory(jobs.get(0).getId());
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        bf.firstFit(jobs.get(4));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        bf.firstFit(jobs.get(5));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        bf.firstFit(jobs.get(6));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        bf.firstFit(jobs.get(7));
        bf.getMemory().getFreePartitionChains().sort(Comparator.comparingInt(Memory.Partition::getSize));
        bf.print();
        System.out.println("-----------------------------------------------------------");
    }
}
