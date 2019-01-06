package experiment.schedulers;

/**
 * Create by ming on 18-12-30 下午8:30
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class PCB {

    /**
     * ID
     */
    private int processId;

    /**
     * 进程名
     */
    private String processName;

    /**
     * 到达时间
     */
    private int arrivalTime;

    /**
     * 需要的运行时间
     */
    private int totalTime;

    /**
     * 剩余时间
     */
    private int remainingTime;

    /**
     * 周转时间
     */
    private int turnAroundTime;
    /**
     * 等待时间
     */
    private int waitTime;
    /**
     * 优先权
     */
    private double rp;
    /**
     * 进程状态
     */
    private ProcessState processState;

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public double getRp() {
        return rp;
    }

    public void setRp(double rp) {
        this.rp = rp;
    }

    @Override
    public String toString() {
        return "PCB{" +
                "processId=" + processId +
                ", processName='" + processName + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", totalTime=" + totalTime +
                ", remainingTime=" + remainingTime +
                ", turnAroundTime=" + turnAroundTime +
                ", waitTime=" + waitTime +
                ", rp=" + rp +
                ", processState=" + processState +
                '}';
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public enum ProcessState {
        /**
         * 就绪
         */
        WAIT,
        /**
         * 运行
         */
        RUN,
        /**
         * 完成
         */
        FINISH
    }
}