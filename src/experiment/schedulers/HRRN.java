package experiment.schedulers;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by ming on 18-12-30 下午11:46
 * <p>
 * 高响应比调度
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class HRRN {

    /**
     * 当前的进程
     */
    private static PCB currentProcess;
    /**
     * 存放一开始的进程
     */
    private static Queue<PCB> processQueue = new LinkedList<>();
    /**
     * 就绪队列,根据响应比排序
     */
    private static Queue<PCB> waitQueue = new PriorityQueue<>((o1, o2) -> String.valueOf(o2.getRp()).compareTo(String.valueOf(o1.getRp())));
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
    private DecimalFormat format = new DecimalFormat("#.000");

    public HRRN(PCB[] pcbs) {
        processQueue.addAll(Arrays.asList(pcbs));
        totalTime = Arrays.stream(pcbs).mapToInt(PCB::getTotalTime).sum();
        clock = 0;
    }

    private static void printProcess() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("当前时间：" + clock);
        System.out.println("正在运行的进程：");
        if (currentProcess != null) {
            System.out.printf("进程名称：%s  :  到达时间：%d   需要的总时间：%d   剩余时间：%d   等待时间：%d   优先级：%.3f\n",
                    currentProcess.getProcessName(), currentProcess.getArrivalTime(),
                    currentProcess.getTotalTime(), currentProcess.getRemainingTime(),
                    currentProcess.getWaitTime(), currentProcess.getRp());
        } else {
            System.out.println("没有正在运行的进程");
        }

        System.out.println("\n就绪的进程：");

        if (!waitQueue.isEmpty()) {
            for (PCB pcb : waitQueue) {
                System.out.printf("进程名称：%s  :  到达时间：%d   需要的总时间：%d   剩余时间：%d   等待时间：%d   优先级：%.3f\n",
                        pcb.getProcessName(), pcb.getArrivalTime(), pcb.getTotalTime(), pcb.getRemainingTime(),
                        pcb.getWaitTime(), pcb.getRp());
            }
        } else {
            System.out.println("没有就绪的进程");
        }

        System.out.println("\n完成的进程：");

        if (!finishQueue.isEmpty()) {
            for (PCB pcb : finishQueue) {
                System.out.printf("进程名称：%s  :  到达时间：%d   需要的总时间：%d   周转时间：%d\n",
                        pcb.getProcessName(), pcb.getArrivalTime(), pcb.getTotalTime(), pcb.getTurnAroundTime());
            }
        } else {
            System.out.println("没有完成的进程");
        }
        System.out.println("----------------------------------------------------------------\n");
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
                if (String.valueOf(currentProcess.getRp()).compareTo(String.valueOf(waitQueue.peek().getRp())) < 0) {
                    currentProcess.setProcessState(PCB.ProcessState.WAIT);
                    waitQueue.add(currentProcess);
                    currentProcess = waitQueue.poll();
                    Objects.requireNonNull(currentProcess).setProcessState(PCB.ProcessState.RUN);
                    printProcess();
                }
            }
        }
    }

    public double avgTurnAroundTime() {
        return finishQueue.stream().collect(Collectors.averagingInt(PCB::getTurnAroundTime));
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
                } else {
                    currentProcess.setRp(Double.parseDouble(format.format(
                            ((currentProcess.getWaitTime() + currentProcess.getRemainingTime()) * 1.0)
                                    / currentProcess.getRemainingTime())));
                }
            }
            for (PCB pcb : waitQueue) {
                pcb.setWaitTime(pcb.getWaitTime() + 1);
                pcb.setRp(Double.parseDouble(format.format(((pcb.getWaitTime() + pcb.getRemainingTime()) * 1.0) / pcb.getRemainingTime())));
            }
            clock++;
        }
    }
}
