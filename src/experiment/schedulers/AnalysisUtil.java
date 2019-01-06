package experiment.schedulers;

import java.util.Queue;

/**
 * Create by ming on 18-12-30 下午11:52
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class AnalysisUtil {

    public static void print(int clock, Queue<PCB> waitQueue, Queue<PCB> finishQueue, PCB currentProcess) {
        System.out.println("----------------------------------------------------------------");
        System.out.println("当前时间：" + clock);
        System.out.println("正在运行的进程：");
        if (currentProcess != null) {
            System.out.printf("进程名称：%s  :  到达时间：%d   需要的总时间：%d   剩余时间：%d\n",
                    currentProcess.getProcessName(), currentProcess.getArrivalTime(), currentProcess.getTotalTime(), currentProcess.getRemainingTime());
        } else {
            System.out.println("没有正在运行的进程");
        }

        System.out.println("\n就绪的进程：");

        if (!waitQueue.isEmpty()) {
            for (PCB pcb : waitQueue) {
                System.out.printf("进程名称：%s  :  到达时间：%d   需要的总时间：%d   剩余时间：%d\n",
                        pcb.getProcessName(), pcb.getArrivalTime(), pcb.getTotalTime(), pcb.getRemainingTime());
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
}
