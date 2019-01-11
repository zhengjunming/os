package course.design;

import course.design.file.FileSystem;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create by ming on 19-1-7 上午1:08
 * <p>
 * OS课设
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class OperatingSystem {

    /**
     * 时间片
     */
    private static final int Q = 4;

    /**
     * 当前的进程
     */
    private static PCB currentProcess;

    /**
     * 等待队列
     */
    private static Queue<PCB> waitQueue = new LinkedList<>();

    /**
     * 后备队列
     */
    private static Queue<JCB> poolQueue = new PriorityQueue<>(Comparator.comparingInt(JCB::getArrivalTime));

    /**
     * 正在运行的作业
     */
    private static Queue<JCB> currentJob = new LinkedList<>();

    /**
     * 存放一开始的作业
     */
    private static Queue<JCB> jobQueue = new PriorityQueue<>(Comparator.comparingInt(JCB::getArrivalTime));

    /**
     * 已完成的作业
     */
    private static Queue<JCB> finishQueue = new LinkedList<>();

    private static DynamicDistribution distribution;

    private static JCB[] jcbs;

    /**
     * 时钟
     */
    private static AtomicInteger clock = new AtomicInteger(0);
    private static AtomicInteger jobNumber = new AtomicInteger(1);
    private static int memorySize = 2048;

    public OperatingSystem() {

    }

    public static void main(String[] args) {
        OperatingSystem os = new OperatingSystem();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            mainMenu();
            System.out.print("请输入你的选择：");
            int choose = scanner.nextInt();
            switch (choose) {
                case 1:
                    scheduler();
                    System.out.println("\n\n\n");
                    break;
                case 2:
                    new FileSystem();
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("请输入正确的值");
                    break;
            }
        }
    }

    private static void scheduler() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入操作系统的内存大小[K]，范围1-2048：");
        memorySize = scanner.nextInt();
        if (memorySize > 2048 || memorySize <= 0) {
            memorySize = 1024;
        }
        Memory memory = new Memory(memorySize);
        System.out.println("操作系统内存大小设置为：" + memorySize + "K.");
        distribution = new DynamicDistribution(memory);
        System.out.print("请输入你想要生成的作业数量（1-15）：");
        int jobSize = scanner.nextInt();
        if (jobSize <= 0 || jobSize > 15) {
            System.out.println("输入有误，已经设为默认值5");
            jobSize = 5;
        }
        jcbs = new JCB[jobSize];
        List<JCB> jobs = produceJob(jobSize);
        System.out.println("---------------------------------------------------------");
        System.out.println("生成的作业信息如下：");
        printInitJob(jobs);
        System.out.println("---------------------------------------------------------");
        jobQueue.addAll(jobs);
        while (true) {
            System.out.println("\n---------------------------------------------------------");
            System.out.printf("当前时刻：%d\n", clock.get());
            // 放入后备队列
            if (!jobQueue.isEmpty()) {
                Iterator<JCB> iterator = jobQueue.iterator();
                while (iterator.hasNext()) {
                    JCB job = iterator.next();
                    if (job.getArrivalTime() > clock.get() - Q && job.getArrivalTime() <= clock.get()) {
                        job.setJobState(JCB.JobState.WAIT);
                        System.out.printf("作业%s到达，进入后备队列。\n", job.getJobName());
                        poolQueue.add(job);
                        iterator.remove();
                    }
                }
                System.out.println();
            }

            // 从后备队列拿作业
            if (!poolQueue.isEmpty()) {
                JCB job = poolQueue.poll();
                if (distribution.bestFit(job)) {
                    // 生成进程
                    PCB process = produceProcess(job);
                    waitQueue.add(process);
                    job.setJobState(JCB.JobState.RUN);
                    currentJob.add(job);
                } else {
                    System.out.println("作业" + job.getJobName() + "申请内存失败，继续留在后备队列！原因：内存中最大的空闲分区大小" +
                            memory.getFreePartitionChains().get(memory.getFreePartitionChains().size() - 1).getSize()
                            + "K小于作业需要的内存大小" + job.getSize() + "K.");
                    poolQueue.add(job);
                }
                System.out.println();
            }
            rr();
            distribution.print();
            System.out.print("后备队列中的作业：");
            if (poolQueue.size() == 0) {
                System.out.println("没有");
            } else {
                System.out.println();
                printInitJob(new ArrayList<>(poolQueue));
            }
            System.out.println("\n运行中的作业：");
            if (currentJob.size() == 0) {
                System.out.println("没有");
            } else {
                printRunningJob(currentJob);
            }
            System.out.print("\n已完成的作业：");
            if (finishQueue.size() == 0) {
                System.out.println("没有");
            } else {
                System.out.println();
                printFinishJob(finishQueue);
            }
            if (jobQueue.size() == 0 && poolQueue.size() == 0 && currentJob.size() == 0) {
                break;
            }
        }
    }

    private static void rr() {
        if (currentProcess == null) {
            // 当前进程为空
            if (!waitQueue.isEmpty()) {
                PCB process = waitQueue.poll();
                process.setProcessState(PCB.ProcessState.RUN);
                currentProcess = process;
            }
        } else if (currentProcess.getRemainingTime() == 0) {
            // 进程已经执行完毕
            JCB job = getJob(currentProcess.getJobId());
            currentJob.remove(job);
            job.setFinishTime(clock.get());
            finishQueue.add(job);
            System.out.printf("作业%d释放内存%dK\n", job.getJobId(), job.getSize());
            // 释放内存
            distribution.recoveryMemory(job.getJobId());
            currentProcess = null;
            if (!waitQueue.isEmpty()) {
                // 选择新进程
                PCB process = waitQueue.poll();
                process.setProcessState(PCB.ProcessState.RUN);
                currentProcess = process;
            }
        } else {
            currentProcess.setProcessState(PCB.ProcessState.WAIT);
            waitQueue.add(currentProcess);
            currentProcess = waitQueue.poll();
            Objects.requireNonNull(currentProcess).setProcessState(PCB.ProcessState.RUN);
        }
        // 修改剩余时间
        if (currentProcess != null) {
            if (Q >= currentProcess.getRemainingTime()) {
                int remainingTime = currentProcess.getRemainingTime();
                currentProcess.setRemainingTime(0);
                clock.compareAndSet(clock.get(), clock.get() + remainingTime);
                JCB job = getJob(currentProcess.getJobId());
                job.setRemainingTime(currentProcess.getRemainingTime());
                return;
            } else {
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - Q);
                JCB job = getJob(currentProcess.getJobId());
                job.setRemainingTime(currentProcess.getRemainingTime());
            }
        }
        // 下一个时间片
        clock.compareAndSet(clock.get(), clock.get() + Q);
    }

    private static PCB produceProcess(JCB job) {
        PCB process = new PCB();
        process.setJobId(job.getJobId());
        process.setProcessState(PCB.ProcessState.WAIT);
        process.setArrivalTime(job.getArrivalTime());
        process.setNeedTime(job.getNeedTime());
        process.setRemainingTime(process.getNeedTime());
        process.setProcessName(job.getJobName().replace("job", "process"));
        return process;
    }

    private static void printInitJob(List<JCB> jobs) {
        System.out.println("作业ID      作业名称     到达时间      需要运行时间    作业大小");
        for (JCB job : jobs) {
            System.out.printf("%3d          %-6s      %3d          %3d          %4dK\n",
                    job.getJobId(), job.getJobName(), job.getArrivalTime(), job.getNeedTime(), job.getSize());
        }
    }

    private static void printRunningJob(Queue<JCB> jobs) {
        System.out.println("作业ID      作业名称     到达时间      剩余运行时间    作业大小");
        for (JCB job : jobs) {
            System.out.printf("%3d          %-6s      %3d          %3d          %4dK\n",
                    job.getJobId(), job.getJobName(), job.getArrivalTime(), job.getRemainingTime(), job.getSize());
        }
    }

    private static void printFinishJob(Queue<JCB> jobs) {
        System.out.println("作业ID      作业名称     到达时间        完成时间    作业大小");
        for (JCB job : jobs) {
            System.out.printf("%3d          %-6s      %3d          %3d          %4dK\n",
                    job.getJobId(), job.getJobName(), job.getArrivalTime(), job.getFinishTime(), job.getSize());
        }
    }

    private static void mainMenu() {
        System.out.print("*****************************************************************\n");
        System.out.println();
        System.out.print("                      3116005120 郑俊铭                           \n");
        System.out.print("                        操作系统课程设计                             \n");
        System.out.println();
        System.out.print("                      1. 作业调度&进程调度\n");
        System.out.println("                      2. 文件系统");
        System.out.print("*****************************************************************\n");
    }

    private static JCB getJob(int jobId) {
        for (JCB job : jcbs) {
            if (job.getJobId() == jobId) {
                return job;
            }
        }
        return null;
    }

    private static List<JCB> produceJob(int jobSize) {
        List<JCB> jobs = new ArrayList<>();
        for (int i = 0; i < jobSize; i++) {
            JCB job = new JCB();
            job.setJobName("job" + jobNumber.get());
            job.setJobId(jobNumber.getAndIncrement());
            job.setArrivalTime(clock.get() + (int) (1 + Math.random() * 25));
            job.setNeedTime((int) (1 + Math.random() * 50));
            job.setSize((int) (1 + Math.random() * (memorySize * 0.5)));
            job.setRemainingTime(job.getNeedTime());
            jobs.add(job);
            jcbs[i] = job;
        }
        return jobs;
    }
}
