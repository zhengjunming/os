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
            if (!waitQueue.isEmpty()) {
                PCB process = waitQueue.poll();
                process.setProcessState(PCB.ProcessState.RUN);
                currentProcess = process;
                printProcess();
            }
        } else if (currentProcess.getRemainingTime() == 0) {
            currentProcess.setProcessState(PCB.ProcessState.FINISH);
            finishQueue.add(currentProcess);
            currentProcess = null;
            if (!waitQueue.isEmpty()) {
                PCB process = waitQueue.poll();
                process.setProcessState(PCB.ProcessState.RUN);
                currentProcess = process;
            }
            printProcess();
        } else if (currentProcess.getRemainingTime() > 0) {
            if (!waitQueue.isEmpty()) {
                if (waitQueue.peek().getRemainingTime() < currentProcess.getRemainingTime()) {
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

    public void avgTurnAroundTime() {
        double avgTurnAroundTime = finishQueue.stream().collect(Collectors.averagingInt(PCB::getTurnAroundTime));
        System.out.println("平均周转时间为： " + avgTurnAroundTime);
    }

    public void execute() {
        while (clock <= totalTime) {
            if (processQueue.size() != 0) {
                for (PCB pcb : processQueue) {
                    if (pcb.getArrivalTime() == clock) {
                        waitQueue.add(pcb);
                    }
                }
            }
            scheduleProcesses();
            if (currentProcess != null) {
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                if (currentProcess.getRemainingTime() == 0) {
                    currentProcess.setTurnAroundTime(clock - currentProcess.getArrivalTime() + 1);
                }
            }
            clock++;
        }
    }
}
