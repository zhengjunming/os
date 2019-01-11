package experiment.schedulers;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by ming on 18-12-30 下午8:29
 * <p>
 * 时间片轮转
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class RR {

    /**
     * 轮转时间片
     */
    private static final int Q = 4;
    /**
     * 当前的进程
     */
    private static PCB currentProcess;
    /**
     * 存放一开始的进程
     */
    private static Queue<PCB> processQueue = new LinkedList<>();
    /**
     * 等待队列
     */
    private static Queue<PCB> waitQueue = new LinkedList<>();
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

    public RR(PCB[] pcbs) {
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
        } else {
            currentProcess.setProcessState(PCB.ProcessState.WAIT);
            waitQueue.add(currentProcess);
            currentProcess = waitQueue.poll();
            Objects.requireNonNull(currentProcess).setProcessState(PCB.ProcessState.RUN);
            printProcess();
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
                Iterator<PCB> iterator = processQueue.iterator();
                while (iterator.hasNext()) {
                    PCB pcb = iterator.next();
                    if (pcb.getArrivalTime() > clock - Q && pcb.getArrivalTime() <= clock) {
                        waitQueue.add(pcb);
                        iterator.remove();
                    }
                }
            }
            scheduleProcesses();
            if (currentProcess != null) {
                if (Q >= currentProcess.getRemainingTime()) {
                    int remainingTime = currentProcess.getRemainingTime();
                    currentProcess.setRemainingTime(0);
                    currentProcess.setTurnAroundTime(clock + remainingTime - currentProcess.getArrivalTime());
                    clock += remainingTime;
                    continue;
                } else {
                    currentProcess.setRemainingTime(currentProcess.getRemainingTime() - Q);
                }
            }
            clock += Q;
        }
    }
}
