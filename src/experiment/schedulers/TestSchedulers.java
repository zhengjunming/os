package experiment.schedulers;

/**
 * Create by ming on 18-12-30 下午8:05
 * <p>
 * 测试调度算法
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class TestSchedulers {

    public static void main(String[] args) {
        PCB[] pcbs = new PCB[5];
        PCB temp = new PCB();
        for (int i = 0; i < 5; i++) {
            PCB pcb = new PCB();
            pcb.setProcessId(i + 1);
            pcb.setProcessName(String.valueOf((char) ('A' + i)));
            pcb.setWaitTime(0);
            if (i == 0) {
                pcb.setArrivalTime(0);
            } else {
                pcb.setArrivalTime(temp.getArrivalTime() + (int) (1 + Math.random() * 5));
            }
            pcb.setTotalTime((int) (1 + Math.random() * 20));
            pcb.setRemainingTime(pcb.getTotalTime());
            pcb.setProcessState(PCB.ProcessState.WAIT);
            pcb.setRp(1.0);
            pcbs[i] = pcb;
            temp = pcb;
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println("进程信息如下：");
        for (PCB pcb : pcbs) {
            System.out.printf("进程名称：%s  :  到达时间：%d   需要的总时间：%d\n",
                    pcb.getProcessName(), pcb.getArrivalTime(), pcb.getTotalTime());
        }
        System.out.println("----------------------------------------------------------------\n");

        SJF sjf = new SJF(pcbs);
        sjf.execute();
        sjf.avgTurnAroundTime();

        for (PCB pcb : pcbs) {
            pcb.setRemainingTime(pcb.getTotalTime());
            pcb.setProcessState(PCB.ProcessState.WAIT);
        }

        RR rr = new RR(pcbs);
        rr.execute();
        rr.avgTurnAroundTime();

        for (PCB pcb : pcbs) {
            pcb.setRemainingTime(pcb.getTotalTime());
            pcb.setProcessState(PCB.ProcessState.WAIT);
        }

        HRRN hrrn = new HRRN(pcbs);
        hrrn.execute();
        hrrn.avgTurnAroundTime();
    }
}
