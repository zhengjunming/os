package experiment.banker;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Create by ming on 18-12-21 下午1:50
 * <p>
 * 银行家算法
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class Banker extends Thread {

    /**
     * 资源数
     */
    private final static int M = 3;

    /**
     * 线程数
     */
    private final static int N = 5;

    /**
     * 可利用资源向量
     */
    private static int[] available = {10, 15, 12};

    /**
     * 等待的进程
     */
    private static List<PCB> waitPcb = new LinkedList<>();

    /**
     * 存放进程数组
     */
    private static PCB[] pcbs = new PCB[N];

    /**
     * 运行的进程
     */
    private static PCB currentProcess;

    /**
     * 阻塞的进程
     */
    private static List<PCB> blockPcb = new LinkedList<>();

    /**
     * 完成的进程
     */
    private static List<PCB> finishPcb = new ArrayList<>();

    public Banker() {
    }

    public static void main(String[] args) {
        new Banker().start();
    }

    @Override
    public void run() {
        init();
        printInitState();
        int count = 0;
        do {
            System.out.println("当前时间: " + count++);
            bankerAlgorithm();
        } while (!isAllFinished());
        printPcb();
    }

    private boolean isSafe() {
        int[] work = new int[M];
        System.arraycopy(available, 0, work, 0, available.length);
        boolean[] finish = {false, false, false, false, false};
        int count = 0;
        while (count < N) {
            for (int i = 0; i < N; i++) {
                if (!finish[i] && pcbs[i].need[0] <= work[0] && pcbs[i].need[1] <= work[1] && pcbs[i].need[2] <= work[2]) {
                    finish[i] = true;
                    for (int j = 0; j < M; j++) {
                        work[j] += pcbs[i].allocation[j];
                    }
                    break;
                }
            }
            count++;
        }

        for (boolean fin : finish) {
            if (!fin) {
                return false;
            }
        }
        return true;
    }

    private void bankerAlgorithm() {
        if (currentProcess != null) {
            if (isFinished(currentProcess)) {
                // 回收资源
                System.out.printf("进程%s运行完成,回收资源[A, B, C] ---> [%d, %d, %d]\n", currentProcess.processName, currentProcess.allocation[0],
                        currentProcess.allocation[1], currentProcess.allocation[2]);
                recoveryResources(currentProcess);
                currentProcess.processState = PCB.ProcessState.FINISH;
                finishPcb.add(currentProcess);
                currentProcess = null;
            } else {
                // 时间片用完
                // 进入就绪队列
                currentProcess.processState = PCB.ProcessState.WAIT;
                waitPcb.add(currentProcess);
            }

            Iterator<PCB> iterator = blockPcb.iterator();
            while (iterator.hasNext()) {
                PCB pcb = iterator.next();
                if (pcb.request[0] > available[0] || pcb.request[1] > available[1] || pcb.request[2] > available[2]) {
                    System.out.printf("%s请求的资源数量[%d, %d, %d]大于系统拥有的资源数量[%d, %d, %d]，继续待在阻塞队列！\n",
                            pcb.processName, pcb.request[0], pcb.request[1], pcb.request[2],
                            available[0], available[1], available[2]);
                    continue;
                }
                // 尝试分配资源
                tryDistributeResources(pcb);
                if (isSafe()) {
                    pcb.processState = PCB.ProcessState.RUN;
                    currentProcess = pcb;
                    iterator.remove();
                    System.out.printf("进程%s申请资源序列[A, B, C] ---> [%d, %d, %d]安全,可以分配!\n", pcb.processName, pcb.request[0],
                            pcb.request[1], pcb.request[2]);
                    printPcb();
                    return;
                } else {
                    System.out.printf("进程%s申请资源序列[A, B, C] ---> [%d, %d, %d]不安全,不可以分配!\n", pcb.processName, pcb.request[0],
                            pcb.request[1], pcb.request[2]);
                    cancelDistribute(pcb);
                }
            }

            if (waitPcb.size() == 0 && isAllFinished()) {
                return;
            }
            findRunPcb();
        } else {
            findRunPcb();
        }
    }

    private void findRunPcb() {
        for (int i = 0; i < waitPcb.size(); i++) {
            PCB pcb = waitPcb.get(i);
            waitPcb.remove(i);
            i--;
            applyResources(pcb);
            System.out.printf("%s 申请了资源[A, B, C] --> [%d, %d, %d]\n", pcb.processName, pcb.request[0], pcb.request[1], pcb.request[2]);
            if (pcb.request[0] > available[0] || pcb.request[1] > available[1] || pcb.request[2] > available[2]) {
                System.out.printf("进程%s申请的资源[%d, %d, %d]大于现有资源[%d, %d, %d],进入阻塞队列\n", pcb.processName,
                        pcb.request[0], pcb.request[1], pcb.request[2],
                        available[0], available[1], available[2]);
                pcb.processState = PCB.ProcessState.BLOCK;
                blockPcb.add(pcb);
                continue;
            }
            tryDistributeResources(pcb);
            if (isSafe()) {
                pcb.processState = PCB.ProcessState.RUN;
                currentProcess = pcb;
                System.out.printf("进程%s申请资源序列[A, B, C] ---> [%d, %d, %d]安全,可以分配!\n", pcb.processName, pcb.request[0],
                        pcb.request[1], pcb.request[2]);
                printPcb();
                return;
            } else {
                System.out.printf("进程%s申请资源序列[A, B, C] ---> [%d, %d, %d]不安全,不可以分配!\n", pcb.processName, pcb.request[0],
                        pcb.request[1], pcb.request[2]);
                cancelDistribute(pcb);
                waitPcb.add(pcb);
            }
        }
    }

    private void printPcb() {
        System.out.println("\n=======================================================================");
        System.out.println("此时系统资源[A, B, C]：" + Arrays.toString(available) + "\n");
        if (currentProcess != null) {
            System.out.print("正在运行的进程: \n");
            System.out.print("进程名   Need[A, B, C]   Allocation[A, B, C]\n");
            System.out.printf("%-6s  %-17s  %-17s\n", currentProcess.processName, Arrays.toString(currentProcess.need)
                    , Arrays.toString(currentProcess.allocation));
            System.out.println();
        }
        if (!waitPcb.isEmpty()) {
            System.out.print("就绪的进程: \n");
            System.out.print("进程名   Need[A, B, C]   Allocation[A, B, C]\n");
            waitPcb.forEach(pcb -> System.out.printf("%-6s  %-17s  %-17s\n", pcb.processName, Arrays.toString(pcb.need)
                    , Arrays.toString(pcb.allocation)));
            System.out.println();
        }
        if (!blockPcb.isEmpty()) {
            System.out.print("阻塞的进程: \n");
            System.out.print("进程名   Need[A, B, C]    Allocation[A, B, C]\n");
            blockPcb.forEach(pcb -> System.out.printf("%-6s  %-17s  %-17s\n", pcb.processName, Arrays.toString(pcb.need)
                    , Arrays.toString(pcb.allocation)));
            System.out.println();
        }
        if (!finishPcb.isEmpty()) {
            System.out.print("已完成的进程: ");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            finishPcb.forEach(pcb -> stringBuilder.append(pcb.processName).append(", "));
            stringBuilder.replace(stringBuilder.lastIndexOf(", "), stringBuilder.length(), "");
            stringBuilder.append("]");
            System.out.println(stringBuilder.toString());
        }
        System.out.println("=======================================================================\n");
    }

    private void applyResources(PCB pcb) {
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < M; i++) {
            pcb.request[i] = random.nextInt(pcb.need[i] + 1);
        }
    }

    private void tryDistributeResources(PCB pcb) {
        for (int i = 0; i < M; i++) {
            available[i] = available[i] - pcb.request[i];
            pcb.allocation[i] += pcb.request[i];
            pcb.need[i] -= pcb.request[i];
        }
    }

    private void cancelDistribute(PCB pcb) {
        for (int i = 0; i < M; i++) {
            available[i] += pcb.request[i];
            pcb.allocation[i] -= pcb.request[i];
            pcb.need[i] += pcb.request[i];
        }
    }

    private void recoveryResources(PCB pcb) {
        for (int i = 0; i < M; i++) {
            available[i] += pcb.allocation[i];
        }
    }

    private boolean isFinished(PCB pcb) {
        for (int i = 0; i < M; i++) {
            if (pcb.need[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private void init() {
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < N; i++) {
            PCB pcb = new PCB();
            pcb.setProcessName("P" + i);
            for (int j = 0; j < M; j++) {
                pcb.max[j] = pcb.need[j] = random.nextInt((available[j]));
            }
            pcb.processState = PCB.ProcessState.WAIT;
            pcb.allocation = new int[]{0, 0, 0};
            pcb.request = new int[]{0, 0, 0};
            pcbs[i] = pcb;
            waitPcb.add(pcb);
        }
    }

    private void printInitState() {
        System.out.println("------------------------------------------------------");
        System.out.println("资源数量（Available）：");
        System.out.printf("%4s %4s %4s\n", "A", "B", "C");
        System.out.printf("%4d %4d %4d\n", available[0], available[1], available[2]);
        System.out.println("\n初始的资源分配情况");
        System.out.println("              Max        Allocation      Need");
        for (PCB pcb : waitPcb) {
            System.out.printf("   %s   |%2d %4d %4d |%2d %4d %4d |%2d %4d %4d\n",
                    pcb.getProcessName(), pcb.getMax()[0], pcb.getMax()[1], pcb.getMax()[2],
                    pcb.getAllocation()[0], pcb.getAllocation()[1], pcb.getAllocation()[2],
                    pcb.getNeed()[0], pcb.getNeed()[1], pcb.getNeed()[2]);
        }
        System.out.println("------------------------------------------------------");
    }

    private boolean isAllFinished() {
        return finishPcb.size() == N;
    }

    /**
     * 进程PCB
     */
    private static class PCB {

        /**
         * 名称
         */
        private String processName;

        /**
         * 进程状态
         */
        private ProcessState processState;

        /**
         * 需求
         */
        private int[] need = new int[M];

        /**
         * 已分配的资源数
         */
        private int[] allocation = new int[M];

        /**
         * 最大需求
         */
        private int[] max = new int[M];

        /**
         * 请求的资源
         */
        private int[] request = new int[M];


        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }

        public ProcessState getProcessState() {
            return processState;
        }

        public void setProcessState(ProcessState processState) {
            this.processState = processState;
        }


        public int[] getNeed() {
            return need;
        }

        public void setNeed(int[] need) {
            this.need = need;
        }

        @Override
        public String toString() {
            return "PCB{" +
                    "processName='" + processName + '\'' +
                    ", processState=" + processState +
                    ", need=" + Arrays.toString(need) +
                    ", allocation=" + Arrays.toString(allocation) +
                    ", max=" + Arrays.toString(max) +
                    ", request=" + Arrays.toString(request) +
                    '}';
        }

        public int[] getAllocation() {
            return allocation;
        }

        public void setAllocation(int[] allocation) {
            this.allocation = allocation;
        }

        public int[] getMax() {
            return max;
        }

        public void setMax(int[] max) {
            this.max = max;
        }

        enum ProcessState {

            /**
             * 等待状态
             */
            WAIT,
            /**
             * 运行状态
             */
            RUN,
            /**
             * 阻塞状态
             */
            BLOCK,
            /**
             * 完成状态
             */
            FINISH
        }
    }
}
