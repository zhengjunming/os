package experiment.schedulers;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by ming on 18-12-30 下午3:55
 * <p>
 * 进程短作业调度
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class SJF {

    /**
     * 当前的进程
     */
    private static PCB currentProcess;

    /**
     * 存放一开始的进程
     */
    private static Queue<PCB> processQueue = new PriorityQueue<>(Comparator.comparingInt(PCB::getArrivalTime));

    /**
     * 就绪队列，以剩余时间排序，时间少的在对头
     */
    private static Queue<PCB> waitQueue = new PriorityQueue<>(Comparator.comparingInt(PCB::getRemainingTime));

    /**
     * 已完成的队列
     */
    private static Queue<PCB> finishQueue = new LinkedList<>();

    /**
     * 总时间片
     */
    private static int totalTime;

    /**
     * 时钟
     */
    private static int clock;

    public SJF(PCB[] pcbs) {
        processQueue.addAll(Arrays.asList(pcbs));
        totalTime = Arrays.stream(pcbs).mapToInt(PCB::getTotalTime).sum();
        clock = 0;
    }

    private static void scheduleProcesses() {
        if (currentProcess == null) {
            // 当前进程为空
            if (!waitQueue.isEmpty()) {
                // 在就绪队列查找进程
                PCB process = waitQueue.poll();
                process.setProcessState(PCB.ProcessState.RUN);
                currentProcess = process;
                printProcess();
            }
        } else if (currentProcess.getRemainingTime() == 0) {
            // 剩余时间为0
            currentProcess.setProcessState(PCB.ProcessState.FINISH);
            finishQueue.add(currentProcess);
            currentProcess = null;
            if (!waitQueue.isEmpty()) {
                // 在就绪队列查找进程
                PCB process = waitQueue.poll();
                process.setProcessState(PCB.ProcessState.RUN);
                currentProcess = process;
            }
            printProcess();
        } else if (currentProcess.getRemainingTime() > 0) {
            // 剩余时间大于0
            if (!waitQueue.isEmpty()) {
                if (waitQueue.peek().getRemainingTime() < currentProcess.getRemainingTime()) {
                    // 判断是否有进程需要运行时间小于正在运行的进程
                    currentProcess.setProcessState(PCB.ProcessState.WAIT);
                    waitQueue.add(currentProcess);
                    currentProcess = waitQueue.poll();
                    Objects.requireNonNull(currentProcess).setProcessState(PCB.ProcessState.RUN);
                    printProcess();
                }
            }
        }
    }

    private static void printProcess() {
        AnalysisUtil.print(clock, waitQueue, finishQueue, currentProcess);
    }

    public double avgTurnAroundTime() {
        return finishQueue.stream().collect(Collectors.averagingInt(PCB::getTurnAroundTime));
    }

    public void execute() {
        while (clock <= totalTime) {
            if (processQueue.size() != 0) {
                for (PCB pcb : processQueue) {
                    // 判断是否有进程到达
                    if (pcb.getArrivalTime() == clock) {
                        waitQueue.add(pcb);
                    }
                }
            }
            // 进程调度
            scheduleProcesses();
            // 减少正在运行的进程的剩余时间
            if (currentProcess != null) {
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                if (currentProcess.getRemainingTime() == 0) {
                    // 计算该进程的周转时间
                    currentProcess.setTurnAroundTime(clock - currentProcess.getArrivalTime() + 1);
                }
            }
            clock++;
        }
    }
}
